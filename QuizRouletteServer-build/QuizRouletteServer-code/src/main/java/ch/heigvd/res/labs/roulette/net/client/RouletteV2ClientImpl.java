package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    @Override
    public void disconnect() throws IOException {
        // TODO - like super + check response
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        // TODO - like super + check new response
    }

    @Override
    public void clearDataStore() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Student> listStudents() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getNumberOfCommands() {
        // TODO - Gets number of commands after BYE command
        return 0;
    }

    public int getNumberOfStudentAdded() {
        // TODO - Gets number of new students added
        return 0;
    }

    public boolean checkSuccessOfCommand() {
        // TODO - Gets the success of the command
        return true;
    }
}
