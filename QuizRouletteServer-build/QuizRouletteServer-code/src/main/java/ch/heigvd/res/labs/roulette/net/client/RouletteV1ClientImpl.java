package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsStoreImpl;
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
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket clientSocket;
    private StudentsStoreImpl studentsStore;

    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        studentsStore = new StudentsStoreImpl();
    }

    @Override
    public void disconnect() throws IOException {
        clientSocket.close();
    }

    @Override
    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        Student student = new Student(fullname);
        studentsStore.addStudent(student);
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        for (Student s : students) {
            studentsStore.addStudent(s);
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        return studentsStore.pickRandomStudent();

    }

    @Override
    public int getNumberOfStudents() throws IOException {
        return studentsStore.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        return RouletteV1Protocol.VERSION;
    }

}
