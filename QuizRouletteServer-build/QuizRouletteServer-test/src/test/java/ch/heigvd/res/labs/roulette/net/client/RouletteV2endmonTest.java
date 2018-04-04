package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Miguel Lopes Gouveia(endmon), Rémy Nasserzare(remynz)
 */
public class RouletteV2endmonTest
{

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "endmon")
  public void theServerShouldRunOnPort2613() throws IOException
  {
	  assertEquals(roulettePair.getServer().getPort(), 2613);
  }
  
  @Test
  @TestAuthor(githubId = "endmon")
  public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException 
  {
    assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }
  
  @Test
  @TestAuthor(githubId = "endmon")
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException 
  {
    assertTrue(roulettePair.getClient().isConnected());
  }
  
}
