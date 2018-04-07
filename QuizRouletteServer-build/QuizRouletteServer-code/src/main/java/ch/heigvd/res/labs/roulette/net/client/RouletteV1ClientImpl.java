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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Mentor Reka
 * @author Kamil Amrani
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    protected Socket clientSocket   = null;
    protected BufferedReader reader = null;
    protected PrintWriter writer    = null;
    protected boolean isConnected   = false;

    @Override
    public void connect(String server, int port) throws IOException {
        try {
            Socket clientSocket = new Socket(server, port);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream());
            // Read communications
            reader.readLine();
            LOG.log(Level.INFO, "connected to " + server + ':' + port + " ... ");
            isConnected = true;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occured during connection to socket : {0}", e.getMessage());
            cleanSession();
            throw e;
        }
    }

    @Override
    public void disconnect() throws IOException {
        LOG.log(Level.INFO, "disconnected ... ");
        writer.println(RouletteV1Protocol.CMD_BYE);
        try {
            cleanSession();
        } catch (IOException e) {
            LOG.log(Level.INFO, "An error occured during disconnection : {0}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
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
        for (Student student : students) {
            writer.println(student.getFullname());
            writer.flush();
        }
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        reader.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();
        String student = reader.readLine();

        RandomCommandResponse randomReponse = JsonObjectMapper.parseJson(student, RandomCommandResponse.class);
        if(randomReponse.getError() != null){
            throw new EmptyStoreException();
        }
        return Student.fromJson(student);
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        return getInfo().getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        return getInfo().getProtocolVersion();
    }

    /**
     * Handle classes variables and socket in case of an exception or on disconnect.
     * This method will safely clause socket, reader and writer.
     */
    protected void cleanSession() throws IOException {
        try {
            if (clientSocket != null)
                clientSocket.close();

            if (reader != null)
                reader.close();

            if (writer != null)
                writer.close();
            isConnected = false;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An exception during close happened : ", e);
            throw e;
        }
    }

    /**
     * Will communicate with server in order to get Info in JSON format and
     * return an Object InfoCommandResponse
     * @return the corresponding InfoCommandResponse object
     * @throws IOException
     */
    private InfoCommandResponse getInfo() throws IOException {
        InfoCommandResponse info;
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();
        info = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
        return info;
    }

}
