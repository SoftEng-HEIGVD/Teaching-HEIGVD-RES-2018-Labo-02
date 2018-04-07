package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private int numberOfStudentAdded;
  private boolean successOfCommand;
  @Override
  public void clearDataStore() throws IOException {
    checkForFirstRequest();

    out.println(RouletteV2Protocol.CMD_CLEAR);
    numberOfCommands++;
    out.flush();

    serveResponse = in.readLine();

    if (serveResponse.equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
      LOG.log(Level.INFO, "data cleared successfully in the server");
    } else {
      throw new IOException("clear went wrong");
    }
  }

  @Override
  public List<Student> listStudents() throws IOException {
    checkForFirstRequest();

    out.println(RouletteV2Protocol.CMD_LIST);
    numberOfCommands++;
    out.flush();

    serveResponse = in.readLine();

    return JsonObjectMapper.parseJson(serveResponse, StudentsList.class).getStudents();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    checkForFirstRequest();

    out.println(RouletteV2Protocol.CMD_LOAD);
    numberOfCommands++;
    out.flush();

    serveResponse = in.readLine();

    out.println(fullname + "\n" + RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    out.flush();

    serveResponse = in.readLine();
    LoadCommandResponse response = JsonObjectMapper.parseJson(serveResponse,LoadCommandResponse.class);
    numberOfStudentAdded = response.getNumberOfNewStudents();
    successOfCommand = response.getStatus().equals("success");
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    checkForFirstRequest();

    out.println(RouletteV1Protocol.CMD_LOAD);
    numberOfCommands++;
    out.flush();

    serveResponse = in.readLine();

    String studdentStr = "";
    for (Student student : students) {
      studdentStr += student.getFullname() + "\n";
    }

    out.println(studdentStr + RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    out.flush();
    serveResponse = in.readLine();
    LoadCommandResponse lcResponse = JsonObjectMapper.parseJson(serveResponse, LoadCommandResponse.class);
    numberOfStudentAdded = lcResponse.getNumberOfNewStudents();
    if(lcResponse.getStatus().equals("success")){
        successOfCommand = true;
      LOG.log(Level.INFO, "students loaded successfully in the server");
    }else{
        successOfCommand = false;
      throw new IOException("loading went wrong");
    }
  }

    public int getNumberOfStudentAdded() {
        return numberOfStudentAdded;
    }

    public boolean checkSuccessOfCommand(){
      return successOfCommand;
    }
}
