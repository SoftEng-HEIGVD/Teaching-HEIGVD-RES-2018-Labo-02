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
import static org.junit.Assert.assertTrue;

public class RouletteV2LionelNanchenTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

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

    @Test
    @TestAuthor(githubId = "LionelNanchen")
    public void theServerShouldCountAllCommmands() throws IOException {
        Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

        bufferedReader.readLine();
        printWriter.println(RouletteV2Protocol.CMD_LOAD);
        bufferedReader.readLine();
        printWriter.println("sacha");
        printWriter.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        bufferedReader.readLine();
        printWriter.println(RouletteV2Protocol.CMD_RANDOM);
        bufferedReader.readLine();
        printWriter.println(RouletteV2Protocol.CMD_INFO);
        bufferedReader.readLine();
        printWriter.println(RouletteV2Protocol.CMD_BYE);

        String status = bufferedReader.readLine();
        boolean b = false;
        if (status.contains("4")) {
            b = true;
        }
        assertTrue(b);
    }

}
