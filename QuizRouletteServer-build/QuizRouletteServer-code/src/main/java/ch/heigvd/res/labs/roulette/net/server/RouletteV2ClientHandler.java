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
 * @author modified by Yann Lederrey and Joel Schar
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

  private final IStudentsStore store;

  private int commandsUsedInSession = 0;

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
    while (!done && ((command = reader.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
        case RouletteV2Protocol.CMD_CLEAR:
          commandsUsedInSession++;
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:
          commandsUsedInSession++;
          ListCommandResponse lsResponse = new ListCommandResponse();
          lsResponse.setStudents(store.listStudents());
          writer.println(JsonObjectMapper.toJson(lsResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_RANDOM:
          commandsUsedInSession++;
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
          commandsUsedInSession++;
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          commandsUsedInSession++;
          InfoCommandResponse infoResponse = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(infoResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          commandsUsedInSession++;
          LoadCommandResponse ldResponse = new LoadCommandResponse();
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          writer.flush();
          int nbStudentsBefore = store.getNumberOfStudents();
          store.importData(reader);
          int nbStudentsAfter = store.getNumberOfStudents();
          ldResponse.setNumberOfNewStudents(nbStudentsAfter - nbStudentsBefore);
          ldResponse.setStatus("success");
          writer.println(JsonObjectMapper.toJson(ldResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          commandsUsedInSession++;
          ByeCommandResponse byeResponse = new ByeCommandResponse();
          byeResponse.setNumberOfCommands(commandsUsedInSession);
          byeResponse.setStatus("success");
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
