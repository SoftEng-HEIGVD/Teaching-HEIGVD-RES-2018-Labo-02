package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Olivier Liechti, Samuel Mayor, Alexandra Korukova
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
     * Returns the number of students added the last time loadStudent() or loadStudents() was called
     * @return the number of students added during the last load
     * @throws IOException
     */
    public int getNumberOfStudentAdded() throws IOException;

    /**
     * Returns the number of commands invoked
     * @return number of commands invoked
     * @throws IOException
     */
    public int getNumberOfCommands() throws IOException;

    /**
     * Checks the status of LOAD and BYE commands.
     * @return true if the command in question is BYE, true if the command in question is LOAD and the server
     * has succeeded to import the students to the store, false otherwise
     * @throws IOException
     */
    public boolean checkSuccessOfCommand() throws IOException;

}