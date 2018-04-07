package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author loic-schurch, Jokau
 */
public class LoadCommandResponse {

    private String status;
    private int numberOfNewStudents;

    public String getStatus() {
        return status;
    }
    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }
}
