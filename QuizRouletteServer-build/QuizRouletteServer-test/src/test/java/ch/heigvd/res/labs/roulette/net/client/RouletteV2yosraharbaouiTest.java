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
 * @author Yosra Harbaoui
 */
public class RouletteV2yosraharbaouiTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
    
    @Test
    @TestAuthor(githubId = "yosra-harbaoui")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }
  
    @Test
    @TestAuthor(githubId = "yosra-harbaoui")
    public void theServerListSouldBeEmptyAtSetup() throws IOException {
        IRouletteV2Client clientV2 = (IRouletteV2Client) roulettePair.getClient();
        assertTrue(clientV2.listStudents().isEmpty());
    }
       
    @Test
    @TestAuthor(githubId = "yosra-harbaoui")
    public void theServerShouldGetTheNumberOfStudentsInTheStore() throws IOException {
        IRouletteV2Client clientV2 = (IRouletteV2Client) roulettePair.getClient();
        clientV2.loadStudent("Yosra");
        clientV2.loadStudent("Olivier");
        clientV2.loadStudent("Miguel");
        assertEquals(3 , clientV2.getNumberOfStudents());
    }  
    
    @Test
    @TestAuthor(githubId = "yosra-harbaoui")
    public void theServerListShouldBeEmptyAfterClearDataStore() throws IOException {
        IRouletteV2Client clientV2 = (IRouletteV2Client) roulettePair.getClient();
        clientV2.loadStudent("Yosra");
        clientV2.loadStudent("Olivier");
        clientV2.loadStudent("Miguel");
        assertEquals(3, clientV2.getNumberOfStudents());
        clientV2.clearDataStore();
        assertEquals(0, clientV2.getNumberOfStudents());

    }
}
