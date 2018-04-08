package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import java.util.List;
import java.util.logging.Level;
import ch.heigvd.res.labs.roulette.data.EmptyStoreException;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    
    //private int numberOfStudentAdded=0;
    //private int numberOfcommands=0;
    //private boolean commandStatut=false;

    private ByeCommandResponse bye;
    private LoadCommandResponse load;
    private int numberOfCommands;

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
        send(RouletteV2Protocol.CMD_BYE);

        // we check if the server has closed the connection
        //answer = readline();
        bye = JsonObjectMapper.parseJson(is.readLine(), ByeCommandResponse.class);
        if(!bye.getStatus().equals("success")){
            LOG.log(Level.SEVERE, "ClientV2 : BYE failure");
        }
        client.close();

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

        load = JsonObjectMapper.parseJson(is.readLine(), LoadCommandResponse.class);
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

        load = JsonObjectMapper.parseJson(is.readLine(), LoadCommandResponse.class);
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
        send(RouletteV2Protocol.CMD_INFO);
        //answer = readline();
        InfoCommandResponse response = JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class);

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
        send(RouletteV2Protocol.CMD_CLEAR);

        //answer = is.readLine();

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
        send(RouletteV2Protocol.CMD_LIST);
        //answer = readline();
        StudentsList reponse = JsonObjectMapper.parseJson(is.readLine(), StudentsList.class);

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
            return load.getNumberOfStudents();
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
            return bye.getnumberOfCommands();
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
    
    
     
    private void send(String msg) throws IOException{
        os.write(msg);
        os.newLine();
        os.flush();

    }
  
    protected String readline() throws IOException {
        String line = "";
        do {
            line = is.readLine();
        } while ( line.equalsIgnoreCase(MSG_HELLO));
        return line;
    }

}

    
    
    
   
