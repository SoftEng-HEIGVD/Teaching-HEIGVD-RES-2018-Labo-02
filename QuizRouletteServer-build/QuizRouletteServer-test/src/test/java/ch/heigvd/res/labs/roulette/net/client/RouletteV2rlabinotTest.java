package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.client.IRouletteV2Client;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Labinot Rashiti
 * @author Romain Gallay
 */

public class RouletteV2rlabinotTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "romaingallay")
    public void theServerShouldReturnCorrectProtocolVersion() throws IOException {
        String version = roulettePair.getClient().getProtocolVersion();
        assertEquals("2.0", version);
    }

    @Test
    @TestAuthor(githubId = "romaingallay")
    public void theServerShouldHaveZeroStudentAfterClear() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        client.loadStudent("Pierre");
        client.loadStudent("Paul");
        client.loadStudent("Jacques");

        assertEquals(3, client.getNumberOfStudents());

        client.clearDataStore();

        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "romaingallay")
    public void theServerShouldReturnTheStudentList() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        String pierre = "Pierre";
        String paul = "Paul";

        client.loadStudent(pierre);
        client.loadStudent(paul);

        assertEquals(pierre, client.listStudents().get(0).getFullname());
        assertEquals(pierre, client.listStudents().get(1).getFullname());

    }


}
