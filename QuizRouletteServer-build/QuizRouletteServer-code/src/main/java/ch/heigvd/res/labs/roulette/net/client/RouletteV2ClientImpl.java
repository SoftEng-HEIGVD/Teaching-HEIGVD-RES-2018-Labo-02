package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.*;
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
    is.readLine();
  }


  @Override
  public List<Student> listStudents() throws IOException {
    os.println(RouletteV2Protocol.CMD_LIST);
    os.flush();
    return InfoCmdStd().getStudents();
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

  // ???
  private InfoCommandResponseV3 InfoCmdRsp3() throws IOException{
    os.println(RouletteV2Protocol.CMD_BYE);
    os.flush();
    InfoCommandResponseV3 iCR3 = JsonObjectMapper.parseJson(is.readLine() , InfoCommandResponseV3.class);
    super.disconnect();
    return iCR3;
  }

  private InfoCommandStudents InfoCmdStd() throws IOException{
    os.println(RouletteV2Protocol.CMD_LIST);
    os.flush();
    InfoCommandStudents iCRS = JsonObjectMapper.parseJson(is.readLine() , InfoCommandStudents.class);
    return iCRS;
  }


}
