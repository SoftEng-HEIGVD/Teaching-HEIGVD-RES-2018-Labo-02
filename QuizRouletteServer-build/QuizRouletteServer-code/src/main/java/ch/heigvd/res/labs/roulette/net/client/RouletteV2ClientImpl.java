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
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());
  private static final String SUCCESS_STR = "success";
  private boolean status;
  private int nbrOfStudentNewlyImported = 0;
  private int nbrOfSentCommands = 0;

  @Override
  public void clearDataStore() throws IOException {
    writeAndFlush(RouletteV2Protocol.CMD_CLEAR);
    ++nbrOfSentCommands;
    LOG.log(Level.INFO, reader.readLine());
  }

  @Override
  public void disconnect() throws IOException {
    writeAndFlush(RouletteV2Protocol.CMD_BYE);
    ++nbrOfSentCommands;

    String byeResponse = reader.readLine();
    ByeCommandResponse bye = JsonObjectMapper.parseJson(byeResponse, ByeCommandResponse.class);
    status = bye.getStatus().equals(SUCCESS_STR);
    nbrOfSentCommands = bye.getNumberOfCommands();

    reader.close();
    writer.close();
    clientSocket.close();
  }

  @Override
  public List<Student> listStudents() throws IOException {
    writeAndFlush(RouletteV2Protocol.CMD_LIST);
    ++nbrOfSentCommands;
    StudentsList list = new StudentsList();
    try {
      list = JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class);
    } catch (IOException e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
    }
    return list.getStudents();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    writeAndFlush(RouletteV2Protocol.CMD_LOAD);
    ++nbrOfSentCommands;

    // Event if we won't need the server's response, we read it to avoid further reading problems
    LOG.log(Level.INFO, reader.readLine());
    writeAndFlush(fullname);
    writeAndFlush(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    String loadResponse = reader.readLine();
    LOG.log(Level.CONFIG, "response : " + loadResponse);
    LoadCommandResponse load = JsonObjectMapper.parseJson(loadResponse, LoadCommandResponse.class);
    status = load.getStatus().equals(SUCCESS_STR);
    nbrOfStudentNewlyImported = load.getNumberOfNewStudents();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    writeAndFlush(RouletteV2Protocol.CMD_LOAD);
    ++nbrOfSentCommands;
    LOG.log(Level.INFO, reader.readLine());

    // Writing the name of each student
    for(Student s : students) {
      writeAndFlush(s.getFullname());
    }

    writeAndFlush(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    String loadResponse = reader.readLine();
    LoadCommandResponse load = JsonObjectMapper.parseJson(loadResponse, LoadCommandResponse.class);
    status = load.getStatus().equals(SUCCESS_STR);
    nbrOfStudentNewlyImported = load.getNumberOfNewStudents();
  }

  @Override
  public int getNumberOfStudentAdded() {
    return nbrOfStudentNewlyImported;
  }

  @Override
  public int getNumberOfCommands() {
    return nbrOfSentCommands;
  }

  @Override
  public boolean checkSuccessOfCommand() {
    return status;
  }

  @Override
  public String getProtocolVersion() throws IOException {
    writeAndFlush(RouletteV2Protocol.CMD_INFO);
    ++nbrOfSentCommands;
    String infoResponse = reader.readLine();

    InfoCommandResponse info = JsonObjectMapper.parseJson(infoResponse, InfoCommandResponse.class);
    return info.getProtocolVersion();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    ++nbrOfSentCommands;
    return super.pickRandomStudent();
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    ++nbrOfSentCommands;
    return super.getNumberOfStudents();
  }
}
