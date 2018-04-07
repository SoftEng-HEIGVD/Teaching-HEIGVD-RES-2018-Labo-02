package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti and Guillaume Hochet
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private int lastStudentsAdded = 0;
    private int numberOfCommands = 0;
    private boolean lastCommandSuccess = false;

    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

    @Override
    public void clearDataStore() throws IOException {

        LOG.info("Clearing students datastore");
        numberOfCommands++;
        out.println(RouletteV2Protocol.CMD_CLEAR);
        out.flush();

        in.readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {

        LOG.info("Listing students");
        numberOfCommands++;

        out.println(RouletteV2Protocol.CMD_LIST);
        out.flush();

        return JsonObjectMapper.parseJson(in.readLine(), ListStudentCommandResponse.class).getStudents();
    }

    public int getNumberOfStudentAdded() {

        LOG.info("Retrieving number of students added");
        return lastStudentsAdded;
    }

    public int getNumberOfCommands() {

        return numberOfCommands;
    }

    @Override
    public boolean checkSuccessOfCommand() {

        return lastCommandSuccess;
    }

    public void disconnect() throws IOException {

        LOG.info("Attempting to disconnect");
        numberOfCommands++;

        if(connexion == null)
            return;

        out.println(RouletteV2Protocol.CMD_BYE);
        out.flush();

        ByeCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), ByeCommandResponse.class);
        lastCommandSuccess = response.getStatus().equals("success");

        connexion.close();
        in.close();
        out.close();

        connexion = null;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {

        LOG.info("Loading student " + fullname);
        numberOfCommands++;

        _loadStudent(fullname);
        LoadStudentCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), LoadStudentCommandResponse.class);
        lastCommandSuccess = response.getStatus().equals("success");

        if(lastCommandSuccess)
            lastStudentsAdded = response.getNumberOfNewStudents();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {

        LOG.info("Loading list of students");
        numberOfCommands++;

        _loadStudents(students);
        LoadStudentCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), LoadStudentCommandResponse.class);
        lastCommandSuccess = response.getStatus().equals("success");

        if(lastCommandSuccess)
            lastStudentsAdded = response.getNumberOfNewStudents();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {

        numberOfCommands++;
        return super.pickRandomStudent();
    }

    @Override
    public int getNumberOfStudents() throws IOException {

        return super.getNumberOfStudents();
    }

    @Override
    protected InfoCommandResponse getServerInfo() throws IOException {

        numberOfCommands++;
        out.println(RouletteV1Protocol.CMD_INFO);
        out.flush();

        return JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);
    }
}
