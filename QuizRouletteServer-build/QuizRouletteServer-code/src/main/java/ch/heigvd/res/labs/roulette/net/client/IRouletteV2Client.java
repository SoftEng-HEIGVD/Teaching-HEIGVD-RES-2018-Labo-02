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
   * Return the number of students added after the last load
   *
   * @return the number of students added after the last load
   * @throws -
   */
  public int getNumberOfStudentAdded();

  /**
   * Return the number of commands executed during the session
   *
   * @return the number of commands executed during the session
   * @throws -
   */
  public int getNumberOfCommands();

  /**
   * Return a bool which indicate if the command was a success or not
   *
   * @return  a bool which indicate if the command was a success or not
   * @throws -
   */
  public boolean checkSuccessOfCommand();

}
