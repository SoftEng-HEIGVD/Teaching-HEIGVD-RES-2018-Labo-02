package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
  
  private int numberOfCommande;
  private int NumberOfStudentAdded;
    
  @Override
  public void clearDataStore() throws IOException {
    out.println(RouletteV2Protocol.CMD_CLEAR);
    out.flush();
    super.in.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
    out.println(RouletteV2Protocol.CMD_LIST);
    out.flush();
    String response = in.readLine();

    return JsonObjectMapper.parseJson(response, StudentsList.class).getStudents();
  }
 
  public int getNumberOfCommands()throws IOException{
  throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public int getNumberOfStudentAdded() throws IOException{
      throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean checkSuccessOfCommand()throws IOException{
  throw new UnsupportedOperationException("Not supported yet.");
  }
}
