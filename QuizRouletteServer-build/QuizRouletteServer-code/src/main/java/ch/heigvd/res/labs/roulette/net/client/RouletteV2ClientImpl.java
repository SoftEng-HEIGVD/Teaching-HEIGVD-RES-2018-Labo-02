package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Olivier Nicole
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());
  private int numberOfCommands = 0;
  private int numberOfStudentAdded = 0;
  private boolean successOfCommand = false;


  @Override
  public void clearDataStore() throws IOException {

    LOG.info("Clear data store");

    ++numberOfCommands;

    write(RouletteV2Protocol.CMD_CLEAR);
    read();
  }

  @Override
  public List<Student> listStudents() throws IOException {

    LOG.info("List students");

    ++numberOfCommands;

    write(RouletteV2Protocol.CMD_LIST);

    ListCommandResponse response = JsonObjectMapper.parseJson(read(), ListCommandResponse.class);

    return response.getStudents();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    ++numberOfCommands;

    LOG.info("Loading student");

    write(RouletteV2Protocol.CMD_LOAD);
    read();

    //send the new student to the server
    write(fullname);

    write(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    LoadCommandResponse response = JsonObjectMapper.parseJson(read(), LoadCommandResponse.class);
    successOfCommand = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
    numberOfStudentAdded = response.getNumberOfNewStudents();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    ++numberOfCommands;

    LOG.info("Loading students");

    write(RouletteV2Protocol.CMD_LOAD);
    read();

    //sent all students to the server
    for(Student student : students){
      write(student.getFullname());
    }

    write(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    LoadCommandResponse response = JsonObjectMapper.parseJson(read(), LoadCommandResponse.class);
    successOfCommand = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
    numberOfStudentAdded = response.getNumberOfNewStudents();
  }

  @Override
  public void disconnect() throws IOException {
    ++numberOfCommands;
    write(RouletteV2Protocol.CMD_BYE);
    ByeCommandResponse response = JsonObjectMapper.parseJson(read(), ByeCommandResponse.class);
    successOfCommand = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
    closeAll();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException{
    ++numberOfCommands;
    return super.pickRandomStudent();
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    ++numberOfCommands;
    return super.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    ++numberOfCommands;
    return super.getProtocolVersion();
  }

  @Override
  public int getNumberOfStudentAdded(){
    return numberOfStudentAdded;
  }

  @Override
  public int getNumberOfCommands() {
    return numberOfCommands;
  }

  @Override
  public boolean checkSuccessOfCommand() {
    return successOfCommand;
  }
}
