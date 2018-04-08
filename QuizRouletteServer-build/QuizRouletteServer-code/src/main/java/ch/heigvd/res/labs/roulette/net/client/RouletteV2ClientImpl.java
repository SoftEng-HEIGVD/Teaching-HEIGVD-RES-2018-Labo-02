package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
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
 * @author Iando Rafidimalala
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

  @Override
  public String getProtocolVersion() throws IOException {
      commandStatus = false;
      numberOfCommand++;
      String protoVersion = super.getProtocolVersion();      
      commandStatus = true; 
      
      return protoVersion;
  }
  
  @Override
  public void clearDataStore() throws IOException {
    commandStatus = false;
    numberOfCommand++;
    out.println(RouletteV2Protocol.CMD_CLEAR);
    out.flush();
    
    LOG.log(Level.INFO, "Data erased. Server notifies: {0}", in.readLine());
    
    commandStatus = true;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      commandStatus = false;
      numberOfCommand++;
      super.loadStudent(fullname);
      NumberOfStudentAdded = 1;
      
      commandStatus = true;
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      commandStatus = false;
      numberOfCommand++;
      super.loadStudents(students);
      NumberOfStudentAdded = students.size();
      
      commandStatus = true;    
  }
  
  @Override
  public List<Student> listStudents() throws IOException {
    commandStatus = false;
    numberOfCommand++;
    out.println(RouletteV2Protocol.CMD_LIST);
    out.flush();  
    String response = in.readLine();
    List<Student> listS = JsonObjectMapper.parseJson(response, StudentsList.class).getStudents();
    
    commandStatus = true;
    return listS;
  }

  @Override
  public int getNumberOfStudents() throws IOException {  
    commandStatus = false;
    numberOfCommand++;
    int count = super.getNumberOfStudents();
    
    commandStatus = true;
      
    return count;
  }  

  
  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    commandStatus = false;
    numberOfCommand++;
    Student student = super.pickRandomStudent();
    
    commandStatus = true;
      
    return student;      
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
