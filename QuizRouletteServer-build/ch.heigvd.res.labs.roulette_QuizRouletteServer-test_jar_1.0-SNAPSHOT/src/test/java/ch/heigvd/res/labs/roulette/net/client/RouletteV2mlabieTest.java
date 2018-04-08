package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Marc Labie
 */
public class RouletteV2mlabieTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


  // Reprise des 4 tests implémenter dans RouletteV1WasadigiTest pour s'assurer que le le serveur
  // et le client fonctionne, et que la version a bien changé.
  @Test
  @TestAuthor(githubId = "wasadigi")
  public void theTestRouletteServerShouldRunDuringTests() throws IOException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = "wasadigi")
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = {"wasadigi", "mlabie"})
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }

  @Test
  @TestAuthor(githubId = {"wasadigi", "mlabie"})
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }



  @Test
  @TestAuthor(githubId = "mlabie")
  public void theServerShouldHaveZeroStudentsAfterClear() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("sacha");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("olivier");
    assertEquals(2, client.getNumberOfStudents());
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "mlabie")
  public void theServerShouldReturnAListWithStudents() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    List<Student> students = client.listStudents();
    assertEquals(0, students.size());
    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");
    students = client.listStudents();
    assertEquals(3, students.size());
    assertEquals("[{\"fullname\":\"sacha\"},{\"fullname\":\"olivier\"},{\"fullname\":\"fabienne\"}]",
              JsonObjectMapper.toJson(students));
  }

  @Test
  @TestAuthor(githubId = "mlabie")
  public void theServerShouldReturnTheNumberOfEntriesAfterLoad() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    List<Student> students = client.listStudents();
    students.add(new Student("sacha"));
    students.add(new Student("olivier"));
    students.add(new Student("fabienne"));
    //int nbrOfLoad = client.loadStudents(students);
    client.loadStudents(students);
    int nbrOfLoad = client.getNumberOfStudentAdded();
    assertEquals(3, nbrOfLoad);
  }

  @Test
  @TestAuthor(githubId = "mlabie")
  public void theServerShouldReturnTheNumberOfCommandAfterEndingConnection() throws IOException, EmptyStoreException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    client.loadStudent("sacha");
    client.listStudents();
    client.getNumberOfStudents();

    client.clearDataStore();
    exception.expect(EmptyStoreException.class);
    client.pickRandomStudent();
    //int nbrOfCommand = client.disconnect();
    client.disconnect();
    int nbrOfCommand = client.getNumberOfCommands();
    assertEquals(5, nbrOfCommand);
  }
}
