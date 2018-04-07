package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * @author Guillaume Hochet
 */
public class LoadStudentCommandResponse {

    private String status;

    private int numberOfNewStudents;

    public LoadStudentCommandResponse() {}

    public LoadStudentCommandResponse(String status, int numberOfStudentsAdded) {

        this.status = status;
        this.numberOfNewStudents  = numberOfStudentsAdded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }
}
