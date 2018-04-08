package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Julien Biefer (Amadeous) et Léo Cortès (Schnoudli)
 */
public class RouletteV2AmadeousTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "amadeous")
    public void sendingClearCommandShouldClearTheServer() throws IOException {
        // As IRouletteV2Client extedns IRouletteV1Client, we can simply cast it
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("Test");
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "amadeous")
    public void askingListOfStudentShouldReturnACorrectList() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("Test1");
        client.loadStudent("Test2");
        client.loadStudent("Test3");

        assertEquals("Test1", client.listStudents().get(0).getFullname());
        assertEquals("Test2", client.listStudents().get(1).getFullname());
        assertEquals("Test3", client.listStudents().get(2).getFullname());
    }

    @Test
    @TestAuthor(githubId = "amadeous")
    public void askingListOfStudentWhenThereIsNoStudentShouldReturnAnEmptyList() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        assertEquals(0, client.listStudents().size());
    }

    @Test
    @TestAuthor(githubId = "amadeous")
    public void loadingAStudentShouldBeSuccessful() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        int actualNb = client.getNumberOfStudents();
        client.loadStudent("Test");

        assertEquals("Test", client.listStudents().get(actualNb).getFullname());
    }

    @Test
    @TestAuthor(githubId = "amadeous")
    public void loadingAStudentShouldIncreaseTheNumberOfStudentStored() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        int beforeLoad = client.getNumberOfStudents();
        client.loadStudent("Test");
        int afterLoad = client.getNumberOfStudents();
        assertTrue(beforeLoad < afterLoad);
    }

    @Test
    @TestAuthor(githubId = "amadeous")
    public void sendingByeShouldReturnTheCorrectNumberOfSendCommands() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.getNumberOfStudents();
        client.getProtocolVersion();
        boolean exceptionThrown = false;
        try {
            client.pickRandomStudent();
        } catch (EmptyStoreException e) {
            exceptionThrown = true;
        }

        client.disconnect();

        assertTrue(exceptionThrown);
        // Uses the additional method getNumberOfCommands as suggested in the RES 2018 - Annonces Telegram Channel
        assertEquals(4, client.getNumberOfCommands());
    }

    @Test
    @TestAuthor(githubId = "amadeous")
    // This is an adaptation of the same test for v1
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    /*
    @Test
    @TestAuthor(githubId = "amadeous")
    public void theServerShouldReturnTheCorrectDefaultPort() throws IOException {
    // Not for the ephemeral pair!!
        assertEquals(roulettePair.getServer().getPort(), RouletteV2Protocol.DEFAULT_PORT);
    }
    */

}
