package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

    final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

    private final IStudentsStore store;

    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store = store;
    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

        writer.println("Hello. Online HELP is available. Will you find it?");
        writer.flush();

        String command;
        int numberOfCommands = 0;
        boolean done = false;

        while (!done && ((command = reader.readLine()) != null)) {
            LOG.log(Level.INFO, "COMMAND: {0}", command);
            switch (command.toUpperCase()) {
                case RouletteV1Protocol.CMD_RANDOM:
                    ++numberOfCommands;
                    RandomCommandResponse rcResponse = new RandomCommandResponse();
                    try {
                        rcResponse.setFullname(store.pickRandomStudent().getFullname());
                    } catch (EmptyStoreException ex) {
                        rcResponse.setError("There is no student, you cannot pick a random one");
                    }
                    writer.println(JsonObjectMapper.toJson(rcResponse));
                    writer.flush();
                    break;
                case RouletteV1Protocol.CMD_HELP:
                    ++numberOfCommands;
                    writer.println("Commands: " + Arrays.toString(RouletteV1Protocol.SUPPORTED_COMMANDS));
                    break;
                case RouletteV1Protocol.CMD_INFO:
                    ++numberOfCommands;
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;
                case RouletteV1Protocol.CMD_LOAD:
                    // TODO - other tests?
                    ++numberOfCommands;
                    writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
                    writer.flush();

                    String state;
                    try {
                        store.importData(reader);
                        state = "success";
                    }
                    catch(IOException e){
                        state = "fail";
                    }

                    writer.println(RouletteV1Protocol.RESPONSE_LOAD_DONE);

                    LoadCommandResponse loadResponse = new LoadCommandResponse(state, store.getNumberOfStudentAdded());
                    writer.println(JsonObjectMapper.toJson(loadResponse));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LIST:
                    // TODO - other tests?
                    ++numberOfCommands;
                    writer.println(JsonObjectMapper.toJson(store.listStudents()));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_CLEAR:
                    // TODO - other tests?
                    ++numberOfCommands;
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;
                case RouletteV1Protocol.CMD_BYE:
                    // TODO - other tests?
                    ++numberOfCommands;
                    ByeCommandResponse byeResponse = new ByeCommandResponse("success", numberOfCommands);
                    writer.println(JsonObjectMapper.toJson(byeResponse));
                    writer.flush();
                    done = true;
                    break;
                default:
                    writer.println("Huh? please use HELP if you don't know what commands are available.");
                    writer.flush();
                    break;
            }
            writer.flush();
        }
    }
}
