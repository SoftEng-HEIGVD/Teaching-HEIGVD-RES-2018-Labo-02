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
   * Get the number of commands sent by the client to the server during this session.
   * @return The number of commands that were send by the client
   * @throws IOException
   */
  public int getNumberOfCommands() throws IOException;

  /**
   * Get the number of students added by the last load command sent.
   * @return The number of students added
   * @throws IOException
   */
  public int getNumberOfStudentAdded() throws IOException;

  /**
   * Check if the last command send to the server was managed successfuly.
   * @return true if the command was a success.
   * @throws IOException
   */
  public boolean checkSuccessOfCommand() throws IOException;

}
