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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    Socket socket;
    boolean isConnected = false;
    BufferedReader in;
    PrintWriter out;

    // method used to send a string to the server. Action commonly done in this code so this avoid duplicating code.
    void sendMessageToServer(String s) {
        out.println(s);
        out.flush();
    }

    @Override
    public void connect(String server, int port) throws IOException {
        try {
            socket = new Socket(server, port);
            isConnected = true;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to connect to the server {0}", e.getMessage());
            return;
        }

        // reading the server response. This response isn't used but will crash the programm if not read.
        // the other in.readLine(); in the following programm (in load's functions) are used for the same reason.
        in.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        if (isConnected == false) {
            return;
        }

        sendMessageToServer(RouletteV1Protocol.CMD_BYE);

        // closing the socket and the input/output
        socket.close();
        in.close();
        out.close();
        isConnected = false;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        sendMessageToServer(RouletteV1Protocol.CMD_LOAD);

        in.readLine();
        sendMessageToServer(fullname);
        sendMessageToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        in.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        sendMessageToServer(RouletteV1Protocol.CMD_LOAD);
        in.readLine();
        for (Student s : students) {
            sendMessageToServer(s.getFullname());
        }
        sendMessageToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        in.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        sendMessageToServer(RouletteV1Protocol.CMD_RANDOM);

        // get the server's answer and convert it from its Json form to the expected one (RandomCommandResponse)
        RandomCommandResponse rcResponse = JsonObjectMapper.parseJson(in.readLine(), RandomCommandResponse.class);

        // if the answer contains an error, we throw the custom exception
        if (rcResponse.getError() != null) {
            throw new EmptyStoreException();
        }

        // Otherwise, we return the newly created Student using the username returned by the server
        return new Student(rcResponse.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        sendMessageToServer(RouletteV1Protocol.CMD_INFO);

        // get the server's answer and convert it from its Json form to the expected one (InfoCommandResponse)
        InfoCommandResponse inResponse = JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);

        return inResponse.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        sendMessageToServer(RouletteV1Protocol.CMD_INFO);

        // get the server's answer and convert it from its Json form to the expected one (InfoCommandResponse)
        InfoCommandResponse inResponse = JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);

        return inResponse.getProtocolVersion();
    }
}
