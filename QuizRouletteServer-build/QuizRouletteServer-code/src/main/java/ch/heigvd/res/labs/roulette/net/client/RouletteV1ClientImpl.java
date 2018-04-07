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
 * @author Olivier Liechti and Guillaume Hochet
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    Socket connexion;
    BufferedReader in;
    PrintWriter out;
    boolean connected = false;

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    @Override
    public void connect(String server, int port) throws IOException {

        LOG.info("Attempting to connect");

        try {

            connexion = new Socket(server, port);
            in = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
            out = new PrintWriter(connexion.getOutputStream());
            connected = true;

            in.readLine();

        } catch (IOException e) {
            LOG.severe(e.getMessage());
            disconnect();
        }
    }

    @Override
    public void disconnect() throws IOException {

        LOG.info("Attempting to disconnect");
        connected = false;

        out.println(RouletteV1Protocol.CMD_BYE);
        out.flush();

        if(in != null)
            in.close();

        if(out != null)
            out.close();

        if(connexion != null && connexion.isConnected())
            connexion.close();
    }

    @Override
    public boolean isConnected() {

        return connected && connexion != null;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {

        LOG.info("Loading student " + fullname);
        _loadStudent(fullname);
        in.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {

        LOG.info("Loading list of students");
        _loadStudents(students);
        in.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {

        LOG.info("Picking a random student");
        out.println(RouletteV1Protocol.CMD_RANDOM);
        out.flush();
        RandomCommandResponse response = JsonObjectMapper.parseJson(in.readLine(), RandomCommandResponse.class);

        if(response.getError() != null)
            throw new EmptyStoreException();

        return new Student(response.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {

        LOG.info("Retrieving number of students");
        return getServerInfo().getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {

        LOG.info("Retrieving protocol version");
        return getServerInfo().getProtocolVersion();
    }

    /**
     * Retrieves server information
     * @return  Server info
     * @throws IOException
     */
    protected InfoCommandResponse getServerInfo() throws IOException {

        out.println(RouletteV1Protocol.CMD_INFO);
        out.flush();

        return JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);
    }

    /**
     * Logic shared among clients to load a single student
     * @param student
     * @throws IOException
     */
    void _loadStudent(String student) throws IOException {

        out.println(RouletteV1Protocol.CMD_LOAD);
        out.flush();

        in.readLine(); //Get server response

        out.println(student); //Write student
        out.flush();

        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();
    }

    /**
     * Logic shared among clients to load multiple students
     * @param students
     * @throws IOException
     */
    void _loadStudents(List<Student> students) throws IOException {

        out.println(RouletteV1Protocol.CMD_LOAD);
        out.flush();

        in.readLine();

        for(Student student : students)
            out.println(student.getFullname());

        out.flush();

        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();
    }
}
