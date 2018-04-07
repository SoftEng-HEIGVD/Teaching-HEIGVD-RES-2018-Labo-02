package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
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
 * @author Labinot Rashiti
 * @author Romain Gallay
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

   // Variables for new fonctionnalities of V2
   boolean commandSuccess = false;
   int numberOfStudentsAdded = 0;
   int nbCommands = 0;

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
      commandSuccess = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
      nbCommands++;
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      ++nbCommands;

      out.println(RouletteV2Protocol.CMD_LOAD);
      out.flush();
      in.readLine();

      //sent all students to the server
      for (Student student : students) {
         out.println(student.getFullname());
         out.flush();
      }

      // end the load
      out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();

      LoadCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), LoadCommandResponse.class);
      commandSuccess = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
      numberOfStudentsAdded = response.getNumberOfNewStudents();
   }

   @Override
   public void disconnect() throws IOException {
      out.println(RouletteV2Protocol.CMD_BYE);
      out.flush();

      ByeCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), ByeCommandResponse.class);
      nbCommands++;
      commandSuccess = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
      connected = false;
      clean();
   }

   // THIS PART IS FOR REDEFINITION of V1, we need it because we have to count the commands.
   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      nbCommands++;
      return super.pickRandomStudent();
   }

   @Override
   public int getNumberOfStudents() throws IOException {
      nbCommands++;
      return super.getNumberOfStudents();
   }

   @Override
   public String getProtocolVersion() throws IOException {
      nbCommands++;
      return super.getProtocolVersion();
   }

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

}
