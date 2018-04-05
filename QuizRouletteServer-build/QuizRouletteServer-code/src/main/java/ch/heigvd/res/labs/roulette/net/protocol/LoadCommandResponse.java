package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "INFO" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * 
 * @author Olivier Liechti
 */
public class LoadCommandResponse {

  private String status;
  private int numberOfNewAddedStudent;

  public LoadCommandResponse() { }

  public LoadCommandResponse(String status, int numberOfNewAddedStudent) {
    this.status = status;
    this.numberOfNewAddedStudent = numberOfNewAddedStudent;
  }

  public String getStatus() { return status; }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getNumberOfNewAddedStudent() {
    return numberOfNewAddedStudent;
  }

  public void setNumberOfNewAddedStudent(int numberOfNewAddedStudent) {
    this.numberOfNewAddedStudent = numberOfNewAddedStudent;
  }

}
