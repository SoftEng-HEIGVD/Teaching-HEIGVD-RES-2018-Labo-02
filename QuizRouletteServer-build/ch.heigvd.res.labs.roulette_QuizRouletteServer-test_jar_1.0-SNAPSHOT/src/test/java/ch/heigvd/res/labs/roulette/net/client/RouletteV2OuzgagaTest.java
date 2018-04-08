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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class contains automated tests to validate the client and the server implementation of the Roulette Protocol (version 2)
 *
 * @author Rochat Antoine & Schopfer Benoit
 */
public class RouletteV2OuzgagaTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Rule
	public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
	
	@Test
	@TestAuthor(githubId = {"Nooka10", "ouzgaga"})
	public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
		assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
	}
	
	@Test
	@TestAuthor(githubId = {"Nooka10", "ouzgaga"})
	public void theServerShouldHaveZeroStudentsAfterClear() throws IOException {
		IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
		client.loadStudent("benoit");
		client.loadStudent("antoine");
		client.clearDataStore();
		assertEquals(0, client.getNumberOfStudents());
	}
	
	
	@Test
	@TestAuthor(githubId = {"Nooka10", "ouzgaga"})
	public void theServerShouldSendTheCorrectResponseWhenLoadListAndByeAreCalled() throws IOException {
		
		Socket client = new Socket("localhost", roulettePair.getServer().getPort());
		
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		PrintWriter out = new PrintWriter(client.getOutputStream());
		
		String responsesFromServer;
		
		in.readLine(); // welcome message from the server
		
		out.println("LOAD");
		out.flush();
		in.readLine(); // Send your data [end with ENDOFDATA]
		
		out.println("john doe");
		out.flush();
		
		out.println("bill smith");
		out.flush();
		
		out.println("ENDOFDATA");
		out.flush();
		responsesFromServer = in.readLine(); // check the load command response from the server
		assertEquals("{\"status\":\"success\",\"numberOfNewStudents\":2}", responsesFromServer);
		
		out.println("LIST");
		out.flush();
		responsesFromServer = in.readLine(); // check the list commmand response from the server
		assertEquals("{\"students\":[{\"fullname\":\"john doe\"},{\"fullname\":\"bill smith\"}]}", responsesFromServer);
		
		out.println("BYE");
		out.flush();
		responsesFromServer = in.readLine(); // check the bye command response from the server
		assertEquals("{\"status\":\"success\",\"numberOfCommands\":3}", responsesFromServer);
	}
	
	
	@Test
	@TestAuthor(githubId = {"Nooka10", "ouzgaga"})
	public void theServerShouldSendTheCorrectNumberOfNewStudentsAdded() throws IOException {
		
		IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
		
		List<Student> serverStudents = new ArrayList<>();
		
		serverStudents.add(new Student("Antoine Rochat"));
		serverStudents.add(new Student("Benoit Schopfer"));
		
		client.loadStudents(serverStudents);
		
		assertEquals(2, client.getNumberOfStudentAdded());
		
		client.loadStudent("Chuck Norris");
		
		assertEquals(1, client.getNumberOfStudentAdded());
	}
	
	@Test
	@TestAuthor(githubId = {"Nooka10", "ouzgaga"})
	public void theServerShouldSendTheCorrectNumberOfCommands() throws IOException, EmptyStoreException {
		
		IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
		
		client.loadStudent("Chuck Norris");                     // command #1
		
		List<Student> serverStudents = new ArrayList<>();
		
		serverStudents.add(new Student("Antoine Rochat"));
		serverStudents.add(new Student("Benoit Schopfer"));
		
		client.loadStudents(serverStudents);                            // command #2
		
		client.pickRandomStudent();                                     // command #3
		
		client.getProtocolVersion();                                    // command #4
		
		client.getNumberOfStudents();                                   // command #5
		
		client.pickRandomStudent();                                     // command #6
		
		client.listStudents();                                          // command #7
		
		client.clearDataStore();                                        // command #8
		
		client.listStudents();                                          // command #9
		
		client.disconnect();                                            // command #10
		
		assertEquals(10, client.getNumberOfCommands());
		
	}
}
