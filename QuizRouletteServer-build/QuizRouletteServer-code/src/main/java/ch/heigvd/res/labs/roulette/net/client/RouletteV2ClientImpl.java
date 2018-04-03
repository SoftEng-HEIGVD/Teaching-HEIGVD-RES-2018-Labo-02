package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private int numberOfStudentAdded;
  private int numberOfCommands;
  
  @Override
  public void clearDataStore() throws IOException {
    sendServerMessage(RouletteV2Protocol.CMD_CLEAR);
    readServerMessage();
  }

  @Override
  public List<Student> listStudents() throws IOException {
    sendServerMessage(RouletteV2Protocol.CMD_LIST);
    StudentsList studentList = JsonObjectMapper.parseJson(readServerMessage(),StudentsList.class);
    return studentList.getStudents();
  }
  
  @Override
  public void loadStudent(String fullname) throws IOException {
    sendServerMessage(RouletteV2Protocol.CMD_LOAD);
    readServerMessage();
    sendServerMessage(fullname);
    sendServerMessage(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    LoadCommandResponse lcResponse = JsonObjectMapper.parseJson(readServerMessage(),LoadCommandResponse.class);
    numberOfStudentAdded = lcResponse.getNumberOfNewStudents();
  }
  
  @Override
  public void loadStudents(List<Student> students) throws IOException {
    sendServerMessage(RouletteV2Protocol.CMD_LOAD);
    readServerMessage();
    for (Student student : students) {
      sendServerMessage(student.getFullname());
    }
    sendServerMessage(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    LoadCommandResponse lcResponse = JsonObjectMapper.parseJson(readServerMessage(),LoadCommandResponse.class);
    numberOfStudentAdded = lcResponse.getNumberOfNewStudents();  }
  
  @Override
  public void disconnect() throws IOException {
    connected = false;
    sendServerMessage(RouletteV2Protocol.CMD_BYE);
    ByeCommandResponse bcResponse = JsonObjectMapper.parseJson(readServerMessage(),ByeCommandResponse.class);
    numberOfCommands = bcResponse.getNumberOfCommands();
    cleanup();
  }
  
  @Override
  public int getNumberOfStudentAdded(){
    return numberOfStudentAdded;
  }
  
  @Override
  public int getNumberOfCommands(){
    return numberOfCommands;
  }
  
}
