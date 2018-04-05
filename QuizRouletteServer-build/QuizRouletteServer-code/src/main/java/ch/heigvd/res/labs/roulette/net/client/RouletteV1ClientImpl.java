package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * Modified by :
 * @author Loic Frueh
 * @author Dejvid Muaremi
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
  private Socket socket;
  private boolean isConnected;
  private BufferedReader input;
  private PrintWriter output;
  
  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  
  @Override
  public void connect(String server, int port) throws IOException {
    socket = new Socket(server, port);
    isConnected = true;
    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    try {
      output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }
    catch (IOException e){
      LOG.log(Level.SEVERE, "Cannot write to the server {0}", e.getMessage());
      return;
    }
    input.readLine();
  }
  
  @Override
  public void disconnect() throws IOException {
    if(!isConnected){
      return;
    }
    
    // Tell to the server to close the connection.
    sender(RouletteV1Protocol.CMD_BYE);
    
    // Close all the resources
    socket.close();
    input.close();
    output.close();
    isConnected = false;
  }
  
  @Override
  public boolean isConnected() {
    return isConnected;
  }
  
  @Override
  public void loadStudent(String fullname) throws IOException {
    
    // Tell the server to start to load the data
    sender(RouletteV1Protocol.CMD_LOAD);
    input.readLine();
    
    // Send the data
    sender(fullname);
    
    // Tell the server that all the data are loaded
    sender(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    input.readLine();
  }
  
  @Override
  public void loadStudents(List<Student> students) throws IOException {
    // Tell the server to start to load the data
    sender(RouletteV1Protocol.CMD_LOAD);
    input.readLine();
    
    for (Student s : students) {
      sender(s.getFullname());
    }
  
    // Tell the server that all the data are loaded
    sender(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    input.readLine();
  }
  
  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    sender(RouletteV1Protocol.CMD_RANDOM);
    RandomCommandResponse rcr = JsonObjectMapper.parseJson(input.readLine(), RandomCommandResponse.class);
    
    if(rcr.getError() != null){
      throw new EmptyStoreException();
    }
    return new Student(rcr.getFullname());
  }
  
  @Override
  public int getNumberOfStudents() throws IOException {
    sender(RouletteV1Protocol.CMD_INFO);
    
    InfoCommandResponse icr = JsonObjectMapper.parseJson(input.readLine(), InfoCommandResponse.class);
    
    return icr.getNumberOfStudents();
  }
  
  @Override
  public String getProtocolVersion() throws IOException {
    sender(RouletteV1Protocol.CMD_INFO);
    InfoCommandResponse icr = JsonObjectMapper.parseJson(input.readLine(), InfoCommandResponse.class);
    return icr.getProtocolVersion();
  }
  
  /***
   * This sender is used for the communication with the server.
   * @param toServer The message to send to the server.
   */
  private void sender(String toServer){
    output.println(toServer);
    output.flush();
  }
  
}
