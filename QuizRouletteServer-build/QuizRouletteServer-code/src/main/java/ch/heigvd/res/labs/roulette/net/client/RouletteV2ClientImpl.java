package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    int nbNewStudents = 0;
    int nbCommands = 0;
    boolean commandStatus;

    @Override
    public void clearDataStore() throws IOException {
        sendMessageToServer(RouletteV2Protocol.CMD_CLEAR);
        in.readLine();
        ++nbCommands;
    }

    @Override
    public List<Student> listStudents() throws IOException {
        sendMessageToServer(RouletteV2Protocol.CMD_LIST);

        // We store the server response to get the number of students
        ListCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), ListCommandResponse.class);
        ++nbCommands;
        return response.getStudents();
    }

    @Override
    public void loadStudent(String fullname) throws IOException{
        sendMessageToServer(RouletteV2Protocol.CMD_LOAD);
        ++nbCommands;
        in.readLine();

        sendMessageToServer(fullname);
        sendMessageToServer(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        // We store the server response to get the status and the number of students
        LoadCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
        commandStatus = response.getStatus().equals(RouletteV2Protocol.SUCCESS_RESPONSE);
        nbNewStudents = response.getNumberOfNewStudents();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        sendMessageToServer(RouletteV1Protocol.CMD_LOAD);
        ++nbCommands;
        in.readLine();

        for (Student s : students) {
            sendMessageToServer(s.getFullname());
        }
        sendMessageToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        LoadCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
        commandStatus = response.getStatus().equals(RouletteV2Protocol.SUCCESS_RESPONSE);
        nbNewStudents = response.getNumberOfNewStudents();
    }

    @Override
    public void disconnect() throws IOException {
        if (isConnected == false) {
            return;
        }

        sendMessageToServer(RouletteV1Protocol.CMD_BYE);

        ByeCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), ByeCommandResponse.class);
        commandStatus = response.getStatus().equals(RouletteV2Protocol.SUCCESS_RESPONSE);
        nbCommands = response.getNumberOfCommands();

        // closing the socket and the input/output
        socket.close();
        in.close();
        out.close();
        isConnected = false;
    }


    // We override the V1 methods so that the number of commands sent is updated when they are used
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        ++nbCommands;
        return super.pickRandomStudent();
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        ++nbCommands;
        return super.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        ++nbCommands;
        return super.getProtocolVersion();
    }

    public int getNumberOfCommands(){
        return nbCommands;
    }

    public int getNumberOfStudentAdded(){
        return nbNewStudents;
    }

    public boolean checkSuccessOfCommand(){
        return commandStatus;
    }
}