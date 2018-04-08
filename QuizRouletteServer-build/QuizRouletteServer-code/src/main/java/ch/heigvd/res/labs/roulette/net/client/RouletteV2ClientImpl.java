package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 * @author Adam Zouari
 * @author Nair Alic
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    public final static String RESPONSE_SUCCESS = "success";
    private LoadCommandResponse loadJson;
    private ByeCommandResponse byeJson;
    private ListCommandResponse listStudentsJson;
    private boolean status;
    private int nbOfCommands = 0;

    @Override
    public void clearDataStore() throws IOException {
        sendToServer(RouletteV2Protocol.CMD_CLEAR);
        nbOfCommands++;

        readFromServer();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        sendToServer(RouletteV2Protocol.CMD_LIST);
        nbOfCommands++;

        listStudentsJson = JsonObjectMapper.parseJson(readFromServer(), ListCommandResponse.class);
        return new ArrayList<>(listStudentsJson.getStudents());
    }

    @Override
    public void disconnect() throws IOException {
        nbOfCommands++;
        if (clientSocket != null) {
            sendToServer(RouletteV2Protocol.CMD_BYE);

            byeJson = JsonObjectMapper.parseJson(readFromServer(), ByeCommandResponse.class);

            // free all ressources
            clientSocket.close();
            clientSocket = null;
            in.close();
            out.close();
        }
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        sendToServer(RouletteV2Protocol.CMD_LOAD);
        readFromServer();

        sendToServer(fullname);
        sendToServer(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        loadJson = JsonObjectMapper.parseJson(readFromServer(), LoadCommandResponse.class);
        status = loadJson.getStatus().equals(RESPONSE_SUCCESS);
        if (status) {
            nbOfCommands++;
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        sendToServer(RouletteV2Protocol.CMD_LOAD);
        readFromServer();

        for (Student s : students) {
            sendToServer(s.getFullname());
        }
        sendToServer(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        loadJson = JsonObjectMapper.parseJson(readFromServer(), LoadCommandResponse.class);
        status = loadJson.getStatus().equals(RESPONSE_SUCCESS);
        if (status) {
            nbOfCommands++;
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        nbOfCommands++;
        return super.pickRandomStudent();
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        nbOfCommands++;
        return super.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        nbOfCommands++;
        return super.getProtocolVersion();
    }

    @Override
    public int getNumberOfStudentAdded() {
        return loadJson.getNumberOfNewStudents();
    }

    @Override
    public int getNumberOfCommands() {
        return nbOfCommands;
    }

    @Override
    public boolean checkSuccessOfCommand() {
        return status;
    }
}
