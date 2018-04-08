package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RouletteV2JokauTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "wasadigi")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "wasadigi")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    // Adapted from wasadigi
    @Test
    @TestAuthor(githubId = "jokau, loic-schurch")
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV1Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "jokau, loic-schurch")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }


    @Test
    @TestAuthor(githubId = "jokau, loic-schurch")
    public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "SoftEng-HEIGVD"})
    public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "wasadigi, jokau, loic-schurch")
    public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        exception.expect(EmptyStoreException.class);
        client.pickRandomStudent();
    }

    @Test
    @TestAuthor(githubId = "SoftEng-HEIGVD")
    public void theServerShouldCountStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("olivier");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());
    }

    // new V2 specific tests
    @Test
    @TestAuthor(githubId = "jokau, loic-schurch")
    public void theServerShouldAddStudentsInList() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        ArrayList<Student> a = new ArrayList<>();
        a.add(new Student("a"));
        a.add(new Student("b"));
        a.add(new Student("c"));
        client.loadStudents(a);
        assertEquals(3, client.getNumberOfStudents());
    }
    @Test
    @TestAuthor(githubId = "jokau, loic-schurch")
    public void theServerShouldClearDataStore() throws IOException{
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();;
        client.loadStudent("sacha");
        client.loadStudent("olivier");
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "jokau, loic-schurch")
    public void theServerShouldListStudents()  throws IOException{
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("sacha");
        client.loadStudent("olivier");
        client.loadStudent("fabienne");
        List<Student> student = client.listStudents();
        assertEquals("sacha", student.get(0).getFullname());
        assertEquals("olivier", student.get(1).getFullname());
        assertEquals("fabienne", student.get(2).getFullname());
    }

    @Test
    @TestAuthor(githubId = "jokau, loic-schurch")
    public void theServerShouldCountStudentsAdded() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        ArrayList<Student> a = new ArrayList<>();
        a.add(new Student("a"));
        a.add(new Student("b"));
        a.add(new Student("c"));
        client.loadStudents(a);
        assertEquals(3, client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
        assertEquals(3, client.getNumberOfStudentAdded());
    }

    @Test
    @TestAuthor(githubId = "jokau, loic-schurch")
    public void theServerShouldCountTheNumberOfCommands() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("a");
        client.listStudents();
        client.clearDataStore();
        assertEquals(3, client.getNumberOfCommands());
    }
}