package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
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
  protected int nbStudentAdded = 0;
  @Override
  public void clearDataStore() throws IOException {
    printlnWithFlush(RouletteV2Protocol.CMD_CLEAR);
    bris.readLine();
    nbCommamde++;
  }

  @Override
  public List<Student> listStudents() throws IOException {
    printlnWithFlush(RouletteV2Protocol.CMD_LIST);
    ListCommandResponse lcResponse = JsonObjectMapper.parseJson(bris.readLine(),ListCommandResponse.class);
    nbCommamde++;
    return lcResponse.getStudents();
  }


  public int getNumberOfStudentAdded() throws IOException {
    return nbStudentAdded;
  }

  public int getNumberOfCommands() throws IOException {
    return nbCommamde;
  }

  public boolean checkSuccessOfCommand(){
    return lastCommandStatus;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {

    printlnWithFlush(RouletteV1Protocol.CMD_LOAD);

    //Send data line
    LOG.log(Level.INFO,bris.readLine());

    printlnWithFlush(fullname );
    printlnWithFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);


    LoadCommandResponse lc = JsonObjectMapper.parseJson(bris.readLine(),LoadCommandResponse.class);
    nbStudentAdded = lc.getNumberOfNewStudents();
    if(lc.getStatus().equalsIgnoreCase(RouletteV2Protocol.REPONSE_FAILURE)){
      lastCommandStatus = false;
    }else{
      lastCommandStatus = true;
    }

    nbCommamde++;
  }



  public void loadStudents(List<Student> students) throws IOException {

    printlnWithFlush(RouletteV1Protocol.CMD_LOAD);

    //Send of data line
    LOG.log(Level.INFO,bris.readLine());

    for(Student s : students){
      printlnWithFlush(s.getFullname());
    }
    printlnWithFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

    //Send data loaded linse
    LoadCommandResponse lc = JsonObjectMapper.parseJson(bris.readLine(),LoadCommandResponse.class);
    nbStudentAdded = lc.getNumberOfNewStudents();
    if(lc.getStatus().equalsIgnoreCase(RouletteV2Protocol.REPONSE_FAILURE)){
      lastCommandStatus = false;
    }else{
      lastCommandStatus = true;
    }

    nbCommamde++;
  }


}
