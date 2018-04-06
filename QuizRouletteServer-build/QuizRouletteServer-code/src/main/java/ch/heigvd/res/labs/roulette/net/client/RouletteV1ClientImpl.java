
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * Modified by : Julien Biefer, Léo Cortès
 *
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    protected static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    protected Socket clientSocket = null;
    protected BufferedReader reader = null;
    protected PrintWriter writer = null;


    protected void writeAndFlush(String s) {
        writer.write(s + "\n");
        writer.flush();
    }

    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new PrintWriter(clientSocket.getOutputStream());

        // Read the "welcome" string from the server (even if not used)
        reader.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        writeAndFlush(RouletteV1Protocol.CMD_BYE);
        reader.close();
        writer.close();
        clientSocket.close();
    }

    @Override
    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        writeAndFlush(RouletteV1Protocol.CMD_LOAD);

        // Event if we won't need the server's response, we read it to avoid further reading problems
        LOG.log(Level.INFO, reader.readLine());
        writeAndFlush(fullname);
        writeAndFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        LOG.log(Level.INFO, reader.readLine());
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        writeAndFlush(RouletteV1Protocol.CMD_LOAD);
        LOG.log(Level.INFO, reader.readLine());

        // Writing the name of each student
        for(Student s : students) {
            writeAndFlush(s.getFullname());
        }

        writeAndFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        LOG.log(Level.INFO, reader.readLine());
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        writeAndFlush(RouletteV1Protocol.CMD_RANDOM);
        String response = reader.readLine();
        Student student;

        // If we can't build a Student object from the JSON String it's the "error" message from the server
        try {
            student = JsonObjectMapper.parseJson(response, Student.class);
        }
        catch (IOException e){
            throw new EmptyStoreException();
        }

        return student;
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        writeAndFlush(RouletteV1Protocol.CMD_INFO);
        String infoResponse = reader.readLine();

        // Building the InfoCommandResponse from the server's JSON String
        InfoCommandResponse info = JsonObjectMapper.parseJson(infoResponse, InfoCommandResponse.class);
        return info.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        writeAndFlush(RouletteV1Protocol.CMD_INFO);
        String infoResponse = reader.readLine();

        InfoCommandResponse info = JsonObjectMapper.parseJson(infoResponse, InfoCommandResponse.class);
        return info.getProtocolVersion();
    }
}
