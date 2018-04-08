package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
  protected BufferedReader bris = null;
  protected PrintWriter pwos = null;
  protected int nbCommamde = 0;
  protected boolean lastCommandStatus = true;
  protected boolean isOpen;

  private static final String END_OF_STRING = "\n";

  protected static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  protected void printlnWithFlush(String s){
    pwos.println(s);
    pwos.flush();
  }

  @Override
  public void connect(String server, int port) throws IOException {
      clientSocket = new Socket(server,port);
      bris = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
      pwos = new PrintWriter(clientSocket.getOutputStream());

      //read the "Hello " from server
       LOG.log(Level.INFO,bris.readLine());
       isOpen = true;
  }

  @Override
  public void disconnect() throws IOException {
    printlnWithFlush(RouletteV1Protocol.CMD_BYE );
    nbCommamde++;
    isOpen = false;
  }

  @Override
  public boolean isConnected() {
    return isOpen;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

    printlnWithFlush(RouletteV1Protocol.CMD_LOAD);

    //Send data line
    LOG.log(Level.INFO,bris.readLine());

    printlnWithFlush(fullname );
    printlnWithFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    //Send data loaded line
    LOG.log(Level.INFO,bris.readLine());

    nbCommamde++;
  }

  public void loadStudents(List<Student> students) throws IOException {

    printlnWithFlush(RouletteV1Protocol.CMD_LOAD);

    //Send of data line
    LOG.log(Level.INFO,bris.readLine());

    for(Student s : students){
      printlnWithFlush(s.getFullname());

    }
    printlnWithFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    //Send data loaded line
    LOG.log(Level.INFO,bris.readLine());
    nbCommamde++;

  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

    printlnWithFlush(RouletteV1Protocol.CMD_RANDOM);

    RandomCommandResponse student = JsonObjectMapper.parseJson(bris.readLine(),RandomCommandResponse.class);
    nbCommamde++;
    if(student.getError() != null){

      throw new EmptyStoreException();
    }

    return new Student(student.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {

    printlnWithFlush(RouletteV1Protocol.CMD_INFO);


    InfoCommandResponse  info = JsonObjectMapper.parseJson(bris.readLine(),InfoCommandResponse.class);
    nbCommamde++;
    return info.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {

    printlnWithFlush(RouletteV1Protocol.CMD_INFO);


    InfoCommandResponse  info = JsonObjectMapper.parseJson(bris.readLine(),InfoCommandResponse.class);
    nbCommamde++;
    return info.getProtocolVersion();
  }



}
