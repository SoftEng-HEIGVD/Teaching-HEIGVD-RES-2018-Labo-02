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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 * @author Zacharie Nguefack
 * @author cedric  Lankeu
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final String ENCODING = "UTF-8"; // specify encoding
    private boolean isConnect = false;
    private BufferedReader in;
    private PrintWriter out;
    private Socket ClientSocket;


    @Override
    public void connect(String server, int port) throws IOException {
        //create the connection between the client and the server
        ClientSocket = new Socket(server, port);
        
        //exchange information through the input and output flows
        in = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream(), ENCODING));
        out = new PrintWriter(new OutputStreamWriter(ClientSocket.getOutputStream(), ENCODING));
        
        // Hello. Online HELP is available. Will you find it?
        in.readLine(); 
        
        this.isConnect = true;
    }

    @Override
    public void disconnect() throws IOException {
        if (isConnect) {
            isConnect = false;
            
            //close the input and output flows
            in.close();
            out.close();
            
            // close the connexion 
            ClientSocket.close();
        } 
    }

    @Override
    public boolean isConnected() {
        return isConnect;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        // command to server to load data
        out.println(RouletteV1Protocol.CMD_LOAD);
        out.flush();
        
        // read message:  Send your data [end with ENDOFDATA]
        in.readLine();
        
        // send the name to server
        out.println(fullname);
        out.flush();

        // end of data command
        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();

        // read message ENDOFDATA
        in.readLine();

    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {

        if (students != null && !students.isEmpty()) {

            // command to server to load data
            out.println(RouletteV1Protocol.CMD_LOAD);
            out.flush();

            // read message:  Send your data [end with ENDOFDATA] 
            in.readLine();
            
            //browse the list and send data to the server
            for (Student student : students) {
                if (student != null && !student.getFullname().isEmpty()) {
                    out.println(student.getFullname());
                    out.flush();
                }
            }
            // end of data command
            out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            out.flush();
            
            // read message DATA LOADED
            in.readLine();
            

        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        
        //send the loader command to server
        out.println(RouletteV1Protocol.CMD_RANDOM);
        out.flush();
        
        // read server response and parse it to Json format
        RandomCommandResponse ServerResponse = JsonObjectMapper.parseJson(in.readLine(), RandomCommandResponse.class);

        if (ServerResponse.getError() == null && ServerResponse.getError().isEmpty() == true) {
            return new Student(ServerResponse.getFullname());
        } else {
            throw new EmptyStoreException();
        }

    }

    @Override
    public int getNumberOfStudents() throws IOException {
        
        // send command to server
        out.println(RouletteV1Protocol.CMD_INFO);
        out.flush();
        
        // return number of students 
        return JsonObjectMapper.parseJson(in.readLine(),
                InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        
        // send command to server
        out.println(RouletteV1Protocol.CMD_INFO);
        out.flush();
        
        return JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class).getProtocolVersion();
    }

}
