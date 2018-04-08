package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti, Marc Labie
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private int numberOfNewStudent = 0;
  private int numberOfCommands   = 0;
  private String status = "";

  @Override
  public void clearDataStore() throws IOException {
      numberOfCommands++;  // we need to count it this way so we don't disconnect...
    try {
      pw.println(RouletteV2Protocol.CMD_CLEAR);  // CLEAR
      answer = br.readLine();   // lit : "DATASTORE CLEARED"
    }catch (IOException e){
      throw e;
    }
  }

  @Override
  public List<Student> listStudents() throws IOException {
      numberOfCommands++;  // we need to count it this way so we don't disconnect...
    List<Student> students;
    try {
      pw.println(RouletteV2Protocol.CMD_LIST);  // LIST
      answer = br.readLine();
    }catch (IOException e){
      throw e;
    }
    students = JsonObjectMapper.parseJson(answer, StudentListCommand.class).getStudents();

    return students;
  }


  @Override
  public void loadStudent(String fullname) throws IOException {
      numberOfCommands++;  // we need to count it this way so we don't disconnect...

    try {
      pw.println(RouletteV2Protocol.CMD_LOAD);  // LOAD
      br.readLine();              // Lit : "Send your data: [end with ENDOFDATA]"
      pw.println(fullname);
      pw.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);  // ENDOFDATA
      answer = br.readLine();

    }catch (IOException e){
      throw e;
    }

    // update the status and the numberOfNewStudents.
    status             = JsonObjectMapper.parseJson(answer, LoadCommandResponse.class).getStatus();
    numberOfNewStudent = JsonObjectMapper.parseJson(answer, LoadCommandResponse.class).getNumberOfNewStudents();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      numberOfCommands++;  // we need to count it this way so we don't disconnect...
    try {
      pw.println(RouletteV2Protocol.CMD_LOAD);  // LOAD
      br.readLine();              // Lit : "Send your data: [end with ENDOFDATA]"
      for(Student s : students)
        pw.println(s.getFullname());

      pw.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);  // ENDOFDATA
      answer = br.readLine();

    }catch (IOException e){
      throw e;
    }

    // update the status and the numberOfNewStudents.
    status             = JsonObjectMapper.parseJson(answer, LoadCommandResponse.class).getStatus();
    numberOfNewStudent = JsonObjectMapper.parseJson(answer, LoadCommandResponse.class).getNumberOfNewStudents();
  }

  @Override
  public void disconnect() throws IOException {

      numberOfCommands++;
    if(!this.isConnected())
        return;

    try {
      pw.println(RouletteV2Protocol.CMD_BYE);  // BYE
      answer = br.readLine();
      pw.close();
      br.close();
      is.close();
      os.close();
      socket.close();
    }catch (IOException e){
      throw e;
    }
    // update the status and the number of commands sent.
    status           = JsonObjectMapper.parseJson(answer, ByeCommandResponse.class).getStatus();
    // Okay. I have spend enough time on this. This makes no sense. Having an internal counter that counts differently
      // than the one for the server ? It makes my code incomprehensible.
    //numberOfCommands = JsonObjectMapper.parseJson(answer, ByeCommandResponse.class).getNumberOfCommands();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      numberOfCommands++;  // we need to count it this way so we don't disconnect...
      return super.pickRandomStudent();
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      numberOfCommands++;  // we need to count it this way so we don't disconnect...
      return super.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
      numberOfCommands++;  // we need to count it this way so we don't disconnect...
      return super.getProtocolVersion();
  }

  @Override
  public int getNumberOfCommands() throws IOException{
    return numberOfCommands;
  }


  @Override
  public int getNumberOfStudentAdded() throws IOException{
    return numberOfNewStudent;
  }

  @Override
  public boolean checkSuccessOfCommand() throws IOException{
    return status.equals("success");
  }
}