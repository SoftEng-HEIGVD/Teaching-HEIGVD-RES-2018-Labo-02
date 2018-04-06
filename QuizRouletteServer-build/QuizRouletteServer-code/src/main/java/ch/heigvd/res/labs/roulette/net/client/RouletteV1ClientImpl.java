package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsStoreImpl;
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
 * modify by Olivier Kopp
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  Socket socket = null;
  BufferedReader br = null;
  PrintWriter pw = null;


  public void send(String message){
    pw.println(message);
    pw.flush();
  }

  @Override
  public void connect(String server, int port) throws IOException {
    socket = new Socket(server, port);
    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    br.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    if(socket == null){
      return;
    }
    else{
      send(RouletteV1Protocol.CMD_BYE);
      socket.close();
      socket = null;
      pw.close();
      br.close();
    }
  }

  @Override
  public boolean isConnected() {
    return socket != null;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    send(RouletteV1Protocol.CMD_LOAD);
    send(fullname);
    br.readLine();
    send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    br.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    send(RouletteV1Protocol.CMD_LOAD);
    for (Student s : students){
      send(s.getFullname());
    }
    br.readLine();
    send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    br.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    send(RouletteV1Protocol.CMD_RANDOM);
    RandomCommandResponse rcr = JsonObjectMapper.parseJson(br.readLine(), RandomCommandResponse.class);
    if(rcr.getError() != null){
      throw new EmptyStoreException();
    }
    return new Student(rcr.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    send(RouletteV1Protocol.CMD_INFO);
    InfoCommandResponse icr = JsonObjectMapper.parseJson(br.readLine(), InfoCommandResponse.class);
    return icr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    send(RouletteV1Protocol.CMD_INFO);
    InfoCommandResponse icr = JsonObjectMapper.parseJson(br.readLine(), InfoCommandResponse.class);
    return icr.getProtocolVersion();
  }



}
