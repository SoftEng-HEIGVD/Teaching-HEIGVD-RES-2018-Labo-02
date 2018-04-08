package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.*;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    int numberOfStudentsAdded = 0;
    int nbOfCommands = 0;
    boolean commandSucceeded = false;


  @Override
  public void clearDataStore() throws IOException {

    writer.println(RouletteV2Protocol.CMD_CLEAR);
    writer.flush();

    String serverAnswer = reader.readLine();
    if (!serverAnswer.equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
      System.out.println("An error occured while clearing data");
    }
    nbOfCommands++;
  }

  @Override
  public List<Student> listStudents() throws IOException {

    writer.println(RouletteV2Protocol.CMD_LIST);
    writer.flush();

    String serverAnswer = reader.readLine();

    nbOfCommands++;

    // We parse the JSON serverAnswer and get a list of students from it
    ListCommandResponse students = JsonObjectMapper.parseJson(serverAnswer, ListCommandResponse.class);
    return students.getStudents();
  }
    @Override
    public void loadStudent(String fullname) throws IOException {

        writer.println(RouletteV2Protocol.CMD_LOAD);
        writer.flush();

        String loadResponse = reader.readLine();

        if (!loadResponse.equals(RouletteV2Protocol.RESPONSE_LOAD_START)) {
            System.out.println("Error while using" + RouletteV2Protocol.CMD_LOAD + "  command");
            return;
        }
        writer.println(fullname);
        writer.flush();

        writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();

        loadResponse = reader.readLine();

        LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(loadResponse, LoadCommandResponse.class);
        commandSucceeded = loadCommandResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
        numberOfStudentsAdded = loadCommandResponse.getNumberOfNewStudents();
        nbOfCommands++;
    }
    @Override
    public void loadStudents(List<Student> students) throws IOException {

        writer.println(RouletteV2Protocol.CMD_LOAD);
        writer.flush();

        String loadResponse = reader.readLine();

        for (Student student : students) {
            writer.println(student.getFullname());
            writer.flush();
        }

        writer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();

        nbOfCommands++;


        if (!loadResponse.equals(RouletteV2Protocol.RESPONSE_LOAD_DONE)) {
            System.out.println("An error occured at the end of the process");
        }

        loadResponse = reader.readLine();

        LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(loadResponse, LoadCommandResponse.class);
        commandSucceeded = loadCommandResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
        numberOfStudentsAdded = loadCommandResponse.getNumberOfNewStudents();
    }

    @Override
    public void disconnect() throws IOException {
        // calls the isConnected() method
        if (socket == null || !isConnected()) {
            return;
        }

        // We print the BYE message
        writer.println(RouletteV2Protocol.CMD_BYE);
        writer.flush();

        ByeCommandResponse byeResponse = JsonObjectMapper.parseJson(reader.readLine(), ByeCommandResponse.class);
        nbOfCommands++;
        commandSucceeded = byeResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);

        // We close connexion and streams
        reader.close();
        writer.close();
        socket.close();

        socket = null;
    }

    @Override
    public int getNumberOfStudentAdded() {
        return numberOfStudentsAdded;
    }

    @Override
    public int getNumberOfCommands() {
        return nbOfCommands;
    }

    @Override
   public int getNumberOfStudents() throws IOException {
        nbOfCommands++;
        return super.getNumberOfStudents();
    }

    @Override
   public String getProtocolVersion() throws IOException {
        nbOfCommands++;
        return super.getProtocolVersion();
    }

   @Override
   public boolean checkSuccessOfCommand() {
       return commandSucceeded;
    }

    @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
        nbOfCommands++;
        return super.pickRandomStudent();
    }
}
