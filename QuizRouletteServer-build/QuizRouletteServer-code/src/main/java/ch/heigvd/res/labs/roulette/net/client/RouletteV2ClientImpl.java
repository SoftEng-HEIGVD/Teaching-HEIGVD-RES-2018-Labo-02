package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti, modified by Christophe Joyet and Lionel Nanchen
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final Logger LOG       = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private int numberOfStudentAdded      = 0;
    private int numberOfCommand           = 0;
    private boolean isCommandSuccessfull  = false;

    private LinkedList<Student> l = new LinkedList<>();

    @Override
    public void clearDataStore() throws IOException {
        ++numberOfCommand;
        printWriter.println(RouletteV2Protocol.CMD_CLEAR);
        bufferedReader.readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        ++numberOfCommand;
        printWriter.println(RouletteV2Protocol.CMD_LIST);
        ListCommandResponse message = JsonObjectMapper.parseJson(bufferedReader.readLine(), ListCommandResponse.class);
        return message.getStudents();
    }

    @Override
    public void disconnect() throws IOException {
        ++numberOfCommand;
        printWriter.println(RouletteV2Protocol.CMD_BYE);
        ByeCommandResponse message = JsonObjectMapper.parseJson(bufferedReader.readLine(), ByeCommandResponse.class);
        isCommandSuccessfull = message.getStatus().equals(RouletteV2Protocol.SUCCESS);
        //close all
        clientSocket.close();
        clientSocket = null;
        printWriter.close();
        bufferedReader.close();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        ++numberOfCommand;
        printWriter.println(RouletteV2Protocol.CMD_LOAD);
        bufferedReader.readLine();

        printWriter.println(fullname);

        printWriter.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        LoadCommandResponse message = JsonObjectMapper.parseJson(bufferedReader.readLine(), LoadCommandResponse.class);
        isCommandSuccessfull        = message.getStatus().equals(RouletteV2Protocol.SUCCESS);
        numberOfStudentAdded        = message.getNumberOfNewStudents();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        ++numberOfCommand;
        printWriter.println(RouletteV2Protocol.CMD_LOAD);
        bufferedReader.readLine();

        for(Student student : students){
            printWriter.println(student.getFullname());
        }

        printWriter.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        LoadCommandResponse message = JsonObjectMapper.parseJson(bufferedReader.readLine(), LoadCommandResponse.class);
        isCommandSuccessfull        = message.getStatus().equals(RouletteV2Protocol.SUCCESS);
        numberOfStudentAdded        = message.getNumberOfNewStudents();

    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        ++numberOfCommand;
        return super.pickRandomStudent();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        ++numberOfCommand;
        return super.getProtocolVersion();
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        ++numberOfCommand;
        return super.getNumberOfStudents();
    }

    @Override
    public int getNumberOfStudentAdded() {
        return numberOfStudentAdded;
    }

    @Override
    public int getNumberOfCommands() {
        return numberOfCommand;
    }

    @Override
    public boolean checkSuccessOfCommand() {
        return isCommandSuccessfull;
    }

}
