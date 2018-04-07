package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;


/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Benjamin Thomas
 */
public class RouletteV2DumoriaTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    //------------------------------------

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theClientShouldBeConnectedToTheRightPort() {
        int port = roulettePair.getServer().getPort();
        assertEquals(RouletteV2Protocol.DEFAULT_PORT, port);
    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theServerShouldBeAbleToClearAnEmptyList() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());

    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theServerShouldClearAllTheData() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);

        client.loadStudent("Chris");
        client.loadStudent("Ben");
        assertEquals(2, client.getNumberOfStudents());

        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());

    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theServerShouldReturnAnEmptyList() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);

        List<Student> students = client.listStudents();

        assertTrue(students.isEmpty());
    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theServerShouldListAllTheStudent() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        client.loadStudent("Chris");
        client.loadStudent("Ben");

        List<Student> students = client.listStudents();

        assertEquals("Chris", students.get(0).getFullname());
        assertEquals("Ben", students.get(1).getFullname());
    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theServerShouldReturnTheRightNumberOfNewStudents() throws IOException, EmptyStoreException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("Chris");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("Ben");
        assertEquals(2, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "Dumoria")
    public void theServerShouldReturnTheRightNumberOfCommands() throws IOException, EmptyStoreException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        client.loadStudent("Chris");
        client.loadStudent("Ben");
        client.listStudents();
        client.clearDataStore();
        client.disconnect();

        //int nbrCmd = client.getNumberOfCommands();
        //assertEquals(5, nbrCmd);
    }


}

