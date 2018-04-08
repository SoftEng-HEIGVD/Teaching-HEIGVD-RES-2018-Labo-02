/**
 * This class is used to serialize/deserialize the response sent by the
 * server when processing the "BYE" command defined in the protocol
 * specification. The JsonObjectMapper utility class can use this class.
 *
 * @author Bryan Curchod, François Burgener
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author François Burgener, Bryan Curchod
 */
public class ByeCommandReponse {

    private String status;
    private int numberOfCommands;

    /**
     * default constructor
     */
    public ByeCommandReponse() {
    }

    /**
     * Constructor to set the status and number of commands attributes
     * @param status
     * @param numberOfCommands
     */
    public ByeCommandReponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    /**
     * Get the status. The status represents the state of the servers attempt to close the connexion.
     * @return status of the process of closing the connection from the server side
     */
    public String getStatus() {
        return status;
    }

    /**
     * Get the number of command for this session.
     * @return number of command sent by the client during this session
     */
    public int getNumberOfCommands() {
        return numberOfCommands;
    }

}
