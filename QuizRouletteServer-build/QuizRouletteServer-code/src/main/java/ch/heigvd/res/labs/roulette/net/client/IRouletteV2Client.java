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
   * This returns the number of student added in the last load
   * @return the number of sutdent added
   * @throws IOException
   */
  public int getNumberOfStudentAdded() throws IOException;

  /**
   * This methode give the number of commands send by the client to the server
   *
   * @return the number of command sent to the server
   * @throws IOException
   */
  public int getNumberOfCommands() throws IOException;

  /**
   *
   *
   * @return
   */
  public boolean checkSuccessOfCommand();
}
