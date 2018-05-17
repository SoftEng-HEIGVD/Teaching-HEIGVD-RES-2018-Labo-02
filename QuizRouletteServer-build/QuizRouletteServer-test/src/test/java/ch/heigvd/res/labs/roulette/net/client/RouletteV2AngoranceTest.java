package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 *
 * @modifiedBy Daniel Gonzalez Lopez, Héléna Line Reymond
 */
public class RouletteV2AngoranceTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
    
    @Test
    @TestAuthor(githubId = {"Angorance", "LNAline"})
    public void theServerShouldClearStudents() throws IOException {
    
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        
        // Add students on the server
        List<Student> serverStudents = new ArrayList<>();
        
        serverStudents.add(new Student("Sacha"));
        serverStudents.add(new Student("Olivier"));
        serverStudents.add(new Student("Fabienne"));
        
        client.loadStudents(serverStudents);
        
        // Clear the list of students from the client
        assertEquals(serverStudents.size(), client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }
    
    @Test
    @TestAuthor(githubId = {"Angorance", "LNAline"})
    public void theServerShouldListStudents() throws IOException {
    
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        
        // Add students on the server
        List<Student> serverStudents = new ArrayList<>();
        
        serverStudents.add(new Student("Sacha"));
        serverStudents.add(new Student("Olivier"));
        serverStudents.add(new Student("Fabienne"));
    
        client.loadStudents(serverStudents);
        
        // Get the list of students from the client
        List<Student> clientStudents = client.listStudents();
        
        // Check if the list of student is the same on client and server
        assertEquals(serverStudents.size(), client.getNumberOfStudents());
        
        assertTrue(clientStudents.containsAll(serverStudents));
    }
    
    @Test
    @TestAuthor(githubId = {"Angorance", "LNAline"})
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }
    
    @Test
    @TestAuthor(githubId = {"Angorance", "LNAline"})
    public void theServerShouldReturnTheCorrectNumberOfNewStudents() throws IOException {
    
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    
        // Add students on the server
        List<Student> serverStudents = new ArrayList<>();
    
        serverStudents.add(new Student("Sacha"));
        serverStudents.add(new Student("Olivier"));
        serverStudents.add(new Student("Fabienne"));
    
        client.loadStudents(serverStudents);
    
        assertEquals(3, client.getNumberOfStudentAdded());
        
        client.loadStudent("Lastone");
    
        assertEquals(1, client.getNumberOfStudentAdded());
    }
    
    @Test
    @TestAuthor(githubId = {"Angorance", "LNAline"})
    public void theServerShouldReturnTheCorrectNumberOfCommandsExecuted() throws IOException, EmptyStoreException {
    
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        
        client.getProtocolVersion();                                    // 1
    
        // Add students on the server
        List<Student> serverStudents = new ArrayList<>();
    
        serverStudents.add(new Student("Sacha"));
        serverStudents.add(new Student("Olivier"));
        serverStudents.add(new Student("Fabienne"));
    
        client.loadStudents(serverStudents);                            // 2
        
        System.out.println("plop " + client.getNumberOfStudents());                                   // 3
        
        client.pickRandomStudent();                                     // 4
        
        client.listStudents();                                          // 5
        
        client.clearDataStore();                                        // 6
        
        client.disconnect();                                            // 7
        
        assertEquals(7, client.getNumberOfCommands());
    }
    
    @Test
    @TestAuthor(githubId = {"Angorance", "LNAline"})
    public void theServerShouldReturnTheStatusOfTheExecutedCommand() throws IOException {
    
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        
        client.loadStudent("Hilarb");
        
        assertTrue(client.checkSuccessOfCommand());
    }
}
