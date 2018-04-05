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
   * The last LOAD command has stored the number of students entered in the store
   * @return number of students added
   * @throws IOException
   */
  public int getNumberOfStudentAdded() throws IOException;

  /**
   * the client keeps track for the number of used commands
   * @return number of commands used in this session
   * @throws IOException
   */
  public int getNumberOfCommands() throws IOException;

  /**
   * the variable successCommand is updated each time the server ansewers wis a success
   * @return value of the successCommand variable
   * @throws IOException
   */
  public boolean checkSuccessOfCommand() throws IOException;
}
