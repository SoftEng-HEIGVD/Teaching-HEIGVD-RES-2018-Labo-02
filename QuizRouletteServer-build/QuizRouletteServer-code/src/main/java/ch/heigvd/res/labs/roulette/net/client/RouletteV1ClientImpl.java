package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
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
 *
 * @author Zacharie Nguefack
 * @author cedric Lankeu
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    final String ENCODING = "UTF-8"; // specify encoding
    boolean isConnect = false;
    BufferedReader in;
    PrintWriter out;
    Socket ClientSocket;

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
            out.println(RouletteV1Protocol.CMD_BYE);
            //close the input and output flows
            in.close();
            out.close();

            // close the connexion 
            ClientSocket.close();
        }
    }

    InfoCommandResponse getInfoCommand() throws IOException {

        out.println(RouletteV2Protocol.CMD_INFO);

        out.flush();

        in.readLine();

        InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);
        return infoResponse;
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

  public Student pickRandomStudent() throws EmptyStoreException, IOException {
     if (isConnected()) {
      out.println(RouletteV1Protocol.CMD_RANDOM);
      out.flush();

      RandomCommandResponse rdResp = JsonObjectMapper.parseJson(in.readLine(), RandomCommandResponse.class);
      if (rdResp.getError() == null) {
        return new Student(rdResp.getFullname());
      } else {
        throw new EmptyStoreException();
      }
    } else {
      throw new IOException("Client not connected to server");
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
