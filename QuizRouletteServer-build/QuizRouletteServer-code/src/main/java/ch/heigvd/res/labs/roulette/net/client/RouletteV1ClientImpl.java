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
 * @author Olivier Liechti, modified by Lionel Nanchen
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket clientSocket = null;
    private BufferedReader bufferedReader = null;
    private PrintWriter printWriter = null;

    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        bufferedReader.readLine(); //pour lire la premiere ligne affichee ("Hello ...")
    }

    @Override
    public void disconnect() throws IOException {
        if (clientSocket.isConnected()) {
            printWriter.println(RouletteV1Protocol.CMD_BYE);
            clientSocket.close();
            bufferedReader.close();
            printWriter.close();
        } else {
            LOG.log(Level.SEVERE, "Client is already disconnected");
        }
    }

    @Override
    public boolean isConnected() {
        if (clientSocket == null) return false;
        return clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        printWriter.println(RouletteV1Protocol.CMD_LOAD);
        bufferedReader.readLine();
        printWriter.println(fullname);
        printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        bufferedReader.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        printWriter.println(RouletteV1Protocol.CMD_LOAD);
        bufferedReader.readLine();
        for (Student student : students) {
            printWriter.println(student.getFullname());
        }
        printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        bufferedReader.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        printWriter.println(RouletteV1Protocol.CMD_RANDOM);
        RandomCommandResponse response = JsonObjectMapper.parseJson(bufferedReader.readLine(), RandomCommandResponse.class);

        if (!response.getError().isEmpty()) {
            LOG.log(Level.SEVERE, "There is no student");
            throw new EmptyStoreException();
        }

        return Student.fromJson(response.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        printWriter.println(RouletteV1Protocol.CMD_INFO);

        InfoCommandResponse response = JsonObjectMapper.parseJson(bufferedReader.readLine(), InfoCommandResponse.class);
        return response.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        printWriter.println(RouletteV1Protocol.CMD_INFO);
        InfoCommandResponse response = JsonObjectMapper.parseJson(bufferedReader.readLine(), InfoCommandResponse.class);
        return response.getProtocolVersion();
    }
}
