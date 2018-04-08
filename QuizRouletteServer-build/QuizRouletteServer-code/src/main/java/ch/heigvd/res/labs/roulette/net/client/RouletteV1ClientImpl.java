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
 * Modified by :
 * @author Loic Frueh
 * @author Dejvid Muaremi
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
    Socket socket;
    boolean isConnected;
    BufferedReader input;
    PrintWriter output;

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    /**
     * This method is used to make the connection with a server on the choosen port.
     * @param server The server you want to connect with.
     * @param port   The port you want to connect on.
     */
    @Override
    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);
        isConnected = true;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Cannot write to the server {0}", e.getMessage());
            return;
        }
        input.readLine();
    }


    /**
     * This method close the connection with the server. (use the BYE specification)
     */
    @Override
    public void disconnect() throws IOException {
        if (!isConnected) {
            return;
        }

        // Tell to the server to close the connection.
        sender(RouletteV1Protocol.CMD_BYE);

        // Close all the resources
        socket.close();
        input.close();
        output.close();
        isConnected = false;
    }

    /**
     * Indicate if the server is connected or not.
     * @return true if the server is connected or false if not.
     */
    @Override
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * This method use the LOAD specification of the Roulette Protocol V1.
     * This version send one student's fullname to the server and then terminate the LOAD (with ENDOFDATA).
     * @param fullname The full name of the student you want to load in the data store of the server.
     */
    @Override
    public void loadStudent(String fullname) throws IOException {

        // Tell the server to start to load the data
        sender(RouletteV1Protocol.CMD_LOAD);
        input.readLine();

        // Send the data
        sender(fullname);

        // Tell the server that all the data are loaded
        sender(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        input.readLine();
    }

    /**
     * This method use the LOAD specification of the Roulette Protocol V1.
     * This version send a list of students to the server and then terminate the LOAD (with ENDOFDATA).
     * @param students The list of students whose full name you want to load in the data store of the server.
     */
    @Override
    public void loadStudents(List<Student> students) throws IOException {
        // Tell the server to start to load the data
        sender(RouletteV1Protocol.CMD_LOAD);
        input.readLine();

        for (Student s : students) {
            sender(s.getFullname());
        }

        // Tell the server that all the data are loaded
        sender(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        input.readLine();
    }

    /**
     * This method use the RANDOM specification of the Roulette Protocol V1.
     * Send the RANDOM command to the server in order to receive a randomly selected student of its data store.
     * The server's response is deserialized and transformed in a Student Object.
     * @return a Student that has been randomly selected by the server in its data store.
     */
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        sender(RouletteV1Protocol.CMD_RANDOM);
        RandomCommandResponse rcr = JsonObjectMapper.parseJson(input.readLine(), RandomCommandResponse.class);

        if (rcr.getError() != null) {
            throw new EmptyStoreException();
        }
        return new Student(rcr.getFullname());
    }

    /**
     * This method use a part of the INFO specification of the Roulette Protocol V1.
     * Send the INFO command to the server in order to receive the number of student currently in its data store
     * The server's response is deserialized.
     * @return the number of students currently in the data store.
     */
    @Override
    public int getNumberOfStudents() throws IOException {
        sender(RouletteV1Protocol.CMD_INFO);

        InfoCommandResponse icr = JsonObjectMapper.parseJson(input.readLine(), InfoCommandResponse.class);

        return icr.getNumberOfStudents();
    }

    /**
     * This method use a part of the INFO specification of the Roulette Protocol V1.
     * Send the INFO command to the server in order to receive the protocol version.
     * The server's response needs to be deserialized.
     * @return the version of the protocol in use by the server
     */
    @Override
    public String getProtocolVersion() throws IOException {
        sender(RouletteV1Protocol.CMD_INFO);
        InfoCommandResponse icr = JsonObjectMapper.parseJson(input.readLine(), InfoCommandResponse.class);
        return icr.getProtocolVersion();
    }

    /***
     * This sender is used for the communication with the server.
     * @param toServer The message to send to the server.
     */
    void sender(String toServer) {
        output.println(toServer);
        output.flush();
    }

}
