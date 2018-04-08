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
 * @author Youndzo Francine
 * @author Lemdjo Marie
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
 
    private int numberOfStudentAdded=0;
    private int numberOfCommands=0;
    private boolean commandStatus=false;
    
    
    @Override
 public void clearDataStore() throws IOException {
        os.write(RouletteV2Protocol.CMD_CLEAR);
        os.flush();
        if (!readline().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE))
            LOG.log(Level.SEVERE, "remote server did not reply {0}", RouletteV2Protocol.RESPONSE_CLEAR_DONE);
        
        numberOfCommands++;
        
    }

    @Override
    public List<Student> listStudents() throws IOException {
        os.write(RouletteV2Protocol.CMD_LIST);
        os.flush();
        numberOfCommands++;
        return JsonObjectMapper.parseJson(readline(), StudentsList.class).getStudents();
        
    }
    
    @Override
    public void loadStudent(String fullname) throws IOException {
        os.write(RouletteV2Protocol.CMD_LOAD);
        os.flush();
        is.readLine();
        os.write(fullname);
        os.flush();
        os.write(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
        LoadCommandResponse cmdResponse = JsonObjectMapper.parseJson(is.readLine(), LoadCommandResponse.class);
        commandStatus = cmdResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
        numberOfStudentAdded = cmdResponse.getNumberOfStudents();
        ++numberOfCommands;
    }
    
     @Override
    public void loadStudents(List<Student> students) throws IOException {
        try {
            os.write(RouletteV2Protocol.CMD_LOAD);
            os.flush();
            is.readLine();

            for (Student student : students) {
                os.write(student.getFullname());
                os.flush();
            }

            os.write(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            os.flush();

            LoadCommandResponse cmdResponse = JsonObjectMapper.parseJson(is.readLine(), LoadCommandResponse.class);
            commandStatus = cmdResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
            numberOfStudentAdded = cmdResponse.getNumberOfStudents();
            ++numberOfCommands;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occured during loadStudents : {0}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void disconnect() throws IOException {
        try {
            os.write(RouletteV2Protocol.CMD_BYE);
            os.flush();
            ByeCommandResponse response = JsonObjectMapper.parseJson(is.readLine(), ByeCommandResponse.class);
            commandStatus = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
            sessionCleaner();
            ++numberOfCommands;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occured during connection to socket : {0}", e.getMessage());
            throw e;
        }
    }

    //  we need this part to count the commands.
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
    public String getProtocolVersion() throws IOException {
        ++numberOfCommands;
        return super.getProtocolVersion();
    }

    @Override
    public int getNumberOfStudentAdded() {
        return numberOfStudentAdded;
    }

    @Override
    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    @Override
    public boolean checkSuccessOfCommand() {
        return commandStatus;
    }

}
