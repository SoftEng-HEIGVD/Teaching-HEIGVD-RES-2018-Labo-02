package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Bryan Curchod, François Burgener
 */
public class RouletteV2francoisburgenerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    
    /*The same tests as the v1 but adapt for v2*/
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

    @Test
    @TestAuthor(githubId = {"wasadigi", "francoisburgener", "BryCur"})
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "francoisburgener", "BryCur"})
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();
        assertEquals(RouletteV2Protocol.VERSION, client.getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "francoisburgener", "BryCur"})
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
    @TestAuthor(githubId = {"SoftEng-HEIGVD", "francoisburgener", "BryCur"})
    public void theServerShouldCountStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("François");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("Bryan");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("Olivier");
        assertEquals(3, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"francoisburgener", "BryCur"})
    public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        exception.expect(EmptyStoreException.class);
        client.pickRandomStudent();
    }

    /*The beginning of the tests of the v2*/
    @Test
    @TestAuthor(githubId = {"francoisburgener", "BryCur"})
    public void theServerShouldClearData() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("François");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("Bryan");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("Olivier");
        assertEquals(3, client.getNumberOfStudents());

        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"francoisburgener", "BryCur"})
    public void theServerShouldListData() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("François");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("Bryan");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("Olivier");
        assertEquals(3, client.getNumberOfStudents());

        List<Student> students = client.listStudents();
        assertEquals("Bryan", students.get(1).getFullname());
        assertEquals("Olivier", students.get(2).getFullname());
    }

    @Test
    @TestAuthor(githubId = {"francoisburgener", "BryCur"})
    public void theDefaultPortShouldBe2613() throws IOException {
        assertEquals(RouletteV2Protocol.DEFAULT_PORT, 2613);
    }

    @Test
    @TestAuthor(githubId = {"francoisburgener", "BryCur"})
    public void theServerShouldReturnTheNumberOfCommand() throws IOException {
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();
        client.loadStudent("Romain");
        client.listStudents();
        client.disconnect();
        
        int numberOfCommands = client.getNumberOfCommands();
        assertEquals(3, numberOfCommands);

    }

    @Test
    @TestAuthor(githubId = {"francoisburgener", "BryCur"})
    public void theServerShouldReturnTheNumberOfAddedStudents() throws IOException {
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();
        client.loadStudent("test");
        assertEquals(1, client.getNumberOfStudentAdded());
        LinkedList<Student> students = new LinkedList<>();
        students.add(new Student("Romain"));
        students.add(new Student("Joel"));
        client.loadStudents(students);
        assertEquals(2, client.getNumberOfStudentAdded());
    }

}
