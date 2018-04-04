package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.QuizRouletteServer;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2SachaKorTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "sachakor")
  public void theTestRouletteServerShouldRunDuringTests() throws IOException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = "sachakor")
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = "sachakor")
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }

  @Test
  @TestAuthor(githubId = "sachakor")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "sachakor")
  public void theServerShouldHaveZeroStudentsAfterClearingDataStore() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.loadStudent("sacha");
    client.loadStudent("steven");
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "sachakor")
  public void theServerShouldSendTheListOfStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    List<Student> studentList = new LinkedList<>();
    studentList.add(new Student("sasha"));
    studentList.add(new Student("sam"));
    client.loadStudents(studentList);
    List<Student> actual = client.listStudents();
    assertEquals(studentList, actual);
  }

  @Test
  @TestAuthor(githubId = "Etnarion")
  public void itShouldBePossibleToConnectToTheServerWithTheDefaultPort() throws IOException {
    RouletteServer server = new RouletteServer(RouletteV2Protocol.DEFAULT_PORT, RouletteV2Protocol.VERSION);
    server.startServer();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", server.getPort());
    assertTrue(client.isConnected());
  }
}
