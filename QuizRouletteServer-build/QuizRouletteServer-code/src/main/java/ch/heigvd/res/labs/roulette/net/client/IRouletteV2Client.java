package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Olivier Liechti, modified by Christophe Joyet and Lionel Nanchen
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
   * Get the number of student added during LOAD command
   *
   * @return the number of students added
   */
  public int getNumberOfStudentAdded();

  /**
   * Get the number of command sent to the server
   *
   * @return the number of commands sent to the server
   */
  public int getNumberOfCommands();

  /**
   * Check if the last command was done succecfully
   *
   * @return true if the last command passed successfully
   */
  public boolean checkSuccessOfCommand();
}
