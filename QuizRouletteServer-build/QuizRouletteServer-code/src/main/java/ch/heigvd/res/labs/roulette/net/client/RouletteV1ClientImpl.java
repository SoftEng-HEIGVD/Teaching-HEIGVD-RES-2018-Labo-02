package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 *
 * @modifiedBy Daniel Gonzalez Lopez, Héléna Line Reymond
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    // We declare the socker and the streams.
    protected Socket clientsocket = null;
    protected BufferedReader in = null;
    protected PrintWriter out = null;

    // String used for server responses.
    protected String response;

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    @Override
    public void connect(String server, int port) throws IOException {

        // We create and connect the socket to the server and port given.
        clientsocket = new Socket(server, port);

        // We initialise the streams with the streams of the socket.
        in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
        out = new PrintWriter(clientsocket.getOutputStream());

        // If the socket is connected, we read the first message of the server.
        if (isConnected()) {

            response = in.readLine();

            // If it's the message expected, all's good.
            if (response.equals("Hello. Online HELP is available. Will you find it?")) {

                // We create info logs.
                LOG.log(Level.INFO, "Response sent by the server: ");
                LOG.log(Level.INFO, response);

            } else {

                // We log the problem occurred.
                LOG.log(Level.SEVERE, "Wrong response from server after connection");
                LOG.log(Level.INFO, "Expected: Hello. Online HELP is available. Will you find it?");
                LOG.log(Level.INFO, "Got: " + response);
            }
        }
    }

    @Override
    public void disconnect() throws IOException {

        // Send the CMD_BYE to the server
        sendToServer(RouletteV1Protocol.CMD_BYE);

        /*
         * Then we close the socket's streams and the socket itself.
         * Before closing, we check if the streams or the socket aren't
         * null pointers.
         */

        if (in != null) {
            in.close();
        }

        if (out != null) {
            out.close();
        }

        if (clientsocket != null) {
            clientsocket.close();
        }
    }

    @Override
    public boolean isConnected() {

        // We check if the client is not null,
        // if the client is not closed
        // and if it's still connected.
        return clientsocket != null && !clientsocket.isClosed()
                && clientsocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {

        // We create a list
        ArrayList<Student> sList = new ArrayList<>();

        // We add the student with the fullname given.
        sList.add(new Student(fullname));

        // And we call loadStudents with the list recently created.
        loadStudents(sList);
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {

        // Send the CMD_LOAD to the server.
        sendToServer(RouletteV1Protocol.CMD_LOAD);

        // We read the response from the server.
        String response = in.readLine();

        // If it's the answer expected
        if (response.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {

            // We create info logs.
            LOG.log(Level.INFO, "Response sent by the server: ");
            LOG.log(Level.INFO, response);

            // Then for each student, we send it's fullname to the server.
            for (Student s : students) {
                sendToServer(s.getFullname());
            }

            // And the CMD_LOAD_ENDOFDATA_MARKER to signify the end of the data.
            sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        } else {

            // Otherwise, we log the problem occurred.
            LOG.log(Level.SEVERE, "Wrong response from server after COMMAND: LOAD");
            LOG.log(Level.INFO, "Expected: " + RouletteV1Protocol.RESPONSE_LOAD_START);
            LOG.log(Level.INFO, "Got: " + response);
        }

        // We read the response of the server after sending the data.
        response = in.readLine();

        // If it's not the answer expected
        if (!response.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {

            // We log the problem occurred.
            LOG.log(Level.SEVERE, "Wrong response from server after COMMAND: END OF LOAD");
            LOG.log(Level.INFO, "Expected: " + RouletteV1Protocol.RESPONSE_LOAD_DONE);
            LOG.log(Level.INFO, "Got: " + response);

        } else {

            // Otherwise, we just create info logs.
            LOG.log(Level.INFO, "Response sent by the server: ");
            LOG.log(Level.INFO, response);
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {

        // Send the CMD_RANDOM to the server.
        sendToServer(RouletteV1Protocol.CMD_RANDOM);

        // We read the response from the server.
        String response = in.readLine();

        // We use the JsonObjectMapper to create an instance of
        // RandomCommandResponse with the response obtained.
        RandomCommandResponse rcr = JsonObjectMapper.parseJson(response,
                RandomCommandResponse.class);

        // If there was an error
        if (rcr.getError() != null) {

            // We log the problem occurred.
            LOG.log(Level.SEVERE, "RandomCommandResponse error:");
            LOG.log(Level.INFO, "EmptyStoreException threw.");

            // We throw an EmptyStoreException.
            throw new EmptyStoreException();
        }

        // Otherwise we create info logs
        LOG.log(Level.INFO, "Response sent by the server: ");
        LOG.log(Level.INFO, response);

        return new Student(rcr.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {

        // Send the CMD_INFO to the server.
        sendToServer(RouletteV1Protocol.CMD_INFO);

        // We read the response from the server.
        String response = in.readLine();

        // We use the JsonObjectMapper to create an instance of
        // InfoCommandResponse with the response obtained.
        InfoCommandResponse icr = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);

        // We create info logs
        LOG.log(Level.INFO, "Response sent by the server: ");
        LOG.log(Level.INFO, response);

        return icr.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {

        // Send the CMD_INFO to the server.
        sendToServer(RouletteV1Protocol.CMD_INFO);

        // We read the response from the server.
        String response = in.readLine();

        // We use the JsonObjectMapper to create an instance of
        // InfoCommandResponse with the response obtained.
        InfoCommandResponse icr = JsonObjectMapper.parseJson(response.toString(), InfoCommandResponse.class);

        // We create info logs
        LOG.log(Level.INFO, "Response sent by the server: ");
        LOG.log(Level.INFO, response);

        return icr.getProtocolVersion();
    }

    protected void sendToServer(String data) {

        // Print the data and flush the stream.
        out.println(data);
        out.flush();
    }

}
