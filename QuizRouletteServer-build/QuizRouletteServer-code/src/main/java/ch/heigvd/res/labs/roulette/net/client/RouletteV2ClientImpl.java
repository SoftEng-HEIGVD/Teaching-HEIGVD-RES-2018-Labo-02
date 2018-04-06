package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;


/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  @Override
  public void clearDataStore() throws IOException {
        System.out.println(RouletteV2Protocol.CMD_CLEAR);
        System.out.flush();
        if (!readline().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE))
            LOG.log(Level.SEVERE, "remote server did not reply {0}", RouletteV2Protocol.RESPONSE_CLEAR_DONE);
  }

  @Override
  public List<Student> listStudents() throws IOException {
        System.out.println(RouletteV2Protocol.CMD_LIST);
        System.out.flush();
        return JsonObjectMapper.parseJson(readline(), StudentsList.class).getStudents();
  }
  
}
