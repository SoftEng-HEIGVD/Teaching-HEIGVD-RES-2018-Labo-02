package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "INFO" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * 
 * @author Olivier Liechti
 */
public class ByeCommandResponse {

  private String status;
  private int numberOfSentCommand;

  public ByeCommandResponse() { }

  public ByeCommandResponse(String status, int numberOfSentCommand) {
    this.status = status;
    this.numberOfSentCommand = numberOfSentCommand;
  }

  public String getStatus() { return status; }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getNumberOfSentCommand() {
    return numberOfSentCommand;
  }

  public void setNumberOfSentCommand(int numberOfSentCommand) {
    this.numberOfSentCommand = numberOfSentCommand;
  }

}
