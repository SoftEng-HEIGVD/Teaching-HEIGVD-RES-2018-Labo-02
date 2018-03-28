package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RouletteV2LionelNanchenTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "SoftEng-HEIGVD")
    public void theServerShouldCountStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("olivier");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        exception.expect(EmptyStoreException.class);
        client.pickRandomStudent();
    }

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theServerShouldBeEmptyAfterClearCommand() throws IOException, EmptyStoreException{
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("sacha");
        client.loadStudent("olivier");
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theServerShouldFetchTheListOfStudents() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        LinkedList<Student> students = new LinkedList<>();
        students.add(new Student("sacha"));
        students.add(new Student("olivier"));
        students.add(new Student("fabienne"));
        client.loadStudents(students);
        assertEquals(students, client.listStudents());
    }

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theServerShouldCountNumberOfStudentCreated() throws IOException {
        Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

        bufferedReader.readLine();
        printWriter.println(RouletteV2Protocol.CMD_LOAD);
        bufferedReader.readLine();
        printWriter.println("sacha");
        printWriter.println("olivier");
        printWriter.println("fabienne");
        printWriter.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        String status = bufferedReader.readLine();
        boolean b = false;
        if (status.contains("3")) {
            b = true;
        }
        assertTrue(b);
    }
}
