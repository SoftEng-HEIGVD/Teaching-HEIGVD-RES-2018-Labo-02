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
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti, modified by Christophe Joyet and Lionel Nanchen
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG         = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  protected Socket clientSocket           = null;
  protected BufferedReader bufferedReader = null;
  protected PrintWriter printWriter       = null;


  @Override
  public void connect(String server, int port) throws IOException {
    //Declaration of the variables
    clientSocket   = new Socket(server, port);
    bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    printWriter    = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
    bufferedReader.readLine(); // read first lign with "Hello"
  }

  @Override
  public void disconnect() throws IOException {
    printWriter.println(RouletteV1Protocol.CMD_BYE);
    bufferedReader.readLine();

    //close socket, BufferedReader and PrintWriter
    clientSocket.close();
    bufferedReader.close();
    printWriter.close();
  }

  @Override
  public boolean isConnected() {
    return clientSocket != null && clientSocket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    printWriter.println(RouletteV1Protocol.CMD_LOAD);
    bufferedReader.readLine();
    printWriter.println(fullname);
    printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    bufferedReader.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    printWriter.println(RouletteV1Protocol.CMD_LOAD);
    bufferedReader.readLine();
    for (Student s : students) {
      printWriter.println(s.getFullname());
    }
    printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    bufferedReader.readLine();

  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    printWriter.println(RouletteV1Protocol.CMD_RANDOM);
    RandomCommandResponse response = JsonObjectMapper.parseJson(bufferedReader.readLine(), RandomCommandResponse.class);

    if (response.getError() != null) {
      throw new EmptyStoreException();
    }

    return new Student (response.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    printWriter.println(RouletteV1Protocol.CMD_INFO);
    InfoCommandResponse response = JsonObjectMapper.parseJson(bufferedReader.readLine(), InfoCommandResponse.class);
    return response.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    printWriter.println(RouletteV1Protocol.CMD_INFO);
    InfoCommandResponse response = JsonObjectMapper.parseJson(bufferedReader.readLine(), InfoCommandResponse.class);
    return response.getProtocolVersion();
  }


}