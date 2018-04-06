package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponseV2;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponseV3;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private BufferedReader is;
  private PrintWriter os;
  public static boolean commandSucceded = false;

  @Override
  public void clearDataStore() throws IOException {
    os.println(RouletteV2Protocol.CMD_CLEAR);
    os.flush();
  }


  @Override
  public List<Student> listStudents() throws IOException {
    os.println(RouletteV2Protocol.CMD_LIST);
    os.flush();
    String response = is.readLine();
    Student s = Student.fromJson(JsonObjectMapper.parseJson(response, String.class));
    //TODO récupérer le JSON sous forme de liste de students
    List<Student> students = new ArrayList<Student>();
    students.add(s);
    return students;
  }


  @Override
  public void loadStudent(String fullname) throws IOException {
    try{
      super.loadStudent(fullname);
      commandSucceded = true;

    }catch(IOException e){
      commandSucceded = false;
    }

  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    try{
      super.loadStudents(students);
      //numberOfNewStudents += students.size();
      commandSucceded = true;

    }catch(IOException e){
      commandSucceded = false;
      throw e;
    }
  }
    @Override
  public int getNumberOfStudentAdded() {
    return 0;
  }

    @Override
    public int getNumberOfCommands() throws IOException {
        return InfoCmdRsp3().getNumberOfCommands();
    }

    @Override
    public boolean checkSuccessOfCommand(){
      return commandSucceded;
    }

  @Override
  public void disconnect() throws IOException {
    os.println(RouletteV2Protocol.CMD_BYE);
    os.flush();
    is.readLine();
    super.disconnect();
  }

  private InfoCommandResponseV2 InfoCmdRsp2() throws IOException{
    InfoCommandResponseV2 iCR2 = JsonObjectMapper.parseJson(is.readLine() , InfoCommandResponseV2.class);
    return iCR2;
  }
  private InfoCommandResponseV3 InfoCmdRsp3() throws IOException{
    InfoCommandResponseV3 iCR3 = JsonObjectMapper.parseJson(is.readLine() , InfoCommandResponseV3.class);
    return iCR3;
  }


}
