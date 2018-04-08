package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import ch.heigvd.res.labs.roulette.net.server.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.server.LoadCommandResponse;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    
    private ByeCommandResponse bcResponse;
    private LoadCommandResponse lcResponse;
    private int numberOfCommand = 0;
    
    private boolean exactCommand = false;

    @Override
    public void clearDataStore() throws IOException {
        out.println(RouletteV2Protocol.CMD_CLEAR);
        in.readLine();
        numberOfCommand++;
    }

    @Override
    public List<Student> listStudents() throws IOException {
        out.println(RouletteV2Protocol.CMD_LIST);
        numberOfCommand++;
        return JsonObjectMapper.parseJson(in.readLine(), ListCommandResponse.class).getStudents();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        out.println(RouletteV1Protocol.CMD_LOAD);
        out.println(fullname);
        in.readLine();
        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        lcResponse = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
        exactCommand = lcResponse.getStatus().equals("success");
        if (exactCommand) {
            numberOfCommand++;
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        out.println(RouletteV1Protocol.CMD_LOAD);
        for (Student s : students) {
            out.println(s.getFullname());
        }
        in.readLine();
        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        lcResponse = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
        exactCommand = lcResponse.getStatus().equals("success");
        if (exactCommand) {
            numberOfCommand++;
        }
    }

    @Override
    public void disconnect() throws IOException {
        numberOfCommand++;
        if (clientSocket == null) {
        } else {
            out.println(RouletteV2Protocol.CMD_BYE);
            bcResponse = JsonObjectMapper.parseJson(in.readLine(), ByeCommandResponse.class);
            exactCommand = bcResponse.getStatus().equals("success");
            clientSocket.close();
            clientSocket = null;
            out.close();
            in.close();
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

    @Override
    public int getNumberOfCommands() {
        return numberOfCommand;
    }

    @Override
    public int getNumberOfStudentAdded() {
        return lcResponse.getNumberOfAddStudents();
    }

    @Override
    public boolean checkSuccessOfCommand() {
        return exactCommand;
    }
}
