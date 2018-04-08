package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandReponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandReponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti, François Burgener, Bryan Curchod
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private ByeCommandReponse bye = null;
    private LoadCommandReponse load = null;
    private int numberOfCommands = 0;

    /**
     * Connect the client to a server and reset the attributes
     * @param server the IP address or DNS name of the servr
     * @param port the TCP port on which the server is listening
     * @throws IOException
     */
    @Override
    public void connect(String server, int port) throws IOException {
        super.connect(server, port);
        bye = null;
        load = null;
        numberOfCommands = 0;
    }

    /**
     * Disconnect the client from the server and close the connexion. Before closing the
     * connection we check if the server has successfully closed the connection
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        // send "BYE" message
        sendToServer(RouletteV2Protocol.CMD_BYE);

        // we check if the server has closed the connection
        answer = readFromServer();
        bye = JsonObjectMapper.parseJson(answer, ByeCommandReponse.class);
        if(!bye.getStatus().equals("success")){
            LOG.log(Level.SEVERE, "ClientV2 : BYE failure");
        }
        close();

        numberOfCommands++;
    }

    /**
     * Transmit the name of a student to the server, we have to check if the server has
     * successfully loaded the students
     * @param fullname the student's full name
     * @throws IOException
     */
    @Override
    public void loadStudent(String fullname) throws IOException {
        super.loadStudent(fullname);

        load = JsonObjectMapper.parseJson(answer, LoadCommandReponse.class);
        if(!checkSuccessOfCommand()){
            LOG.log(Level.SEVERE, "ClientV2 : LOAD failure");
        }

        numberOfCommands++;
    }

    /**
     * Transmit a list of student's name to the server. We have to check id the server has
     * successfully loaded the students list
     * @param students list of student's name
     * @throws IOException
     */
    @Override
    public void loadStudents(List<Student> students) throws IOException {
        super.loadStudents(students);

        load = JsonObjectMapper.parseJson(answer, LoadCommandReponse.class);
        if(!load.getStatus().equals("success")){
            LOG.log(Level.SEVERE, "ClientV2 : LOAD failure");
        }

        numberOfCommands++;
    }

    /**
     * * Ask the server to give a random student previously loaded
     * @return a Student randomly selected
     * @throws EmptyStoreException
     * @throws IOException
     */
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        numberOfCommands++;

        return super.pickRandomStudent();
    }

    /**
     * Ask the server how many students it has stored
     * @return the number of stored students by the server
     * @throws IOException
     */
    @Override
    public int getNumberOfStudents() throws IOException {
        numberOfCommands++;

        return super.getNumberOfStudents();
    }

    /**
     * Ask for the version of the server's protocole
     * @return server's running protocol version
     * @throws IOException
     */
    @Override
    public String getProtocolVersion() throws IOException {
        // "INFO"
        sendToServer(RouletteV2Protocol.CMD_INFO);
        answer = readFromServer();
        InfoCommandResponse response = JsonObjectMapper.parseJson(answer, InfoCommandResponse.class);

        numberOfCommands++;

        return response.getProtocolVersion();
        // TODO
    }

    /**
     * Empty server's student list
     * @throws IOException
     */
    @Override
    public void clearDataStore() throws IOException {
        // send "CLEAR" message
        sendToServer(RouletteV2Protocol.CMD_CLEAR);

        answer = readFromServer();

        numberOfCommands++;

    }

    /**
     * Ask for the list of every student stored in the server
     * @return List of student stored
     * @throws IOException
     */
    @Override
    public List<Student> listStudents() throws IOException {
        // send "LIST" message
        sendToServer(RouletteV2Protocol.CMD_LIST);
        answer = readFromServer();
        StudentsList reponse = JsonObjectMapper.parseJson(answer, StudentsList.class);

        numberOfCommands++;

        return reponse.getStudents();
    }

    /**
     * get the number of students added during this session
     * @return number of students added during this session
     */
    @Override
    public int getNumberOfStudentAdded() {
        if (load != null) {
            return load.getNumberOfNewStudents();
        } else {
            return 0;
        }

    }

    /**
     * get the number of command sent to the server during this session
     * @return number of command sent to the server during this session
     */
    @Override
    public int getNumberOfCommands() {
        if (bye != null) {
            return bye.getNumberOfCommands();
        } else {
            return numberOfCommands;
        }
    }

    /**
     * Check if the LOAD command has been successfully processed by the server
     * @return true if the command has been successfully processed
     */
    @Override
    public boolean checkSuccessOfCommand() {
        return load.getStatus().equals("success");
    }

}
