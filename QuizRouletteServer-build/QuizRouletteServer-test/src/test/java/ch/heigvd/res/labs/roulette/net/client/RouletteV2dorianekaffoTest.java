package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Doriane Kaffo
 */
public class RouletteV2dorianekaffoTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    /*
    @Test
    @TestAuthor(githubId = "dorianekaffo")
    public void theServerShouldListenToTheCorrectPort() {
        assertEquals(RouletteV2Protocol.DEFAULT_PORT, roulettePair.getServer().getPort());
    }
    */

    @Test
    @TestAuthor(githubId = "dorianekaffo")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "dorianekaffo")
    public void theServerShouldNotHaveStudentsAfterClear() throws IOException {
        
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("dodo");
        client.loadStudent("doriane");
        client.loadStudent("kaffo");
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "dorianekaffo")
    public void ClientV2ShouldConnectToRouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
        client.disconnect();
    }
    
    @Test
    @TestAuthor(githubId = "dorianekaffo")
    public void theServerShouldReturnTheGoodNumberOfVersion() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

}
