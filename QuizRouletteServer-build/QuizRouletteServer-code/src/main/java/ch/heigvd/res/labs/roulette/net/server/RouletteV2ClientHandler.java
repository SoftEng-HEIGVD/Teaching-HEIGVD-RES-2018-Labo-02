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
 * @author Olivier Liechti, Marc Labie
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

  IStudentsStore store = new StudentsStoreImpl();

  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer    = new PrintWriter(new OutputStreamWriter(os));

    ByeCommandResponse bcResponse = new ByeCommandResponse();

    writer.println("Hello. Online HELP is available. Will you find it?");
    writer.flush();

    String command;
    boolean done = false;
    while (!done && ((command = reader.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      bcResponse.incrNbrOfCommands();
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
          LoadCommandResponse lcResponse = new LoadCommandResponse();
          // We keep the old number of students to know how many there was before we add more.
          int oldNbrStudents = store.getNumberOfStudents();
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          writer.flush();
          try {
            store.importData(reader);
            // if there was no error during the process, we substract the new number of students to the old
            // one to know how many were added.
            lcResponse.setStatus("success");
            lcResponse.setNumberOfNewStudents(store.getNumberOfStudents() - oldNbrStudents);
          }catch (IOException e){
            lcResponse.setStatus("failure");
            lcResponse.setError(e.getMessage());
          }
          writer.println(JsonObjectMapper.toJson(lcResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          done = true;
          bcResponse.setStatus("success");
          writer.println(JsonObjectMapper.toJson(bcResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:
          StudentListCommand students = new StudentListCommand();
          students.setStudents(store.listStudents());
          writer.println(JsonObjectMapper.toJson(students));
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