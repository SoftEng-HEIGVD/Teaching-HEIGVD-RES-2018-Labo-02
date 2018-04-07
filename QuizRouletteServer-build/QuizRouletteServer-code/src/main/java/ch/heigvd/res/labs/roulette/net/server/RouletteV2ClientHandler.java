package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 *
 * modifiedBy: Daniel Gonzalez Lopez, Héléna Line Reymond
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
        int numberOfCommands = 0;   // Number of commands called by the client
        boolean done = false;

        while (!done && ((command = reader.readLine()) != null)) {
            LOG.log(Level.INFO, "COMMAND: {0}", command);
            switch (command.toUpperCase()) {
                case RouletteV2Protocol.CMD_RANDOM:

                    // One command is called
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
                case RouletteV2Protocol.CMD_HELP:

                    // One command is called
                    ++numberOfCommands;

                    writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;
                case RouletteV2Protocol.CMD_INFO:

                    // One command is called
                    ++numberOfCommands;

                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LOAD:

                    // One command is called
                    ++numberOfCommands;

                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();

                    // Check the state of the command
                    String state;
                    try {
                        store.importData(reader);
                        state = "success";
                    }
                    catch(IOException e){
                        state = "fail";
                    }

                    // Return the state of the command and the number of students added
                    LoadCommandResponse loadResponse = new LoadCommandResponse(state, store.getNumberOfStudentAdded());
                    writer.println(JsonObjectMapper.toJson(loadResponse));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LIST:

                    // One command is called
                    ++numberOfCommands;

                    // Get the list of students
                    StudentsList sl = new StudentsList();
                    sl.setStudents(store.listStudents());

                    writer.println(JsonObjectMapper.toJson(sl));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_CLEAR:

                    // One command is called
                    ++numberOfCommands;

                    // Clear the list of students
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_BYE:

                    // One command is called
                    ++numberOfCommands;

                    // Return the state of the command and the number of commands called by the client
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
