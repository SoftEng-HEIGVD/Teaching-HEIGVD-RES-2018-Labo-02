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
 * modify by : Olivier Kopp
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private int numberOfCommand = 0;
    private LoadCommandResponse loadCommandResponse;
    private ByeCommandResponse byeCommandResponse;
    //store the status of the last command
    private boolean commandOk = false;

    @Override
    public void clearDataStore() throws IOException {
        send(RouletteV2Protocol.CMD_CLEAR);
        br.readLine();
        numberOfCommand++;
    }

    @Override
    public List<Student> listStudents() throws IOException {
        send(RouletteV2Protocol.CMD_LIST);
        numberOfCommand++;
        return JsonObjectMapper.parseJson(br.readLine(), ListCommandResponse.class).getStudents();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        send(RouletteV1Protocol.CMD_LOAD);
        send(fullname);
        br.readLine();
        send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        loadCommandResponse = JsonObjectMapper.parseJson(br.readLine(), LoadCommandResponse.class);
        commandOk = loadCommandResponse.getStatus().equals("success");
        if (commandOk){
            numberOfCommand++;
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        send(RouletteV1Protocol.CMD_LOAD);
        for (Student s : students) {
            send(s.getFullname());
        }
        br.readLine();
        send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        loadCommandResponse = JsonObjectMapper.parseJson(br.readLine(), LoadCommandResponse.class);
        commandOk = loadCommandResponse.getStatus().equals("success");
        if (commandOk){
            numberOfCommand++;
        }
    }

    @Override
    public void disconnect() throws IOException {
        numberOfCommand++;
        if (socket == null) {
            return;
        } else {
            send(RouletteV2Protocol.CMD_BYE);
            byeCommandResponse = JsonObjectMapper.parseJson(br.readLine(), ByeCommandResponse.class);
            commandOk = byeCommandResponse.getStatus().equals("success");
            socket.close();
            socket = null;
            pw.close();
            br.close();
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        numberOfCommand++;
        return super.pickRandomStudent();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        numberOfCommand++;
        return super.getProtocolVersion();
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        numberOfCommand++;
        return super.getNumberOfStudents();
    }

    public int getNumberOfCommands() {
        return numberOfCommand;
    }

    public int getNumberOfStudentAdded() {
        return loadCommandResponse.getNumberOfNewStudents();
    }

    public boolean checkSuccessOfCommand() {
        return commandOk;
    }


}
