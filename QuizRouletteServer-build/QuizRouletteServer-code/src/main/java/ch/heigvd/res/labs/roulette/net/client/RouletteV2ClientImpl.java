package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
  
  protected int numberOfCommand;
  protected int NumberOfStudentAdded;
  private String commandStatus;
  
  @Override
  public void disconnect() throws IOException {
    out.println(RouletteV1Protocol.CMD_BYE);
    out.flush();

    //Get the json response
    String response = in.readLine();

    //numberOfCommand = JsonObjectMapper.parseJson(response, ByeCommandResponse.class).getNumberOfCommands();
    //commandStatus = JsonObjectMapper.parseJson(response, ByeCommandResponse.class).getStatus();
    
    in.close();
    out.close();
    clientSocket.close();
  }   
  
  @Override
  public void clearDataStore() throws IOException {
    out.println(RouletteV2Protocol.CMD_CLEAR);
    out.flush();
    
    in.readLine();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      //Send the data accroding the protocol
      out.println(RouletteV1Protocol.CMD_LOAD);
      out.println(fullname);
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();     
      
      in.readLine(); //read the "send data" line
      String response = in.readLine();// //consume response send by server about the loading status
      //commandStatus = JsonObjectMapper.parseJson(response, LoadCommandResponse.class).getStatus();
      //NumberOfStudentAdded = JsonObjectMapper.parseJson(response, LoadCommandResponse.class).getNumberOfNewStudents(); 
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      out.println(RouletteV1Protocol.CMD_LOAD);
      for (Student student : students) {
          out.println(student.getFullname());
      }
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();
      
      in.readLine();
      String response = in.readLine(); //consume response send by server
      //commandStatus = JsonObjectMapper.parseJson(response, LoadCommandResponse.class).getStatus();
      //NumberOfStudentAdded = JsonObjectMapper.parseJson(response, LoadCommandResponse.class).getNumberOfNewStudents();       
  }
  
  @Override
  public List<Student> listStudents() throws IOException {
    out.println(RouletteV2Protocol.CMD_LIST);
    out.flush();  
    String response = in.readLine();
    
    return JsonObjectMapper.parseJson(response, StudentsList.class).getStudents();
  }
 
  @Override
  public int getNumberOfCommands()throws IOException{
    return numberOfCommand;
  }
  
  @Override
  public int getNumberOfStudentAdded() throws IOException{
      return NumberOfStudentAdded;
  }
  
  @Override
  public boolean checkSuccessOfCommand()throws IOException{
      return commandStatus.equals(Status.Success.toString());
  }
}
