package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
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
 * @author Lederrey Yann, Schar Joel
 */
public class RouletteV2YannledTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "wasadigi")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = "wasadigi")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "joelschar")
    public void theServerShouldBeAbleToClearDataStore() throws IOException {

    }

    @Test
    @TestAuthor(githubId = "joelschar")
    public void theServerShouldBeAbleToListStoredStudents() throws IOException {

    }

    @Test
    @TestAuthor(githubId = "joelschar")
    public void theServerShouldInformAboutTheNumberOfNewStudentsAsResponseToLoad() throws IOException {

    }

    @Test
    @TestAuthor(githubId = "joelschar")
    public void theServerShouldReturnTheNumberOfUsedCommandsAsResponseToBye() throws IOException {

    }


}