package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsStoreImpl;
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

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket clientSocket;
  BufferedReader is;
  PrintWriter os;
  private int nbStudents;

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    os = new PrintWriter(clientSocket.getOutputStream());
    is.readLine(); // read the hello message
  }

  @Override
  public void disconnect() throws IOException {
    clientSocket.close();
  }

  @Override
  public boolean isConnected() {
    if (clientSocket != null) {
      return clientSocket.isConnected() && !clientSocket.isClosed();
    }
    return false;
  }

  public boolean isClosed() {
    if (clientSocket != null) {
      return clientSocket.isClosed();
    }
    return false;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    String response;
    os.println(RouletteV1Protocol.CMD_LOAD);
    os.flush();
    if((response = is.readLine()).equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
      os.println(fullname);
      os.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      os.flush();
      response = is.readLine();
    }
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    String response;
    os.println(RouletteV1Protocol.CMD_LOAD);
    os.flush();
    response = is.readLine();
    if((response.equals(RouletteV1Protocol.RESPONSE_LOAD_START))) {
      for (Student s : students) {
        os.println(s.getFullname());
      }
      os.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      os.flush();
      response = is.readLine();
    }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    if(getNumberOfStudents() == 0) {
      throw new EmptyStoreException();
    }
    os.println(RouletteV1Protocol.CMD_RANDOM);
    os.flush();
    return new Student(is.readLine());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    os.println(RouletteV1Protocol.CMD_INFO);
    os.flush();
    String response = is.readLine();
    InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
    nbStudents = infoCommandResponse.getNumberOfStudents();
    return nbStudents;
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV1Protocol.VERSION;
  }


}