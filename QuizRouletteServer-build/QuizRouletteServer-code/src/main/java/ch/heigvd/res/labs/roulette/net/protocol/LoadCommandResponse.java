package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created on 05.04.18.
 *
 * @author Max
 */
public class LoadCommandResponse {
    private String status = "new";
    private int numberOfNewStudents = -1;

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
        status = "success";
    }

    public void setError() {
        status = "error";
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

}
