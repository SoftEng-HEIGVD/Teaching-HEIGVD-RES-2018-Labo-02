package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        
        // Create the client from the information of the server
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        
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
        
        // Create the client from the informations of the server
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        
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
        
        // Create the client from the informations of the server
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
    
        // Add students on the server
        List<Student> serverStudents = new ArrayList<>();
    
        serverStudents.add(new Student("Sacha"));
        serverStudents.add(new Student("Olivier"));
        serverStudents.add(new Student("Fabienne"));
    
        client.loadStudents(serverStudents);
    
        assertEquals(3, client.getNumberOfStudentsAdded());
        
        client.loadStudent("Lastone");
    
        assertEquals(1, client.getNumberOfStudentsAdded());
    }
    
    @Test
    @TestAuthor(githubId = {"Angorance", "LNAline"})
    public void theServerShouldReturnTheCorrectNumberOfCommandsExecuted() throws IOException, EmptyStoreException {
        
        // Create the client from the informations of the server
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        
        client.connect("localhost", port);                       // 0
        
        client.getProtocolVersion();                                    // 1
    
        // Add students on the server
        List<Student> serverStudents = new ArrayList<>();
    
        serverStudents.add(new Student("Sacha"));
        serverStudents.add(new Student("Olivier"));
        serverStudents.add(new Student("Fabienne"));
    
        client.loadStudents(serverStudents);                            // 2
        
        client.getNumberOfStudents();                                   // 3
        
        client.pickRandomStudent();                                     // 4
        
        client.listStudents();                                          // 5
        
        client.clearDataStore();                                        // 6
        
        client.disconnect();                                            // 7
        
        assertEquals(7, client.getNumberOfCommands());
    }
}
