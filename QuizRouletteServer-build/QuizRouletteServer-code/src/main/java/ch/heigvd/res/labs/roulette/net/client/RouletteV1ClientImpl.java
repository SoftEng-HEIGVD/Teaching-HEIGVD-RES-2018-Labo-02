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
    BufferedReader bufferedReader = null;
    PrintWriter printWriter = null;

    @Override
    public void connect(String server, int port) throws IOException {
        try {
            socket = new Socket(server, port);
            printWriter = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            LOG.log(Level.FINE, bufferedReader.readLine());

        } catch (UnknownHostException uhe){
            throw new RuntimeException("Unknown Hosts: " + server);
        }

        LOG.log(Level.INFO, "Connected to server " + server + ":" + port);
    }

    @Override
    public void disconnect() throws IOException {

        if(socket != null && socket.isConnected()){
            printWriter.println(RouletteV1Protocol.CMD_BYE);
            printWriter.flush();
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

            printWriter.println(RouletteV1Protocol.CMD_LOAD);   // load
            printWriter.flush();


            LOG.log(Level.FINE, bufferedReader.readLine()); // Send your data [end with ENDOFDATA]

            printWriter.println(fullname);                      // new student
            printWriter.flush();
            printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);   // endload
            printWriter.flush();

            LOG.log(Level.FINE, bufferedReader.readLine()); //DATA LOADED

        } else {
            LOG.log(Level.INFO, "Not conected to server.");
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if(socket != null && socket.isConnected()){

            printWriter.println(RouletteV1Protocol.CMD_LOAD);   // load
            printWriter.flush();

            LOG.log(Level.FINE, bufferedReader.readLine()); // Send your data [end with ENDOFDATA]

            for(Student student : students){
                printWriter.println(student.getFullname());
                printWriter.flush();
            }

            printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);   // endload
            printWriter.flush();


            LOG.log(Level.FINE, bufferedReader.readLine()); //DATA LOADED

        } else {
            LOG.log(Level.INFO, "Not conected to server.");
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {

        if(socket != null && socket.isConnected()){
            if(getNumberOfStudents() == 0){
                throw new EmptyStoreException();
            }

            Student student = new Student();

            printWriter.println(RouletteV1Protocol.CMD_RANDOM);   // get random student
            printWriter.flush();

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
            printWriter.println(RouletteV1Protocol.CMD_INFO);   // get random student
            printWriter.flush();
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
            //bufferedReader.reset();
            printWriter.println(RouletteV1Protocol.CMD_INFO);   // get random student
            printWriter.flush();

            InfoCommandResponse icr = JsonObjectMapper.parseJson(bufferedReader.readLine(), InfoCommandResponse.class);

            return icr.getProtocolVersion();

        } else {
            LOG.log(Level.INFO, "Not connected to server.");
            return null;
        }
    }
}
