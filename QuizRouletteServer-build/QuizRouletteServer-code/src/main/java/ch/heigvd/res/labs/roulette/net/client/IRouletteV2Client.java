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
     * Get the number of commands made by the client, either counted by the
     * client itself or recovered from a response from the server.
     *
     * @return Number of commands made by the client
     */
    public int getNumberOfCommands();

    /**
     * Get the number of newly added students after a LOAD command.
     *
     * @return Number of recently added students
     */
    public int getNumberOfStudentAdded();

    /**
     * Get the status of the command. True if it is a success, false otherwise.
     *
     * @return Boolean representing the status of a command
     */
    public boolean checkSuccessOfCommand();

}
