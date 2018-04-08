package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Miguel Lopes Gouveia(endmon)
 * @author Rémy Nasserzare(remynz)
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    int numberCommands;
    int numberStudents;
    boolean successOfCommand;

    
  @Override
  public void clearDataStore() throws IOException 
  {
    numberCommands++;
    outp.println(RouletteV2Protocol.CMD_CLEAR);
    outp.flush();
    inp.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException 
  {
    numberCommands++;
    outp.println(RouletteV2Protocol.CMD_LIST);
    outp.flush();
    return JsonObjectMapper.parseJson(inp.readLine(), StudentsList.class).getStudents();
  }
  
  @Override
   public void loadStudent(String fullname) throws IOException 
   {
      numberCommands++;
      outp.println(RouletteV2Protocol.CMD_LOAD);
      outp.flush();
      inp.readLine();

      outp.println(fullname);
      outp.flush();
      
      outp.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      outp.flush();

      LoadCommandResponse response = JsonObjectMapper.parseJson(inp.readLine(), LoadCommandResponse.class);
      numberStudents = response.getNumberOfNewStudents();
      successOfCommand = response.getStatus().equals("success");
      
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException 
   {
      numberCommands++;

      outp.println(RouletteV2Protocol.CMD_LOAD);
      outp.flush();
      inp.readLine();

      for (Student student : students)
      {
         outp.println(student.getFullname());
         outp.flush();
      }

      outp.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      outp.flush();

      LoadCommandResponse response = JsonObjectMapper.parseJson(inp.readLine(), LoadCommandResponse.class);
      successOfCommand = response.getStatus().equals("success");
      numberStudents = response.getNumberOfNewStudents();
   }

   @Override
   public void disconnect() throws IOException 
   {
      numberCommands++;
      if(isConnected() == false)return;
      
      outp.println(RouletteV2Protocol.CMD_BYE);
      outp.flush();
      inp.readLine();

      inp.close();
      outp.close();
      skt.close();
      okConnect = 0;
   } 
  
  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException
  {
      numberCommands++;
      return super.pickRandomStudent();
  }
  
  @Override
  public int getNumberOfStudents() throws IOException
  {
      numberCommands++;
      return super.getNumberOfStudents();
   }
  
  @Override
   public String getProtocolVersion() throws IOException
   {
      numberCommands++;
      return super.getProtocolVersion();
   }
  
  @Override
  public int getNumberOfCommands() throws IOException
  {
      return numberCommands;
  }
  
  @Override
  public int getNumberOfStudentAdded() throws IOException
  {
      return numberStudents;
  }
  
  public boolean checkSuccessOfCommand() throws IOException
  {
      return successOfCommand;
  }
}