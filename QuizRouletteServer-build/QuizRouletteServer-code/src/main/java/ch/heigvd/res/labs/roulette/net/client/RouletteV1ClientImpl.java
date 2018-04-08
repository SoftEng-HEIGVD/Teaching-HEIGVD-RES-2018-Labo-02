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
 * @author Miguel Lopes Gouveia(endmon)
 * @author Rémy Nasserzare(remynz)
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
  BufferedReader inp;
  PrintWriter outp;
  Socket skt;
  int okConnect;

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  @Override
  public void connect(String server, int port) throws IOException {
    okConnect = 1;
    skt = new Socket(server, port);

    inp = new BufferedReader(new InputStreamReader(skt.getInputStream()));

    try {
      outp = new PrintWriter(new OutputStreamWriter(skt.getOutputStream()));

    } catch (IOException ex){
      LOG.log(Level.SEVERE, "Cannot write to the server {0}", ex.getMessage());
      return;
    }
    inp.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    if(isConnected() == false)
      return;
  
    outp.println(RouletteV1Protocol.CMD_BYE);
    outp.flush(); 

    okConnect = 0;
    inp.close();
    outp.close();
    skt.close();
  }

  @Override
  public boolean isConnected() {
    if(okConnect == 1) 
      return true;
    else 
      return false;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    outp.println(RouletteV1Protocol.CMD_LOAD);
    outp.flush(); 

    inp.readLine();

    outp.println(fullname);
    outp.flush(); 
    
    outp.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    outp.flush(); 

    inp.readLine();

  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    
    outp.println(RouletteV1Protocol.CMD_LOAD);
    outp.flush(); 

    inp.readLine();
    String full;
    for(Student s : students){
      full = s.getFullname();
      outp.println(full);
      outp.flush(); 
    }

  outp.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    outp.flush(); 

    inp.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    outp.println(RouletteV1Protocol.CMD_RANDOM);
    outp.flush(); 

    RandomCommandResponse rnd = JsonObjectMapper.parseJson(inp.readLine(), RandomCommandResponse.class);

    if(rnd.getError() != null) throw new EmptyStoreException();

    return new Student(rnd.getFullname());
  }



  @Override
  public int getNumberOfStudents() throws IOException {
    outp.println(RouletteV1Protocol.CMD_INFO);
    outp.flush(); 

    InfoCommandResponse inf = JsonObjectMapper.parseJson(inp.readLine(), InfoCommandResponse.class);

    int nbrStud = inf.getNumberOfStudents();

    return nbrStud;
  }

  @Override
  public String getProtocolVersion() throws IOException {
    outp.println(RouletteV1Protocol.CMD_INFO);
    outp.flush(); 
    
    InfoCommandResponse inf = JsonObjectMapper.parseJson(inp.readLine(), InfoCommandResponse.class);
    
    String protoVrs = inf.getProtocolVersion();

    return protoVrs;

  }

}