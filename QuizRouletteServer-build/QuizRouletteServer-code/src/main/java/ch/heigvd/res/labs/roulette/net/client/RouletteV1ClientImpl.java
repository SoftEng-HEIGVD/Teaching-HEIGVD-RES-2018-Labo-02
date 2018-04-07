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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Labinot Rashiti
 * @author Romain Gallay
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

   // Preparation of the communication Client-Server
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
         in.readLine(); // receive the message of the server
         connected = true;
      } catch (IOException e) {
         LOG.log(Level.SEVERE, e.getMessage(), e);
         clean(); // clean all the variables
      }
   }

   @Override
   public void disconnect() throws IOException {
      out.println(RouletteV1Protocol.CMD_BYE);
      connected = false;
      clean();
   }

   public void clean() throws IOException {
      try {
         if (in != null) {
            in.close();
         }

         if (out != null) {
            out.close();
         }

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
      out.flush(); // send the message
      in.readLine(); // receive the message of the server

      out.println(fullname);
      out.flush(); // send the message

      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush(); // send the message
      in.readLine(); // receive the message of the server
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      // Send the command LOAD
      out.println(RouletteV1Protocol.CMD_LOAD);
      out.flush();
      in.readLine();

      // send all the student one by one to the server
      for (Student student : students) {
         out.println(student.getFullname());
         out.flush();
      }

      // end the load
      out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      out.flush();
      in.readLine();
   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      out.println(RouletteV1Protocol.CMD_RANDOM);
      out.flush(); // send the message
      String response = in.readLine(); // receive the message of the server
      RandomCommandResponse info = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);

      // check if empty
      if (info.getError() != null) {
         throw new EmptyStoreException();
      }
      return new Student(info.getFullname());
   }

   @Override
   public int getNumberOfStudents() throws IOException {
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush(); // send the message
      String response = in.readLine(); // receive the message of the server
      InfoCommandResponse info = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
      return info.getNumberOfStudents();
   }

   @Override
   public String getProtocolVersion() throws IOException {
      out.println(RouletteV1Protocol.CMD_INFO);
      out.flush(); // send the message
      String response = in.readLine(); // receive the message of the server
      InfoCommandResponse info = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
      return info.getProtocolVersion();
   }

}
