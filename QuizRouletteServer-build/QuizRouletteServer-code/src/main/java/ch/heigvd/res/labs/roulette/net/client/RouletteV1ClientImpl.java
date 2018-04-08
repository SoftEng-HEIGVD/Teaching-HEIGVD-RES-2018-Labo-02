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
 * @author Walid Koubaa
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  protected Socket socket = new Socket();
  protected BufferedReader reader;
  protected PrintWriter writer;

  @Override
  public void connect(String server, int port) throws IOException {
      if (isConnected()) {
          return;
      }

    // we connect to ther server with a defined port
      this.socket = new Socket(server, port);
      this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
      this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

      reader.readLine();
  }

  @Override
  public void disconnect() throws IOException {
      // calls the isConnected() method
      if (socket == null || !isConnected()) {
          return;
      }

      // We print the BYE message
      writer.println(RouletteV1Protocol.CMD_BYE);
      writer.flush();

      // We close connexion and streams
      reader.close();
      writer.close();
      socket.close();

      socket = null;
  }

  @Override
  public boolean isConnected() {
      return !(socket == null || !socket.isConnected());
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

      writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();

      String loadResponse = reader.readLine();
      if (!loadResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
          System.out.println("Error while using" + RouletteV1Protocol.CMD_LOAD + "  command");
          return;
      }
      writer.println(fullname);
      writer.flush();

      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();

      loadResponse = reader.readLine();

      if (!loadResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {
          System.out.println("An error occured at the end of the process");
      }
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {

      writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();

      for (Student student : students) {
          writer.println(student.getFullname());
          writer.flush();
      }

      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();

      String loadResponse = reader.readLine();

      if (!loadResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {
          System.out.println("An error occured at the end of the process");
      }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      writer.println(RouletteV1Protocol.CMD_RANDOM);
      writer.flush();

      RandomCommandResponse info = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);

      // in case where a student has no name
      if (info.getFullname() == null) {
          throw new EmptyStoreException();
      }

      Student randomStudent = new Student(info.getFullname());
      return randomStudent;
  }

  @Override
  public int getNumberOfStudents() throws IOException {

      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();

      String readLine = reader.readLine();
      InfoCommandResponse info = JsonObjectMapper.parseJson(readLine, InfoCommandResponse.class);

      return info.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {

      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();

      InfoCommandResponse info = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

      return info.getProtocolVersion();
  }

}
