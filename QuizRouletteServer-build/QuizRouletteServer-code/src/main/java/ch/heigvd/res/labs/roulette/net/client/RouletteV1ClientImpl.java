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
 * @author Olivier Liechti, François Burgener, Bryan Curchod
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    // attribute to communicate with the server
    private Socket sock = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    
    protected String answer;

    protected static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    /**
     * This method establish a connection between our client and a server
     * @param server the IP address or DNS name of the servr
     * @param port the TCP port on which the server is listening
     * @throws IOException
     */
    @Override
    public void connect(String server, int port) throws IOException {
        sock = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        writer = new PrintWriter(sock.getOutputStream());
        if(isConnected())
            readFromServer();
    }

    /**
     * Disconnect the client from the server and close the connexion
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        // send "BYE" message
        sendToServer(RouletteV1Protocol.CMD_BYE);
        // closing the connection
        close();
    }

    /**
     * check if the client is connected to a server (the link is bounded and we can communicate)
     * @return true if the client is successfully connected to the server
     */
    @Override
    public boolean isConnected() {
        // we have to check if the connexion is not closed : CF Oracle documentation : socket.isConnected()
        return sock != null && !sock.isClosed() && sock.isConnected();
    }

    /**
     * Transmit the name of a student to the server
     * @param fullname the student's full name
     * @throws IOException
     */
    @Override
    public void loadStudent(String fullname) throws IOException {
        // initiate the loading
        sendToServer(RouletteV1Protocol.CMD_LOAD);
        // empty the buffer and check if we received the message
        answer = readFromServer();
        if(answer.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
            // send the "fullname" message to the server
            sendToServer(fullname);
            
            // "ENDOFDATA
            sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        }else{
            LOG.log(Level.SEVERE,"No reponse from server after command LOAD");
        }

        // empty the buffer for the next command.
        answer = readFromServer();
        if(!answer.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
            LOG.log(Level.SEVERE,"No answer from server after ENDOFDATA");
        }
    }

    /**
     * Transmit a list of student's name to the server
     * @param students list of student's name
     * @throws IOException
     */
    @Override
    public void loadStudents(List<Student> students) throws IOException {
        // initiate the loading
        sendToServer(RouletteV1Protocol.CMD_LOAD);

        // empty the buffer and check if we received the message
        answer = readFromServer();
        if(answer.equals(RouletteV1Protocol.RESPONSE_LOAD_START)){
            for(Student student : students){
                sendToServer(student.getFullname());
            }
            // ENDOFDATA signal to end the loading
            sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        }else{
            LOG.log(Level.SEVERE,"No reponse from server after command LOAD");
        }

        // empty the buffer for the next command.
        answer = readFromServer();
        if(!answer.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
            LOG.log(Level.SEVERE,"No answer from server after ENDOFDATA");
        }
        
    }

    /**
     * Ask the server to give a random student previously loaded
     * @return a Student randomly selected
     * @throws EmptyStoreException when the server doesn't have a single student stored
     * @throws IOException
     */
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        // send "RANDOM" message to the server and catch the answer
        sendToServer(RouletteV1Protocol.CMD_RANDOM);
        
        answer = readFromServer();

        // un-serialisation of the JSON
        RandomCommandResponse reponse = JsonObjectMapper.parseJson(answer,RandomCommandResponse.class);

        // check if the server's student's list isn't empty
        if(reponse.getError() != null){
            throw new EmptyStoreException();
        }

        return new Student(reponse.getFullname());
    }

    /**
     * Ask the server how many students it has stored
     * @return the number of stored students by the server
     * @throws IOException
     */
    @Override
    public int getNumberOfStudents() throws IOException {
        // send INFO message
        sendToServer(RouletteV1Protocol.CMD_INFO);
        answer = readFromServer();
        // un-serialisation of the JSON
        InfoCommandResponse response = JsonObjectMapper.parseJson(answer,InfoCommandResponse.class);
        return response.getNumberOfStudents();
    }

    /**
     * Ask for the version of the server's protocole
     * @return server's running protocol version
     * @throws IOException
     */
    @Override
    public String getProtocolVersion() throws IOException {
        // send INFO message
        sendToServer(RouletteV1Protocol.CMD_INFO);
        answer = readFromServer();
        // un-serialisation of the JSON
        InfoCommandResponse response = JsonObjectMapper.parseJson(answer,InfoCommandResponse.class);
        return response.getProtocolVersion();
    }

    /**
     * Properly close the reader, writer and connection socket
     * @throws IOException
     */
    protected void close() throws IOException{
        if(writer != null){
            writer.close();
        }
        
        if(reader != null){
            reader.close();
        }
        
        if(sock != null){
            sock.close();
        }
    }

    /**
     * This methode send a message to the server
     * @param data message to send
     */
    protected void sendToServer(String data){
        writer.println(data);
        writer.flush();
    }

    /**
     * This methode read the answer from the server
     * @return
     * @throws IOException
     */
    protected String readFromServer() throws IOException {
        return reader.readLine();
    }
    
    



}
