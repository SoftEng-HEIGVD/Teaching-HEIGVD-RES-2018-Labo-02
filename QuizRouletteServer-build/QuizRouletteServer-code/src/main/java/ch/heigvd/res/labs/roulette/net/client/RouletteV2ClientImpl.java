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
 * @author Guillaume Blanco, Patrick Neto
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private int numberOfStudentAdded = 0;
  private boolean successOfCommand = false;
  private int numberOfCommands = 0;

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
        //Verify if client is connected
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

    @Override
    public void loadStudent(String fullname) throws IOException {
        numberOfCommands++;
        out.println(RouletteV1Protocol.CMD_LOAD);
        br.readLine();
        out.println(fullname);
        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        LoadCommandResponse message = JsonObjectMapper.parseJson(br.readLine(), LoadCommandResponse.class);
        successOfCommand        = message.getStatus().equals(RouletteV2Protocol.RESPONSE_SUCCESS);
        numberOfStudentAdded        = message.getNumberOfNewStudents();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        numberOfCommands++;
        if(students != null) {
            out.println(RouletteV1Protocol.CMD_LOAD);
            br.readLine();
            //
            for (Student student : students)
                out.println(student.getFullname());

            out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            LoadCommandResponse message = JsonObjectMapper.parseJson(br.readLine(), LoadCommandResponse.class);

            numberOfStudentAdded = message.getNumberOfNewStudents();
            successOfCommand = message.getStatus().equals(RouletteV2Protocol.RESPONSE_SUCCESS);
        }
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
