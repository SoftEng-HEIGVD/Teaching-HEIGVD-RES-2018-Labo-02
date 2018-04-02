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

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket clientSocket;
  private BufferedReader in;
  private PrintWriter out;
  private String serveResponse = "";
  private boolean connected = false;
  private boolean firstRequest = true;


  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    out = new PrintWriter(clientSocket.getOutputStream());
    connected = true;
  }

  @Override
  public void disconnect() throws IOException {
    LOG.log(Level.INFO, "{ClientRouletteV1} has requested to be disconnected.");
    connected = false;
    out.println(RouletteV1Protocol.CMD_BYE);
    cleanup();
  }

  @Override
  public boolean isConnected() {
    return connected;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    checkForFirstRequest();

    out.println(RouletteV1Protocol.CMD_LOAD);
    out.flush();

    serveResponse = in.readLine();

    out.println(fullname + "\n" + RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    out.flush();

    //check if we receive the "load done" message from the server, should be always true
    if((serveResponse = in.readLine()).equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
      LOG.log(Level.INFO, "student loaded successfully in the server");
    }else{
      throw new IOException("loading went wrong");
    }
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    checkForFirstRequest();

    out.println(RouletteV1Protocol.CMD_LOAD);
    out.flush();

    serveResponse = in.readLine();

    String studdentStr = "";
    for (Student student : students) {
      studdentStr += student.getFullname() + "\n";
    }

    out.println(studdentStr + RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    out.flush();

    if((serveResponse = in.readLine()).equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
      LOG.log(Level.INFO, "students loaded successfully in the server");
    }else{
      throw new IOException("loading went wrong");
    }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    checkForFirstRequest();

    out.println(RouletteV1Protocol.CMD_RANDOM);
    out.flush();

    serveResponse = in.readLine();

    RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(serveResponse, RandomCommandResponse.class);

    if (randomResponse.getError() != null) {
      throw new EmptyStoreException();
    }

    return new Student(randomResponse.getFullname());
  }

  private InfoCommandResponse getInfoCommand() throws IOException {
    checkForFirstRequest();

    out.println(RouletteV1Protocol.CMD_INFO);
    out.flush();

    serveResponse = in.readLine();

    InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(serveResponse, InfoCommandResponse.class);
    return infoResponse;
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    return getInfoCommand().getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return getInfoCommand().getProtocolVersion();
  }

  //if it's the first time we make a request, we're waiting for the welcome message first
  private void checkForFirstRequest() throws IOException {
    if(firstRequest) {
      while (!connected || (serveResponse = in.readLine()).isEmpty());
      firstRequest = false;
    }
  }

  private void cleanup() throws IOException {
    if (in != null)
      in.close();

    if (out != null)
      out.close();

    if (clientSocket != null)
      clientSocket.close();
  }


}
