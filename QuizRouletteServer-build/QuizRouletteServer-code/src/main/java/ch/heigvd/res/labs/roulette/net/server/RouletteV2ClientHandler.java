package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
    int nbCommande = 0;
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

    writer.println("Hello. Online HELP is available. Will you find it?");
    writer.flush();

    String command;
    boolean done = false;
    while (!done && ((command = reader.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
        case RouletteV2Protocol.CMD_RANDOM:
          nbCommande++;
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
          nbCommande++;
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          nbCommande++;
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          nbCommande++;
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          writer.flush();
          int begining = store.getNumberOfStudents();
          LoadCommandResponse loadCommandResponse = new LoadCommandResponse();
          try {
            store.importData(reader);
            loadCommandResponse.setStatus(RouletteV2Protocol.REPONSE_SUCCESS);
            int end = store.getNumberOfStudents();
            loadCommandResponse.setNumberOfNewStudents(end-begining);
          }catch (IOException e){
            loadCommandResponse.setStatus(RouletteV2Protocol.REPONSE_FAILURE);
            loadCommandResponse.setNumberOfNewStudents(0);
          }
          writer.println(JsonObjectMapper.toJson(loadCommandResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          nbCommande++;
          ByeCommandeResponse bcResponse = new ByeCommandeResponse();
          bcResponse.setNumberOfCommands(nbCommande);
          bcResponse.setStatus(RouletteV2Protocol.REPONSE_SUCCESS);
          writer.println(JsonObjectMapper.toJson(bcResponse));
          done = true;
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          nbCommande++;
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          break;
        case RouletteV2Protocol.CMD_LIST:
          nbCommande++;
          ListCommandResponse lcResponse = new ListCommandResponse();
          lcResponse.setStudents(store.listStudents());
          writer.println(JsonObjectMapper.toJson(lcResponse));
          break;

        default:
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          break;
      }
      writer.flush();
    }
    //closing all connection
    writer.close();
    reader.close();
  }
}


