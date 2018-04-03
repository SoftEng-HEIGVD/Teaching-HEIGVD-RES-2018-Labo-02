package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
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

    out.println(RouletteV2Protocol.CMD_CLEAR);
    out.flush(); // send the message
    in.readLine(); // read the answer from server

    }

  @Override
  public List<Student> listStudents() throws IOException {

    String list;

    out.println(RouletteV2Protocol.CMD_LIST);
    out.flush();

    list = in.readLine(); // read the answer from server

    // parse the JSON answer from server and get a List<Student> from it
    return JsonObjectMapper.parseJson(list, StudentsList.class).getStudents();
  }
  
}
