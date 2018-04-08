package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.schoolpulse.TestAuthor;
import static org.junit.Assert.*;
import org.junit.Test;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.LinkedList;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 */
public class RouletteV2ManalitoTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = {"manalito", "nfluckiger"})
    public void ClearShouldEraseAllStudents() throws IOException{

        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        client.loadStudent("Fluckiger");
        client.loadStudent("Siu");
        client.loadStudent("LastOfTheStudents");

        assertEquals(3, client.getNumberOfStudents());

        client.clearDataStore();

        assertTrue(client.listStudents().isEmpty());

    }
    
    @Test
    @TestAuthor(githubId = {"manalito", "nfluckiger"})
    public void ServerShouldGiveRightVersion() throws IOException{
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = {"manalito", "nfluckiger"})
    public void ServersShouldReturnTheRightListOfStudents() throws  IOException{

        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        LinkedList<Student> students = new LinkedList<>();

        students.add(new Student("Nathan"));
        students.add(new Student("Aurelien"));
        students.add(new Student("TheLastOfTheStudents"));


        client.loadStudents(students);

        assertEquals(students, client.listStudents());
        assertEquals(students.size(), client.getNumberOfStudents());
    }
    
    @Test
    @TestAuthor(githubId = {"manalito", "nfluckiger"})
    public void ServersShouldReturnNumberOfStudentAdded() throws  IOException{

        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        LinkedList<Student> students = new LinkedList<>();

        students.add(new Student("Nathan"));
        students.add(new Student("Aurelien"));
        students.add(new Student("TheLastOfTheStudents"));
        client.loadStudents(students);
    
        int numberOfNewStuddents = client.getNumberOfStudentAdded();
        
        assertEquals(3, numberOfNewStuddents);
    }
    
    @Test
    @TestAuthor(githubId = {"manalito", "nfluckiger"})
    public void ServersShouldReturnNumberOfCommandsTyped() throws  IOException{
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        client.loadStudent("Nathan");
        client.getProtocolVersion();
        client.getNumberOfStudents();
        
        assertEquals(3, client.getNumberOfCommands());
    }
    
    



}