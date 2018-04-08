package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Loic Frueh
 * @author Dejvid Muaremi
 */
public class RouletteV2Ludwig9392Test {
  
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  /*
  @Test
  @TestAuthor(githubId = "Ludwig9392")
  public void theTestRouletteServerShouldListenOnTheCorrectPort(){
    int port = roulettePair.getServer().getPort();
    assertEquals(2613,port);
  }
  */
  
  @Test
  @TestAuthor(githubId = "Ludwig9392")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }
  
  @Test
  @TestAuthor(githubId = "Ludwig9392")
  public void theServerShouldBeAbleToClearTheData() throws IOException {
    roulettePair.getClient().loadStudent("Loic");
    roulettePair.getClient().loadStudent("Dejvid");
    roulettePair.getClient().loadStudent("Adam");
    assertEquals(3, roulettePair.getClient().getNumberOfStudents());
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.clearDataStore();
    assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }
  
  @Test
  @TestAuthor(githubId = "Ludwig9392")
  public void theClientShouldBeAbleToReturnTheCorrectNumberOfStudentAdded() throws IOException{
    roulettePair.getClient().loadStudent("Michel");
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    assertEquals(1, client.getNumberOfStudentAdded());
    
    List<Student> students = new ArrayList<Student>();
    students.add(new Student("Dejvid"));
    students.add(new Student("Loic"));
    students.add(new Student("Adam"));
    roulettePair.getClient().loadStudents(students);
    assertEquals(3, client.getNumberOfStudentAdded());
    
  }
  
  @Test
  @TestAuthor(githubId = "Ludwig9392")
  public void theClientShouldBeAbleToReturnTheCorrectNumberOfCommandsSent() throws IOException{
    roulettePair.getClient().loadStudent("Jean");
    roulettePair.getClient().loadStudent("Pierre");
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.clearDataStore();
    client.listStudents();
    assertEquals(4, client.getNumberOfCommands());
  }
  
  @Test
  @TestAuthor(githubId = "Ludwig9392")
  public void theServerShouldBeAbleToReturnTheListOfStudent() throws IOException{
    roulettePair.getClient().loadStudent("Michel");
    roulettePair.getClient().loadStudent("Dejvid");
    roulettePair.getClient().loadStudent("Loic");
    roulettePair.getClient().loadStudent("Adam");
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    List<Student> students = client.listStudents();
    assertEquals("Michel", students.get(0).getFullname());
    assertEquals("Dejvid", students.get(1).getFullname());
    assertEquals("Loic", students.get(2).getFullname());
    assertEquals("Adam", students.get(3).getFullname());
  }
  
}
