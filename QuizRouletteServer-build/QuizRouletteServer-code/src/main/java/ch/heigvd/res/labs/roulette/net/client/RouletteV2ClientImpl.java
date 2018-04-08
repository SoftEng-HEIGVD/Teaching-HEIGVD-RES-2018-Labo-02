package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

/*  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private BufferedReader is;
  private PrintWriter os; */
  private boolean commandSucceded = false;
  private int nbStudentsAdded = 0;
  private int nbCommands = 0;


  @Override
  public void clearDataStore() throws IOException {
    os.println(RouletteV2Protocol.CMD_CLEAR);
    os.flush();
    ++nbCommands;
      System.out.println("CLEARDATA++" + nbCommands);
      System.out.flush();
    LOG.info("BAH ALORS");
    LOG.info("server response BAH ALORS" + is.readLine() );
    validateCommand();
  }


  @Override
  public List<Student> listStudents() throws IOException {
    os.println(RouletteV2Protocol.CMD_LIST);
    os.flush();
      ++nbCommands;
      System.out.println("LIST++" + nbCommands);
      System.out.flush();
    StudentsList sL = JsonObjectMapper.parseJson(is.readLine() , StudentsList.class);

    validateCommand();
    return sL.getStudents();
  }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {


        os.println(RouletteV2Protocol.CMD_RANDOM);
        os.flush();
        String rep = is.readLine();
        if(getNumberOfStudents() == 0)
            throw new EmptyStoreException();
        ++nbCommands;
        return Student.fromJson(JsonObjectMapper.parseJson(rep, RandomCommandResponse.class).getFullname() );
    }
    /*
 @Override
 public Student pickRandomStudent() throws EmptyStoreException, IOException {
     System.out.println("RANDOM++" + nbCommands);
     System.out.flush();
     ++nbCommands;
     return super.pickRandomStudent();
 }*/

  @Override
  public void loadStudent(String fullname) throws IOException {
    try{
        LOG.info("Loading a student on server...");

        os.println(RouletteV2Protocol.CMD_LOAD);
        os.flush();
        ++nbCommands;
        System.out.println("LOAD++" + nbCommands);
        System.out.flush();
        LOG.info("server response " +is.readLine() );
        os.println( fullname );
        os.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
        LOG.info("server response " + is.readLine() );

        LoadCommandResponseV2 loadCR = JsonObjectMapper.parseJson(is.readLine(), LoadCommandResponseV2.class);
        LOG.info("server response " + loadCR.getStatus() );
        LOG.info("Loaded");

        nbStudentsAdded = loadCR.getNumberOfNewStudents();

        validateCommand();
    }catch(IOException e){
      commandSucceded = false;
    }

  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    try{
    //  super.loadStudents(students);
      //numberOfNewStudents += students.size();
        LOG.info("Loading list of Students on server...");

        os.println(RouletteV2Protocol.CMD_LOAD);
        os.flush();
        ++nbCommands;
        System.out.println("LOADS++" + nbCommands);
        System.out.flush();
        LOG.info("server response " +is.readLine() );
        for ( Student s: students) {
            os.println( s.getFullname() );
        }

        os.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
        LOG.info("server response " +is.readLine() );

        LoadCommandResponseV2 loadCR = JsonObjectMapper.parseJson(is.readLine(), LoadCommandResponseV2.class);
        LOG.info("server response " + loadCR.getStatus() );
        LOG.info("Loaded");

        nbStudentsAdded = loadCR.getNumberOfNewStudents();

        validateCommand();

    }catch(IOException e){
      commandSucceded = false;
      throw e;
    }
  }



    @Override
  public int getNumberOfStudentAdded() {
    return nbStudentsAdded;
  }
/*
    public int getNumberOfStudents() throws IOException {
        nbCommands++;
        return super.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        nbCommands++;
        return super.getProtocolVersion();
    }
    */

    @Override
    public int getNumberOfCommands() throws IOException {
        System.out.println(nbCommands);
        System.out.flush();
        return nbCommands;
    }

    @Override
    public boolean checkSuccessOfCommand(){
      return commandSucceded;
    }

  @Override
  public void disconnect() throws IOException {
    os.println(RouletteV2Protocol.CMD_BYE);
    os.flush();
      ++nbCommands;
      System.out.println("BYE++" + nbCommands);
      System.out.flush();
      ByeCommandResponseV2 byeCR = JsonObjectMapper.parseJson(is.readLine(), ByeCommandResponseV2.class);
      nbCommands = byeCR.getNumberOfCommands();
      is.close();
      os.close();
      socket.close();
      LOG.info("Disconnected from server.");
  }

    @Override
    public String getProtocolVersion() throws IOException {
        System.out.println("INFO++" + nbCommands);
        System.out.flush();
      ++nbCommands;
      return super.getProtocolVersion();
    }


    @Override
    public int getNumberOfStudents() throws IOException {
        System.out.println("INFO++" + nbCommands);
        System.out.flush();
        ++nbCommands;
        return super.getNumberOfStudents();
    }

    private void validateCommand(){
       // nbCommands++;
        commandSucceded = true;
    }


}
