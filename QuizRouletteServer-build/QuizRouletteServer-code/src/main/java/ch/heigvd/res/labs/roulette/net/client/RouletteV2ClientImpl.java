package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private int numberOfCommands = 0;
    private int numberOfStudentAdded = 0;
    private boolean commandSuccess = false;

    @Override
    public void clearDataStore() throws IOException {
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();
        reader.readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        writer.println(RouletteV2Protocol.CMD_LIST);
        numberOfCommands++;
        writer.flush();

        ListCommandResponse listCommandResponse = JsonObjectMapper.parseJson(reader.readLine(), ListCommandResponse.class);
        return listCommandResponse.getStudents();
    }

    @Override
    public int getNumberOfCommands() throws IOException {
        return numberOfCommands;
    }

    @Override
    public int getNumberOfStudentAdded() throws IOException {
        return numberOfStudentAdded;
    }

    @Override
    public boolean checkSuccessOfCommand() throws IOException {
        return false;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        super.loadStudent(fullname);

        LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
        commandSuccess = loadCommandResponse.getStatus().equals("success");

        if(commandSuccess) {
            numberOfCommands++;
            numberOfStudentAdded = loadCommandResponse.getNumberOfStudentAdded();
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        super.loadStudents(students);

        LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
        commandSuccess = loadCommandResponse.getStatus().equals("success");

        if(commandSuccess) {
            numberOfCommands++;
            numberOfStudentAdded = loadCommandResponse.getNumberOfStudentAdded();
        }
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
        writer.println(RouletteV2Protocol.CMD_INFO);
        numberOfCommands++;
        writer.flush();

        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

        return infoCommandResponse.getProtocolVersion();
    }

    @Override
    public void disconnect() throws IOException {

        writer.println(RouletteV2Protocol.CMD_BYE);
        writer.flush();

        ByeCommandResponse byeCommandResponse = JsonObjectMapper.parseJson(reader.readLine(), ByeCommandResponse.class);
        commandSuccess = byeCommandResponse.getStatus().equals("success");

        if(clientSocket != null) {
            clientSocket.close();
        }

        if(writer != null) {
            writer.close();
        }

        if(reader != null) {
            reader.close();
        }

        if(commandSuccess) {
            numberOfCommands++;
        }
    }
}
