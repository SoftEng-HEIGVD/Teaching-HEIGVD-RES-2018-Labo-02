package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class RouletteV2SmithHeigTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "smithheig")
    public void theServerShouldHaveZeroStudentAfterClear() throws IOException {
        roulettePair.getClient().loadStudent("bob");
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.clearDataStore(); // test clear data
        assertEquals(0,roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "smithheig")
    public void testClientReceivedCorrectListOfStudentFromServer() throws IOException {
        roulettePair.getClient().loadStudent("bob");
        roulettePair.getClient().loadStudent("john");
        Student bob = new Student("bob");
        Student john = new Student("john");

        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        List<Student> students = client.listStudents();

        assertTrue(students.contains(bob));
        assertTrue(students.contains(john));
        assertEquals(students.size(),2);
    }

}
