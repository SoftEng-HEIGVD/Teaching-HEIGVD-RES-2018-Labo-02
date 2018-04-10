package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.server.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.LoadCommandResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * 
 * @author @author doriane kaffo
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    
    private int numberOfCommands = 0;
    private int numberOfStudentsAdded = 0;
    private boolean commandSuccess = false;
        
    @Override
    public void disconnect() throws IOException {
        ++numberOfCommands;      
        writer.println(RouletteV2Protocol.CMD_BYE);
        ByeCommandResponse message = JsonObjectMapper.parseJson(reader.readLine(), ByeCommandResponse.class);       
        commandSuccess = message.getStatus().equals(RouletteV2Protocol.RESPONSE_SUCCESS);
        clientSocket.close();
        clientSocket = null;
        writer.close();
        reader.close();
    }
    
  @Override
  public void clearDataStore() throws IOException {
        ++numberOfCommands; 
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();
   
        reader.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
        ++numberOfCommands; 
        writer.println(RouletteV2Protocol.CMD_LIST);
        writer.flush();
                   
        StudentsList list = JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class);              
        return list.getStudents(); 
  }
  
  @Override 
      public void loadStudent(String fullname) throws IOException {
        ++numberOfCommands;
        writer.println(RouletteV2Protocol.CMD_LOAD);
        writer.flush();
        reader.readLine();
        writer.println(fullname);
        writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        LoadCommandResponse loadCommadResponse = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
        commandSuccess = loadCommadResponse.getStatus().equals(RouletteV2Protocol.RESPONSE_SUCCESS);
        numberOfStudentsAdded = loadCommadResponse.getNumberOfNewStudents();
    }
      
    @Override
    public void loadStudents(List<Student> students) throws IOException {
        ++numberOfCommands;
        writer.println(RouletteV2Protocol.CMD_LOAD);
        writer.flush();
        
        reader.readLine();
       
        for (Student s : students) {
            writer.println(s.getFullname());
        }
           
        writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        
        LoadCommandResponse loadCommadResponse = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
        commandSuccess = loadCommadResponse.getStatus().equals(RouletteV2Protocol.RESPONSE_SUCCESS);
        numberOfStudentsAdded = loadCommadResponse.getNumberOfNewStudents();
    }
    
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        ++numberOfCommands;
        return super.pickRandomStudent();       
    }
    
    @Override
    public int getNumberOfStudents() throws IOException {
        ++numberOfCommands;
        return super.getNumberOfStudents();
    }
    
    @Override
    public String getProtocolVersion() throws IOException {
        ++numberOfCommands;
        return super.getProtocolVersion();
    }
    
  @Override 
  public int getNumberOfCommands(){
      return numberOfCommands;
  }
  
  @Override 
  public int getNumberOfStudentAdded(){
      return numberOfStudentsAdded;
  }
  
  @Override 
  public boolean checkSuccessOfCommand(){
      return commandSuccess;
  }
}