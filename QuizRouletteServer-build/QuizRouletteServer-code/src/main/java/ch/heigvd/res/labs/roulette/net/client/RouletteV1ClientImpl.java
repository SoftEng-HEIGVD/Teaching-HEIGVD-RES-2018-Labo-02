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
  1. Create a socket
  2. Make a connection request on an IP address / port
  3. Read and write bytes through this socket, communicating with the client
  4. Close the client socket


RSION = "1.0";

public final static int DEFAULT_PORT = 1313;

public final static String CMD_HELP = "HELP";
public final static String CMD_RANDOM = "RANDOM";
public final static String CMD_LOAD = "LOAD";
public final static String CMD_INFO = "INFO";
public final static String CMD_BYE = "BYE";

public final static String CMD_LOAD_ENDOFDATA_MARKER = "ENDOFDATA";

public final static String RESPONSE_LOAD_START = "Send your data [end with ENDOFDATA]";
public final static String RESPONSE_LOAD_DONE = "DATA LOADED";

public final static String[] SUPPORTED_COMM
 */
//To change body of generated methods, choose Tools | Templates.
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;


  @Override
  public void connect(String server, int port) throws IOException {
    if(!isConnected()){
      socket = new Socket(server, port);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream());

      System.out.println(readServer());
    }
  }

  @Override
  public void disconnect() throws IOException {
    if(isConnected()){
      writeServer(CMD_BYE);

      socket.close();
      in.close();
      out.close();
    }
  }

  @Override
  public boolean isConnected() {
    return socket != null && in != null && out != null;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    writeServer(CMD_LOAD);
    System.out.println(readServer());

    writeServer(fullname);

    writeServer(CMD_LOAD_ENDOFDATA_MARKER);
    System.out.println(readServer());
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    writeServer(CMD_LOAD);
    System.out.println(readServer());

    for(Student student : students){
      writeServer(student.getFullname());
    }

    writeServer(CMD_LOAD_ENDOFDATA_MARKER);
    System.out.println(readServer());
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    writeServer(CMD_RANDOM);

    Student student = new Student(readServer());

    if(student.getFullname().contains("error"))
      throw new EmptyStoreException();

    return student;

  }

  @Override
  public int getNumberOfStudents() throws IOException {
    writeServer(CMD_INFO);

    String str = readServer();
    int nbr = 0;
    int res = 0;
    boolean lastWasDigit = false;

    for (int i = 0; i < str.length(); ++i) {
      if (Character.isDigit(str.charAt(i)) && !lastWasDigit) {
        ++nbr;
        lastWasDigit = true;
      }
      if (!Character.isDigit(str.charAt(i))) {
        lastWasDigit = false;
      }
      if (nbr == 3) {

        int count = i + 1;
        while (count < str.length() && Character.isDigit(str.charAt(count))) {
          ++count;
        }
        res = Integer.parseInt(str.substring(i, count));
        i = str.length();
      }
    }
    return res;
  }

  @Override
  public String getProtocolVersion() throws IOException {//test pour version >10

    writeServer(CMD_INFO);

    String str = readServer();
    String res = "";

    for (int i = 0; i < str.length(); ++i) {
      if(Character.isDigit(str.charAt(i))){
        res += str.charAt(i);
      }else if(str.charAt(i) == '.'){
        res += str.charAt(i);
        while(Character.isDigit(str.charAt(++i))){
          res += str.charAt(i);
        }
        i = str.length();
      }

    }
    return res;

  }

  public String readServer() throws IOException{
    return in.readLine();
  }

  public void writeServer(String str){
    out.write(str);
    out.write('\n');
    out.flush();
  }


}
