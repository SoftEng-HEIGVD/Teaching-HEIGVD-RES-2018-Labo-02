package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Christophe Joyet
 */
public class RouletteV2christophejoyetTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    //tests from RouletteV1WasadigiTest not necessary
    /*
    @Test
    @TestAuthor(githubId = "christophe-joyet")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }
    @Test
    @TestAuthor(githubId = "christophe-joyet")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }
    @Test
    @TestAuthor(githubId = "christophe-joyet")
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }
    @Test
    @TestAuthor(githubId = "christophe-joyet")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV1Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }
    @Test
    @TestAuthor(githubId = "christophe-joyet")
    public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }
    @Test
    @TestAuthor(githubId = {"christophe-joyet", "SoftEng-HEIGVD"})
    public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }
    @Test
    @TestAuthor(githubId = "christophe-joyet")
    public void theNumberOfStudentSentByTheClient() throws IOException{
        IRouletteV2Client client;
    }*/

    @Test
    @TestAuthor(githubId = "christophe-joyet")
    public void serverMustBeClearedAfterCleared() throws IOException {
        IRouletteV2Client client;
        client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("olivier");
        client.loadStudent("ambert");
        client.loadStudent("nicole");
        client.loadStudent("georges");
        client.clearDataStore();
        assertTrue(client.getNumberOfStudents() == 0);

    }

    @Test
    @TestAuthor(githubId = "christophe-joyet")
    public void numberOfTheStudentLinesSentByTheClientWithLoadCommand() throws IOException {
        Socket clientSocket;
        clientSocket = new Socket("localhost", roulettePair.server.getPort());
        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        br.readLine();
        pw.println(RouletteV2Protocol.CMD_LOAD);
        pw.flush();
        pw.println("olivier");
        pw.println("ambert");
        pw.println("nicole");
        pw.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        pw.flush();

        br.readLine(); //to read the end of the data answer
        String numberOfLines = br.readLine();
        assertEquals(numberOfLines, "{\"status\":\"success\",\"numberOfNewStudents\":3}");


    }

    @Test
    @TestAuthor(githubId = "christophe-joyet")
    public void listOfStudentShouldBeCorrect() throws IOException {
        IRouletteV2Client client;
        client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("olivier");
        client.loadStudent("ambert");
        LinkedList<Student> listOfStudent = new LinkedList<Student>();
        listOfStudent.add(new Student("olivier"));
        listOfStudent.add(new Student("ambert"));
        assertEquals(listOfStudent, client.listStudents());
    }


}