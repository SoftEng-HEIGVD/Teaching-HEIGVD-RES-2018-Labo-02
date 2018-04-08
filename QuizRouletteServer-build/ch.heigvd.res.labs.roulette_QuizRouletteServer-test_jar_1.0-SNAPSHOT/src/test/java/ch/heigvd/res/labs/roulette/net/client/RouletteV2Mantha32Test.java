package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */

public class RouletteV2Mantha32Test {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
  
  @Test
  @TestAuthor(githubId = "Mantha32")
  public void theServerShouldReturnTheCorrectProtocolVersion() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "Mantha32")
  public void theServerShouldHaveNoStudentsAfterClear() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("Mantha32");
    client.loadStudent("Iando Rafid");
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }
 

  @Test
  @TestAuthor(githubId = "Mantha32")
  public void theServerShouldHaveReturnListOfStudent() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.clearDataStore();
    
    List<Student> students;
    students = Arrays.asList(new Student("Mantha32"), 
                             new Student("Iando Rafid"));
    client.loadStudents(students);
    
    List<Student> StudentsInStore = client.listStudents();    
    assertEquals(students.size(), StudentsInStore.size());
    
    for(Student st : StudentsInStore){
        Assert.assertTrue(students.contains(st));
    }
    
  }
  
  @Test
  @TestAuthor(githubId = "Mantha32")
  public void theServerShouldReturnTheNumberOfStudentAdded() throws IOException{
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.clearDataStore();
    Socket socket = new Socket("localhost", roulettePair.getServer().getPort());
    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    
    input.readLine();
    
    output.println(RouletteV2Protocol.CMD_LOAD);
    output.flush();
    input.readLine();
    output.println("Mantha32");
    output.println("Iando Rafid");
    output.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    output.flush();

    String result = input.readLine();

    assertEquals("{\"status\":\"success\",\"numberOfNewStudents\":2}", result);   
  }
  

  
  @Test
  @TestAuthor(githubId = "Mantha32")
  public void theServerShouldReturnTheNumberOfCommands() throws IOException{
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.clearDataStore();
    
    Socket socket = new Socket("localhost", roulettePair.getServer().getPort());
    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    
    input.readLine();
    
    output.println(RouletteV2Protocol.CMD_LOAD);
    output.flush();
    input.readLine();
    
    output.println("Mantha 32");
    output.println("Iando Rafid");
    output.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    output.flush();
    input.readLine();

    output.println(RouletteV2Protocol.CMD_INFO);
    output.flush();
    input.readLine();

    output.println(RouletteV2Protocol.CMD_BYE);
    output.flush();
    String result = input.readLine();

    assertEquals("{\"status\":\"success\",\"numberOfCommands\":3}", result);
  }  
}
