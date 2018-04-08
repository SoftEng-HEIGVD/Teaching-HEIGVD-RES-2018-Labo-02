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
  protected Socket clientSocket = null;
  protected PrintWriter out = null;
  protected BufferedReader br = null;
  protected static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
    br.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    if(clientSocket.isConnected()) {
      out.println(RouletteV1Protocol.CMD_BYE);
      br.close();
      out.close();
      clientSocket.close();
    } else{
      LOG.log(Level.WARNING, "Client is already disconnected!");
    }
  }

  @Override
  public boolean isConnected() {
    if(clientSocket != null) {
      return clientSocket.isConnected();
    } else{
      return false;
    }
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    out.println(RouletteV1Protocol.CMD_LOAD);
    br.readLine();
    out.println(fullname);
    out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    br.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    if(students != null) {
      out.println(RouletteV1Protocol.CMD_LOAD);
      br.readLine();
      //Print every student
      for (Student student : students)
        out.println(student);

      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      br.readLine();
    }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    out.println(RouletteV1Protocol.CMD_RANDOM);
    RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(br.readLine(), RandomCommandResponse.class);

    //If there are no students in DB
    if(!randomResponse.getError().isEmpty()) {
      LOG.log(Level.SEVERE, "No students in DB!");
      throw new EmptyStoreException();
    }

    return Student.fromJson(randomResponse.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    out.println(RouletteV1Protocol.CMD_INFO);
    InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(br.readLine(), InfoCommandResponse.class);
    return infoResponse.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    out.println(RouletteV1Protocol.CMD_INFO);
    InfoCommandResponse info = JsonObjectMapper.parseJson(br.readLine(), InfoCommandResponse.class);
    return info.getProtocolVersion();
  }
}
