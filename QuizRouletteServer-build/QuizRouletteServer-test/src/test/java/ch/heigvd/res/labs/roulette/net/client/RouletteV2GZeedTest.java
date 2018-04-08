package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author GZeed
 */
public class RouletteV2GZeedTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "GZeed")
    public void itShouldBePossibleListStudentToARouletteServer() throws Exception {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.client;
        client.clearDataStore();
        assertTrue(client.listStudents().isEmpty());
        List<Student> sts = new ArrayList<Student>();
        sts.add(new Student("pier"));
        sts.add(new Student("paul"));
        sts.add(new Student("jack"));

        client.loadStudents(sts);

        assertEquals(sts, client.listStudents());

    }

    @Test
    @TestAuthor(githubId = "GZeed")
    public void itShouldBePossibleListAnyStudentToARouletteServer() throws Exception {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.client;
        client.clearDataStore();
        assertTrue(client.listStudents().isEmpty());
    }

    @Test
    @TestAuthor(githubId = "GZeed")
    public void theServerShouldCountStudentsAndClearAllStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.client;
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("olivier");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());

    }
}