package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.LinkedList;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Mentor Reka & Kamil Amrani
 * @authorId: mraheigvd & kamkill01011
 */
public class RouletteV2AmraniRekaTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  private LinkedList<Student> fillStudents(IRouletteV2Client client) throws IOException {
    LinkedList<Student> studentList = new LinkedList<>();
    studentList.add(new Student("Mentor Reka"));
    studentList.add(new Student("Kamil Amrani"));
    client.loadStudents(studentList);
    return studentList;
  }

  @Test
  @TestAuthor(githubId = {"mraheigvd", "kamkill01011" } )
  public void theTestRouletteServerShouldRunDuringTests() throws IOException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = {"mraheigvd", "kamkill01011" } )
  public void theTestRouletteClientShouldBeConnectedWhenTestStarts() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = {"mraheigvd", "kamkill01011" } )
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", roulettePair.getServer().getPort());
    assertTrue(client.isConnected());
  }

  @Test
  @TestAuthor(githubId = {"mraheigvd", "kamkill01011" } )
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    System.out.println(roulettePair.getClient().getProtocolVersion() + " --- " + RouletteV2Protocol.VERSION);
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = {"mraheigvd", "kamkill01011" } )
  public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", roulettePair.getServer().getPort());
    int numberOfStudents = client.getNumberOfStudents();
    assertEquals(0, numberOfStudents);
  }

  @Test
  @TestAuthor(githubId = {"mraheigvd", "kamkill01011" } )
  public void theServerShouldCountStudents() throws IOException {
    IRouletteV1Client client = roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("reka");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("amrani");
    assertEquals(2, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = {"mraheigvd", "kamkill01011" } )
  public void theServerShouldReturnAllStudentsPreviouslyAdded() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    LinkedList<Student> studentList = fillStudents(client);
    assertEquals(client.listStudents(), studentList);
  }

  @Test
  @TestAuthor(githubId = {"mraheigvd", "kamkill01011" } )
  public void theServerShouldReturnNothingAfterClean() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    assertNull(client.listStudents());
  }

}
