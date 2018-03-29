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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
    socket = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream());

    LOG.info(in.readLine());
  }

  @Override
  public void disconnect() throws IOException {
    LOG.info("Client disconnect");
    in.close();
    out.close();
    socket.close();

  }

  @Override
  public boolean isConnected() {
    if(socket == null){
      return false;
    }
    return socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    List<Student> students = new ArrayList<>();
    students.add(new Student(fullname));
    this.loadStudents(students);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    out.println(RouletteV1Protocol.CMD_LOAD);
    out.flush();

    LOG.info(in.readLine());

    for(Student student : students)
      out.println(student.getFullname());
    out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    out.flush();

    LOG.info(in.readLine());
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    out.println(RouletteV1Protocol.CMD_RANDOM);
    out.flush();

    String response = in.readLine();
    LOG.info(response);
    RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);

    if(!randomResponse.getError().isEmpty()){
      LOG.info(randomResponse.getError());
      throw new EmptyStoreException();
    }

    return new Student(randomResponse.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {

    // ask server for info
    out.println(RouletteV1Protocol.CMD_INFO);
    out.flush();

    // process answer
    String response = in.readLine();
    LOG.info(response);
    InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);

    // extract value
    return infoResponse.getNumberOfStudents();

  }

  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV1Protocol.VERSION;
  }

}
