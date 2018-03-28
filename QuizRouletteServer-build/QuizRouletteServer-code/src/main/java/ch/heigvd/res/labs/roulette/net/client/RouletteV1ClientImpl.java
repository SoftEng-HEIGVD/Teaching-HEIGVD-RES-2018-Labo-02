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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Labinot Rashiti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

   Socket clientSocket = null;
   BufferedReader in = null;
   PrintWriter out = null;
   boolean connected = false;

   @Override
   public void connect(String server, int port) throws IOException {
      try {
         Socket clientSocket = new Socket(server, port);

         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         out = new PrintWriter(clientSocket.getOutputStream());
         connected = true;
         out.println();
         out.flush();
         in.readLine();
      } catch (IOException e) {
         LOG.log(Level.SEVERE, e.getMessage(), e);
      }
   }

   @Override
   public void disconnect() throws IOException {
      try {
         if (in != null) {
            in.close();
         }
      } catch (IOException ex) {
         LOG.log(Level.SEVERE, ex.getMessage(), ex);
      }

      if (out != null) {
         out.close();
      }

      try {
         if (clientSocket != null) {
            clientSocket.close();
         }
      } catch (IOException ex) {
         LOG.log(Level.SEVERE, ex.getMessage(), ex);
      }
   }

   @Override
   public boolean isConnected() {
      return connected;
   }

   @Override
   public void loadStudent(String fullname) throws IOException {
      out.println(RouletteV1Protocol.CMD_LOAD);
      out.flush();
      in.readLine(); // show the answer of the server
      out.println(fullname);
      out.flush();
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();
      in.readLine(); // show the answer of the server
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      for (Student student : students) {
         loadStudent(student.getFullname());
      }
   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      out.println(RouletteV1Protocol.CMD_RANDOM);
      out.flush();
      String response = in.readLine();
      RandomCommandResponse info = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);
      if (info.getError() != null) {
         throw new EmptyStoreException();
      }
      return new Student(info.getFullname());
   }

   @Override
   public int getNumberOfStudents() throws IOException {
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();
      String response = in.readLine();
      InfoCommandResponse info = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
      return info.getNumberOfStudents();
   }

   @Override
   public String getProtocolVersion() throws IOException {
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush();
      String response = in.readLine();
      InfoCommandResponse info = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
      return info.getProtocolVersion();
   }
}
