package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.client.RouletteV2ClientImpl;
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

    private final static Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

    private int commandPerformed = 0;
    private IStudentsStore store;

    public RouletteV2ClientHandler(IStudentsStore store) {
        this.store  = store;
    }

    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {

        BufferedReader reader   = new BufferedReader(new InputStreamReader(is));
        PrintWriter writer      = new PrintWriter(new OutputStreamWriter(os));

        writer.println("Hello. Online HELP is available. Will you find it?");
        writer.flush();

        String command;

        boolean done = false;

        while (!done && ((command = reader.readLine()) != null)) {

            boolean correctCommand = true;

            LOG.log(Level.INFO, "COMMAND: {0}", command);

            switch (command.toUpperCase()) {

                case RouletteV2Protocol.CMD_CLEAR:

                    store.clear();
                    writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_LIST:

                    writer.println(JsonObjectMapper.toJson(new ListStudentCommandResponse(store.listStudents())));
                    writer.flush();
                    break;

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

                    InfoCommandResponse response = new InfoCommandResponse(RouletteV1Protocol.VERSION,
                            store.getNumberOfStudents());
                    writer.println(JsonObjectMapper.toJson(response));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_LOAD:

                    int previousNumber = store.getNumberOfStudents();
                    writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
                    writer.flush();
                    try {
                        store.importData(reader);
                    } catch (IOException e) {
                        writer.println(JsonObjectMapper.toJson(new LoadStudentCommandResponse("failure", -1)));
                        writer.flush();
                        throw e;
                    }

                    writer.println(JsonObjectMapper.toJson(new LoadStudentCommandResponse("success", store.getNumberOfStudents() - previousNumber)));
                    writer.flush();
                    break;

                case RouletteV2Protocol.CMD_BYE:
                    writer.println(JsonObjectMapper.toJson(new ByeCommandResponse("success", ++commandPerformed)));
                    writer.flush();
                    done = true;
                    break;
                default:
                    correctCommand = false;
                    writer.println("Huh? please use HELP if you don't know what commands are available.");
                    writer.flush();
                    break;
            }

            writer.flush();

            if(correctCommand)
                ++commandPerformed;
        }

    }

}