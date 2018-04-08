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

    int numberOfCommands = 0;
    String command;
    boolean done = false;
    while (!done && ((command = reader.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
        case RouletteV2Protocol.CMD_RANDOM:
          numberOfCommands++;
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
          numberOfCommands++;
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          numberOfCommands++;
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          numberOfCommands++;
          try {
            store.importData(reader);
          }catch (Exception e){
            RouletteV2Protocol.etat = "fail";
          }
          LoadCommandResponse lRep = new LoadCommandResponse(RouletteV2Protocol.etat, store.getNumberOfNewStudents());
          writer.println(JsonObjectMapper.toJson(lRep));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          numberOfCommands++;
          ByeCommandResponse bRep = new ByeCommandResponse(RouletteV2Protocol.etat = "success",numberOfCommands);
          writer.println(JsonObjectMapper.toJson(bRep));
          writer.flush();
          done = true;
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          numberOfCommands++;
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:
          numberOfCommands++;
          StudentsList listOfStudent = new StudentsList();
          listOfStudent.setStudents(store.listStudents());
          writer.println(JsonObjectMapper.toJson(listOfStudent));
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
