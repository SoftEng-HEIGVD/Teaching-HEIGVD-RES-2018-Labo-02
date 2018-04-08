package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

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

    protected static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    protected BufferedReader is;
    protected PrintWriter os;
    protected Socket socket;


    @Override
  public void connect(String server, int port) throws IOException {
      socket = new Socket(server, port);
      is = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
      os = new PrintWriter( socket.getOutputStream() );
   //   String resp = is.readLine();
   //   System.out.println( resp );
      LOG.info("Connected to server " + server + " on port " + port + "." );
      is.readLine();

  }

  @Override
  public void disconnect() throws IOException {

      os.println(RouletteV2Protocol.CMD_BYE);
      os.flush();

      is.close();
      os.close();
      socket.close();
      LOG.info("Disconnected from server.");
  }

  @Override
  public boolean isConnected() {


        return socket != null;


  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      LOG.info("Loading " + fullname + "on server...");

      os.println(CONNECT.LOAD.toString());
      os.flush();

      is.readLine();
      os.println(fullname);
      os.println(CONNECT.ENDOFDATA.toString());
      os.flush();
      is.readLine();

      LOG.info("Loaded");

  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      LOG.info("Loading list of Students on server...");

      os.println(CONNECT.LOAD.toString());
      LOG.info("server response " +is.readLine() );
      for ( Student s: students) {
          os.println( s.toString() );
      }
      os.println(CONNECT.ENDOFDATA.toString());
      os.flush();
      LOG.info("server response " +is.readLine() );

      LOG.info("Loaded");
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {


      os.println(CONNECT.RANDOM.toString());
      os.flush();
      String rep = is.readLine();
      if(getNumberOfStudents() == 0)
          throw new EmptyStoreException();
      return Student.fromJson( JsonObjectMapper.parseJson(rep, String.class) );
  }

  @Override
  public int getNumberOfStudents() throws IOException {

      System.out.print("COUCOU");
      return InfoCmdRsp().getNumberOfStudents();

  }

  @Override
  public String getProtocolVersion() throws IOException {

     return InfoCmdRsp().getProtocolVersion();
  }

  private InfoCommandResponse InfoCmdRsp() throws IOException{
      os.println(RouletteV1Protocol.CMD_INFO);
      os.flush();
    // is.readLine();
   // is.readLine();
      InfoCommandResponse iCR = JsonObjectMapper.parseJson(is.readLine() , InfoCommandResponse.class);
      return iCR;
  }



}
