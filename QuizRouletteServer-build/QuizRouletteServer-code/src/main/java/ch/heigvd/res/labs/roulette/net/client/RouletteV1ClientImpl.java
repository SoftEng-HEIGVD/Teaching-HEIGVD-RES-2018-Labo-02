package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import sun.awt.SunToolkit;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Olivier Nicole
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  private Socket socket = null;
  private BufferedReader bufferedReader = null;
  private PrintWriter printWriter = null;

  @Override
  public void connect(String server, int port) throws IOException {

    LOG.info("Client try to start the connection to the server");

    //create the socket
    socket = new Socket(server, port);

    try {
      //create the writer and the reader
      bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
      printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
    } catch (IOException ex) {
      LOG.severe("IO Exception : " + ex);
    }

    //read the welcome message "Hello..."
    read();

    LOG.info("Client connected");
  }

  @Override
  public void disconnect() throws IOException {

    if(isConnected()) {
      //send the bye command to the server and close all allocated ressources
      write(RouletteV1Protocol.CMD_BYE);
      socket.close();
      bufferedReader.close();
      printWriter.close();
      LOG.info("Client disconnected");
    } else {
      LOG.info("Client already disconnected");
    }
  }

  @Override
  public boolean isConnected() {
    return socket != null && socket.isConnected() && !socket.isClosed();
  }

  /**
   * method used to read the bufferedReader
   *
   * @return the value of readLine from the bufferedReader
   * @throws IOException
   */
  protected String read() throws IOException {
    return bufferedReader.readLine();
  }

  /**
   * method used to write into the printWriter, and flush
   *
   * @param message the message that will be sent to the server
   */
  protected void write(String message) {
    printWriter.println(message);
    printWriter.flush();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

    LOG.info("Loading student");

    write(RouletteV1Protocol.CMD_LOAD);
    read();

    //send the new student to the server
    write(fullname);

    write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    read();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {

    LOG.info("Loading students");

    write(RouletteV1Protocol.CMD_LOAD);
    read();

    //sent all students to the server
    for(Student student : students){
      write(student.getFullname());
    }

    write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    read();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

    LOG.info("Pick random student");

    write(RouletteV1Protocol.CMD_RANDOM);

    String readStudent = read();
    RandomCommandResponse response = JsonObjectMapper.parseJson(readStudent, RandomCommandResponse.class);

    if(response.getError() != null){
      LOG.info("Error while parsing Json");
      throw new EmptyStoreException();
    }

    return Student.fromJson(readStudent);
  }

  /**
   * method used to send the command info to the server
   *
   * @return the InfoCommandResponse given by JsonObjectMapper.parseJson(...)
   * @throws IOException
   */
  protected InfoCommandResponse getInfoResponse() throws IOException {

    write(RouletteV1Protocol.CMD_INFO);
    return JsonObjectMapper.parseJson(read(), InfoCommandResponse.class);
  }

  @Override
  public int getNumberOfStudents() throws IOException {

    LOG.info("Get the number of students");

    return getInfoResponse().getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {

    LOG.info("Get protocol version");

    return getInfoResponse().getProtocolVersion();
  }

  public void closeAll() throws IOException {
    socket.close();
    bufferedReader.close();
    printWriter.close();
  }
}
