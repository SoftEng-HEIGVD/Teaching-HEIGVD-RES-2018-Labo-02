package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

   final String SUCCESS_MSG = "success";
   boolean commandSuccess = false;
   int numberOfStudentsAdded = 0;
   int nbCommands = 0;
   
   @Override
   public int getNumberOfStudentAdded() {
      return numberOfStudentsAdded;
   }

   @Override
   public int getNumberOfCommands() {
      return nbCommands;
   }

   @Override
   public boolean checkSuccessOfCommand() {
      return commandSuccess;
   }
  
   
   @Override
   public void disconnect() throws IOException {
      connected = false;
      out.println(RouletteV2Protocol.CMD_BYE);
      out.flush();

      ByeCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), ByeCommandResponse.class);
      nbCommands = response.getNumberOfCommands();
      clean();
   }
   
   @Override
   public void clearDataStore() throws IOException {
      out.println(RouletteV2Protocol.CMD_CLEAR);
      out.flush(); // sent the message procotol
      in.readLine(); // read the answer from server
      nbCommands++;

   }

   @Override
   public List<Student> listStudents() throws IOException {
      // Send the command protocol
      out.println(RouletteV2Protocol.CMD_LIST);
      out.flush();

      String list = in.readLine(); // read the answer from server
      nbCommands++;

      // parse the JSON answer from server and get a List<Student> from it
      return JsonObjectMapper.parseJson(list, StudentsList.class).getStudents();
   }

   @Override
   public void loadStudent(String fullname) throws IOException {
      // Send the command protocol
      out.println(RouletteV2Protocol.CMD_LOAD);
      out.flush();
      in.readLine();

      // give the name of the student
      out.println(fullname);
      out.flush();

      // End the load
      out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();

      LoadCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
      numberOfStudentsAdded = response.getNumberOfNewStudents();
      commandSuccess = response.getStatus().equals(SUCCESS_MSG);
      nbCommands++;
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      // Send the command protocol
      out.println(RouletteV2Protocol.CMD_LOAD);
      out.flush();
      in.readLine();

      for (Student student : students) {
         out.println(student.getFullname());
         out.flush();
      }
      
      // End the load
      out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      LoadCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
      numberOfStudentsAdded = response.getNumberOfNewStudents();
      commandSuccess = response.getStatus().equals(SUCCESS_MSG);
      nbCommands++;
   }


   

   
}
