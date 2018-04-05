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
   * Method for getting the number of student added since the last load.
   *
   * @return the number of student added since the last load
   */
  public int getNumberOfStudentAdded();

  /**
   * Method for getting the number of commands sent by the client during the session.
   *
   * @return the number of commands send by the client during the session
   */
  public int getNumberOfCommands();

  /**
   * Method for retrieving the success of a command
   *
   * @return if the last sent command was successful or not
   */
  public boolean checkSuccessOfCommand();

}
