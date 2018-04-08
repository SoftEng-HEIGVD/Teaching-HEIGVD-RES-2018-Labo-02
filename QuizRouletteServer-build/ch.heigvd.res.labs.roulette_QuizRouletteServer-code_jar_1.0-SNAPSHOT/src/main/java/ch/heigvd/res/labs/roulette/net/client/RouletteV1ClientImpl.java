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
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 * @author dorianekaffo
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  
     Socket clientSocket = null;
     BufferedReader reader = null;
     PrintWriter writer = null;
 
    @Override
    public void connect(String server, int port) throws IOException {
      
        // extablit connexion with server
        clientSocket = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
        writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"), true);
   
        //read data 
        reader.readLine();
    }

    @Override
    public void disconnect() throws IOException {
      
        writer.println(RouletteV1Protocol.CMD_BYE);         
        writer.flush();
        reader.readLine();
  
        writer.close();
        reader.close();
        clientSocket.close();
    }

    @Override
    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
     
        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();
        reader.readLine();
        writer.println(fullname);
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        reader.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
                        
        writer.println(RouletteV1Protocol.CMD_LOAD);
        reader.readLine();
        for (Student s : students) {
          writer.println(s.getFullname());
        }
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        reader.readLine();
        
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush(); 
        RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);
        if (randomResponse.getError() != null) {
            throw new EmptyStoreException();
        }
        return new Student(randomResponse.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();                
        return JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
      
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();
        
     
        return JsonObjectMapper.parseJson(reader.readLine(),
                InfoCommandResponse.class).getProtocolVersion();       
    }

}