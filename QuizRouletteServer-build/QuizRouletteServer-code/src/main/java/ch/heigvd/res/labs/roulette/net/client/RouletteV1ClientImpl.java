package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
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

    Socket socket = null;

    @Override
    public void connect(String server, int port) throws IOException {
        try {
            socket = new Socket(server, port);
        } catch (UnknownHostException uhe){
            throw new RuntimeException("Unknown Hosts: " + server);
        }

        LOG.log(Level.INFO, "Connected to server " + server + ":" + port);
    }

    @Override
    public void disconnect() throws IOException {

        if(socket != null && socket.isConnected()){
            socket.getOutputStream().write(RouletteV1Protocol.CMD_BYE.getBytes());
            socket.close();
            LOG.log(Level.INFO, "Disconected from server.");
        } else {
            LOG.log(Level.INFO, "Not conected to server.");
        }
    }

    @Override
    public boolean isConnected() {
        if(socket != null && socket.isConnected()){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        if(socket != null && socket.isConnected()){
            // get the outputstream to the server
            OutputStream os = socket.getOutputStream();

            os.write(RouletteV1Protocol.CMD_LOAD.getBytes());   // load
            os.write(fullname.getBytes());                      // new student
            os.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER.getBytes());   // endload

        } else {
            LOG.log(Level.INFO, "Not conected to server.");
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if(socket != null && socket.isConnected()){
            // get the outputstream to the server
            OutputStream os = socket.getOutputStream();

            os.write(RouletteV1Protocol.CMD_LOAD.getBytes());   // load

            for(Student student : students){
                os.write(student.getFullname().getBytes());
            }

            os.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER.getBytes());   // endload

        } else {
            LOG.log(Level.INFO, "Not conected to server.");
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {

        if(socket != null && socket.isConnected()){
            Student student = new Student();

            // get the outputstream to the server
            OutputStream os = socket.getOutputStream();
            os.write(RouletteV1Protocol.CMD_RANDOM.getBytes());   // get random student

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            student.setFullname(bufferedReader.readLine());

            return student;

        } else {
            LOG.log(Level.INFO, "Not conected to server.");
            return null;
        }
    }

    @Override
    public int getNumberOfStudents() throws IOException {

        if(socket != null && socket.isConnected()){

            // get the outputstream to the server
            OutputStream os = socket.getOutputStream();
            os.write(RouletteV1Protocol.CMD_INFO.getBytes());   // get random student

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InfoCommandResponse icr = JsonObjectMapper.parseJson(bufferedReader.readLine(), InfoCommandResponse.class);

            return icr.getNumberOfStudents();

        } else {
            LOG.log(Level.INFO, "Not connected to server.");
            return 0;
        }
    }

    @Override
    public String getProtocolVersion() throws IOException {
        if (socket != null && socket.isConnected()) {

            // get the outputstream to the server
            OutputStream os = socket.getOutputStream();
            os.write(RouletteV1Protocol.CMD_INFO.getBytes());   // get random student

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InfoCommandResponse icr = JsonObjectMapper.parseJson(bufferedReader.readLine(), InfoCommandResponse.class);

            return icr.getProtocolVersion();

        } else {
            LOG.log(Level.INFO, "Not connected to server.");
            return null;
        }
    }
}
