package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
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
import java.nio.charset.StandardCharsets;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    protected static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    protected Socket clientSocket = null;
    protected PrintWriter os = null;
    protected BufferedReader is = null;
    protected int nbCommand = 0;
    protected int nbStudent = 0;

    @Override
    public void connect(String server, int port) throws IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        LOG.info("Try to connect");
        try {
            clientSocket = new Socket(server, port);
            os = new PrintWriter(clientSocket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

        } catch (IOException e) {
            LOG.severe(e.getMessage());
            disconnect();
        }
    }

    @Override
    public void disconnect() throws IOException {
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        LOG.info("Try to disconnect");
        if (os != null) {
            os.close();
        }
        if (is != null) {
            is.close();
        }

        if (isConnected()) {
            clientSocket.close();
        }

        nbCommand++;
    }

    @Override
    public boolean isConnected() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        nbCommand++;
        return clientSocket != null;

    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        nbCommand++;
        LOG.info("Try to load " + fullname);
        os.println(RouletteV1Protocol.CMD_LOAD);
        os.flush();

        is.readLine();

        os.println(fullname);
        os.flush();
        nbStudent++;

        os.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        nbCommand++;
        LOG.info("Try to load a Student List");
        os.println(RouletteV1Protocol.CMD_LOAD);
        os.flush();

        is.readLine();

        for (Student student : students) {
            os.println(student.getFullname());
            os.flush();
            nbStudent++;
        }

        os.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        nbCommand++;
        LOG.info("Pick a random student");
        os.println(RouletteV1Protocol.CMD_RANDOM);
        os.flush();

        RandomCommandResponse response = JsonObjectMapper.parseJson(is.readLine(), RandomCommandResponse.class);

        if (response.getError() != null) {
            throw new EmptyStoreException();
        }

        return new Student(response.getFullname());

    }

    @Override
    public int getNumberOfStudents() throws IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        nbCommand++;
        LOG.info("Get the number of Students");
        return getServerInfo().getNumberOfStudents();

    }

    @Override
    public String getProtocolVersion() throws IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        nbCommand++;
        LOG.info("Get the protocol version");
        return getServerInfo().getProtocolVersion();

    }

    protected InfoCommandResponse getServerInfo() throws IOException {

        os.println(RouletteV1Protocol.CMD_INFO);
        os.flush();

        return JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class);
    }

}
