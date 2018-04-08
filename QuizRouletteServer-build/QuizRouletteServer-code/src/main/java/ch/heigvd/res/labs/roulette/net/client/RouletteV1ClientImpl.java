package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
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

    protected int nbCommands = 0;
    protected boolean connected;


    /** ATTRIBUTS **/
    Socket clientSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    @Override
    public void connect(String server, int port) throws IOException {
        try {
            clientSocket = new Socket(server, port);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            skipMessageServer(); // Read: welcome message
            connected = true;
        } catch(IOException e){
            LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
            cleanup();
            return;
        }
    }

    @Override
    public void disconnect() throws IOException {
        nbCommands++;
        if(isConnected()) {
            LOG.log(Level.INFO, "client has request to be disconnect.");
            sendToServer(RouletteV1Protocol.CMD_BYE);
            connected = false;
            cleanup();
        }
    }

    @Override
    public boolean isConnected() {
       return connected;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        nbCommands++;
        sendToServer(RouletteV1Protocol.CMD_LOAD);
        skipMessageServer(); // Read: Send your data [end with ENDOFDATA]
        sendToServer(fullname); // Send name of the student to load
        sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        skipMessageServer(); // Read: DATA LOADED
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        nbCommands++;
        LOG.log(Level.INFO, "Load students", students);
        sendToServer(RouletteV1Protocol.CMD_LOAD);
        skipMessageServer(); // Read: Send your data [end with ENDOFDATA]

        for(Student s: students){
            sendToServer(s.getFullname()); // name of the student to load
        }

        sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER); // END of loading data
        skipMessageServer(); // Read: DATA LOADED
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        nbCommands++;
        sendToServer(RouletteV1Protocol.CMD_RANDOM);
        String s = in.readLine(); // get response from server

        LOG.log(Level.INFO, s);

        RandomCommandResponse rcr = JsonObjectMapper.parseJson(s, RandomCommandResponse.class); // Check if error

        if(rcr.getError() != null){
            throw new EmptyStoreException();
        }

        return Student.fromJson(s); // return student from string (Json)
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        nbCommands++;
        sendToServer(RouletteV1Protocol.CMD_INFO);
        String s = in.readLine(); // Read response from server

        LOG.log(Level.INFO, s); // Log data received

        InfoCommandResponse info = JsonObjectMapper.parseJson(s, InfoCommandResponse.class); // Parse data

        return info.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        nbCommands++;
        sendToServer(RouletteV1Protocol.CMD_INFO);
        String s = in.readLine();

        InfoCommandResponse info = JsonObjectMapper.parseJson(s, InfoCommandResponse.class);

        return info.getProtocolVersion();
    }

    protected void cleanup(){
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }

        if (out != null) {
            out.close();
        }

        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    protected void sendToServer(String s){
        out.println(s);
        out.flush();
    }

    protected void skipMessageServer() throws IOException{
        in.readLine();
    }

}
