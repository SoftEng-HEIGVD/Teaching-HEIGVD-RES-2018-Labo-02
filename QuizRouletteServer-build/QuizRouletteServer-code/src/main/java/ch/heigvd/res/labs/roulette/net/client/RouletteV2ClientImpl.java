package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Mentor Reka
 * @author Kamil Amrani
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());


    private int nbStudentsAdded = 0;
    private int nbCommands      = 0;
    private boolean cmdSuccess  = false;

    @Override
    public void clearDataStore() throws IOException {
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();
        reader.readLine();
        ++nbCommands;
    }

    @Override
    public List<Student> listStudents() throws IOException {
        writer.println(RouletteV2Protocol.CMD_LIST);
        writer.flush();
        StudentsList studentList = JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class);
        ++nbCommands;
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
        cmdSuccess = cmdResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
        nbStudentsAdded = cmdResponse.getNumberOfNewStudents();
        ++nbCommands;
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
            cmdSuccess = cmdResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
            nbStudentsAdded = cmdResponse.getNumberOfNewStudents();
            ++nbCommands;
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
            cmdSuccess = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
            cleanSession();
            ++nbCommands;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occured during connection to socket : {0}", e.getMessage());
            throw e;
        }
    }

    // THIS PART IS FOR REDEFINITION of V1, we need it because we have to count the commands.
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

    @Override
    public int getNumberOfStudentAdded() {
        return nbStudentsAdded;
    }

    @Override
    public int getNumberOfCommands() {
        return nbCommands;
    }

    @Override
    public boolean checkSuccessOfCommand() {
        return cmdSuccess;
    }

}
