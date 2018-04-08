package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Yosra Harbaoui
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    
    private int numberOfCommands = 0;
    private int numberOfStudentsAdded = 0;
    private boolean commandSuccess = false;
        
    @Override
    public void disconnect() throws IOException {
       /**
       * 1. increment the number of commands sent to th server
       */
        ++numberOfCommands;
        /**
         * 2. send the BYE command throw the writer
         */        
        writer.println(RouletteV2Protocol.CMD_BYE);
        
        /**
         * 3.Check the success of the command
         */
        ByeCommandResponse message = JsonObjectMapper.parseJson(reader.readLine(), ByeCommandResponse.class);       
        commandSuccess = message.getStatus().equals(RouletteV2Protocol.RESPONSE_SUCCESS);
        /**
         * 4. close the objects used to communicate with the server
         */
        socket.close();
        socket = null;
        writer.close();
        reader.close();
    }
    
  @Override
  public void clearDataStore() throws IOException {
      /**
       * 1. increment the number of commands sent to th server
       */
        ++numberOfCommands; 
        /**
         * 2. send the CLEAR command throw the writer to clear the data
         */
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();
        
        /**
         * 3. read the server response 
         */
        reader.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
       /**
        * 1. increment the number of commands sent to th server
        */
        ++numberOfCommands; 
        /**
         * 2. send the LIST command throw the writer 
         */ 
        writer.println(RouletteV2Protocol.CMD_LIST);
        writer.flush();
        /**
         * 3. load information to read the list of students name sent from the server
         */
                   
        StudentsList list = JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class);              
        return list.getStudents(); 
  }
  
  @Override 
      public void loadStudent(String fullname) throws IOException {
       /**
       * 1. increment the number of commands sent to th server
       */
        ++numberOfCommands;
        /**
         * 2. send the LOAD command throw the writer to load information from
         * the server
         */
        
        writer.println(RouletteV2Protocol.CMD_LOAD);
        writer.flush();

        /**
         * 3.read the information sent from the server
         */
        reader.readLine();

        /**
         * 4. send the full name of the student throw the writer
         */
        writer.println(fullname);

        /**
         * 5. send the ENDOFDATA_MARKER
         */
        writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        /**
         * 6. Check if the command was successfully done 
         */
        LoadCommandResponse loadCommadResponse = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
        commandSuccess = loadCommadResponse.getStatus().equals(RouletteV2Protocol.RESPONSE_SUCCESS);
        numberOfStudentsAdded = loadCommadResponse.getNumberOfNewStudents();
    }
      
    @Override
    public void loadStudents(List<Student> students) throws IOException {
       /**
       * 1. increment the number of commands sent to th server
       */
        ++numberOfCommands;
        /**
         * 2. send the LOAD command throw the writer to load information from
         * the server
         */
        writer.println(RouletteV2Protocol.CMD_LOAD);
        writer.flush();
        
        reader.readLine();
        
        /**
         * 2. load the list of student by loading each student fullname (using
         * loadStudent method)
         */
        for (Student s : students) {
            writer.println(s.getFullname());
        }
        
        /**
         * 3. send the ENDOFDATA_MARKER
         */     
        writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        
        /**
         * 4. Check if the command was successfully done 
         */
        LoadCommandResponse loadCommadResponse = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
        commandSuccess = loadCommadResponse.getStatus().equals(RouletteV2Protocol.RESPONSE_SUCCESS);
        numberOfStudentsAdded = loadCommadResponse.getNumberOfNewStudents();
    }
    
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
       /**
       * 1. increment the number of commands sent to th server
       */
        ++numberOfCommands;
        
        /**
         * Load a random student using the override of the V1
         */
        return super.pickRandomStudent();       
    }
    
    @Override
    public int getNumberOfStudents() throws IOException {
       /**
       * 1. increment the number of commands sent to th server
       */
        ++numberOfCommands;
        
        /**
         * 2. get the number of students using the override of V1
         */ 
        return super.getNumberOfStudents();
    }
    
    @Override
    public String getProtocolVersion() throws IOException {
       /**
       * 1. increment the number of commands sent to th server
       */
        ++numberOfCommands;
        /**
         * 2. return the protocol version  using the override of V1
         */ 
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
