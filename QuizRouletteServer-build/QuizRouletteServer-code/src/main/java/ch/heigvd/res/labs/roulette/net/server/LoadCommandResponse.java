package ch.heigvd.res.labs.roulette.net.server;

/**
 *
 * @author Doriane Kaffo
 */
public class LoadCommandResponse {
    private String status;
    private int numberOfAddStudents;

    public LoadCommandResponse(String status, int numberOfNewStudents){
        this.status = status;
        this.numberOfAddStudents = numberOfNewStudents;
    }

    public LoadCommandResponse(){}

    public String getStatus() {
        return status;
    }

    public int getNumberOfAddStudents() {
        return numberOfAddStudents;
    }

}
