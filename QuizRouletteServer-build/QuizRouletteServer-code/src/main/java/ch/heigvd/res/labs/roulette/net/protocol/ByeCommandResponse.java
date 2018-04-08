package ch.heigvd.res.labs.roulette.net.protocol;


/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * 
 * @author Miguel Lopes Gouveia(endmon)
 * @author Rémy Nasserzare(remynz)
 */
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;

    public ByeCommandResponse() {
    }

    public ByeCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    /**
     * This method get the end's status of the command.
     * @return "success" if everythings went well or "failure" if not.
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method set the end's status of the command.
     * @param status The end's status of the command. ("success" if everythings went well or "failure" if not.)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * This method get the number of command used since the begining of the connection.
     * @return the number of command used since the begining of the connection.
     */
    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    /**
     * This method set the number of command used since the begining of the connection.
     * @param numberOfCommands the number of command used since the begining of the connection.
     */
    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }
}