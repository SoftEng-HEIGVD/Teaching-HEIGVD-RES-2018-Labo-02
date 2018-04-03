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

  static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket socket = null;
  protected BufferedReader reader = null;
  protected PrintWriter writer = null;
  

  @Override
  public void connect(String server, int port) throws IOException {
     socket = new Socket(server, port);
     reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     writer = new PrintWriter(socket.getOutputStream());
     if(isConnected()) {
        reader.readLine();
     }
  }

  @Override
  public void disconnect() throws IOException {
     socket.close();
     reader.close();
     writer.close();
     
  }

  @Override
  public boolean isConnected() {
     if(socket == null) {
        return false;
     }
     return socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
     writer.println(RouletteV1Protocol.CMD_LOAD);
     writer.flush();
     
     String serverResponse = reader.readLine();
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

  @Override
  public void loadStudents(List<Student> students) throws IOException {
     writer.println(RouletteV1Protocol.CMD_LOAD);
     writer.flush();
     
     String serverResponse = reader.readLine();
     if(serverResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
        for (Student student : students) {
           writer.println(student.getFullname());
           writer.flush();
           writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
           writer.flush();
        }
     }else{
        LOG.log(Level.SEVERE,"problem with LOAD answer from server");
     }
     serverResponse = reader.readLine();
     if(!serverResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
        LOG.log(Level.SEVERE,"problem with ENDOFDATA answer from server");
     }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
     writer.println(RouletteV1Protocol.CMD_RANDOM);
     writer.flush();
     
     RandomCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);
     
     if(!response.getError().isEmpty()){
         throw new EmptyStoreException();
     }
     return new Student(response.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
     writer.println(RouletteV1Protocol.CMD_INFO);
     writer.flush();
     InfoCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
     return response.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
     writer.println(RouletteV1Protocol.CMD_INFO);
     writer.flush();
     InfoCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(),InfoCommandResponse.class);
     return response.getProtocolVersion();
  }

}
