package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 * @author Iando Rafidimalala
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket clientSocket;
  protected BufferedReader in;
  protected PrintWriter out;
  
  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server,port);
    in =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
    
    //read the hello line
    in.readLine();
   
      
        
  }

  @Override
  public void disconnect() throws IOException {
    out.println(RouletteV1Protocol.CMD_BYE);
    out.flush();
    in.close();
    out.close();
    clientSocket.close();
  }

  @Override
  public boolean isConnected() {
      if(clientSocket == null)
          return false;
      
    return clientSocket.isConnected(); 
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      //Send the data accroding the protocol
      out.println(RouletteV1Protocol.CMD_LOAD);
      out.println(fullname);
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();     
      
      in.readLine(); //read the "send data" line
      in.readLine();// read the data
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      out.println(RouletteV1Protocol.CMD_LOAD);
      for (Student student : students) {
          out.println(student.getFullname());
      }
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      out.println(RouletteV1Protocol.CMD_RANDOM);
      out.flush();  
      
      //Get the json response
      String response = in.readLine();
      
      //check out the response validity
      //if the payload is well-formed and contains the fullname, the error should be null
    if(JsonObjectMapper.parseJson(response, RandomCommandResponse.class).getError() == null) {
      return new Student(JsonObjectMapper.parseJson(response, RandomCommandResponse.class).getFullname());
    } else {
      throw new EmptyStoreException();
    }      
  
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      out.println(RouletteV1Protocol.CMD_INFO);
      
      out.flush();
      
      String response = in.readLine();
      System.err.println(response);
      
      return JsonObjectMapper.parseJson(response, InfoCommandResponse.class).getNumberOfStudents();
      
  }

  @Override
  public String getProtocolVersion() throws IOException {
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();
      
      String response = in.readLine();
      
      return JsonObjectMapper.parseJson(response, InfoCommandResponse.class).getProtocolVersion();
  }



}
