package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponseV2;
import ch.heigvd.res.labs.roulette.net.protocol.InfoNewStudent;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    String statusComand;
    @Override
    public void clearDataStore() throws IOException {
        numberOfCommands++;
        LOG.info("Clearing data from store");
        write(RouletteV2Protocol.CMD_CLEAR);
        buffReader.readLine();
        write(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
        buffReader.readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        numberOfCommands++;
        LOG.info("listing students...");
        write(RouletteV2Protocol.CMD_LIST);
        ListCommandResponse listCommandResponse = JsonObjectMapper.parseJson(buffReader.readLine(), ListCommandResponse.class);
        return listCommandResponse.getStudents();
    }
    @Override
    public void disconnect() throws IOException {
        if (socket != null) {
            LOG.info("disconnecting...");
            write(RouletteV2Protocol.CMD_BYE);
            numberOfCommands++;
          
            InfoCommandResponseV2 response = JsonObjectMapper.parseJson(buffReader.readLine(), InfoCommandResponseV2 .class);
        statusComand = response.getStatus();
        numberOfCommands = response.getNumberOfCommands();
          buffReader.close();
            printWriter.close();
            socket.close();
        } else {
            LOG.info("already disconnected...");
        }
    }

}
