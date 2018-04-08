package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Antoine Rochat & Benoit Schopfer
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
	
	protected static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
	
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	protected boolean connected = false;
	
	@Override
	public void connect(String server, int port) throws IOException {
		try {
			clientSocket = new Socket(server, port);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream());
			connected = true;
			LOG.log(Level.INFO, "CONNECTED TO: " + server + ':' + port);
			LOG.log(Level.INFO, in.readLine());
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
			cleanup();
		}
	}
	
	@Override
	public void disconnect() throws IOException {
		LOG.log(Level.INFO, "** socket closed");
		connected = false;
		out.println(RouletteV1Protocol.CMD_BYE);
		cleanup();
	}
	
	@Override
	public boolean isConnected() {
		return connected;
	}
	
	@Override
	public void loadStudent(String fullname) throws IOException {
		sendServerMessage(RouletteV1Protocol.CMD_LOAD);
		readServerMessage();
		sendServerMessage(fullname);
		sendServerMessage(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
		readServerMessage();
	}
	
	@Override
	public void loadStudents(List<Student> students) throws IOException {
		sendServerMessage(RouletteV1Protocol.CMD_LOAD);
		readServerMessage();
		for (Student student : students) {
			sendServerMessage(student.getFullname());
		}
		sendServerMessage(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
		readServerMessage();
	}
	
	@Override
	public Student pickRandomStudent() throws EmptyStoreException, IOException {
		sendServerMessage(RouletteV1Protocol.CMD_RANDOM);
		String student = readServerMessage();
		
		RandomCommandResponse randomReponse = JsonObjectMapper.parseJson(student, RandomCommandResponse.class);
		if(randomReponse.getError() != null){
			throw new EmptyStoreException();
		}
		
		return Student.fromJson(student);
	}
	
	@Override
	public int getNumberOfStudents() throws IOException {
		sendServerMessage(RouletteV1Protocol.CMD_INFO);
		InfoCommandResponse info = JsonObjectMapper.parseJson(readServerMessage(), InfoCommandResponse.class);
		return info.getNumberOfStudents();
	}
	
	@Override
	public String getProtocolVersion() throws IOException {
		sendServerMessage(RouletteV1Protocol.CMD_INFO);
		InfoCommandResponse info = JsonObjectMapper.parseJson(readServerMessage(), InfoCommandResponse.class);
		return info.getProtocolVersion();
	}
	
	protected void cleanup() {
		
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}
		
		if (out != null) {
			out.close();
		}
		
		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	protected void sendServerMessage(String message) {
		out.println(message);
		out.flush();
	}
	
	protected String readServerMessage() throws IOException {
		String message = in.readLine();
		LOG.log(Level.INFO, message);
		return message;
	}
}
