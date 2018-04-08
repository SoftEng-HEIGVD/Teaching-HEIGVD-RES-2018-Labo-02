package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author David Jaquet
 * @author Vincent Guidoux
 */
public class ByeCommandResponse {


    private int numberOfCommands;
    private String status;

    public ByeCommandResponse() { }

    public ByeCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public String getStatus() {
        return status;
    }

    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}