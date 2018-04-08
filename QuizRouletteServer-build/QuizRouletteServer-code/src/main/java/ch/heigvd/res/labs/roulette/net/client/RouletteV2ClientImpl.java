package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ListStudentCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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

    private boolean statusLastCommand = false;
    private int nbNewStudents = 0;

    @Override
    public void clearDataStore() throws IOException {
        LOG.log(Level.INFO, "Clearing the data store...");

        writeInWriter(RouletteV2Protocol.CMD_CLEAR);
        reader.readLine();

        nbCommands++;
    }

    @Override
    public List<Student> listStudents() throws IOException {
        LOG.log(Level.INFO, "Listing the students...");

        writeInWriter(RouletteV2Protocol.CMD_LIST);

        nbCommands++;

        return JsonObjectMapper.parseJson(reader.readLine(), ListStudentCommandResponse.class).getStudents();
    }

    @Override
    public int getNumberOfStudentAdded() throws IOException {
        return nbNewStudents;
    }

    @Override
    public boolean checkSuccessOfCommand() throws IOException {
        return statusLastCommand;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        LOG.log(Level.INFO, "Loading student " + fullname);

        writeInWriter(RouletteV2Protocol.CMD_LOAD);

        reader.readLine();

        writeInWriter(fullname);
        writeInWriter(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

//        String responseReader = reader.readLine();

        LoadCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
        statusLastCommand = response.getStatus().equals("success");

        if(statusLastCommand)
            nbNewStudents = response.getNumberOfNewStudents();

        nbCommands++;
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        LOG.log(Level.INFO, "Loading a list of students");

        writeInWriter(RouletteV2Protocol.CMD_LOAD);

        reader.readLine();

        for (Student student : students)
            writeInWriter(student.getFullname() );

        writeInWriter(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        LoadCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), LoadCommandResponse.class);
        statusLastCommand = response.getStatus().equals("success");

        if(statusLastCommand)
            nbNewStudents = response.getNumberOfNewStudents();

        nbCommands++;
    }

}