package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandReponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandReponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandReponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    ByeCommandReponse bye;
    LoadCommandReponse load;

    @Override
    public void connect(String server, int port) throws IOException {
        super.connect(server, port);

        bye = null;
        load = null;
    }

    @Override
    public void disconnect() throws IOException {
        // send "BYE" message
        sendToServer(RouletteV2Protocol.CMD_BYE);
        String answer = readFromServer();
        bye = JsonObjectMapper.parseJson(answer, ByeCommandReponse.class);
        // closing the connection
        sock.close();
        //TODO
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        // initilize the loading
        sendToServer(RouletteV1Protocol.CMD_LOAD);
        // empty the buffer and check if we received the message
        String s = readFromServer();
        if (!s.equals("")) {
            // send the "fullname" message to the server
            sendToServer(fullname);
        }
        // "ENDOFDATA
        sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        String answer = readFromServer();
        load = JsonObjectMapper.parseJson(answer, LoadCommandReponse.class);
        //TODO
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        //LOAD
        sendToServer(RouletteV1Protocol.CMD_LOAD);

        // empty the buffer and check if we received the message
        String s = readFromServer();
        if (!s.equals("")) {
            for (Student student : students) {
                sendToServer(student.getFullname());
            }
        }
        //ENDOFDATA
        sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        String answer = readFromServer();
        load = JsonObjectMapper.parseJson(answer, LoadCommandReponse.class);
        //TODO
    }

    @Override
    public String getProtocolVersion() throws IOException {
        // "INFO"
        sendToServer(RouletteV2Protocol.CMD_INFO);
        String answer = readFromServer();
        InfoCommandResponse response = JsonObjectMapper.parseJson(answer, InfoCommandResponse.class);
        return response.getProtocolVersion();
        // TODO
    }

    @Override
    public void clearDataStore() throws IOException {
        // send "CLEAR" message
        sendToServer(RouletteV2Protocol.CMD_CLEAR);
        //TODO

    }

    @Override
    public List<Student> listStudents() throws IOException {
        // send "LIST" message
        sendToServer(RouletteV2Protocol.CMD_LIST);
        String answer = readFromServer();
        ListCommandReponse reponse = JsonObjectMapper.parseJson(answer, ListCommandReponse.class);
        return new ArrayList<Student>(Arrays.asList(reponse.getStudents()));
        //TODO
    }

    public int getNumberOfAddedNewStudent() {
        if (load != null) {
            return load.getNumberOfNewStudents();
        } else {
            return 0;
        }

    }

    public int getNumberOfCommands() {
        if (bye != null) {
            return bye.getNumberOfCommands();
        } else {
            return 0;
        }
    }

}
