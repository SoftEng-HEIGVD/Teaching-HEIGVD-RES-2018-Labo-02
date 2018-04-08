package ch.heigvd.res.labs.roulette.net.client;

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
 * @author Adam Zouari
 * @author Nair Alic
 */
public class RouletteV2AdamZouariTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "AdamZouari")
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = "AdamZouari")
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }
  
  @Test
  @TestAuthor(githubId = "AdamZouari")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "AdamZouari")
  public void theServerShouldHaveZeroStudentsAfterClear() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    client.loadStudent("Adam");
    client.loadStudent("Nair");
    client.clearDataStore();
    int numberOfStudents = client.getNumberOfStudents();
    assertEquals(0, numberOfStudents);
  }

  @Test
  @TestAuthor(githubId = "AdamZouari")
  public void theServerShouldStillHaveZeroStudentsAfterClear() throws IOException {
    assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }


  @Test
  @TestAuthor(githubId = "AdamZouari")
  public void theServerShouldListStudents() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    
    Student adam = new Student("Adam");
    Student nair = new  Student ("Nair");
    Student olivier = new  Student ("Olivier");
    
    client.loadStudent("Adam");
    client.loadStudent("Nair");
    
    List<Student> expectedList = new ArrayList<> ();
    expectedList.add(adam);
    expectedList.add(nair);
            
    assertEquals(expectedList, client.listStudents());
      
    expectedList.add(olivier);
    
    assertNotEquals(expectedList, client.listStudents());
    
  }

}
