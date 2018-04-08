package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti, Samuel Mayor, Alexandra Korukova
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private int nbStudentsLoaded;
    private boolean loadSuccess;
    private int nbOfCommands;

    public void disconnect() throws IOException {
        nbOfCommands++;
        os.println(RouletteV2Protocol.CMD_BYE);
        os.flush();
        is.readLine();
        super.disconnect();
    }

    @Override
    public void clearDataStore() throws IOException {
        nbOfCommands++;
        os.println(RouletteV2Protocol.CMD_CLEAR);
        os.flush();
        is.readLine(); // server should respond with the "clear done" message
    }

    @Override
    public List<Student> listStudents() throws IOException {
        nbOfCommands++;
        os.println(RouletteV2Protocol.CMD_LIST);
        os.flush();
        String jsonStudentList = is.readLine();
        jsonStudentList = jsonStudentList.substring(jsonStudentList.indexOf("["), jsonStudentList.indexOf("]")+1);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Student> studentList = objectMapper.readValue(jsonStudentList, new TypeReference<List<Student>>(){});
        return studentList;
    }

    public String getProtocolVersion() throws IOException {
        nbOfCommands++;
        os.println(RouletteV2Protocol.CMD_INFO);
        os.flush();
        String json = is.readLine();
        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(json, InfoCommandResponse.class);
        return infoCommandResponse.getProtocolVersion();
    }

    public void loadStudent(String fullname) throws IOException {
        nbOfCommands++;
        String response;
        os.println(RouletteV2Protocol.CMD_LOAD);
        os.flush();
        if((response = is.readLine()).equals(RouletteV2Protocol.RESPONSE_LOAD_START)) {
            nbStudentsLoaded = 0;
            os.println(fullname);
            os.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            nbStudentsLoaded++;
            os.flush();
            response = is.readLine();
            LoadCommandResponse lcResponse = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
            if(lcResponse.getStatus().equals("success")) {
                loadSuccess = true;
            }
        }
    }

    public void loadStudents(List<Student> students) throws IOException {
        nbOfCommands++;
        loadSuccess = false;
        String response;
        os.println(RouletteV2Protocol.CMD_LOAD);
        os.flush();
        response = is.readLine();
        if((response.equals(RouletteV2Protocol.RESPONSE_LOAD_START))) {
            nbStudentsLoaded = 0;
            for (Student s : students) {
                os.println(s.getFullname());
                nbStudentsLoaded++;
            }
            os.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            os.flush();
            response = is.readLine();
            LoadCommandResponse lcResponse = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
            if(lcResponse.getStatus().equals("success")) {
                loadSuccess = true;
            }
        }
    }

    @Override
    public int getNumberOfCommands() throws IOException {
        return nbOfCommands;
    }

    @Override
    public int getNumberOfStudentAdded() {
        nbOfCommands++;
        return nbStudentsLoaded;
    }

    @Override
    public boolean checkSuccessOfCommand() {
        nbOfCommands++;
        return loadSuccess;
    }

    public int getNumberOfStudents() throws IOException {
        nbOfCommands++;
        return super.getNumberOfStudents();
    }

}