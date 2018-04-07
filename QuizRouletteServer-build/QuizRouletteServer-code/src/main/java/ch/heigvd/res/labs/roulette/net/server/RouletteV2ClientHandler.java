package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
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

    final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

    private final IStudentsStore store;
    private int nbCommands;
    private int nbStudentsAdded;

    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store = store;
        this.nbCommands = 0;
        this.nbStudentsAdded = 0;
    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

        writer.println("Hello. Online HELP is available. Will you find if?");
        writer.flush();

        String command;
        boolean done = false;
        while (!done && ((command = reader.readLine()) != null)) {
            LOG.log(Level.INFO, "COMMAND: {0}", command);
            ++nbCommands;
            switch (command.toUpperCase()) {
                case RouletteV2Protocol.CMD_RANDOM:
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
                    writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;

                case RouletteV2Protocol.CMD_INFO:
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_LOAD:
                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    boolean isSuccess;
                    int initialNbStudents = store.getNumberOfStudents();
                    try {
                        store.importData(reader);
                        isSuccess = true;
                    }catch(IOException e){
                        isSuccess = false;
                    }
                    int nbNewStudents = store.getNumberOfStudents() - initialNbStudents;
                    nbStudentsAdded += nbNewStudents;
                    LoadCommandResponse ldResponse = new LoadCommandResponse(isSuccess == true ? "success" : "failure", nbNewStudents);
                    writer.println(JsonObjectMapper.toJson(ldResponse));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_BYE:
                    done = true;
                    ByeCommandResponse bResponse = new ByeCommandResponse("success", nbCommands);
                    writer.println(JsonObjectMapper.toJson(bResponse));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_CLEAR:
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LIST:
                    ListCommandResponse ltResponse = new ListCommandResponse((Student[]) store.listStudents().toArray());
                    writer.println(JsonObjectMapper.toJson(ltResponse));
                    writer.flush();
                    break;

                default:
                    writer.println("Huh? please use HELP if you don't know what commands are available.");
                    writer.flush();
                    break;
            }
            writer.flush();
        }
    }

    public int getNumberOfCommands(){
        return nbCommands;
    }

    public int getNumberOfStudentAdded(){
        return nbStudentsAdded;
    }

}