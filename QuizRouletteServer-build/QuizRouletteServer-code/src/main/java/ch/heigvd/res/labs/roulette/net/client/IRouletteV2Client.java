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
   * The returned value is set by the command "LOAD"
   *
   * @return the number of students added by load
   */
  public int getNumberOfStudentAdded();

  /**
   * The returned value is incremented when a command is called
   *
   * @return the number of commands sent to the server
   */
  public int getNumberOfCommands();

  /**
   * Check the succes of the last command sent to the server
   *
   * @return true if the last command passed successfully
   */
  public boolean checkSuccessOfCommand();
}
