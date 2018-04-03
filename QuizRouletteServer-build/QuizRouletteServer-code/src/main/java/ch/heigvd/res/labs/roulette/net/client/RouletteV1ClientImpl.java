package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  
    private Socket client;
    boolean clientConnected=false;
    private BufferedWriter os;
    private BufferedReader is;
    private String MSG_HELLO="Hello. Online HELP is available. Will you find it?";


  @Override
  public void connect(String server, int port) throws IOException {
    if (clientConnected)
        clientConnected = false;
    client = new Socket(server, port);
    os = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    is = new BufferedReader(new InputStreamReader(client.getInputStream()));
    clientConnected=true;
    LOG.info(is.readLine());
  }

  @Override
  public void disconnect() throws IOException {
        send(RouletteV1Protocol.CMD_BYE);
        os.close();
        is.close();
        client.close();
  }

  @Override
  public boolean isConnected() {
      return clientConnected;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
        send(RouletteV1Protocol.CMD_LOAD);
        send(fullname);
        send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        //flush answers
        is.readLine();
        is.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
        if(students == null || students.isEmpty())
            return;
        send(RouletteV1Protocol.CMD_LOAD); 
        for(Student s : students){
            if(s!=null && !s.getFullname().isEmpty()){
                send(s.getFullname());
            }
        }     
        send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
        send(RouletteV1Protocol.CMD_RANDOM);
        RandomCommandResponse response = JsonObjectMapper.parseJson(is.readLine(), RandomCommandResponse.class);
        if (response.getError() != null) throw new EmptyStoreException();
        return new Student(response.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
        send(RouletteV1Protocol.CMD_INFO);
        return JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class).getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
        send(RouletteV1Protocol.CMD_INFO);
        return JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class).getProtocolVersion();
  }
  
    private void send(String msg) throws IOException{
        os.write(msg);
        os.newLine();
        os.flush();
    }
  
    protected String readline() throws IOException {
        String line = "";
        do {
            line = is.readLine();
        } while ( line.equalsIgnoreCase(MSG_HELLO));
        return line;
    }



}
