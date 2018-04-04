package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ch.heigvd.res.labs.roulette.data.Student;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RouletteV2Pacco1217Test {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "Pacco1217")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException{
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "Pacco1217")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = "Pacco1217")
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "Pacco1217")
    public void itShouldBePossibleToResetTheDataOfAServer() throws IOException{
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("Antonio");
        client.loadStudent("Corentin");
        assertEquals(2, client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "Pacco1217")
    public void serverShouldReturnAList() throws IOException{
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("Antonio");
        client.loadStudent("Corentin");
        List<Student> students = client.listStudents();
        assertEquals("Antonio", students.get(0).getFullname());
        assertEquals("Corentin", students.get(1).getFullname());
    }
}
