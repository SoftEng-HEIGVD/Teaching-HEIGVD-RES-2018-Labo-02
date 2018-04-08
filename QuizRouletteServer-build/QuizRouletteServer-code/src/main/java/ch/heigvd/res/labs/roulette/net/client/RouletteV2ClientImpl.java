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
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.util.List;
import java.util.logging.Level;
import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Youndzo Francine
 * @author Lemdjo Marie
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
 
     private int numberOfCommands = 0;
    private boolean commandStatus = false;
    private int numberOfStudentsAdded = 0;
   
    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());


    @Override
    public void clearDataStore() throws IOException {
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();
        reader.readLine();
        ++numberOfCommands;
    }

    @Override
    public List<Student> listStudents() throws IOException {
        writer.println(RouletteV2Protocol.CMD_LIST);
        writer.flush();
        StudentsList studentList = JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class);
        ++numberOfCommands;
        return studentList.getStudents();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        writer.println(RouletteV2Protocol.CMD_LOAD);
        writer.flush();
        reader.readLine();
        writer.println(fullname);
        writer.flush();
        writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        LoadCommandResponse cmdResponse = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
        commandStatus = cmdResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
        numberOfStudentsAdded = cmdResponse.getNumberOfNewStudents();
        ++numberOfCommands;
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        try {
            writer.println(RouletteV2Protocol.CMD_LOAD);
            writer.flush();
            reader.readLine();

            for (Student student : students) {
                writer.println(student.getFullname());
                writer.flush();
            }

            writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            writer.flush();

            LoadCommandResponse cmdResponse = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
            commandStatus = cmdResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
            numberOfStudentsAdded = cmdResponse.getNumberOfNewStudents();
            ++numberOfCommands;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occured during loadStudents : {0}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void disconnect() throws IOException {
        try {
            writer.println(RouletteV2Protocol.CMD_BYE);
            writer.flush();
            ByeCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), ByeCommandResponse.class);
            commandStatus = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
            sessionClean();
            ++numberOfCommands;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occured during connection to socket : {0}", e.getMessage());
            throw e;
        }
    }

  
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        ++numberOfCommands;
        return super.pickRandomStudent();
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        ++numberOfCommands;
        return super.getNumberOfStudents();
    }
    
    
    @Override
    public int getNumberOfStudentAdded() {
        return numberOfStudentsAdded;
    }

    @Override
    public int getNumberOfCommands() {
        return numberOfCommands;
    }
    
        @Override
    public String getProtocolVersion() throws IOException {
        ++numberOfCommands;
        return super.getProtocolVersion();
    }

    @Override
    public boolean checkSuccessOfCommand() {
        return commandStatus;
    }

}
