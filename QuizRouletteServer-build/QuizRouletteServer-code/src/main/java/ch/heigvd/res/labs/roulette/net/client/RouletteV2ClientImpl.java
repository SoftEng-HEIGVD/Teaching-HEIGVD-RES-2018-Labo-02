package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.StudentCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.DisconnectCommandResponse;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  protected boolean lastCommandSuccess = false;
    
  @Override
  public void clearDataStore() throws IOException {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    nbCommamde++;
    os.println(RouletteV2Protocol.CMD_CLEAR);
    os.flush();
    
    is.readLine();

  }

  @Override
  public List<Student> listStudents() throws IOException {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        ++nbCommamde;
        StudentsList studentList = new StudentsList();
        os.println(RouletteV2Protocol.CMD_LIST);
        os.flush();
        
        return studentList.getStudents();

  }

  public int getNumberOfStudentAdded() throws IOException{

      return nbStudent;
  }


  public int getNumberOfCommands() throws IOException{
      return nbCommamde;
  }


  public boolean checkSuccessOfCommand(){
      return lastCommandSuccess;
  }
  
  public void loadStudent(String fullname) throws IOException {
      super.loadStudent(fullname);
      if(clientSocket == null)return;
      
      StudentCommandResponse response = JsonObjectMapper.parseJson(is.readLine(), StudentCommandResponse.class);
      lastCommandSuccess = response.getStatus().equals("success");
      
      
  }
  
  public void loadStudents(List<Student> students) throws IOException {
      super.loadStudents(students);
      
      StudentCommandResponse response = JsonObjectMapper.parseJson(is.readLine(), StudentCommandResponse.class);
      lastCommandSuccess = response.getStatus().equals("success");
  }
  
  
  public void disconnect() throws IOException {
        os.println(RouletteV2Protocol.CMD_BYE);
        os.flush();

        DisconnectCommandResponse response = JsonObjectMapper.parseJson(is.readLine(), DisconnectCommandResponse.class);
        lastCommandSuccess = response.getStatus().equals("success");
        
        super.disconnect();
  }
}