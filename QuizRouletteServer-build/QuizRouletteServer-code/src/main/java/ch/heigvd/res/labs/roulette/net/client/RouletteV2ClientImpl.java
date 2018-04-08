package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (v2).
 *
 * @author Olivier Liechti
 *
 * modifiedBy: Daniel Gonzalez Lopez, Héléna Line Reymond
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl
        implements IRouletteV2Client {

    private int commandCounter;

    private ByeCommandResponse bcr;
    private LoadCommandResponse lcr;

    private static final Logger LOG =
            Logger.getLogger(RouletteV2ClientImpl.class.getName());

    @Override
    public void connect(String server, int port) throws IOException {

        // We connect to the server
        super.connect(server, port);

        // And we set the ByCommandResponse and LoadCommandResponse instances
        // to null, to avoid recovering data from older connections
        bcr = null;
        lcr = null;

        // And we set back commandCounter to 0
        commandCounter = 0;
    }

    @Override
    public void disconnect() throws IOException {

        // Send the CMD_BYE to the server
        sendCommand(RouletteV2Protocol.CMD_BYE);

        response = in.readLine();

        // We create info logs
        LOG.log(Level.INFO, "Response sent by the server:");
        LOG.log(Level.INFO, response);

        // We recover the response in the ByeCommandResponse instance
        bcr = JsonObjectMapper.parseJson(response, ByeCommandResponse.class);

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
    public void loadStudents(List<Student> students) throws IOException {

        // Send the CMD_LOAD to the server.
        sendCommand(RouletteV1Protocol.CMD_LOAD);

        // We read the response from the server.
        String response = in.readLine();

        // If it's the answer expected
        if (response.equals(RouletteV2Protocol.RESPONSE_LOAD_START)) {

            // We create info logs.
            LOG.log(Level.INFO, "Response sent by the server: ");
            LOG.log(Level.INFO, response);

            // Then for each student, we send it's fullname to the server.
            for (Student s : students) {
                sendData(s.getFullname());
            }

            // And the CMD_LOAD_ENDOFDATA_MARKER to signify the end of the data.
            sendCommand(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        } else {

            // Otherwise, we log the problem occurred.
            LOG.log(Level.SEVERE,
                    "Wrong response from server after COMMAND: LOAD");
            LOG.log(Level.INFO, "Expected: "
                    + RouletteV1Protocol.RESPONSE_LOAD_START);
            LOG.log(Level.INFO, "Got: " + response);
        }

        // We read the response of the server after sending the data.
        response = in.readLine();

        // Recover the response and save it in the LoadCommandResponse instance
        lcr = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
    }

    @Override
    public void clearDataStore() throws IOException {

        // Send the CMD_CLEAR to the server.
        sendCommand(RouletteV2Protocol.CMD_CLEAR);

        response = in.readLine();

        // We check if we got the right answer from the server
        if (response.equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {

            // All good, we create logs
            LOG.log(Level.INFO, "Response sent by the server:");
            LOG.log(Level.INFO, response);

        } else {

            // We log the problem occurred.
            LOG.log(Level.SEVERE,
                    "Wrong response from server after connection");
            LOG.log(Level.INFO, "Expected: "
                    + RouletteV2Protocol.RESPONSE_CLEAR_DONE);
            LOG.log(Level.INFO, "Got: " + response);
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {

        // Send the CMD_LIST to the server.
        sendCommand(RouletteV2Protocol.CMD_LIST);

        response = in.readLine();

        // We create info logs
        LOG.log(Level.INFO, "Response sent by the server:");
        LOG.log(Level.INFO, response);

        // Recover the response and save it in a StudentList instance
        StudentsList sl =
                JsonObjectMapper.parseJson(response, StudentsList.class);

        return sl.getStudents();
    }

    public int getNumberOfCommands() {

        int numOfCommands;

        // If the ByeCommandResponse instance is not null, we return the number
        // of commands given by the server response.
        // Otherwise, we return the data from our local command counter
        if (bcr != null) {
            numOfCommands = bcr.getNumberOfCommands();
        } else {
            numOfCommands = commandCounter;
        }

        return numOfCommands;
    }

    public int getNumberOfStudentAdded() {
        return lcr.getNumberOfNewStudents();
    }

    public boolean checkSuccessOfCommand() {

        // If the ByeCommandResponse instance is null, we recover the status of
        // the LOAD command. Otherwise, we recover the status of the BYE command
        if (bcr != null) {
            return bcr.getStatus().equals("success");
        } else {
            return lcr.getStatus().equals("success");
        }
    }

    @Override
    protected void sendCommand(String command) {

        // If the command is a real command (and not an end of command), we
        // increment the local command counter
        if (!command.equals(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER)) {
            ++commandCounter;
        }

        // And we call the super class method
        super.sendCommand(command);
    }
}
