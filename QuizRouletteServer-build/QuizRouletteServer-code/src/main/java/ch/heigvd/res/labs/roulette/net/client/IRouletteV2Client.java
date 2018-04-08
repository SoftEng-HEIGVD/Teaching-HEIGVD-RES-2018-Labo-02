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
     * response and converts it into a list of Student objects (using the
     * JsonObjectMapper class and the StudentsList class).
     *
     * @return the list of students currently in the store
     * @throws IOException
     */
    public List<Student> listStudents() throws IOException;

    /**
     * New method added
     * Asks the server how many students have been added on the last load
     * @return the number of commands sent to the server
     * @throws IOException
     */
    public int getNumberOfStudentAdded() throws IOException;

    /**
     * New method added
     * Asks the server how many commands have being typed
     * @return the number of commands sent to the server
     * @throws IOException
     */
    public int getNumberOfCommands() throws IOException;
    
    /**
     * New method added
     * Asks the server if the last command succeed
     * @return true if command succeed
     * @throws IOException
     */
    public boolean checkSuccessOfCommand() throws IOException;

}
