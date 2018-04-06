package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti, modyfied by loic-schurch, Jokau
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private int nbSuccessfulCommands = 0;
  private boolean lastSucceeded = false;
  private int numberOfStudentAdded = 0;

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  @Override
  public void loadStudent(String fullname) throws IOException {
    if (isConnected()) {
      // send command
      out.println(RouletteV2Protocol.CMD_LOAD);
      out.println(fullname);
      out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();

      //server responses (string + JSON)
      LOG.log(Level.INFO, in.readLine()); // "send your data..."
      try {
        LoadCommandResponse ldResp = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
        if (lastSucceeded = ldResp.getStatus().equals("success")) { // update lastSucceeded
          //SUCCESS
          numberOfStudentAdded = 1;                                   // update numberOfStudentAdded
          nbSuccessfulCommands++;                                   // update nbSuccessfulCommands
        }
        // lastSucceeded already updated
        LOG.log(Level.SEVERE, "Error : last load status is failure");
      } catch (IOException e) {
        lastSucceeded = false;
        LOG.log(Level.SEVERE, "Error while parsing LoadCommandResponse");
        throw e;
      }
    } else {
      LOG.log(Level.SEVERE, "CLIENT NOT CONNECTED TO SERVER");
      throw new IOException("Client not connected to server");
    }
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    if (isConnected()) {
      // send commands
      out.println(RouletteV2Protocol.CMD_LOAD);
      for(Student student : students) {
        out.println(student.getFullname());
      }
      out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();

      // server responses (String + JSON)
      LOG.log(Level.INFO, in.readLine()); // send your data...
      try {
        LoadCommandResponse ldResp = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
        if (lastSucceeded = ldResp.getStatus().equals("success")) { // update lastSucceeded
          numberOfStudentAdded = ldResp.getNumberOfNewStudents();  // update numberOfStudentAdded with server info
          nbSuccessfulCommands++;                                   // update nbSuccessfulCommands
        }
        // lastSucceeded already updated
        LOG.log(Level.SEVERE, "Error : last load status is failure");
      } catch (IOException e) {
        lastSucceeded = false;
        LOG.log(Level.SEVERE, "error while parsing LoadCommandResponse");
        throw e;
      }
    } else {
      throw new IOException("Client not connected to server");
    }
  }

  @Override
  public void clearDataStore() throws IOException {
    if (isConnected()) {
      // send command
      out.println(RouletteV2Protocol.CMD_CLEAR);
      out.flush();

      // server response (only string)
      if (lastSucceeded = in.readLine().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
        nbSuccessfulCommands++;

      } else {
        LOG.log(Level.SEVERE, "Error : unexpected server response");
        // lastSucceeded already updated
      }
    } else {
      throw new IOException("Client not connected to server");
    }
  }

  @Override
  public List<Student> listStudents() throws IOException {
    if (isConnected()) {
      // send command
      out.println(RouletteV2Protocol.CMD_LIST);
      out.flush();

      try {
        // server response (only JSON)
        StudentsList studentsList = JsonObjectMapper.parseJson(in.readLine(), StudentsList.class);
        lastSucceeded = true;
        nbSuccessfulCommands++;
        return studentsList.getStudents();
      } catch (IOException e) {
        lastSucceeded = false;
        LOG.log(Level.SEVERE, "error while parsing studentList");
        throw e;
      }
    } else {
      lastSucceeded = false;
      throw new IOException("Client not connected to server");
    }
  }

  @Override
  public void disconnect() throws IOException {
    super.disconnect();
    nbSuccessfulCommands++;
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    try {
      Student student = super.pickRandomStudent();
      nbSuccessfulCommands++;
      return student;
    } catch (EmptyStoreException e) {
      //Command has been successfully sent, but store is empty
      nbSuccessfulCommands++;
      throw e;
    }
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    int nbStudents = super.getNumberOfStudents();
    //if no thrown expection -> success
    nbSuccessfulCommands++;
    return nbStudents;
  }

  @Override
  public String getProtocolVersion() throws IOException {
    String version = super.getProtocolVersion();
    //if no thrown expection -> success
    nbSuccessfulCommands++;
    return version;
  }

  @Override
  public int getNumberOfStudentAdded() {
    return numberOfStudentAdded;
  }

  @Override
  public int getNumberOfCommands() {
    return nbSuccessfulCommands;
  }

  @Override
  public boolean checkSuccessOfCommand() {
    return lastSucceeded;
  }

}
