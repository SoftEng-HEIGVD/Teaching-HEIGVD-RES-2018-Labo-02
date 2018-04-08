package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
  
  protected int numberOfCommand = 0;
  protected int NumberOfStudentAdded = 0;
  private boolean commandStatus = false;
  
  @Override
  public void disconnect() throws IOException {
      numberOfCommand++;
      commandStatus = false;
      super.disconnect();
      commandStatus = true;
      
  }   

  public String getProtocolVersion() throws IOException {
      commandStatus = false;
      String protoVersion = super.getProtocolVersion();
      numberOfCommand++;
      commandStatus = true; 
      
      return protoVersion;
  }
  
  @Override
  public void clearDataStore() throws IOException {
    commandStatus = false;  
    out.println(RouletteV2Protocol.CMD_CLEAR);
    out.flush();
    
    LOG.log(Level.INFO, "Data erased. Server notifies: {0}", in.readLine());
    numberOfCommand++;
    commandStatus = true;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      commandStatus = false;
      super.loadStudent(fullname);
      NumberOfStudentAdded = 1;
      numberOfCommand++;
      commandStatus = true;
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      commandStatus = false;
      super.loadStudents(students);
      NumberOfStudentAdded = students.size();
      numberOfCommand++;
      commandStatus = true;    
  }
  
  @Override
  public List<Student> listStudents() throws IOException {
    commandStatus = false;
    out.println(RouletteV2Protocol.CMD_LIST);
    out.flush();  
    String response = in.readLine();
    List<Student> listS = JsonObjectMapper.parseJson(response, StudentsList.class).getStudents();
    numberOfCommand++;
    commandStatus = true;
    return listS;
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
      return commandStatus;
  }
}
