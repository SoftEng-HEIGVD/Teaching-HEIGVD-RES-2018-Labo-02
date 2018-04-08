package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Olivier Liechti
 * @author Yosra Harbaoui
 */
public interface IRouletteV2Client extends IRouletteV1Client {

  /**
   * Clears the students data store, by invoking the CLEAR command defined in
   * the protocol (version 2).
   * 
   * @throws IOException 
   */
  public void clearDataStore() throws IOException;

  /**
   * Invokes the LIST command defined in the protocol (version 2), parses the
   * response and converts it into a list of Student objects (using the JsonObjectMapper
   * class and the StudentsList class).
   * 
   * @return the list of students currently in the store
   * @throws IOException 
   */
  public List<Student> listStudents() throws IOException;
  
  /**
   * Get the number of commands sent to the server
   * @return the number of commands sent to the server
   * @throw IOException 
   */
  public int getNumberOfCommands() throws IOException;
  
  /**
   * Get the number of students stored in the server. 
   * @return the number of students stored in the server
   * @throws IOException 
   */
  public int getNumberOfStudentAdded() throws IOException;
  
  /**
   * Check if the command was successfully done
   * @return a boolean the success of the command 
   * @throws IOException
   */
  public boolean checkSuccessOfCommand() throws IOException;
}
