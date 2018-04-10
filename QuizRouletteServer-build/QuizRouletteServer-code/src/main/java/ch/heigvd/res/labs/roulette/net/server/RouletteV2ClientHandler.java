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
        boolean done = false;

        int commandCounter = 0;
        while (!done && ((command = reader.readLine()) != null)) {

            commandCounter++;

            LOG.log(Level.INFO, "COMMAND: {0}", command);
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
                    writer.println("Commands: " + Arrays.toString
        (RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;
                    
                case RouletteV2Protocol.CMD_INFO:
                    InfoCommandResponse response = new InfoCommandResponse
        (RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;
                case RouletteV2Protocol.CMD_LOAD:

                    writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    LoadCommandResponseV2 loadCommandResponseV2; //ACK 
                    synchronized (this) { // synchronization to give the resoponse that the client add student 
                        int previousNomberOfStudents = store.getNumberOfStudents();

                        store.importData(reader);

                        loadCommandResponseV2 = new LoadCommandResponseV2();
                        loadCommandResponseV2.setStatus("success");
                        // returns the old number of students minus the new number
                        loadCommandResponseV2.setnumberOfAddStudents
        (store.getNumberOfStudents() - previousNomberOfStudents);
                    }
                    writer.println(JsonObjectMapper.toJson(loadCommandResponseV2));
                    writer.flush();
                    break;
                    
                case RouletteV2Protocol.CMD_BYE:
                    done = true;
                    ByeCommandResponseV2 byeCommandResponseV2 = new ByeCommandResponseV2();
                    byeCommandResponseV2.setStatus("success");
                    byeCommandResponseV2.setnumberOfCommands(commandCounter);
                    writer.println(JsonObjectMapper.toJson(byeCommandResponseV2));
                    writer.flush();
                    break;
                    
                case RouletteV2Protocol.CMD_CLEAR:
                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;
                    
                case RouletteV2Protocol.CMD_LIST:
                    StudentsList sl = new StudentsList();
                    sl.setStudents(store.listStudents());
                    writer.println(JsonObjectMapper.toJson(sl));
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
}
