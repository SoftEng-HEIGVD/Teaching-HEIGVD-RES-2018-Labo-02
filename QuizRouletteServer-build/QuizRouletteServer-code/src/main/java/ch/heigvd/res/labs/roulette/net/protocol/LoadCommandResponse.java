package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Dejvid Muaremi
 * @author Loic Frueh
 */
public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse() {
    }

    public LoadCommandResponse(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
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
     * This method get the number of students added in the data store by the last successfull LOAD command used.
     * @return The number of students added in the data store by the last successfull LOAD command used.
     */
    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    /**
     * This method set the number of students added in the data store by the last successfull LOAD command used.
     * @param numberOfNewStudents The number of students added in the data store by the last successfull LOAD command used.
     */
    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }
}
