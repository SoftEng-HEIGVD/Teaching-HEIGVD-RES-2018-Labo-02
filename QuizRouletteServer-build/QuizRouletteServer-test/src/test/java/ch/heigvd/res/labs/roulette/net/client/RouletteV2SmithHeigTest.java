package ch.heigvd.res.labs.roulette.net.client;


import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.*;

public class RouletteV2SmithHeigTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    /**
     * When the server clear, should have 0 students after that
     * @throws IOException
     */
    @Test
    @TestAuthor(githubId = "smithheig")
    public void theServerShouldHaveZeroStudentAfterClear() throws IOException {
        roulettePair.getClient().loadStudent("bob");
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.clearDataStore(); // test clear data
        assertEquals(0,roulettePair.getClient().getNumberOfStudents());
    }

    /**
     * Test if the client revieved the list of the current students on the server
     * @throws IOException
     */
    @Test
    @TestAuthor(githubId = "smithheig")
    public void testClientReceivedCorrectListOfStudentFromServer() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("bob");
        client.loadStudent("john");

        assertTrue(client.listStudents().contains(new Student("bob")));
        assertTrue(client.listStudents().contains(new Student("john")));
    }

    /**
     * Test if the server give you a Bye message correct
     * @throws IOException
     */
    @Test
    @TestAuthor(githubId = "smithheig")
    public void testResponseFromServerWhenExitWithBye() throws IOException{
        Socket client = new Socket("localhost",roulettePair.getServer().getPort());
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());

        in.readLine();

        out.println("BYE");
        out.flush();

        String s = in.readLine();
        assertEquals(s, "{\"status\":\"success\",\"numberOfCommands\":1}");
    }

}
