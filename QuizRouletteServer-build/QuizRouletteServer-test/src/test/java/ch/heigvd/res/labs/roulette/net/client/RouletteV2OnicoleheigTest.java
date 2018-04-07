package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Nicole
 */
public class RouletteV2OnicoleheigTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "onicoleheig")
  public void dataStoreShouldBeCleared() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("jean");
    client.loadStudent("peuplu");
    assertEquals(2, client.getNumberOfStudents());
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "onicoleheig")
  public void studentsShouldBeInTheList() throws IOException {
    ArrayList<Student> students = new ArrayList();
    students.add(new Student("jean"));
    students.add(new Student("peuplu"));

    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

    for(Student s : students){
      client.loadStudent(s.getFullname());
    }

    ArrayList<Student> response = (ArrayList) client.listStudents();

    for(int i = 0; i < students.size(); ++i){
      assertEquals(students.get(i), response.get(i));
    }
  }

  @Test
  @TestAuthor(githubId = "onicoleheig")
  public void serverVersionShouldBeCorrect() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "onicoleheig")
  public void loadStudentsShouldReturnTheCorrectNumberOfNewStudents() throws IOException {
    Socket socket = new Socket("localhost", roulettePair.getServer().getPort());

    //create the writer and the reader
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));

    bufferedReader.readLine();

    printWriter.println(RouletteV2Protocol.CMD_LOAD);
    printWriter.flush();

    printWriter.println("jean");
    printWriter.println("peuplu");

    printWriter.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    printWriter.flush();

    //read end of data response
    bufferedReader.readLine();

    assertEquals("{\"status\":\"success\",\"numberOfNewStudents\":2}", bufferedReader.readLine());
  }
}
