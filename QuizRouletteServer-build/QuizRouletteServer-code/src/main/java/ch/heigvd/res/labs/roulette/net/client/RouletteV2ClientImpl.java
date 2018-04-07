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
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private int numberOfCommands = 0;
    private boolean lastCommandSuccess = false;

    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

    @Override
    public void clearDataStore() throws IOException {

        LOG.info("Clearing students datastore");
        numberOfCommands++;
        out.write(RouletteV2Protocol.CMD_CLEAR);
        out.flush();

        in.readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {

        LOG.info("Listing students");
        numberOfCommands++;

        out.write(RouletteV2Protocol.CMD_LIST);
        out.flush();

        return JsonObjectMapper.parseJson(in.readLine(), ListStudentCommandResponse.class).getStudents();
    }

    public int getNumberOfStudentAdded() throws IOException {

        LOG.info("Retrieving number of students added");
        return getServerInfo().getNumberOfStudents();
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

        if(connexion == null)
            return;

        numberOfCommands++;
        out.write(RouletteV2Protocol.CMD_BYE);
        out.flush();
        out.close();


        ByeCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), ByeCommandResponse.class);
        lastCommandSuccess = response.getStatus().equals("success");

        connexion.close();
        in.close();
        out.close();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {

        LOG.info("Loading student " + fullname);
        numberOfCommands++;

        _loadStudent(fullname);
        lastCommandSuccess = JsonObjectMapper.parseJson(in.readLine(), LoadStudentCommandResponse.class)
                .getStatus().equals("success");
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {

        LOG.info("Loading list of students");
        numberOfCommands++;

        _loadStudents(students);
        lastCommandSuccess = JsonObjectMapper.parseJson(in.readLine(), LoadStudentCommandResponse.class)
                .getStatus().equals("success");
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
    protected InfoCommandResponse getServerInfo() throws IOException {

        numberOfCommands++;
        out.println(RouletteV1Protocol.CMD_INFO);
        out.flush();

        return JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);
    }
}
