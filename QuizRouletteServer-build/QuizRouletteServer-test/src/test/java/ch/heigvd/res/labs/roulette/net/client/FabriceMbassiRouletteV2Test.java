package ch.heigvd.res.labs.roulette.net.client;
 
 import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
 import ch.heigvd.res.labs.roulette.data.Student;
 import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
 import ch.heigvd.schoolpulse.TestAuthor;
 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.ExpectedException;
 
 import java.io.*;
 import java.net.Socket;
 import java.util.LinkedList;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.assertFalse;

 public class FabriceMbassiRouletteV2Test {
	 

     @Rule
     public ExpectedException exception = ExpectedException.none();
 
     @Rule
     public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
     
     public void clearServerAfterClearCommand() throws IOException, EmptyStoreException{
         IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
         client.loadStudent("Fabrice");
         client.loadStudent("Mbassi");
         client.clearDataStore();
         assertEquals(0, client.getNumberOfStudents());
     }
	 /*
	 public void ServerListStudents() throws IOException {
     int port = roulettePair.getServer().getPort();
     IRouletteV2Client client = new RouletteV2ClientImpl();
     client.connect("localhost", port);
     
     Student fa = new Student("Fabrice");
     Student mb = new  Student ("Mbassi");
     
     client.loadStudent("Fabrice");
     client.loadStudent("Mbassi");
     
     List<Student> expectedList = new ArrayList<> ();
     expectedList.add(Fabrice);
     expectedList.add(Mbassi);
             
     assertEquals(expectedList, client.listStudents());
       
     expectedList.add(erica);
     
     assertNotEquals(expectedList, client.listStudents());
     
   }
   */
	 public void ConnectedClientTestStart() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }
  
	 
	  public void ConnectToARouletteServerOk() throws IOException{
         int port = roulettePair.getServer().getPort();
         IRouletteV2Client client = new RouletteV2ClientImpl();
         assertFalse(client.isConnected());
         client.connect("localhost", port);
         assertTrue(client.isConnected());
     }
	 public void NumberOfVersionNumber() throws IOException {
       assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }
 } 