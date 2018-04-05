package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
   
   @Override
   public int getNumberOfCommands() throws IOException {
      out.println(RouletteV2Protocol.CMD_BYE);
      out.flush();
      String response = in.readLine(); // read the answer from server
      ByeCommandResponse info = JsonObjectMapper.parseJson(response, ByeCommandResponse.class);
      return info.getNumberOfCommands();
   }

   @Override
   public int getNumberOfStudentAdded() throws IOException {
      out.println(RouletteV2Protocol.CMD_LOAD);
      out.flush();
      String response = in.readLine(); // read the answer from server
      LoadCommandResponse info = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
      return info.getNumberOfNewStudents();
   }
   
   
  @Override
  public void clearDataStore() throws IOException {

    out.println(RouletteV2Protocol.CMD_CLEAR);
    out.flush(); // send the message
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
