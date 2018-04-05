package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket clientSocket = new Socket();

    BufferedReader is = null;
    PrintWriter os = null;

    private RouletteV1Protocol rv1p;

    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        os = new PrintWriter(clientSocket.getOutputStream());
        is.readLine();

    }

    @Override
    public void disconnect() throws IOException {
        os.println(RouletteV1Protocol.CMD_BYE);
        is.close();
        os.close();
        clientSocket.close();

    }

    @Override
    public boolean isConnected() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return clientSocket.isConnected();

    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        os.println(RouletteV1Protocol.CMD_LOAD);
        os.flush();
        is.readLine();
        os.println(fullname);
        os.flush();
        os.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
        is.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {

        for (Student s : students) {
            loadStudent(s.getFullname());
        }

    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        os.println(RouletteV1Protocol.CMD_RANDOM);
        os.flush();
        String response = is.readLine();
        RandomCommandResponse resp = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);
        if ( !resp.getError().isEmpty()) {
            throw new EmptyStoreException();
        }
        return new Student(resp.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        os.println(RouletteV1Protocol.CMD_INFO);
        os.flush();
        String response = is.readLine();
        InfoCommandResponse resp = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
        return resp.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        os.println(RouletteV1Protocol.CMD_INFO);
        os.flush();
        String response = is.readLine();
        InfoCommandResponse resp = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
        return resp.getProtocolVersion();

    }
}
