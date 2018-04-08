package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 */
public class RouletteV2tranqui793Test {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "tranqui793")
    public void theServerShouldHaveZeroStudentsAfterDataHasBeenCleared() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        //test if at start he has 0 students
        assertEquals(0, client.getNumberOfStudents());
        //we load some students and test if they have been loaded successfully
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("olivier");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());
        //we clear data and test if the data has been cleared
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "tranqui793")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "tranqui793")
    public void theServerShouldReturnTheCorrectNamesOfStudents() throws IOException {
        List<Student> studentsList = new LinkedList<Student>();
        studentsList.add(new Student("sacha"));
        studentsList.add(new Student("fabienne"));
        studentsList.add(new Student("olivier"));
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("sacha");
        client.loadStudent("olivier");
        client.loadStudent("fabienne");
        List<Student> studentServerList = client.listStudents();
        assertTrue(studentServerList.containsAll(studentsList));
    }

    @Test
    @TestAuthor(githubId = "tranqui793")
    public void theServerShouldHasTheCorrectNumberOfStudents() throws IOException {
                List<Student> studentsList = new LinkedList<Student>();
        studentsList.add(new Student("sacha"));
        studentsList.add(new Student("fabienne"));
        studentsList.add(new Student("olivier"));
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("sacha");
        client.loadStudent("olivier");
        client.loadStudent("fabienne");
        List<Student> studentServerList = client.listStudents();
        assertTrue(studentsList.size()==studentServerList.size());
    }
}
