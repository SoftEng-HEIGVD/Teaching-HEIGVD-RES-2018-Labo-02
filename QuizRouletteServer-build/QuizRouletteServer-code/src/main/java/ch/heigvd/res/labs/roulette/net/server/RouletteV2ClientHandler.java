package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import com.fasterxml.jackson.core.JsonParseException;

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

  private int nbCommandsSuccessful = 0; // only successful cmds (no exception thrown) are counted

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
        case RouletteV2Protocol.CMD_RANDOM:                         // RANDOM no change except cmd count
          nbCommandsSuccessful++;
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            nbCommandsSuccessful--;
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          writer.println(JsonObjectMapper.toJson(rcResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_HELP:                               // HELP no change except cmd count
          nbCommandsSuccessful++;
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:                               // INFO no change except cmd count
          nbCommandsSuccessful++;
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_CLEAR:                              // CLEAR
          nbCommandsSuccessful++; // no exceptions are thrown from this block
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:                               // LIST
          nbCommandsSuccessful++;
          StudentsList students = new StudentsList();
          students.addAll(store.listStudents());
          try {
            writer.println(JsonObjectMapper.toJson(students));
          } catch (JsonParseException e) {
            LOG.log(Level.SEVERE, "Error while creating students JSON");
            nbCommandsSuccessful--;
            throw e;
          }
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:                               // LOAD
          nbCommandsSuccessful++;
          writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
          writer.flush();
          LoadCommandResponse ldResponse = new LoadCommandResponse();
          int storedStudents = 0;
          try {
            storedStudents = store.importData(reader);
            ldResponse.setNumberOfNewStudents(storedStudents);
            ldResponse.setStatus("success");
          } catch (IOException e) {
            nbCommandsSuccessful--;
            ldResponse.setStatus("failure");
          }
          writer.println(JsonObjectMapper.toJson(ldResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:                                // BYE
          nbCommandsSuccessful++;
          done = true;
          ByeCommandResponse byeResponse = new ByeCommandResponse(nbCommandsSuccessful);
          writer.println(JsonObjectMapper.toJson(byeResponse));
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
