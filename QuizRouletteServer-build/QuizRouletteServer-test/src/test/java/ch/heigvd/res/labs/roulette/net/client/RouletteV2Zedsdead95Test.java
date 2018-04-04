package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import ch.heigvd.res.labs.roulette.data.*;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 * @author Walid Koubaa
 *
 */
public class RouletteV2Zedsdead95Test {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = {"wasadigi","zedsdead95"})
    public void testThatTheTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = {"wasadigi","zedsdead95"})
    public void testThatTheTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = "zedsdead95")
    public void testThatItShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertFalse(client.isConnected());
        client.connect("4242", port);
        assertTrue(client.isConnected());
    }

    /*@Test
    @TestAuthor(githubId = "zedsdead95")
    public void testThatTheServerShouldBeAbleToReturnTheListOfStudent() throws IOException{
             roulettePair.getClient().loadStudent("zedsdead");
             roulettePair.getClient().loadStudent("asterix");
             roulettePair.getClient().loadStudent("obelix");
             assertEquals("zedsdead", roulettePair.getClient().getStudentList().get(0).getFullName());
             assertEquals("asterix", roulettePair.getClient().getStudentList().get(1).getFullName());
             assertEquals("obelix", roulettePair.getClient().getStudentList().get(2).getFullName());
    }*/

    @Test
    @TestAuthor(githubId = {"zedsdead95"})
    public void testThatTheServerShouldStillHaveZeroStudentsAtStart() throws IOException {
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "zedsdead95")
    public void testThatTheServerShouldCountStudentsCorrectly() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        roulettePair.getClient().loadStudent("zedsdead");
        roulettePair.getClient().loadStudent("asterix");
        roulettePair.getClient().loadStudent("obelix");
        assertEquals(3, client.getNumberOfStudents());
        /*roulettePair.getClient().clearAllData();
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());*/
    }


}