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
  Socket clientSocket;
  BufferedReader in;
  PrintWriter out;

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  @Override
  public void connect(String server, int port) throws IOException {
    clientSocket = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    LOG.log(Level.INFO, in.readLine());
  }

  @Override
  public void disconnect() throws IOException {
    if (clientSocket != null) {
      out.println(RouletteV1Protocol.CMD_BYE);
      clientSocket.close();
    }
  }

  @Override
  public boolean isConnected() {
    return (clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected());
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    if (isConnected()) {
      out.println(RouletteV1Protocol.CMD_LOAD);
      out.println(fullname);
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();

      LOG.log(Level.INFO, in.readLine()); // send your data
      LOG.log(Level.INFO, in.readLine()); // data loaded
    } else {
      LOG.log(Level.SEVERE, "CLIENT NOT CONNECTED TO SERVER");
    }
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    if (isConnected()) {
      out.println(RouletteV1Protocol.CMD_LOAD);
      for(Student student : students) {
        out.println(student.getFullname());
      }
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();
      LOG.log(Level.INFO, in.readLine()); // send your data
      LOG.log(Level.INFO, in.readLine()); // data loaded
    } else {
      throw new IOException("Client not connected to server");
    }
  }

  @Override
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
    if (isConnected()) {
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();
      return JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class).getNumberOfStudents();
    } else {
      throw new IOException("Client not connected to server");
    }
  }

  @Override
  public String getProtocolVersion() throws IOException {
    if (isConnected()) {
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();
      return JsonObjectMapper.parseJson( in.readLine(), InfoCommandResponse.class).getProtocolVersion();
    } else {
      throw new IOException("Client not connected to server");
    }
  }
}
