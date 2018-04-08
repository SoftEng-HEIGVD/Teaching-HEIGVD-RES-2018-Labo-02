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
import java.io.*;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  protected Socket       socket;
  protected InputStream  is;
  protected OutputStream os;

  protected BufferedReader br;
  protected PrintWriter    pw;

  protected String answer;


  @Override
  public void connect(String server, int port) throws IOException {
    if(isConnected())
      this.disconnect();

    try {
      socket = new Socket(server, port);
      is     = socket.getInputStream();
      os     = socket.getOutputStream();
    }catch (IOException e){
      throw e;
    }

    br = new BufferedReader(new InputStreamReader(is));
    pw = new PrintWriter(new OutputStreamWriter(os), true);

    try {
      // The server sends us "Hello. Online HELP is available. Will you find it?".
      // We ignore it.
      answer = br.readLine();
    }catch (IOException e){
      throw e;
    }
  }

  @Override
  public void disconnect() throws IOException {
    if(!isConnected())
      return;
    try {
      pw.println(RouletteV1Protocol.CMD_BYE);  // BYE
      pw.close();
      br.close();
      is.close();
      os.close();
      socket.close();
    }catch (IOException e){
      throw e;
    }
  }

  @Override
  public boolean isConnected() {
    try {
      return socket.isConnected();
    }
    catch (NullPointerException e){
      return false;
    }

  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    try {
      pw.println(RouletteV1Protocol.CMD_LOAD);  // LOAD
      br.readLine();              // reads : "Send your data: [end with ENDOFDATA]"
      pw.println(fullname);
      pw.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);  // ENDOFDATA
      br.readLine();              // reads : "DATA LOADED"

    }catch (IOException e){
      throw e;
    }
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    for(Student s : students)
      loadStudent(s.getFullname());
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    // If there is no students, send an EmptyStoreException.
    if(getNumberOfStudents() == 0)
      throw new EmptyStoreException();

    try {
      pw.println(RouletteV1Protocol.CMD_RANDOM);  // RANDOM
      answer = br.readLine();
    }catch (IOException e){
      throw e;
    }

    System.out.println(answer);

    return new Student(answer);
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    try {
      pw.println(RouletteV1Protocol.CMD_INFO);  // INFO
      answer = br.readLine();
    }catch (IOException e){
      throw e;
    }

    return JsonObjectMapper.parseJson(answer, InfoCommandResponse.class).getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {

    try {
      pw.println(RouletteV1Protocol.CMD_INFO);  // INFO
      answer = br.readLine();
    }catch (IOException e){
      throw e;
    }

    return JsonObjectMapper.parseJson(answer, InfoCommandResponse.class).getProtocolVersion();
  }
}