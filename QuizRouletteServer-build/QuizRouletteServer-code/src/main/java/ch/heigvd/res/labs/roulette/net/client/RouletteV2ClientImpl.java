package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import ch.heigvd.res.labs.roulette.net.server.LoadCommandResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private int numberOfStudentAdded = 0;
    private int nbOfCommandSuccess = 0;
    private boolean commandSucced = false;

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    @Override
    public void loadStudent(String fullname) throws IOException {
        // send command to server 
        out.println(RouletteV2Protocol.CMD_LOAD);
        out.println(fullname);

        out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();
        
        //READ DATA
        in.readLine();

        try {
            LoadCommandResponse serverResponse = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
            commandSucced = serverResponse.getStatus().equals("success");
            if (commandSucced) {
                numberOfStudentAdded = 1;
                nbOfCommandSuccess++;
            }

        } catch (IOException ex) {
            ex.getStackTrace();
            commandSucced = false;
        }

    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {

        // send commands command to server 
        out.println(RouletteV2Protocol.CMD_LOAD);

        for (Student student : students) {
            out.println(student.getFullname());
        }

        // ENDOFDATA
        out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();

        //READ DATA
        in.readLine();

        try {
            LoadCommandResponse serverResponse = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
            commandSucced = serverResponse.getStatus().equals("success");

            if (commandSucced) {
                numberOfStudentAdded = serverResponse.getNumberOfNewStudents(); 
                nbOfCommandSuccess++;                                   
            }

        } catch (IOException ex) {
            commandSucced = false;
            ex.getStackTrace();

        }

    }

    @Override
    public void clearDataStore() throws IOException {

        // send command to server
        out.println(RouletteV2Protocol.CMD_CLEAR);
        out.flush();

        if (in.readLine().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
            nbOfCommandSuccess++;
        }

    }

    @Override
    public List<Student> listStudents() throws IOException {

        // send command
        out.println(RouletteV2Protocol.CMD_LIST);
        out.flush();

        StudentsList studentsList;
        
            // server response (only JSON)
            studentsList = JsonObjectMapper.parseJson(in.readLine(), StudentsList.class);
            commandSucced = true;
            nbOfCommandSuccess++;
            return studentsList.getStudents();
      
    }

    @Override
    public void disconnect() throws IOException {
        super.disconnect();
        nbOfCommandSuccess++;
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        try {
            Student student = super.pickRandomStudent();
            nbOfCommandSuccess++;
            return student;
        } catch (EmptyStoreException ex) {
            nbOfCommandSuccess++;
            throw ex;
        }
    }

    @Override
    public int getNumberOfStudents() throws IOException { 
        nbOfCommandSuccess++;
        return super.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        nbOfCommandSuccess++;
        return super.getProtocolVersion();
    }

    @Override
    public int getNumberOfStudentAdded() {
        return numberOfStudentAdded;
    }

    @Override
    public int getNumberOfCommands() {
        return nbOfCommandSuccess;
    }

    @Override
    public boolean checkSuccessOfCommand() {
        return commandSucced;
    }

}
