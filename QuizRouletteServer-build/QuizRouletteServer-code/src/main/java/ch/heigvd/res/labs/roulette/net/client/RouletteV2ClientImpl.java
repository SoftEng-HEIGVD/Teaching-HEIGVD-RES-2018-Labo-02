package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    // Not realy usefull just for log
    public void disconnect() throws IOException {
        LOG.log(Level.INFO, "client has request to be disconnect.");
        sendToServer(RouletteV1Protocol.CMD_BYE);

        ByeCommandResponse byeCmdResp = JsonObjectMapper.parseJson(in.readLine(), ByeCommandResponse.class);

        LOG.log(Level.INFO, byeCmdResp.getStatus());

        out.close();
        in.close();
        clientSocket.close();
    }

    @Override
    public void clearDataStore() throws IOException {
        sendToServer(RouletteV2Protocol.CMD_CLEAR);
        skipMessageServer();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        sendToServer(RouletteV2Protocol.CMD_LIST);
        String s = in.readLine();
        StudentsList studentList = JsonObjectMapper.parseJson(s,StudentsList.class);
        return studentList.getStudents();
    }
    @Override
    public void loadStudent(String fullname) throws IOException {
        sendToServer(RouletteV2Protocol.CMD_LOAD);
        skipMessageServer(); // Read: Send your data [end with ENDOFDATA]
        sendToServer(fullname); // Send name of the student to load
        sendToServer(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        skipMessageServer();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        LOG.log(Level.INFO, "Load students", students);
        sendToServer(RouletteV2Protocol.CMD_LOAD);
        skipMessageServer(); // Read: Send your data [end with ENDOFDATA]

        for(Student s: students){
            sendToServer(s.getFullname()); // name of the student to load
        }
        sendToServer(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER); // END of loading data
        skipMessageServer(); // Read: DATA LOADED
    }
}
