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
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
  
  private final int BUFFER_SIZE = 1024;
  
  private Socket clientsocket = null;
  private BufferedReader in = null;
  private PrintWriter out = null;

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  @Override
  public void connect(String server, int port) throws IOException {
    clientsocket = new Socket(server, port);
    
    in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
    out = new PrintWriter(clientsocket.getOutputStream());
    
    if (isConnected()) {
      in.readLine();
    }
  }

  @Override
  public void disconnect() throws IOException {
    sendToServer(RouletteV1Protocol.CMD_BYE);
    
    if (clientsocket != null) {
        clientsocket.close();
    }
    
    if (in != null) {
      in.close();
    }
    
    if (out != null) {
      out.close();
    }
  }

  @Override
  public boolean isConnected() {
    return clientsocket != null && !clientsocket.isClosed() && clientsocket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    sendToServer(RouletteV1Protocol.CMD_LOAD);
    
    String response = in.readLine();
    
    if (response.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
      sendToServer(fullname);
  
      sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    } else {
      LOG.log(Level.SEVERE, "No response from server after command LOAD");
    }
  
    response = in.readLine();
    
    if (!response.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {
      LOG.log(Level.SEVERE, "No response from server after END OF LOAD");
    }
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    sendToServer(RouletteV1Protocol.CMD_LOAD);
  
    String response = in.readLine();
  
    if (response.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
      for (Student s : students) {
        sendToServer(s.getFullname());
      }
    
      sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    } else {
      LOG.log(Level.SEVERE, "No response from server after command LOAD");
    }
  
    response = in.readLine();
  
    if (!response.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {
      LOG.log(Level.SEVERE, "No response from server after END OF LOAD");
    }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    sendToServer(RouletteV1Protocol.CMD_RANDOM);
  
    String response = in.readLine();
    
    RandomCommandResponse rcr = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);
    
    if (rcr.getError() != null) {
      throw new EmptyStoreException();
    }
    
    return new Student(rcr.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    sendToServer(RouletteV1Protocol.CMD_INFO);
    
    String response = in.readLine();
    
    InfoCommandResponse icr = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
    
    return icr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    sendToServer(RouletteV1Protocol.CMD_INFO);
  
    String response = in.readLine();
  
    InfoCommandResponse icr = JsonObjectMapper.parseJson(response.toString(), InfoCommandResponse.class);
  
    return icr.getProtocolVersion();
  }

  private void sendToServer(String data) {
    out.println(data);
    out.flush();
  }

}
