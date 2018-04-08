package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * @author Guillaume Blanco, Patrick Neto
 */
public class LoadCommandResponse {

    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse() {}

    public LoadCommandResponse(String status, int numberOfNewStudents){
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public String getStatus() {
        return status;
    }
}
