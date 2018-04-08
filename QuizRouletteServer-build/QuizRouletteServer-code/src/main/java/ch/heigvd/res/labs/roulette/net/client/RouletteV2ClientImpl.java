package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private int numberOfStudentAdded = 0;
  private int numberOfCommands = 0;
  private boolean successOfCommand = false;

  @Override
  public void clearDataStore() throws IOException {
      numberOfCommands++;
      out.println(RouletteV2Protocol.CMD_CLEAR);
      out.flush();
      br.readLine();
     }

  @Override
  public List<Student> listStudents() throws IOException {
      numberOfCommands++;
      out.println(RouletteV2Protocol.CMD_LIST);
      out.flush();
      String reponse = br.readLine();
      return JsonObjectMapper.parseJson(reponse, StudentsList.class).getStudents();
     }


    public void disconnect() throws IOException {
      numberOfCommands++;
        if(clientSocket.isConnected()) {
            out.println(RouletteV2Protocol.CMD_BYE);
            successOfCommand = JsonObjectMapper.parseJson(br.readLine(), ByeCommandResponse.class).getStatus().equals(RouletteV2Protocol.etat = "success");
            br.close();
            out.close();
            clientSocket.close();
        } else{
            LOG.log(Level.WARNING, "Client is already disconnected!");
        }
    }

    public void loadStudent(String fullname) throws IOException {
        numberOfCommands++;
        out.println(RouletteV1Protocol.CMD_LOAD);
        br.readLine();
        out.println(fullname);
        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        br.readLine();

    }


    public void loadStudents(List<Student> students) throws IOException {
      numberOfCommands++;
     // out.println(RouletteV2Protocol.CMD_LOAD);
      //successOfCommand = JsonObjectMapper.parseJson(br.readLine(),LoadCommandResponse.class).getStatus().equals(RouletteV2Protocol.etat = "success");

    }

    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        numberOfCommands++;
        return super.pickRandomStudent();
    }

    public int getNumberOfStudents() throws IOException {
        numberOfCommands++;
        return super.getNumberOfStudents();
    }

    public String getProtocolVersion() throws IOException {
        numberOfCommands++;
        return super.getProtocolVersion();
    }


  public int getNumberOfStudentAdded(){
    return numberOfStudentAdded;
  }

  public  int getNumberOfCommands(){
    return numberOfCommands;
  }

  public boolean checkSuccessOfCommand(){
      return successOfCommand;
  }

}
