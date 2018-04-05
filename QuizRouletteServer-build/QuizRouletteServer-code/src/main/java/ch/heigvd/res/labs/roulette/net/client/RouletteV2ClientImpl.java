package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {


    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

    private int NbrOfCommands;
    private int NbrOfStudentsAdded;

    @Override
    public void clearDataStore() throws IOException {

        commandSuccess = false;
        NbrOfCommands++;
        if (socket != null && socket.isConnected()) {

            Student student = new Student();

            printWriter.println(RouletteV2Protocol.CMD_CLEAR);   // clear the student list
            printWriter.flush();

            student.setFullname(bufferedReader.readLine());
            commandSuccess = true;
        } else {
            LOG.log(Level.SEVERE, "Not conected to server.");
            throw new IOException();
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {
        commandSuccess = false;
        NbrOfCommands++;
        if (socket != null && socket.isConnected()) {

            printWriter.println(RouletteV2Protocol.CMD_LIST);   // get all students
            printWriter.flush();

            StudentsList studentsList = JsonObjectMapper.parseJson(bufferedReader.readLine(), StudentsList.class);

            commandSuccess = true;
            return studentsList.getStudents();
        } else {
            LOG.log(Level.SEVERE, "Not conected to server.");
            throw new IOException();
        }
    }

    @Override
    public int getNumberOfCommands() {
        return NbrOfCommands;
    }

    @Override
    public int getNumberOfStudentAdded() {
        return NbrOfStudentsAdded;
    }

// V1 functions called with a counter of commands

    @Override
    public void disconnect() throws IOException {
        super.disconnect();
        NbrOfCommands++;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        NbrOfCommands++;
        super.loadStudent(fullname);
        NbrOfStudentsAdded = 1;
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        NbrOfCommands++;
        super.loadStudents(students);
        NbrOfStudentsAdded = students.size();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        NbrOfCommands++;
        return super.pickRandomStudent();
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        NbrOfCommands++;
        return super.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        NbrOfCommands++;
        return super.getProtocolVersion();
    }
}
