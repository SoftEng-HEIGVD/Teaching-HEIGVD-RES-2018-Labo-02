package ch.heigvd.res.labs.roulette.net.protocol;

public class LoadStudentCommandResponse {

    private String status;

    private int numberOfStudentsAdded;

    public LoadStudentCommandResponse() {}

    public LoadStudentCommandResponse(String status, int numberOfStudentsAdded) {

        this.status = status;
        this.numberOfStudentsAdded  = numberOfStudentsAdded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfStudentsAdded() {
        return numberOfStudentsAdded;
    }

    public void setNumberOfStudentsAdded(int numberOfStudentsAdded) {
        this.numberOfStudentsAdded = numberOfStudentsAdded;
    }
}
