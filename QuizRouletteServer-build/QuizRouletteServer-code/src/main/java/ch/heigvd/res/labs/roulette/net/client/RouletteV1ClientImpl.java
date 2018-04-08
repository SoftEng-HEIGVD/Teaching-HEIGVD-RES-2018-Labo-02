package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    protected Socket socket = null;
    protected BufferedReader reader = null;
    protected PrintWriter writer = null;

    protected int nbCommands = 0;

    @Override
    public void connect(String server, int port) throws IOException {
        LOG.log(Level.INFO, "Attempting to connect...");

        socket = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        if (isConnected())
            reader.readLine();

        LOG.log(Level.INFO, "Connected");
    }

    @Override
    public void disconnect() throws IOException {
        LOG.log(Level.INFO, "Attempting to disconnect...");
        closeConnection();
        LOG.log(Level.INFO, "Disconnected");
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        LOG.log(Level.INFO, "Loading student " + fullname);

        writeInWriter(RouletteV1Protocol.CMD_LOAD);

        reader.readLine();

        writeInWriter(fullname);
        writeInWriter(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        nbCommands++;
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        LOG.log(Level.INFO, "Loading a list of students");

        writeInWriter(RouletteV1Protocol.CMD_LOAD);

        reader.readLine();

        for (Student student : students)
            writeInWriter(student.getFullname() + System.lineSeparator());

        writeInWriter(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        nbCommands++;
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        LOG.log(Level.INFO, "Pick a random student");

        writeInWriter(RouletteV1Protocol.CMD_RANDOM);

        RandomCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);

        // There is no student stored
        if (response.getError() != null)
            throw new EmptyStoreException();

        nbCommands++;

        return Student.fromJson(response.toString());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        LOG.log(Level.INFO, "Counting the number of students...");

        writeInWriter(RouletteV1Protocol.CMD_INFO);

        InfoCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

        nbCommands++;

        return response.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        LOG.log(Level.INFO, "Getting the protocol version...");

        writeInWriter(RouletteV1Protocol.CMD_INFO);

        InfoCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

        nbCommands++;

        return response.getProtocolVersion();
    }

    public int getNumberOfCommands() throws IOException {
        return nbCommands;
    }

    private void closeConnection() throws IOException {
        if (writer != null)
            writer.close();

        if (reader != null)
            reader.close();


        if (socket != null)
            socket.close();
    }

    protected void writeInWriter(String value) {
        writer.println(value);
        writer.flush();
    }
}