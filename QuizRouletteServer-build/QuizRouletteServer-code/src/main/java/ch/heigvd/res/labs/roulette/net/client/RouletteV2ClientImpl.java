package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

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

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private BufferedReader is;
  private PrintWriter os;
  private static int numberOfNewStudents = 0;
  private static int numberOfCommands = 0;
  private static boolean commandSucceded = false;

  @Override
  public void clearDataStore() throws IOException {
    ++numberOfCommands;
    os.println(RouletteV2Protocol.CMD_CLEAR);
    os.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
  }


  @Override
  public List<Student> listStudents() throws IOException {
    ++numberOfCommands;
    os.println(RouletteV2Protocol.CMD_LIST);
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    ++numberOfCommands;
    try{
      super.loadStudent(fullname);
      ++numberOfNewStudents;
      commandSucceded = true;

    }catch(IOException e){
      commandSucceded = false;
    }

  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    ++numberOfCommands;
    try{
      super.loadStudents(students);
      numberOfNewStudents += students.size();
      commandSucceded = true;

    }catch(IOException e){
      commandSucceded = false;
      throw e;
    }
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    ++numberOfCommands;
    return super.pickRandomStudent();
  }


    @Override
  public int getNumberOfStudentAdded() {
    return numberOfNewStudents;
  }

    @Override
    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    @Override
    public boolean checkSuccessOfCommand(){
      return commandSucceded;
    }


}
