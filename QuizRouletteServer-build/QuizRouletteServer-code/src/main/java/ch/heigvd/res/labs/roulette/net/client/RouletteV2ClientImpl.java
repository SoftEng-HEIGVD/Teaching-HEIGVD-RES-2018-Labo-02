package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
   
   final String SUCCESS_MSG = "success";
   
   @Override
   public int getNumberOfCommands() throws IOException {
      out.println(RouletteV2Protocol.CMD_BYE);
      out.flush(); // sent the message procotol
      String response = in.readLine(); // read the answer from server
      ByeCommandResponse info = JsonObjectMapper.parseJson(response, ByeCommandResponse.class);
      return info.getNumberOfCommands();
   }

   @Override
   public int getNumberOfStudentAdded() throws IOException {
      out.println(RouletteV2Protocol.CMD_LOAD);
      out.flush(); // sent the message procotol
      String response = in.readLine(); // read the answer from server
      LoadCommandResponse info = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
      return info.getNumberOfNewStudents();
   }

   @Override
   public boolean checkSuccessOfCommand() throws IOException {
      out.println(RouletteV2Protocol.CMD_BYE); // check the command AFTER finishing, so BYE
      out.flush(); // sent the message procotol
      String response = in.readLine(); // read the answer from server
      LoadCommandResponse info = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
      
      // Check the status of the command and return the result
      if (info.getStatus().equals(SUCCESS_MSG)) {
         return true;
      } else {
         return false;
      }
   }
   
   

  @Override
  public void clearDataStore() throws IOException {

    out.println(RouletteV2Protocol.CMD_CLEAR);
    out.flush(); // sent the message procotol
    in.readLine(); // read the answer from server

    }

  @Override
  public List<Student> listStudents() throws IOException {
    out.println(RouletteV2Protocol.CMD_LIST);
    out.flush();

    String list = in.readLine(); // read the answer from server

    // parse the JSON answer from server and get a List<Student> from it
    return JsonObjectMapper.parseJson(list, StudentsList.class).getStudents();
  }
  
}
