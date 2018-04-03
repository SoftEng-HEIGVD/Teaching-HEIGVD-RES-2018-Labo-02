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
import java.io.Reader;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 * @author Adam Zouari
 * @author Nair Alic
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter((clientSocket.getOutputStream()));

        // consume the welcolm messsage from the server
        in.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        sendToServer(RouletteV1Protocol.CMD_BYE);

        in.close();
        out.close();
        clientSocket.close();
    }

    @Override
    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {

        sendToServer(RouletteV1Protocol.CMD_LOAD);
        in.readLine();

        sendToServer(fullname);

        sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        in.readLine();

    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        sendToServer(RouletteV1Protocol.CMD_LOAD);
        in.readLine();

        for (Student s : students) {
            sendToServer(s.getFullname());
        }
        sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        in.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        sendToServer(RouletteV1Protocol.CMD_RANDOM);

        RandomCommandResponse studentJson = JsonObjectMapper.parseJson(in.readLine(), RandomCommandResponse.class);
        if (studentJson.getError() != null) {
            throw new EmptyStoreException();
        }
        return Student.fromJson(studentJson.getFullname());

    }

    @Override
    public int getNumberOfStudents() throws IOException {
        return getInfos().getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        return getInfos().getProtocolVersion();
    }

    private void sendToServer(String s) {
        out.println(s);
        out.flush();
    }

    private InfoCommandResponse getInfos() throws IOException {
        sendToServer(RouletteV1Protocol.CMD_INFO);
        return JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);
    }
}
