package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.EndLoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

   /**
    * last Bye response received from the server
    */
   private ByeCommandResponse bye;
   
   /**
    * last Load response received from the server
    */
   private EndLoadCommandResponse load;
   
   /**
    * number of student added in the last load
    */
   private int numberOfStudentAdded = 0;
   
   /**
    * total of commands sent to the server until now
    */
   private int numberOfCommands = 0;
   
   /**
    * ask to the server to clear all students in his datastore
    * 
    * @throws IOException if a write or read exception happen 
    */
  @Override
  public void clearDataStore() throws IOException {
     writer.println(RouletteV2Protocol.CMD_CLEAR);
     writer.flush();
     
     if(!reader.readLine().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
        LOG.log(Level.SEVERE,"problem with CLEAR answer from server");
     }
     numberOfCommands++;
  }

  /**
   * return a list of all students contained in the server
   * 
   * @return a list of all students contained in the server
   * 
   * @throws IOException if a write or read exception happen
   */
  @Override
  public List<Student> listStudents() throws IOException {
     writer.println(RouletteV2Protocol.CMD_LIST);
     writer.flush();
     ListCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), ListCommandResponse.class);
     System.out.println(response);
     numberOfCommands++;
     numberOfStudentAdded = response.getStudents().size();
     return response.getStudents();
  }
  
  /**
   * return the number of student added in the last load sent to the server
   * 
   * @return return the number of student added in the last load sent to the server
   */
  @Override
  public int getNumberOfStudentAdded() {
      return numberOfStudentAdded;
  }

  /**
   * return the number of commands sent to the server
   * 
   * @return the number of commands sent to the server
   */
   @Override
   public int getNumberOfCommands() {
      return numberOfCommands;
   }

   /**
   * return the state of the last load received
   * 
   * @return the state of the last load received
   */
   @Override
   public boolean checkSuccessOfCommand() {
      return load.getStatus().equals(RouletteV2Protocol.SUCCESS);
   }
   
   /**
    * disconnect the server
    * 
    * @throws IOException if a write or read exception happen
    */
   @Override
  public void disconnect() throws IOException {
     super.disconnect();
     numberOfCommands++;
  }
  
  /**
   * send a new student to the server
   * @param fullname name of the student
   * @throws IOException if a write or read exception happen
   */
  @Override
  public void loadStudent(String fullname) throws IOException {
    super.loadStudent(fullname);
    load = JsonObjectMapper.parseJson(serverResponse, EndLoadCommandResponse.class);
    numberOfStudentAdded = 1;
    numberOfCommands++;
  }
  
  /**
   * send new students to the server
   * @param students list of the student we want the server to add
   * @throws IOException if a write or read exception happen
   */
  @Override
  public void loadStudents(List<Student> students) throws IOException {
     super.loadStudents(students);
     load = JsonObjectMapper.parseJson(serverResponse, EndLoadCommandResponse.class);
     numberOfStudentAdded = load.getNumberOfNewStudents();
     numberOfCommands++;
  }
  
  /**
   * ask to the server a random student contained in the server
   * 
   * @return a random student contained in the server
   * @throws EmptyStoreException if there is no student and we ask a random one
   * @throws IOException if a write or read exception happen
   */
  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
     numberOfCommands++;
     return super.pickRandomStudent();
  }
  
  /**
   * return the current protocol version
   * 
   * @return the current protocol version
   * @throws IOException if a write or read exception happen
   */
  @Override
  public String getProtocolVersion() throws IOException {
     String version = super.getProtocolVersion();
     numberOfCommands++;
     return version;
  }
  
  /**
   * return the current total of student
   * 
   * @return the current total of student
   * @throws IOException if a write or read exception happen
   */
  @Override
  public int getNumberOfStudents() throws IOException {
     int numberOfStudents = super.getNumberOfStudents();
     numberOfCommands++;
     return numberOfStudents;
  }
}
