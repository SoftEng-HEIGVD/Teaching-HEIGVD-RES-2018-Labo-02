package ch.heigvd.res.labs.roulette.net.protocol;

public class LoadCommandResponse {

    private String status;
    private int numberOfStudentAdded;

    public LoadCommandResponse() {

    }

    public LoadCommandResponse(String status, int numberOfStudentAdded) {

        this.status = status;
        this.numberOfStudentAdded = numberOfStudentAdded;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfStudentAdded() {
        return numberOfStudentAdded;
    }
}
