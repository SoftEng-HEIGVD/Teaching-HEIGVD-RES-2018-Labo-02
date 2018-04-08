package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Max Caduff
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private int cmdCounter = 0;
  private int lastNbOfStudentsAdded = 0;
  private boolean cmdSuccess = false;

  @Override
  public void clearDataStore() throws IOException {
    cmdSuccess = false;
    writer.println(RouletteV2Protocol.CMD_CLEAR);
    writer.flush();
    LOG.log(Level.INFO, "Data erased. Server says: {0}", reader.readLine());
    cmdCounter++;
    cmdSuccess = true;
  }

  @Override
  public List<Student> listStudents() throws IOException {
    cmdSuccess = false;
    writer.println(RouletteV2Protocol.CMD_LIST);
    writer.flush();
    cmdCounter++;
    List<Student> lst = JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class ).getStudents();
    cmdSuccess = true;
    return lst;
  }


  @Override
  public void loadStudent(String fullname) throws IOException {

    cmdSuccess = false;
    super.loadStudent(fullname);
    cmdSuccess = true;
    cmdCounter++;
    lastNbOfStudentsAdded = 1;
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {

    cmdSuccess = false;
    super.loadStudents(students);
    cmdSuccess = true;
    cmdCounter++;
    lastNbOfStudentsAdded = students.size();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    cmdSuccess = false;
    cmdCounter++;
    Student rnd =  super.pickRandomStudent();
    cmdSuccess = true;
    return rnd;
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    cmdCounter++;
    cmdSuccess = false;
    int nb = super.getNumberOfStudents();
    cmdSuccess = true;
    return nb;
  }

  @Override
  public String getProtocolVersion() throws IOException {
    cmdCounter++;
    cmdSuccess = false;
    String v = super.getProtocolVersion();
    cmdSuccess = true;
    return v;
  }

  @Override
  public int getNumberOfStudentAdded() {
    return lastNbOfStudentsAdded;
  }

  @Override
  public int getNumberOfCommands() {
    return cmdCounter;
  }
  public boolean checkSuccessOfCommand() {
    return cmdSuccess;
  }

  @Override
  public void disconnect() throws IOException {
    cmdCounter++;
    cmdSuccess = false;
    super.disconnect();
    cmdSuccess = true;
  }
}
