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
 * @author Olivier Liechti, modified by Lionel Nanchen
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

  private final IStudentsStore store;

  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(os), true);

    printWriter.println("Hello. Online HELP is available. Will you find it?");

    String command;
    boolean done = false;
    int numberOfCommands = 0;

    while (!done && ((command = bufferedReader.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      ++numberOfCommands;

      switch (command.toUpperCase()) {
        case RouletteV2Protocol.CMD_RANDOM:
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          printWriter.println(JsonObjectMapper.toJson(rcResponse));
          break;
        case RouletteV2Protocol.CMD_HELP:
          printWriter.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          printWriter.println(JsonObjectMapper.toJson(response));
          break;
        case RouletteV2Protocol.CMD_LOAD:
          printWriter.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          int oldNumber = store.getNumberOfStudents();
          store.importData(bufferedReader);
          int newNumber = store.getNumberOfStudents();
          printWriter.println(JsonObjectMapper.toJson(new LoadCommandResponse(RouletteV2Protocol.SUCCESS, newNumber - oldNumber)));
          break;
        case RouletteV2Protocol.CMD_BYE:
          done = true;
          printWriter.println(JsonObjectMapper.toJson(new ByeCommandResponse(RouletteV2Protocol.SUCCESS, numberOfCommands)));
          break;
        case RouletteV2Protocol.CMD_LIST:
          ListCommandResponse listResponse = new ListCommandResponse(store.listStudents());
          printWriter.println(JsonObjectMapper.toJson(listResponse));
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          printWriter.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          break;
        default:
          printWriter.println("Huh? please use HELP if you don't know what commands are available.");
          break;
      }
      printWriter.flush();
    }
  }

}
