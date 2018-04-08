package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private RouletteV2Protocol rv1p;
    private ByeCommandResponse lasteByeCommand = new ByeCommandResponse();
    private LoadCommandResponse lasteLoadCommandResponse = new LoadCommandResponse();
    private String lasteStatus = "";
    private int nbCommandClient = 0;

    public int getNumberOfCommands() {
        return nbCommandClient;
    }

    @Override
    public void connect(String server, int port) throws IOException {
        super.connect(server, port);
    }

    @Override
    public void disconnect() throws IOException {
        os.println(RouletteV1Protocol.CMD_BYE);
        os.flush();
        String response = is.readLine();
        lasteByeCommand = JsonObjectMapper.parseJson(response, ByeCommandResponse.class);
        lasteStatus = lasteByeCommand.getstatus();
        is.close();
        os.close();
        clientSocket.close();
        nbCommandClient++;

    }

    @Override
    public void clearDataStore() throws IOException {
        os.println(RouletteV2Protocol.CMD_CLEAR);
        os.flush();
        String response = is.readLine();
        nbCommandClient++;
        /*
        if(response == "DATASTORE CLEARED") ok
         */

    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        nbCommandClient++;
        return super.pickRandomStudent();
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        nbCommandClient++;
        return super.getNumberOfStudents();
    }

    public String getProtocolVersion() throws IOException {
        nbCommandClient++;
        return super.getProtocolVersion();

    }

    @Override
    public List<Student> listStudents() throws IOException {
        nbCommandClient++;
        os.println(RouletteV2Protocol.CMD_LIST);
        os.flush();
        String allResponse = is.readLine();
        StudentsList sl = JsonObjectMapper.parseJson(allResponse, StudentsList.class);
        return sl.getStudents();

    }

    @Override
    public boolean checkSuccessOfCommand() {
        nbCommandClient++;
        if (lasteStatus.equals("success")) {
            return true;
        } else {
            return false;
        }
    }

    private void loadStudentt(String fullname) throws IOException {
        os.println(fullname);
        os.flush();

    }

    public void loadStudent(String fullname) throws IOException {
        nbCommandClient++;
        os.println(RouletteV1Protocol.CMD_LOAD);
        os.flush();
        is.readLine();

        loadStudentt(fullname);
        os.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
        String response = "";
        try {
            response = is.readLine();
        } catch (IOException ex) {
            Logger.getLogger(RouletteV2ClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        LoadCommandResponse lCR = null;
        try {
            lasteLoadCommandResponse = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
            lasteStatus = lasteLoadCommandResponse.getstatus();
        } catch (IOException ex) {
            Logger.getLogger(RouletteV2ClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        nbCommandClient++;
        os.println(RouletteV1Protocol.CMD_LOAD);
        os.flush();
        is.readLine();
        for (Student s : students) {
            loadStudentt(s.getFullname());
        }

        os.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
        String response = "";
        try {
            response = is.readLine();
        } catch (IOException ex) {
            Logger.getLogger(RouletteV2ClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        LoadCommandResponse lCR = null;
        try {
            lasteLoadCommandResponse = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
            lasteStatus = lasteLoadCommandResponse.getstatus();
        } catch (IOException ex) {
            Logger.getLogger(RouletteV2ClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getNumberOfStudentAdded() {
        nbCommandClient++;
        return lasteLoadCommandResponse.getnumberOfNewStudents();
    }

}
