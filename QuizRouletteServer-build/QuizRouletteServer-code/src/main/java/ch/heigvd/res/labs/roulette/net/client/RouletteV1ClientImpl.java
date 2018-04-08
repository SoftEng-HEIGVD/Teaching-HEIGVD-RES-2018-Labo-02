package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    Socket socket = null;
    BufferedReader buffReader = null;
    PrintWriter printWriter = null;
    int numberOfCommands;
    int numberOFStudentsAdded;

    @Override
    public void connect(String server, int port) throws IOException {
        numberOFStudentsAdded = 0;
        numberOfCommands = 0;
        LOG.info("connecting...");
        socket = new Socket(server, port);
        buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream());
        buffReader.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        if (socket != null) {
            LOG.info("disconnecting...");
            write(RouletteV1Protocol.CMD_BYE);
            numberOfCommands++;
            buffReader.close();
            printWriter.close();
            socket.close();
            numberOFStudentsAdded=0;
        } else {
            LOG.info("already disconnected...");
        }
    }

    @Override
    public boolean isConnected() {
        if (socket == null) {
            return false;
        }
        return socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        numberOFStudentsAdded++;
        numberOfCommands++;
        LOG.info("load one student...");
        write(RouletteV1Protocol.CMD_LOAD);
        buffReader.readLine();
        write(fullname);
        write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        buffReader.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        numberOfCommands++;
        LOG.info("loading a list of students...");
        write(RouletteV1Protocol.CMD_LOAD);
        buffReader.readLine();
        for (Student s : students) {
            write(s.getFullname());
        }
        numberOFStudentsAdded += students.size();
        write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        buffReader.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        numberOfCommands++;
        LOG.info("picking a random student...");
        write(RouletteV1Protocol.CMD_RANDOM);
        RandomCommandResponse randomCommandResponse = JsonObjectMapper.parseJson(buffReader.readLine(), RandomCommandResponse.class);
        if (randomCommandResponse.getError() != null) {
            throw new EmptyStoreException();

        }
        return Student.fromJson(randomCommandResponse.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        numberOfCommands++;
        write(RouletteV1Protocol.CMD_INFO);
        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(buffReader.readLine(), InfoCommandResponse.class);
        return infoCommandResponse.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        numberOfCommands++;
        write(RouletteV1Protocol.CMD_INFO);
        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(buffReader.readLine(), InfoCommandResponse.class);
        return infoCommandResponse.getProtocolVersion();
    }

    public int getNumberOfStudentAdded() {
        numberOfCommands++;
        return numberOFStudentsAdded;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }
    public boolean checkSuccessOfCommand(){
       return true; 
    }
    /**
     * send to server a string it could be a command or anything
     *
     * @param Sting toSend to the server
     * @throws IOException
     */
    protected void write(String toSend) throws IOException {
        printWriter.println(toSend);
        printWriter.flush();
    }

}
