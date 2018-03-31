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
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private Socket sock = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    @Override
    public void connect(String server, int port) throws IOException {
        sock = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        writer = new PrintWriter(sock.getOutputStream());
        if(isConnected())
            readFromServer();
        
        readFromServer();
    }

    @Override
    public void disconnect() throws IOException {
        // send "BYE" message
        sendToServer(RouletteV1Protocol.CMD_BYE);
        // closing the connection
        close();
    }

    @Override
    public boolean isConnected() {
        // we have to check if the connexion is not closed : CF Oracle documentation : socket.isConnected()
        return sock != null && !sock.isClosed() && sock.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        // initilize the loading
        sendToServer(RouletteV1Protocol.CMD_LOAD);
        // empty the buffer and check if we received the message
        String s = readFromServer();
        if(!s.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
            // send the "fullname" message to the server
            sendToServer(fullname);
        }
        // "ENDOFDATA
        sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        s = readFromServer();
        if(!s.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
            LOG.log(Level.SEVERE,"No reponse from server after ENDOFDATA");
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        //LOAD
        sendToServer(RouletteV1Protocol.CMD_LOAD);

        // empty the buffer and check if we received the message
        String s = readFromServer();
        if(!s.equals(RouletteV1Protocol.RESPONSE_LOAD_START)){
            for(Student student : students){
                sendToServer(student.getFullname());
            }
        }else{
            LOG.log(Level.SEVERE,"No reponse from server after command LOAD");
        }
        //ENDOFDATA
        sendToServer(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        s = readFromServer();
        if(!s.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
            LOG.log(Level.SEVERE,"No reponse from server after ENDOFDATA");
        }
        
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        // send "RANDOM" message to the server and catch the answer
        sendToServer(RouletteV1Protocol.CMD_RANDOM);
        
        String answer = readFromServer();
        
        RandomCommandResponse reponse = JsonObjectMapper.parseJson(answer,RandomCommandResponse.class);
        
        if(reponse.getError() != null){
            throw new EmptyStoreException();
        }

        return new Student(reponse.getFullname());
        // TODO
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        // "INFO
        sendToServer(RouletteV1Protocol.CMD_INFO);
        String answer = readFromServer();
        InfoCommandResponse response = JsonObjectMapper.parseJson(answer,InfoCommandResponse.class);
        return response.getNumberOfStudents();
        // TODO
    }

    @Override
    public String getProtocolVersion() throws IOException {
        // "INFO"
        sendToServer(RouletteV1Protocol.CMD_INFO);
        String answer = readFromServer();
        InfoCommandResponse response = JsonObjectMapper.parseJson(answer,InfoCommandResponse.class);
        return response.getProtocolVersion();
        // TODO
    }

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
    protected void sendToServer(String data){
        writer.println(data);
        writer.flush();
        //TODO
    }

    protected String readFromServer() throws IOException {
        return reader.readLine();
        //TODO
    }
    
    



}
