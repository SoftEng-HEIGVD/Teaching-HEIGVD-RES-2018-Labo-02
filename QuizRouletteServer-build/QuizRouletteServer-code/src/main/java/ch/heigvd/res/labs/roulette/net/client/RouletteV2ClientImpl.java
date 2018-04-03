package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.data.StudentsStoreImpl;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.Arrays;
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
     writer.println(RouletteV2Protocol.CMD_CLEAR);
     writer.flush();
     
     if(!reader.readLine().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
        LOG.log(Level.SEVERE,"problem with CLEAR answer from server");
     }
  }

  @Override
  public List<Student> listStudents() throws IOException {
     writer.println(RouletteV2Protocol.CMD_LIST);
     writer.flush();
     ListCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), ListCommandResponse.class);
     return Arrays.asList(response.getStudents());
  }
  
}
