package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * Modified by :
 * @author Loic Frueh
 * @author Dejvid Muaremi
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private int numberOfCommands;
    private int numberOfNewStudents;
    private boolean isSuccessOfCommands;


    /**
     * This method return the number of students added to the data store in the last LOAD command sent.
     * @return The number of student sent and added in the last LOAD command.
     */
    @Override
    public int getNumberOfStudentAdded() {
        return numberOfNewStudents;
    }

    /**
     * This method return the number of commands used since the begining of the connection.
     * It only counts the commands that ends with the status equals to "success".
     * @return The number of sucessful commands used since the begining of the connection.
     */
    @Override
    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    /**
     * This method return if the last command used was successfully done or not.
     * @return true if the last command ends with in a "success" status or false if not.
     */
    @Override
    public boolean checkSuccessOfCommand() {
        return isSuccessOfCommands;
    }

    /**
     * This method use the CLEAR specification of the Roulette Protocol V2.
     * It send to the server the CLEAR command in order to reset all the data store by cleaning all students.
     */
    @Override
    public void clearDataStore() throws IOException {
        isSuccessOfCommands = false; // in case an exception appears
        sender(RouletteV2Protocol.CMD_CLEAR);
        if (input.readLine().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
            ++numberOfCommands;
            isSuccessOfCommands = true;
        }
    }

    /**
     * This method use the LIST specification of the Roulette Protocol V2.
     * It send to the server the LIST command in order to receive the list of students currently in the store.
     * The server's response is deserialized.
     * @return a List that contains all the students currently loaded in the data store.
     */
    @Override
    public List<Student> listStudents() throws IOException {
        isSuccessOfCommands = false; // in case an exception appears
        sender(RouletteV2Protocol.CMD_LIST);
        List<Student> studentsList = JsonObjectMapper.parseJson(input.readLine(), StudentsList.class).getStudents();
        ++numberOfCommands;
        isSuccessOfCommands = true;
        return studentsList;
    }

    /**
     * This method is used to make the connection with a server on the choosen port.
     * @param server The server you want to connect with.
     * @param port   The port you want to connect on.
     */
    @Override
    public void connect(String server, int port) throws IOException {
        numberOfCommands = 0;
        numberOfNewStudents = 0;
        isSuccessOfCommands = false;
        super.connect(server, port);
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
        isSuccessOfCommands = false; // in case an exception appears
        sender(RouletteV2Protocol.CMD_BYE);
        ByeCommandResponse byeCommandResponse = JsonObjectMapper.parseJson(input.readLine(), ByeCommandResponse.class);
        isSuccessOfCommands = byeCommandResponse.getStatus().equals("success");
        if (isSuccessOfCommands) {
            ++numberOfCommands; // = byeCommandResponse.getNumberOfCommands();
            if (numberOfCommands == byeCommandResponse.getNumberOfCommands()) {
                LOG.log(Level.INFO, "The command count on the client and the server are the same.");
            }
            // Close all the resources
            socket.close();
            input.close();
            output.close();
            isConnected = false;
        } else {
            LOG.log(Level.SEVERE, "Cannot disconnect the client.");
        }
    }

    /**
     * This method use the LOAD specification of the Roulette Protocol V2.
     * This version send one student's fullname to the server and then terminate the LOAD (with ENDOFDATA).
     * In function of the LOAD end's status (success or not), numberOfCommands and numberOfNewStudents will be modified.
     * @param fullname The full name of the student you want to load in the data store of the server.
     */
    @Override
    public void loadStudent(String fullname) throws IOException {
        isSuccessOfCommands = false; // in case an exception appears

        // Tell the server to start to load the data
        sender(RouletteV2Protocol.CMD_LOAD);
        input.readLine();

        // Send the data
        sender(fullname);

        // Tell the server that all the data are loaded
        sender(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(input.readLine(), LoadCommandResponse.class);
        isSuccessOfCommands = loadCommandResponse.getStatus().equals("success");
        if (isSuccessOfCommands) {
            ++numberOfCommands;
            numberOfNewStudents = 1;
        }

    }

    /**
     * This method use the LOAD specification of the Roulette Protocol V2.
     * This version send a list of students to the server and then terminate the LOAD (with ENDOFDATA).
     * In function of the LOAD end's status (success or not), numberOfCommands and numberOfNewStudents will be modified.
     * @param students The list of students whose full name you want to load in the data store of the server.
     */
    @Override
    public void loadStudents(List<Student> students) throws IOException {
        isSuccessOfCommands = false; // in case an exception appears

        // Tell the server to start to load the data
        sender(RouletteV2Protocol.CMD_LOAD);
        input.readLine();

        for (Student s : students) {
            sender(s.getFullname());
        }

        // Tell the server that all the data are loaded
        sender(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(input.readLine(), LoadCommandResponse.class);
        if (isSuccessOfCommands = loadCommandResponse.getStatus().equals("success")) {
            ++numberOfCommands;
            numberOfNewStudents = loadCommandResponse.getNumberOfNewStudents();
        }
    }

    /**
     * This method use the RANDOM specification of the Roulette Protocol V2.
     * Send the RANDOM command to the server in order to receive a randomly selected student of its data store.
     * The server's response is deserialized and transformed in a Student Object.
     * @return a Student that has been randomly selected by the server in its data store.
     */
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        ++numberOfCommands;
        isSuccessOfCommands = true;
        return super.pickRandomStudent();
    }

    /**
     * This method use a part of the INFO specification of the Roulette Protocol V2.
     * Send the INFO command to the server in order to receive the number of student currently in its data store
     * The server's response is deserialized.
     * @return the number of students currently in the data store.
     */
    @Override
    public int getNumberOfStudents() throws IOException {
        ++numberOfCommands;
        isSuccessOfCommands = true;
        return super.getNumberOfStudents();
    }

    /**
     * This method use a part of the INFO specification of the Roulette Protocol V2.
     * Send the INFO command to the server in order to receive the protocol version.
     * The server's response needs to be deserialized.
     * @return the version of the protocol in use by the server
     */
    @Override
    public String getProtocolVersion() throws IOException {
        ++numberOfCommands;
        isSuccessOfCommands = true;
        return super.getProtocolVersion();
    }
}
