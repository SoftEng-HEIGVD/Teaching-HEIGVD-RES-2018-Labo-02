package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
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
 * @author Walid Koubaa
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

  private final IStudentsStore store;
  private int nbCommandsSession = 0;
  private static final String SUCCESS_STATUS = "success";

  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store=store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

    int numberOfNewStudents = 0;

    writer.println("Hello. Online HELP is available. Will you find it?");
    writer.flush();

    String command;
    boolean done = false;
    while (!done && ((command = reader.readLine()) != null)) {
      nbCommandsSession++;
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
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_INFO:
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
          writer.flush();
          int numberOfStudents = store.getNumberOfStudents();
          store.importData(reader);
          int studentsLoaded = store.getNumberOfStudents();
          numberOfNewStudents = studentsLoaded - numberOfStudents;
          LoadCommandResponse load = new LoadCommandResponse(SUCCESS_STATUS, numberOfNewStudents);
          writer.println(JsonObjectMapper.toJson(load));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          ByeCommandResponse bye = new ByeCommandResponse(SUCCESS_STATUS, nbCommandsSession);
          writer.println(JsonObjectMapper.toJson(bye));
          writer.flush();
          done = true;
          break;
        case RouletteV2Protocol.CMD_LIST:
          StudentsList students = new StudentsList();
          students.setStudents(store.listStudents());
          writer.println(JsonObjectMapper.toJson(students));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        default:
          // count number of correct commands only
          --nbCommandsSession;
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          break;
      }
      writer.flush();
    }
  }
}

