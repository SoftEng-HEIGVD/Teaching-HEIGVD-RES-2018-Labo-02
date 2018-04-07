package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 *
 * @ModifiedBy Daniel Gonzalez Lopez, Héléna Line Reymond
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private int commandCounter = 0;

    private String lastCommand;

    private ByeCommandResponse bcr;
    private LoadCommandResponse lcr;

    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

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

        sendToServer(RouletteV2Protocol.CMD_CLEAR);

        response = in.readLine();

        if (response.equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {

            // All good, we create logs
            LOG.log(Level.INFO, "Response sent by the server:");
            LOG.log(Level.INFO, response);

        } else {

            // We log the problem occurred.
            LOG.log(Level.SEVERE, "Wrong response from server after connection");
            LOG.log(Level.INFO, "Expected: " + RouletteV2Protocol.RESPONSE_CLEAR_DONE);
            LOG.log(Level.INFO, "Got: " + response);
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getNumberOfCommands() {

        int numOfCommands;

        if (lastCommand.equals(RouletteV2Protocol.CMD_BYE)) {
            numOfCommands = bcr.getTotalCommands();
        } else {
            numOfCommands = commandCounter;
        }

        return numOfCommands;
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
