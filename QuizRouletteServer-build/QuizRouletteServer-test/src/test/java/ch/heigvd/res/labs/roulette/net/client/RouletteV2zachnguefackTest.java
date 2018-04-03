package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * @author zacharie
 * @author Lankeu
 */
public class RouletteV2zachnguefackTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = {"zachnguefack", "Lankeu"})
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "zachnguefack")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = {"zachnguefack", "Lankeu"})
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = {"zachnguefack", "Lankeu"})
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = {"zachnguefack", "Lankeu"})
    public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }

    @Test
    @TestAuthor(githubId = {"zachnguefack", "Lankeu"})
    public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"zachnguefack", "Lankeu"})
    public void theServerShouldCountStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("Zacharie");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("Cedric");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("Richard");
        assertEquals(3, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"zachnguefack", "Lankeu"})
    public void theShouldNothaveAnyStudentsStoreAfterClearCommand() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("Zacharie");
        client.loadStudent("Cedric");
        client.loadStudent("Richard");
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"zachnguefack", "Lankeu"})
    public void theClientMustConnectToTheRightServer() {
        RouletteV2ClientImpl client = new RouletteV2ClientImpl();
        try {
            client.connect("HackRes2018.ch", roulettePair.server.getPort());
        } catch (IOException ex) {
            Logger.getLogger(RouletteV2zachnguefackTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        assertFalse(client.isConnected());

    }

}
