package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import ch.heigvd.schoolpulse.TestAuthor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author David Jaquet
 * @author Vincent Guidoux
 *
 * @remark We don't test the command tested in the version 1
 */
public class RouletteV2NortalleTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "Nortalle")
    public void theServerShouldBeAbleToClearTheStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        roulettePair.getClient().loadStudent("David Jaquet");

        client.clearDataStore();

        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "Nortalle")
    public void theServerShouldBeAbleToListTheStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        // To be sure to not have data of previous tests
        client.clearDataStore();

        List<Student> studentsSended = new ArrayList<>();
        studentsSended.add(new Student("Mar Labie"));
        studentsSended.add(new Student("David Jaquet"));
        studentsSended.add(new Student("Samuel Mayor"));
        studentsSended.add(new Student("Vincent Guidoux"));
        studentsSended.add(new Student("Guillaume Hochet"));
        client.loadStudents(studentsSended);

        List<Student> studentsReceived = client.listStudents();

        assertEquals(studentsReceived.size(), studentsSended.size());

        for(Student student : studentsReceived)
            assertNotEquals(-1, studentsSended.indexOf(student));
    }

    @Test
    @TestAuthor(githubId = "Nortalle")
    public void theServerShouldReturnTheNumberOfNewStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        client.loadStudent("Julien Biefer");
        client.loadStudent("Johanna Melly");

        assertEquals(2, client.getNumberOfStudentAdded());
    }

    @Test
    @TestAuthor(githubId = "Nortalle")
    public void theServerShouldReturnTheNumberOfCommandsSent() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        client.loadStudent("Yann Lederrey");
        client.loadStudent("Leo Cortes");
        client.getNumberOfStudents();
        client.getProtocolVersion();

        assertEquals(4, client.getNumberOfCommands());
    }
}