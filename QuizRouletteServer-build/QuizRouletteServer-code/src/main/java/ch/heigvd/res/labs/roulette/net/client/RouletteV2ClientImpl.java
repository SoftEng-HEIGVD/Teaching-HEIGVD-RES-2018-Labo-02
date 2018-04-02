package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException {
      send(RouletteV2Protocol.CMD_CLEAR);
      br.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
    send(RouletteV2Protocol.CMD_LIST);
    String listCommandResponse = br.readLine();
    return JsonObjectMapper.parseJson(listCommandResponse, ListCommandResponse.class).getStudents();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
      super.loadStudent(fullname);
      br.readLine();
  }

    @Override
    public void disconnect() throws IOException {
        if(socket == null){
            return;
        }
        else{
            send(RouletteV2Protocol.CMD_BYE);
            br.readLine();
            socket.close();
            socket = null;
            pw.close();
            br.close();
        }
    }



}
