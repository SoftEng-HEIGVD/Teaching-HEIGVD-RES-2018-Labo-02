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

import static ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol.*;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 *
 *
 */
//To change body of generated methods, choose Tools | Templates.
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private boolean connected;

  protected int numberOfCommands = 0;


  @Override
  public void connect(String server, int port) throws IOException {
      socket = new Socket(server, port);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream());
      connected = true;
      LOG.log(Level.INFO, readServer());
  }

  @Override
  public void disconnect() throws IOException {
      writeServer(CMD_BYE);
      LOG.log(Level.INFO, readServer());

      if(socket != null) {
        socket.close();
      }
      if(in != null) {
        in.close();
      }
      if(out != null) {
        out.close();
      }

      connected = false;
      ++numberOfCommands;

  }

  @Override
  public boolean isConnected() {
    return connected && (socket != null);

  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    writeServer(CMD_LOAD);
    LOG.log(Level.INFO, readServer());

    writeServer(fullname);

    writeServer(CMD_LOAD_ENDOFDATA_MARKER);
    LOG.log(Level.INFO, readServer());
    ++numberOfCommands;
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    writeServer(CMD_LOAD);
    LOG.log(Level.INFO, readServer());

    for(Student student : students){
      writeServer(student.getFullname());
    }

    writeServer(CMD_LOAD_ENDOFDATA_MARKER);
    LOG.log(Level.INFO, readServer());
    ++numberOfCommands;
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    writeServer(CMD_RANDOM);
    ++numberOfCommands;

    Student student = new Student(readServer());

    if(student.getFullname().contains("error"))
      throw new EmptyStoreException();

    return student;

  }

  @Override
  public int getNumberOfStudents() throws IOException {
    writeServer(CMD_INFO);
    ++numberOfCommands;
    return JsonObjectMapper.parseJson(readServer(), InfoCommandResponse.class).getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    writeServer(CMD_INFO);
    ++numberOfCommands;
    return JsonObjectMapper.parseJson(readServer(), InfoCommandResponse.class).getProtocolVersion();

  }

  public String readServer() throws IOException{
    return in.readLine();
  }

  public void writeServer(String str){
    out.write(str + '\n');
    out.flush();
  }

  public int getNumberOfCommands(){
    return numberOfCommands;
  }


}
