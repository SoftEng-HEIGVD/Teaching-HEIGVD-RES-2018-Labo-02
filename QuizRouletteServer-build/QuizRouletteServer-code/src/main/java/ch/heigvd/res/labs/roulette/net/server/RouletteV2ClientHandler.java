package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {
  final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

  public int numberOfNewStudents = 0;
  public int numberOfCommands = 0;

  private final IStudentsStore store;

  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {

    //TODO check les success comme il faut
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

    writer.println("Hello. Online HELP is available. Will you find it?");
    writer.flush();

    String command;
    boolean done = false;
    while (!done && ((command = reader.readLine()) != null)) {
      ++numberOfCommands;
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
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          int numberOfNewStudents = store.getNumberOfStudents();
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          writer.flush();
          store.importData(reader);
          //writer.println(RouletteV2Protocol.RESPONSE_LOAD_DONE);
          //writer.flush();
          numberOfNewStudents = store.getNumberOfStudents() - numberOfNewStudents;
          LoadCommandResponseV2 responseLoad = new LoadCommandResponseV2("success", numberOfNewStudents);

          writer.println(JsonObjectMapper.toJson(responseLoad));

          writer.flush();
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:
            StudentsList sL = new StudentsList();
            List<Student> li = store.listStudents();
            String kk = li.get(0).getFullname();
            sL.setStudents( store.listStudents() );
            String str = JsonObjectMapper.toJson(sL);
            writer.println(str);
            writer.flush();
          break;
        case RouletteV2Protocol.CMD_BAILLE:
          writer.println("Are you French?");
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          ByeCommandResponseV2 responseBye = new ByeCommandResponseV2("success", numberOfCommands);
          str = JsonObjectMapper.toJson(responseBye);
          writer.println(str);
          writer.flush();
          done = true;
          break;

        default:
          --numberOfCommands;
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          break;
      }
      writer.flush();
    }

  }

}
