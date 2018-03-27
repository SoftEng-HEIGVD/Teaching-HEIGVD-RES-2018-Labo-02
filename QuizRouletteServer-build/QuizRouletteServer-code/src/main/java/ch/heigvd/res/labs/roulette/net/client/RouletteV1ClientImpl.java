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
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 * @author Yosra Harbaoui
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private Socket socket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    @Override
    public void connect(String server, int port) throws IOException {
        /**
         * 1. connection to the server
         */
        socket = new Socket(server, port);

        /**
         * 2.creation of reader and writer to communicate with the server
         */
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        /**
         * 3.read the 1st line
         */
        reader.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        /**
         * 1. send the BYE command throw the writer to close the connection
         */
        writer.println(RouletteV1Protocol.CMD_BYE);
        writer.flush();
        /**
         * 2.close the objects used to communicate with the server
         */
        writer.close();
        reader.close();
        socket.close();
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        /**
         * 1. send the LOAD command throw the writer to load information from
         * the server
         */
        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();

        /**
         * 2.read the information sent from the server
         */
        reader.readLine();

        /**
         * 3. send the full name of the student throw the writer
         */
        writer.println(fullname);

        /**
         * 4. send the ENDOFDATA_MARKER
         */
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();

        /**
         * 4. read the information sent from the server
         */
        reader.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        /**
         * load the list of student by loading each student fullname (using
         * loadStudent method)
         */
        for (Student s : students) {
            writer.println(s.getFullname());
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        /**
         * 1. send the RANDOM command throw the writer
         */ 
        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();

        /**
         * 2. load information to read the random name sent from the server
         */ 
        RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(reader.readLine(),
                RandomCommandResponse.class);

        /** 
         * if information sent from the server is an error
         */ 
        if (randomResponse.getError() != null) {
            throw new EmptyStoreException();
        }

        return new Student(randomResponse.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        /**
         * 1. send the INFO command throw the writer
         */ 
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();
        
        /**
         * 2. return the number of Students using information sent from the server
         */ 
        return JsonObjectMapper.parseJson(reader.readLine(),
                InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        /**
         * 1. send the INFO command throw the writer
         */ 
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();
        
        /**
         * 2. return the protocol version  using information sent from the server
         */ 
        return JsonObjectMapper.parseJson(reader.readLine(),
                InfoCommandResponse.class).getProtocolVersion();       
    }

}
