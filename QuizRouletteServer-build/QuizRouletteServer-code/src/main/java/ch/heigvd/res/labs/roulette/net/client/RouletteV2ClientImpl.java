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

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private ByeCommandReponse bye;
    private LoadCommandReponse load;
    private int numberOfCommands;

    @Override
    public void connect(String server, int port) throws IOException {
        super.connect(server, port);
        bye = null;
        load = null;
        numberOfCommands = 0;
    }

    @Override
    public void disconnect() throws IOException {
        // send "BYE" message
        sendToServer(RouletteV2Protocol.CMD_BYE);
        answer = readFromServer();
        bye = JsonObjectMapper.parseJson(answer, ByeCommandReponse.class);
        close();
        
        numberOfCommands++;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        super.loadStudent(fullname);
        
        load = JsonObjectMapper.parseJson(answer, LoadCommandReponse.class);
        
        numberOfCommands++;
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        super.loadStudents(students);
        load = JsonObjectMapper.parseJson(answer, LoadCommandReponse.class);
        
        numberOfCommands++;
    }
    
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        numberOfCommands++;
        
        return super.pickRandomStudent();
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        numberOfCommands++;
        
        return super.getNumberOfStudents();
    }

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

    @Override
    public void clearDataStore() throws IOException {
        // send "CLEAR" message
        sendToServer(RouletteV2Protocol.CMD_CLEAR);

        answer = readFromServer();
        
        numberOfCommands++;

    }

    @Override
    public List<Student> listStudents() throws IOException {
        // send "LIST" message
        sendToServer(RouletteV2Protocol.CMD_LIST);
        answer = readFromServer();
        StudentsList reponse = JsonObjectMapper.parseJson(answer, StudentsList.class);
        
        numberOfCommands++;
        
        return reponse.getStudents();
    }

    @Override
    public int getNumberOfStudentAdded() {
        if (load != null) {
            return load.getNumberOfNewStudents();
        } else {
            return 0;
        }

    }

    @Override
    public int getNumberOfCommands() {
        /*if (bye != null) {
            return bye.getNumberOfCommands();
        } else {
            return 0;
        }*/
        
        return numberOfCommands;
    }

    @Override
    public boolean checkSuccessOfCommand() {
        return load.getStatus().equals("success");
    }

}
