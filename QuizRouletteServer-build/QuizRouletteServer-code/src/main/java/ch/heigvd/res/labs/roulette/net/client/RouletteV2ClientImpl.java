package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti, modified by Lionel Nanchen
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

  @Override
  public void clearDataStore() throws IOException {

    LOG.info("Clear data store");

    printWriter.println(RouletteV2Protocol.CMD_CLEAR);
    bufferedReader.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {

    LOG.info("List students");

    printWriter.println(RouletteV2Protocol.CMD_LIST);

    ListCommandResponse response = JsonObjectMapper.parseJson(bufferedReader.readLine(), ListCommandResponse.class);

    return response.getStudents();
  }
}
