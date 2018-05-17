package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

   /**
    * logger helps the debug
    */
  static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  
  /**
   * socket where the client will connect
   */
  private Socket socket = null;
  
  /**
   * way to read answers from the server
   */
  protected BufferedReader reader = null;
  
  /**
   * way to send requests to the server
   */
  protected PrintWriter writer = null;
  
  /**
   * Last answer of the server
   */
  protected String serverResponse;
  
  /**
   * try to connect to a server
   * 
   * @param server ip adress where to connect
   * @param port where we want to connect
   * @throws IOException if a write or read exception happen
   */
  @Override
  public void connect(String server, int port) throws IOException {
     socket = new Socket(server, port);
     reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     writer = new PrintWriter(socket.getOutputStream());
     if(isConnected()) {
        reader.readLine();
     }
  }

  /**
   * disconnect to the server
   * 
   * @throws IOException if a write or read exception happen
   */
  @Override
  public void disconnect() throws IOException {
     socket.close();
     reader.close();
     writer.close();
     
  }

  /**
   * return True if we are connected to the server
   * 
   * @return True if we are connected to the server
   */
  @Override
  public boolean isConnected() {
     if(socket == null || socket.isClosed()) {
        return false;
     }
     return socket.isConnected();
  }

  /**
   * send a new student to the server
   * 
   * @param fullname name of the student
   * @throws IOException if a write or read exception happen
   */
  @Override
  public void loadStudent(String fullname) throws IOException {
     writer.println(RouletteV1Protocol.CMD_LOAD);
     writer.flush();
     
     serverResponse = reader.readLine();
     if(serverResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
        writer.println(fullname);
        writer.flush();
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
     }else{
        LOG.log(Level.SEVERE,"problem with LOAD answer from server");
     }
     serverResponse = reader.readLine();
     if(!serverResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
        LOG.log(Level.SEVERE,"problem with ENDOFDATA answer from server");
     }
  }

  /**
   * send new students to the server
   * 
   * @param students list of the student we want the server to add
   * @throws IOException if a write or read exception happen
   */
  @Override
  public void loadStudents(List<Student> students) throws IOException {
     writer.println(RouletteV1Protocol.CMD_LOAD);
     writer.flush();
     
     serverResponse = reader.readLine();
     if(serverResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
        for (Student student : students) {
           writer.println(student.getFullname());
           writer.flush();
        }
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
     }else{
        LOG.log(Level.SEVERE,"problem with LOAD answer from server");
     }
     serverResponse = reader.readLine();
     if(!serverResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
        LOG.log(Level.SEVERE,"problem with ENDOFDATA answer from server");
     }
  }

  /**
   * ask to the server a random student contained in the server
   * 
   * @return a random student contained in the server
   * @throws EmptyStoreException if there is no student and we ask a random one
   * @throws IOException if a write or read exception happen
   */
  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
     writer.println(RouletteV1Protocol.CMD_RANDOM);
     writer.flush();
     
     RandomCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);
     
     if(response.getError() != null){
         throw new EmptyStoreException();
     }
     return new Student(response.getFullname());
  }

  /**
   * return the current total of students contained in the server
   * 
   * @return the current total of students contained in the server
   * @throws IOException if a write or read exception happen
   */
  @Override
  public int getNumberOfStudents() throws IOException {
     writer.println(RouletteV1Protocol.CMD_INFO);
     writer.flush();
     InfoCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
     return response.getNumberOfStudents();
  }

  /**
   * return the current protocol version
   * 
   * @return the current protocol version
   * @throws IOException if a write or read exception happen
   */
  @Override
  public String getProtocolVersion() throws IOException {
     writer.println(RouletteV1Protocol.CMD_INFO);
     writer.flush();
     InfoCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(),InfoCommandResponse.class);
     return response.getProtocolVersion();
  }

}
