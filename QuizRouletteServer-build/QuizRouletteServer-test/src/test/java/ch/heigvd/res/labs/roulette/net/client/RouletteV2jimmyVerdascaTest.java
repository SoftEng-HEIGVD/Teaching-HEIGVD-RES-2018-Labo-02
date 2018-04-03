package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2jimmyVerdascaTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
  
  /**
   * test if the format of the answer at a BYE command is correct
   * -test the string format
   * -test if the number of commands change
   * -test if several times the same command is sent are counted
   * -test if ENDOFDATA is counted
   * 
   * @throws IOException if don't reach to connect the server
   */
  @Test
  @TestAuthor(githubId = {"jimmyVerdasca", "SoftEng-HEIGVD"})
  public void serverShouldAnswerCorrectlyAtABYECommand() throws IOException {
     //test with BYE only
     Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
     BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
     PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
     
     fromServer.readLine();
     toServer.println(RouletteV2Protocol.CMD_BYE);
     toServer.flush();
     String result = fromServer.readLine();
     
     String expectedResult = "{\"status\":\"success\",\"numberOfCommands\":1}";
     Assert.assertEquals(expectedResult, result);
     
     fromServer.close();
     toServer.close();
     clientSocket.close();
     
     //test with LOAD, 3 X INFO and BYE
     roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
     clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
     fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
     toServer = new PrintWriter(clientSocket.getOutputStream());
     
     expectedResult = "{\"status\":\"success\",\"numberOfCommands\":5}";
     fromServer.readLine();
     
     toServer.println(RouletteV2Protocol.CMD_LOAD);
     toServer.flush();
     fromServer.readLine();
     toServer.println("sasha");
     toServer.flush();
     toServer.println("olivier");
     toServer.flush();
     toServer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
     toServer.flush();
     fromServer.readLine();
     for (int i = 0; i < 3; i++) {
       toServer.println(RouletteV2Protocol.CMD_INFO);
       toServer.flush();
       fromServer.readLine();
     }
     toServer.println(RouletteV2Protocol.CMD_BYE);
     toServer.flush();
     result = fromServer.readLine();
     Assert.assertEquals(expectedResult, result);
     
     fromServer.close();
     toServer.close();
     clientSocket.close();
  }
  
  /**
   * check if the server return the correct number of students 
   * after a LOAD and a ENDOFDATA
   * 
   * @throws IOException if don't reach to connect the server
   */
  @Test
  @TestAuthor(githubId = {"jimmyVerdasca", "SoftEng-HEIGVD"})
  public void serverAnswerAtENDOFDATAWithTheNumberOfStudents() throws IOException {
     Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
     BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
     PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
     
     String expectedResult;
     
     // add from 0 to 9 student with a LOAD and check if the answer of the server is correct
     for (int tryID = 0; tryID < 10; tryID++) {
        int numberOfStudentsAddedThisTry = tryID;
        expectedResult = "{\"status\":\"success\",\"numberOfNewStudents\":" + numberOfStudentsAddedThisTry + "}";
        toServer.println(RouletteV2Protocol.CMD_LOAD);
        toServer.flush();
        fromServer.readLine();
        for(int j = 0; j < numberOfStudentsAddedThisTry; j++) {
           toServer.println("sasha" + (tryID + numberOfStudentsAddedThisTry * 9));
           toServer.flush();
        }
        toServer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        toServer.flush();
        String result = fromServer.readLine();
        Assert.assertEquals(expectedResult, result);
     }
     
     fromServer.close();
     toServer.close();
     clientSocket.close();
  }
  
  /**
   * test if a CLEAR command, really update the database to size 0
   * 
   * @throws IOException if don't reach to connect the server
   */
  @Test
  @TestAuthor(githubId = {"jimmyVerdasca", "SoftEng-HEIGVD"})
  public void CLEARShouldResetTheNumberOfStudentsToZero() throws IOException {
     Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
     BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
     PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
     
     toServer.println(RouletteV2Protocol.CMD_LOAD);
     toServer.flush();
     fromServer.readLine();
     toServer.println("sasha");
     toServer.flush();
     toServer.println("olivier");
     toServer.flush();
     toServer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
     toServer.flush();
     fromServer.readLine();
     //first test that we had really somthing to database
     Assert.assertEquals(2, roulettePair.getClient().getNumberOfStudents());
     toServer.println(RouletteV2Protocol.CMD_CLEAR);
     toServer.flush();
     fromServer.readLine();
     //then check it has been cleaned
     Assert.assertEquals(0, roulettePair.getClient().getNumberOfStudents());
     
     fromServer.close();
     toServer.close();
     clientSocket.close();
  }
}
