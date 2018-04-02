
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

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

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket clientSocket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;


    public void writeAndFLush(String s){
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
        writeAndFLush(RouletteV1Protocol.CMD_BYE);
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
        writeAndFLush(RouletteV1Protocol.CMD_LOAD);

        // Event if we won't need the server's response, we read it to avoid further reading problems
        reader.readLine();
        writeAndFLush(fullname);
        writeAndFLush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        reader.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        writeAndFLush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        reader.readLine();

        // Writing the name of each student
        for(Student s : students){
            writeAndFLush(s.getFullname());
        }

        writeAndFLush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        reader.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        writeAndFLush(RouletteV1Protocol.CMD_RANDOM);
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
        writeAndFLush(RouletteV1Protocol.CMD_INFO);
        String infoResponse = reader.readLine();

        // Building the InfoCommandResponse from the server's JSON String
        InfoCommandResponse infos = JsonObjectMapper.parseJson(infoResponse, InfoCommandResponse.class);
        return infos.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        writeAndFLush(RouletteV1Protocol.CMD_INFO);
        String infoResponse = reader.readLine();

        InfoCommandResponse infos = JsonObjectMapper.parseJson(infoResponse, InfoCommandResponse.class);
        return infos.getProtocolVersion();
    }
}
