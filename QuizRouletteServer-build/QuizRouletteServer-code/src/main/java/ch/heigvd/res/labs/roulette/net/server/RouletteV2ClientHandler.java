package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.EndLoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import static ch.heigvd.res.labs.roulette.net.server.RouletteServer.LOG;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

  /**
   * container of students data
   */
  private final IStudentsStore store;
  
  /**
   * total of commands received for this client
   */
  private int numberOfCommands;
  
  /**
   * nuber of student added in the last load-type command
   */
  private int numberOfStudentAdded = 0;
  
  /**
   * constructor
   * 
   * @param store container of students data
   */
  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
    numberOfCommands = 0;
  }

  /**
   * method where are handled the requests received from the client
   * 
   * @param is reader from the client stream
   * @param os writer to the client stream
   * @throws IOException if a write or read exception happen
   */
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
      command = command.toUpperCase();
      
      //increase number of command if it's a valid command
      if(Arrays.asList(RouletteV2Protocol.SUPPORTED_COMMANDS).contains(command)) {
         numberOfCommands++;
      }
      switch (command) {
         
         
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
          InfoCommandResponse infoResponse = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(infoResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          writer.flush();
          int numberOfStudent = store.getNumberOfStudents();
          store.importData(reader);
          numberOfStudentAdded = store.getNumberOfStudents() - numberOfStudent;
          EndLoadCommandResponse endLoadResponse = new EndLoadCommandResponse(RouletteV2Protocol.SUCCESS, numberOfStudentAdded);
          writer.println(JsonObjectMapper.toJson(endLoadResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          done = true;
          ByeCommandResponse byeResponse = new ByeCommandResponse(numberOfCommands);
          writer.println(JsonObjectMapper.toJson(byeResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:
           ListCommandResponse listResponse = new ListCommandResponse(store.listStudents());
           writer.println(JsonObjectMapper.toJson(listResponse));
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
  
  /**
   * return the number of student added in the last load-type request
   * 
   * @return the number of student added in the last load-type request
   */
  public int getnumberOfStudentAdded() {
     return numberOfStudentAdded;
  }
  
  /**
   * return the current total of commands sent by this client
   * 
   * @return the current total of commands sent by this client
   */
  public int getNumberOfCommands() {
     return numberOfCommands;
  }

}
