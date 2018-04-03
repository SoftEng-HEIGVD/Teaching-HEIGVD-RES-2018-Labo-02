package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 * @author Doriane Kaffo
 *
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    Socket clientSocket = null;
    BufferedReader in = null;
    PrintWriter out = null;

    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
        out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        in.readLine(); // we Read the message of welcome of server
    }

    @Override
    public void disconnect() throws IOException {
        out.println(RouletteV1Protocol.CMD_BYE);
        out.flush();
        in.close();
        out.close();
        clientSocket.close();
    }

    @Override
    public boolean isConnected() {
        if (clientSocket != null) {
            return clientSocket.isConnected();
        }
        return false;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        out.println(RouletteV1Protocol.CMD_LOAD);
        out.flush();
        in.readLine(); // we read the message of the server 
        out.println(fullname);
        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();
        in.readLine(); // we Read the message the server about ENDOFDATA
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        out.println(RouletteV1Protocol.CMD_LOAD);
        out.flush();
        in.readLine(); // Read the message : "Send your data [end with ENDOFDATA]" of the server
        for (Student student : students) {
            out.println(student.getFullname());
        }
        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();
        in.readLine(); // we Read the message the server about DATALOADED 
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        String serverResponseJson;
        RandomCommandResponse serverResponse;

        out.println(RouletteV1Protocol.CMD_RANDOM);
        out.flush();
        serverResponseJson = in.readLine();
        serverResponse = JsonObjectMapper.parseJson(serverResponseJson, RandomCommandResponse.class);
        
        if (!serverResponse.getError().isEmpty()) { //exeception if we have an error
            throw new EmptyStoreException();
        }
        return new Student(serverResponse.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        String serverResponseJson = null;
        InfoCommandResponse serverResponse = null;

        out.println(RouletteV1Protocol.CMD_INFO);
        out.flush();
        serverResponseJson = in.readLine();
        serverResponse = JsonObjectMapper.parseJson(serverResponseJson, InfoCommandResponse.class);
        return serverResponse.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        String serverResponseJson = null;
        InfoCommandResponse serverResponse = null;
        
        out.println(RouletteV1Protocol.CMD_INFO);
        out.flush();
        serverResponseJson = in.readLine();
        serverResponse = JsonObjectMapper.parseJson(serverResponseJson, InfoCommandResponse.class);
        return serverResponse.getProtocolVersion();
    }

}
