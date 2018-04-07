package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Kopp
 */
public class RouletteV2olivierKoppTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = {"olivierKopp"})
  public void clientShouldBeAbleToConnectToRouletteV2() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }

  @Test
  @TestAuthor(githubId = {"olivierKopp"})
  public void studentListShouldBeEmptyAfterClear() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    client.loadStudent("student1");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("student2");
    assertEquals(2, client.getNumberOfStudents());
    client.clearDataStore();
    assertTrue(client.listStudents().isEmpty());
  }

  @Test
  @TestAuthor(githubId = {"olivierKopp"})
  public void serverShouldReturnAllStudentsAfterList() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    client.loadStudent("student1");
    client.loadStudent("student2");
    client.loadStudent("student3");
    assertEquals(3, client.getNumberOfStudents());
    List<Student> students = client.listStudents();
    assertEquals(students.get(0).getFullname(), "student1");
    assertEquals(students.get(1).getFullname(), "student2");
    assertEquals(students.get(2).getFullname(), "student3");
  }

  @Test
  @TestAuthor(githubId = {"olivierKopp"})
  public void serverShouldReturnCorrectVersionInfo() throws IOException{
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    assertEquals(roulettePair.getClient().getProtocolVersion(), RouletteV2Protocol.VERSION);
  }

  
}
