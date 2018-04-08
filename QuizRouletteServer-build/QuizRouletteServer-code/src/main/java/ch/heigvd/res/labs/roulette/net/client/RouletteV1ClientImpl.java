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

    protected Socket clientSocket = null;

    protected BufferedReader reader = null;
    //BufferedWriter writer = null;
    protected PrintWriter    writer = null;

    // initially not connected
    private boolean connectionStatus = false;

    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);

        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        //writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        writer = new PrintWriter(clientSocket.getOutputStream());

        connectionStatus = true;

        // to skip the first line
        reader.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        writer.close();
        reader.close();
        clientSocket.close();

        connectionStatus = false;
    }

    @Override
    public boolean isConnected() {
        return connectionStatus;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();
        reader.readLine();
        writer.println(fullname);
        writer.flush();
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        reader.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {

        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();
        reader.readLine();
        for(Student student : students) {
            writer.println(student.getFullname());
            writer.flush();
        }
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        reader.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if(getNumberOfStudents() == 0)
            throw new EmptyStoreException();

        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();

        RandomCommandResponse randResponse = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);

        return new Student(randResponse.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

        return infoCommandResponse.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

        return infoCommandResponse.getProtocolVersion();
    }


}
