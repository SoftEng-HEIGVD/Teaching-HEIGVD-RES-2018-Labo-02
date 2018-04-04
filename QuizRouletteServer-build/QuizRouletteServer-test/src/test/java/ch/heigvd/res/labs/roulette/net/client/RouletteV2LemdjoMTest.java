/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.io.IOException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author lemdjo Marie
 * @author Kengne francine
 */
public class RouletteV2LemdjoMTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
   
  @Test
  @TestAuthor(githubId = "LemdjoM")
  public void theClientShouldRunTheRightProtocolVersion() throws IOException {
    assertEquals(roulettePair.getClient().getProtocolVersion(),RouletteV2Protocol.VERSION);
  }
  
  @Test
  @TestAuthor(githubId = "LemdjoM")
  public void theClientShouldBeAbleToConnect() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost",roulettePair.getServer().getPort());
    assertTrue(client.isConnected());
  }
  
  @Test
  @TestAuthor(githubId = "LemdjoM")
  public void ClearStudentsStoreMeansThereIsNoMoreStudents() throws IOException{
     IRouletteV2Client client = (IRouletteV2Client) new RouletteV2ClientImpl();
     client.connect("localhost", roulettePair.getServer().getPort());
     client.loadStudent("francine kengne");
     client.clearDataStore();
     assertEquals(0, client.getNumberOfStudents());
     client.disconnect();
  }
  
  @Test
  @TestAuthor(githubId = "LemdjoM")
  public void theNumberOfStudentShouldBeCorrectAtTime() throws IOException{
     assertEquals(roulettePair.getClient().getNumberOfStudents(), 0);
      
     roulettePair.getClient().loadStudent("francine Kengne");
     assertEquals(roulettePair.getClient().getNumberOfStudents(), 1);
       
     roulettePair.getClient().loadStudent("Lemdjo Marie");
     assertEquals(roulettePair.getClient().getNumberOfStudents(), 2);
  } 
    
}
