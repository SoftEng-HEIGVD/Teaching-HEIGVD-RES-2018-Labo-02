package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Daniel Gonzalez Lopez, Héléna Line Reymond
 */
public class LoadCommandResponse {

    private String commandStatus;
    private int totalNewStudents;

    public LoadCommandResponse(String commandStatus, int totalNewStudents) {
        this.commandStatus = commandStatus;
        this.totalNewStudents = totalNewStudents;
    }

    public String getCommandStatus() {
        return commandStatus;
    }

    public int getTotalNewStudents() {
        return totalNewStudents;
    }

    public void setCommandStatus(String commandStatus) {
        this.commandStatus = commandStatus;
    }

    public void setTotalNewStudents(int totalNewStudents) {
        this.totalNewStudents = totalNewStudents;
    }
}
