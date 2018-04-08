package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author edited by Joel Schar and Yann Lederrey
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private int nbCommandsUsed = 0;
  private int nbStudentsLoaded = 0;
  private Boolean successCommand = true;


  @Override
  public void clearDataStore() throws IOException {
    nbCommandsUsed++;
    out.println(RouletteV2Protocol.CMD_CLEAR);
    out.flush();
    LOG.info(in.readLine());
  }

  @Override
  public List<Student> listStudents() throws IOException {
    nbCommandsUsed++;
    out.println(RouletteV2Protocol.CMD_LIST);
    out.flush();

    String response = in.readLine();
    LOG.info(response);

    ListCommandResponse lsResponse = JsonObjectMapper.parseJson(response, ListCommandResponse.class);
    return lsResponse.getStudents();
  }

  @Override
  public int getNumberOfStudentAdded() throws IOException{
    nbCommandsUsed++;
    return nbStudentsLoaded;
  }

  @Override
  public int getNumberOfCommands() throws IOException{
    return nbCommandsUsed;
  }

  @Override
  public String getProtocolVersion() throws IOException {
    nbCommandsUsed++;
    return RouletteV2Protocol.VERSION;
  }

  public boolean checkSuccessOfCommand() throws IOException{
    return successCommand;
  }

  public void disconnect() throws IOException{
    nbCommandsUsed++;
    super.disconnect();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    List<Student> students = new ArrayList<>();
    students.add(new Student(fullname));
    this.loadStudents(students);

    LOG.info("load student : " + fullname);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException{
    nbCommandsUsed++;
    out.println(RouletteV2Protocol.CMD_LOAD);
    out.flush();

    LOG.info(in.readLine());

    for(Student student : students)
      out.println(student.getFullname());

    out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    out.flush();

    String response = in.readLine();

    LOG.info(response);

    LoadCommandResponse ldResponse = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
    successCommand = ldResponse.getStatus().equals("success");

    nbStudentsLoaded = ldResponse.getNumberOfNewStudents();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException{
    nbCommandsUsed++;

      out.println(RouletteV1Protocol.CMD_RANDOM);
      out.flush();

      String response = in.readLine();
      LOG.info(response);
      RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);

      if(randomResponse.getError() != null){
        LOG.info(randomResponse.getError());
        successCommand = false;
        throw new EmptyStoreException();
      }

      return new Student(randomResponse.getFullname());
    }

  public int getNumberOfStudents() throws IOException{
    nbCommandsUsed++;
    return super.getNumberOfStudents();
  }
}
