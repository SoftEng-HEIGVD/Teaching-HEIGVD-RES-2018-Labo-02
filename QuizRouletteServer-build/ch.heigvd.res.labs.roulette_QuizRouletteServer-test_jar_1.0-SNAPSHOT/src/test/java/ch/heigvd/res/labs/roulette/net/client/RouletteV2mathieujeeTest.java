package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Mathieu Jee
 * @author Lionel Burgbacher
 *
 */
public class RouletteV2mathieujeeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "mathieujee")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "mathieujee")
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "mathieujee")
    public void theServerShouldHaveZeroStudentsAfterClear() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        client.loadStudent("student n1");
        client.loadStudent("student n2");
        client.loadStudent("student n3");
        assertEquals(3, client.getNumberOfStudents());
        client.clearDataStore();
        assertTrue(client.listStudents().isEmpty());
    }

    @Test
    @TestAuthor(githubId = "mathieujee")
    public void ClearAnEmptyServerShouldNotProvokeAnError() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        assertEquals(0, client.getNumberOfStudents());
        client.clearDataStore();
        assertTrue(client.listStudents().isEmpty());
    }

    @Test
    @TestAuthor(githubId = "mathieujee")
    public void theServerShouldReturnTheCorrectListOfStudentsAfterList() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        client.loadStudent("student n1");
        client.loadStudent("student n2");
        client.loadStudent("student n3");
        assertEquals(3, client.getNumberOfStudents());
        List<Student> students = client.listStudents();
        assertEquals("student n1", students.get(0).getFullname());
        assertEquals("student n2", students.get(1).getFullname());
        assertEquals("student n3", students.get(2).getFullname());
    }

}
