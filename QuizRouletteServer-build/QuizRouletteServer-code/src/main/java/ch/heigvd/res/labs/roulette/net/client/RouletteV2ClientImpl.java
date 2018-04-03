package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
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
    out.flush();
    LOG.info(in.readLine());
  }

  @Override
  public List<Student> listStudents() throws IOException {
    out.println(RouletteV2Protocol.CMD_LIST);
    out.flush();

    String response = in.readLine();
    LOG.info(response);

    ListCommandResponse lsResponse = JsonObjectMapper.parseJson(response, ListCommandResponse.class);
    return lsResponse.getStudents();
  }

  @Override
  public int getNumberOfStudentAdded() throws IOException{
    out.println(RouletteV2Protocol.CMD_LIST);
    out.flush();

    String response = in.readLine();
    LOG.info(response);

    ListCommandResponse lsResponse = JsonObjectMapper.parseJson(response, ListCommandResponse.class);
    return lsResponse.getStudents().size();
  }

  @Override
  public int getNumberOfCommands() throws IOException{
    out.println(RouletteV2Protocol.CMD_BYE);
    out.flush();

    String response = in.readLine();
    LOG.info(response);

    ByeCommandResponse byeResponse = JsonObjectMapper.parseJson(response, ByeCommandResponse.class);
    return byeResponse.getNumberOfCommands();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    return RouletteV2Protocol.VERSION;
  }

}
