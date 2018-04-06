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
     * Get the number of Students added to the DB at last load query
     * @return the number of student added
     */
  public int getNumberOfStudentAdded();

    /**
     * Get the number of commands sent to the server so far
     * @return the number of commands sent
     */
  public int getNumberOfCommands();

    /**
     * Get the result status of the last command.
     * @return {@code True} if last command succeeded {@code False} otherwise
     */
  public boolean checkSuccessOfCommand();

}

