package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
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
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;

  @Override
  public void connect(String server, int port) throws IOException {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    try{
      socket = new Socket(server, port);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream());

      LOG.info(in.readLine());

    }catch(IOException e){
      LOG.log(Level.SEVERE, "Client could not create socket exit: {0}", e.getMessage());
    }

  }

  @Override
  public void disconnect() throws IOException {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      LOG.info("Client disconnect");
      in.close();
      out.close();
      socket.close();

  }

  @Override
  public boolean isConnected() {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    if(socket == null){
      return false;
    }
    return socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    out.print(RouletteV1Protocol.CMD_LOAD);
    out.print(fullname);
    out.print(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    out.print(RouletteV1Protocol.CMD_LOAD);

    for(Student student : students)
    out.print(student.getFullname());

    out.print(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getNumberOfStudents() throws IOException {

    // ask server for info
    out.println(RouletteV1Protocol.CMD_INFO);
    out.flush();

    // process answer
    String response = in.readLine();
    InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);

    // extract value
    return infoResponse.getNumberOfStudents();

  }

  @Override
  public String getProtocolVersion() throws IOException {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    return RouletteV1Protocol.VERSION;
  }

}
