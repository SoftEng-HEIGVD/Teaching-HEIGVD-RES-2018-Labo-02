package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti, modified by Christophe Joyet and Lionel Nanchen
 */
public class RouletteV2ClientHandler implements IClientHandler {

    final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

    private final IStudentsStore store;

    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store = store; //To change body of gen
    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(os), true);

        printWriter.println("Hello. Online HELP is available. Will you find it?");

        String line;
        int numberOfCommand = 0;
        boolean done        = false;

        while (!done && ((line = bufferedReader.readLine()) != null)) {
            LOG.log(Level.INFO, "COMMAND: {0}", line);
            ++numberOfCommand;
            switch (line.toUpperCase()) {

                case RouletteV2Protocol.CMD_RANDOM:
                    RandomCommandResponse randomResponse = new RandomCommandResponse();
                    try {
                        randomResponse.setFullname(store.pickRandomStudent().getFullname());
                    } catch (EmptyStoreException ex) {
                        randomResponse.setError("There is no student, you cannot pick a random one");
                    }
                    printWriter.println(JsonObjectMapper.toJson(randomResponse));
                    break;

                case RouletteV2Protocol.CMD_HELP:
                    printWriter.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;

                case RouletteV2Protocol.CMD_INFO:
                    InfoCommandResponse infoResponse = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    printWriter.println(JsonObjectMapper.toJson(infoResponse));
                    break;

                case RouletteV2Protocol.CMD_LOAD:
                    printWriter.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    int number = store.getNumberOfStudents();
                    store.importData(bufferedReader);
                    number = store.getNumberOfStudents() - number;
                    printWriter.println(JsonObjectMapper.toJson(new LoadCommandResponse(RouletteV2Protocol.SUCCESS, number)));
                    break;

                case RouletteV2Protocol.CMD_BYE:
                    done = true;
                    printWriter.println(JsonObjectMapper.toJson(new ByeCommandResponse(RouletteV2Protocol.SUCCESS, numberOfCommand)));
                    break;

                case RouletteV2Protocol.CMD_CLEAR:
                    store.clear();
                    printWriter.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    break;

                case RouletteV2Protocol.CMD_LIST:
                    ListCommandResponse list = new ListCommandResponse(store.listStudents());
                    printWriter.println(JsonObjectMapper.toJson(list));
                    break;

                default:
                    printWriter.println("Huh? please use HELP if you don't know what commands are available.");
                    break;
            }
        }
    }

}
