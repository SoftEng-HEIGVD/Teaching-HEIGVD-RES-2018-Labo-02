package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
  
  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  
  private int numberOfCommands;
  private int numberOfNewStudents;
  private boolean isSuccessOfCommands;
  
  @Override
  public int getNumberOfStudentAdded() {
    return numberOfNewStudents;
  }
  
  @Override
  public int getNumberOfCommands() {
    return numberOfCommands;
  }
  
  @Override
  public boolean checkSuccessOfCommand() {
    return isSuccessOfCommands;
  }
  
  @Override
  public void clearDataStore() throws IOException {
    sender(RouletteV2Protocol.CMD_CLEAR);
    if(input.readLine().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)){
      ++numberOfCommands;
    }
  }

  @Override
  public List<Student> listStudents() throws IOException {
    sender(RouletteV2Protocol.CMD_LIST);
    List<Student> studentsList = JsonObjectMapper.parseJson(input.readLine(), StudentsList.class).getStudents();
    ++numberOfCommands;
    return studentsList;
  }
  
  @Override
  public void connect(String server, int port) throws IOException {
    numberOfCommands = 0;
    numberOfNewStudents = 0;
    isSuccessOfCommands = false;
    super.connect(server, port);
  }
  
  @Override
  public void disconnect() throws IOException {
    if(!isConnected){
      return;
    }
    
    // Tell to the server to close the connection.
    sender(RouletteV2Protocol.CMD_BYE);
    ByeCommandResponse byeCommandResponse = JsonObjectMapper.parseJson(input.readLine(),ByeCommandResponse.class);
    isSuccessOfCommands = byeCommandResponse.getStatus().equals("success");
    if(isSuccessOfCommands){
      ++numberOfCommands; // = byeCommandResponse.getNumberOfCommands();
      if(numberOfCommands == byeCommandResponse.getNumberOfCommands()){
        LOG.log(Level.INFO, "The command count on the client and the server are the same.");
      }
      // Close all the resources
      socket.close();
      input.close();
      output.close();
      isConnected = false;
    }
    else{
      LOG.log(Level.SEVERE, "Cannot disconnect the client.");
    }
  }
  
  @Override
  public void loadStudent(String fullname) throws IOException {
    
    // Tell the server to start to load the data
    sender(RouletteV2Protocol.CMD_LOAD);
    input.readLine();
    
    // Send the data
    sender(fullname);
    
    // Tell the server that all the data are loaded
    sender(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(input.readLine(), LoadCommandResponse.class);
    isSuccessOfCommands = loadCommandResponse.getStatus().equals("success");
    if(isSuccessOfCommands){
      ++numberOfCommands;
      numberOfNewStudents = 1;
    }
    
  }
  
  @Override
  public void loadStudents(List<Student> students) throws IOException {
    // Tell the server to start to load the data
    sender(RouletteV2Protocol.CMD_LOAD);
    input.readLine();
    
    for (Student s : students) {
      sender(s.getFullname());
    }
    
    // Tell the server that all the data are loaded
    sender(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(input.readLine(), LoadCommandResponse.class);
    if(isSuccessOfCommands = loadCommandResponse.getStatus().equals("success")){
      ++numberOfCommands;
      numberOfNewStudents = loadCommandResponse.getNumberOfNewStudents();
    }
  }
  
  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    ++numberOfCommands;
    isSuccessOfCommands = true;
    return super.pickRandomStudent();
  }
  
  @Override
  public int getNumberOfStudents() throws IOException {
    ++numberOfCommands;
    isSuccessOfCommands = true;
    return super.getNumberOfStudents();
  }
  
  @Override
  public String getProtocolVersion() throws IOException {
    ++numberOfCommands;
    isSuccessOfCommands = true;
    return super.getProtocolVersion();
  }
}
