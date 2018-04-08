package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Olivier Liechti
 * @author Miguel Lopes Gouveia(endmon)
 * @author Rémy Nasserzare(remynz)
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
   * Return the number of commands used since the beginning
   * 
   * @return the number of commands used
   * @throws IOException 
   */
  public int getNumberOfCommands() throws IOException;
  
  /**
   * Return the number of students added in the last 
   * loadStudents.
   * 
   * @return the number of students added
   * @throws IOException 
   */
  public int getNumberOfStudentAdded() throws IOException;
  
  /**
   * Check if the last command has been successfull
   * 
   * @return if it's a success, return true. Else return false.
   * @throws IOException 
   */
  public boolean checkSuccessOfCommand() throws IOException;
}
