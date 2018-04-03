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

    @Override
    public void connect(String server, int port) throws IOException {
        LOG.info("connecting...");
        socket = new Socket(server, port);
        buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void disconnect() throws IOException {
        if (socket != null) {
            LOG.info("disconnecting...");
            write(RouletteV1Protocol.CMD_BYE);
            buffReader.close();
            printWriter.close();
            socket.close();
        } else {
            LOG.info("already disconnected...");
        }
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        LOG.info("load one student...");
        write(RouletteV1Protocol.CMD_LOAD);
        buffReader.readLine();
        write(fullname);
        write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        buffReader.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        LOG.info("loading a list of students...");
        write(RouletteV1Protocol.CMD_LOAD);
        buffReader.readLine();
        for (Student s : students) {
            write(s.getFullname());
        }
        write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        buffReader.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
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
        write(RouletteV1Protocol.CMD_INFO);
        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(buffReader.readLine(), InfoCommandResponse.class);
        return infoCommandResponse.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        write(RouletteV1Protocol.CMD_INFO);
        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(buffReader.readLine(), InfoCommandResponse.class);
        return infoCommandResponse.getProtocolVersion();
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
