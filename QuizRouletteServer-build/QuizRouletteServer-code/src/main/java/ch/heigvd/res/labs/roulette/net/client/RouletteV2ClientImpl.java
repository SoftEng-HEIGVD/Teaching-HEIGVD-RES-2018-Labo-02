package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol.CMD_LOAD;
import static ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER;
import static ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol.CMD_CLEAR;
import static ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol.CMD_LIST;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());
  private boolean commandSuccess = false;
  private int numberOfStudentAdded = 0;

  @Override
  public void clearDataStore() throws IOException {
    numberOfCommands++;
    writeServer(CMD_CLEAR);
    LOG.log(Level.INFO, readServer());
  }

  @Override
  public List<Student> listStudents() throws IOException {
    numberOfCommands++;
    writeServer(CMD_LIST);
    return JsonObjectMapper.parseJson(readServer(), StudentsList.class).getStudents();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    writeServer(CMD_LOAD);
    LOG.log(Level.INFO, readServer());

    writeServer(fullname);

    writeServer(CMD_LOAD_ENDOFDATA_MARKER);
    LoadCommandResponse res = JsonObjectMapper.parseJson(readServer(), LoadCommandResponse.class);
    numberOfStudentAdded = res.getNumberOfNewStudents();
    commandSuccess = res.getStatus().equals("success");
    ++numberOfCommands;
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    writeServer(CMD_LOAD);
    LOG.log(Level.INFO, readServer());

    for(Student student : students){
      writeServer(student.getFullname());
    }

    writeServer(CMD_LOAD_ENDOFDATA_MARKER);
    LoadCommandResponse res = JsonObjectMapper.parseJson(readServer(), LoadCommandResponse.class);
    numberOfStudentAdded = res.getNumberOfNewStudents();
    commandSuccess = res.getStatus().equals("success");
    ++numberOfCommands;
  }

  public int getNumberOfStudentAdded(){
    return numberOfStudentAdded;
  }

  public boolean checkSuccessOfCommand(){
    return commandSuccess;
  }




}
