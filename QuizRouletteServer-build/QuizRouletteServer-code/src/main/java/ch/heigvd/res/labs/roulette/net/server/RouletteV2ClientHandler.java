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

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
  private IStudentsStore store;


  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

    writer.println("Hello! Type HELP to get the available commands");
    writer.flush();

    String command;
    boolean done = false;
    int commandCounter = 0;

    while (!done && ((command = reader.readLine()) != null)) {
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
          commandCounter++;
          break;

        case RouletteV2Protocol.CMD_HELP:
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          commandCounter++;
          break;

        case RouletteV2Protocol.CMD_INFO:
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          commandCounter++;
          break;

        case RouletteV2Protocol.CMD_LOAD:
          LoadCommandResponse loadAnswer = new LoadCommandResponse();
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          writer.flush();
          try {
            int actualSize = store.getNumberOfStudents();
            store.importData(reader);
            loadAnswer.setNumberOfNewStudents(store.getNumberOfStudents() - actualSize);
          }
          catch (IOException e) {
            loadAnswer.setError();
          }
          writer.println(JsonObjectMapper.toJson(loadAnswer));
          writer.flush();
          commandCounter++;
          break;

        case RouletteV2Protocol.CMD_LIST:
          StudentsList listAnswer = new StudentsList();
          listAnswer.addAll( store.listStudents());
          writer.println(JsonObjectMapper.toJson( listAnswer));
          writer.flush();
          commandCounter++;
          break;

        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          commandCounter++;
          break;

        case RouletteV2Protocol.CMD_BYE:
          commandCounter++;
          ByeCommandResponse byeAnswer = new ByeCommandResponse(commandCounter);
          writer.println(JsonObjectMapper.toJson(byeAnswer));
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


