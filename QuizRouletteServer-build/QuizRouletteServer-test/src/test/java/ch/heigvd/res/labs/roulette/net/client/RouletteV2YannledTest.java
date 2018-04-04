package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    @TestAuthor(githubId = "yannled")
    public void theServerShouldBeAbleToClearDataStore() throws IOException {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Olivier Liechti"));
        students.add(new Student("Olivier Kopp"));
        students.add(new Student("Olivier De Benoist"));
        roulettePair.getClient().loadStudents(students);
        assertEquals(3, roulettePair.getClient().getNumberOfStudents());
        ((IRouletteV2Client) roulettePair.getClient()).clearDataStore();
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "yannled")
    public void theServerShouldBeAbleToListStoredStudents() throws IOException {
        List<Student> students = new ArrayList<>();
        Student olivier1 = new Student("Olivier Liechti");
        Student olivier2 = new Student("Olivier Kopp");
        Student olivier3 = new Student("Olivier De Benoist");
        students.add(olivier1);
        students.add(olivier2);
        students.add(olivier3);
        roulettePair.getClient().loadStudents(students);
        assertEquals(3, roulettePair.getClient().getNumberOfStudents());
        List<Student> listedStudents = ((IRouletteV2Client) roulettePair.getClient()).listStudents();
        for (int i = 0; i < 3; ++i)
            assertEquals(students.get(i).getFullname(), listedStudents.get(i).getFullname());
    }

    @Test
    @TestAuthor(githubId = "yannled")
    public void theServerShouldKeepTheNumberOfUsedCommands() throws IOException, EmptyStoreException {
        roulettePair.getClient().loadStudent("Olivier Liechti");
        roulettePair.getClient().loadStudent("Olivier Koop");
        roulettePair.getClient().loadStudent("Olivier De Benoist");
        assertEquals(3, roulettePair.getClient().getNumberOfStudents());
        assertEquals(4, ((IRouletteV2Client) roulettePair.getClient()).getNumberOfCommands());
    }
}