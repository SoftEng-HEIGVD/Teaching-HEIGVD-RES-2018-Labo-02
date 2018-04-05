package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Olivier Liechti
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
   * Invokes the BYE command defined in the protocol (version 2), parses the
   * response and converts it into a integer (using the JsonObjectMapper
   * class and the ByeCommandResponse class).
   * 
   * @return the number of commands
   * @throws IOException 
   */  
  public int getNumberOfCommands() throws IOException;


   /**
   * Invokes the LOAD command defined in the protocol (version 2), parses the
   * response and converts it into a integer (using the JsonObjectMapper
   * class and the LoadCommandResponse class).
   * 
   * @return the number of new students added
   * @throws IOException 
   */  
  public int getNumberOfStudentAdded() throws IOException;
  
}
