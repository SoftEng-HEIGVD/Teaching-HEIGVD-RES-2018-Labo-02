package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * modify by : Olivier Kopp
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private int numberOfCommand = 0;
    private LoadCommandResponse loadCommandResponse;
    private ByeCommandResponse byeCommandResponse;
    private boolean commandOk = false;

  @Override
  public void clearDataStore() throws IOException {
      send(RouletteV2Protocol.CMD_CLEAR);
      br.readLine();
      numberOfCommand++;
  }

  @Override
  public List<Student> listStudents() throws IOException {
    send(RouletteV2Protocol.CMD_LIST);
    String listCommandResponse = br.readLine();
    numberOfCommand++;
    return JsonObjectMapper.parseJson(listCommandResponse, ListCommandResponse.class).getStudents();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      send(RouletteV1Protocol.CMD_LOAD);
      send(fullname);
      br.readLine();
      send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      numberOfCommand++;
      loadCommandResponse = JsonObjectMapper.parseJson(br.readLine(), LoadCommandResponse.class);
      commandOk = loadCommandResponse.getStatus().equals("success");
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      send(RouletteV1Protocol.CMD_LOAD);
      for (Student s : students){
          send(s.getFullname());
      }
      br.readLine();
      send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      numberOfCommand++;
      loadCommandResponse = JsonObjectMapper.parseJson(br.readLine(), LoadCommandResponse.class);
      commandOk = loadCommandResponse.getStatus().equals("success");
  }

    @Override
    public void disconnect() throws IOException {
        if(socket == null){
            return;
        }
        else{
            send(RouletteV2Protocol.CMD_BYE);
            byeCommandResponse = JsonObjectMapper.parseJson(br.readLine(), ByeCommandResponse.class);
            commandOk = byeCommandResponse.getStatus().equals("success");
            numberOfCommand++;
            socket.close();
            socket = null;
            pw.close();
            br.close();
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
      Student ret = super.pickRandomStudent();
      numberOfCommand++;
      return ret;
    }

    @Override
    public String getProtocolVersion() throws IOException {
      String ret = super.getProtocolVersion();
      numberOfCommand++;
      return ret;
    }

    @Override
    public int getNumberOfStudents() throws IOException {
      int ret = super.getNumberOfStudents();
      numberOfCommand++;
      return ret;
    }

        public int getNumberOfCommands(){
        return numberOfCommand;
    }

    public int getNumberOfStudentAdded(){
      return loadCommandResponse.getNumberOfNewStudents();
    }

    public boolean checkSuccessOfCommand(){
      return commandOk;
    }



}
