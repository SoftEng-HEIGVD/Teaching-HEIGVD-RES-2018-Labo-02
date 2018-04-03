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

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private enum CONNECT {
        RANDOM ("RANDOM"),
        LOAD ("LOAD"),
        HELP ("HELP"),
        INFO ("INFO"),
        BYE  ("BYE"),
        ENDOFDATA ("ENDOFDATA");

        private final String name;
        private CONNECT(String s) {
            name = s;
        }

        public String toString() {
            return this.name;
        }}

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private BufferedReader is;
    private PrintWriter os;
    private Socket socket;


    @Override
  public void connect(String server, int port) throws IOException {
      socket = new Socket(server, port);
      is = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
      os = new PrintWriter( socket.getOutputStream() );
      LOG.info("Connected to server " + server + " on port " + port + "." );

  }

  @Override
  public void disconnect() throws IOException {
      is.close();
      os.close();
      socket.close();
      LOG.info("Disconnected from server.");
  }

  @Override
  public boolean isConnected() {
      return socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      LOG.info("Loading " + fullname + "on server...");

      os.println(CONNECT.LOAD.toString());
      os.println(fullname);
      os.println(CONNECT.ENDOFDATA.toString());
      os.flush();

      LOG.info("Loaded");

  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      LOG.info("Loading list of Students on server...");

      os.println(CONNECT.LOAD.toString());
      for ( Student s: students) {
          os.println( s.toString() );
      }
      os.println(CONNECT.ENDOFDATA.toString());
      os.flush();

      LOG.info("Loaded");
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {

      os.println(CONNECT.RANDOM.toString());
      String rep = is.readLine();

      return Student.fromJson( JsonObjectMapper.parseJson(rep, String.class) );
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getProtocolVersion() throws IOException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }



}
