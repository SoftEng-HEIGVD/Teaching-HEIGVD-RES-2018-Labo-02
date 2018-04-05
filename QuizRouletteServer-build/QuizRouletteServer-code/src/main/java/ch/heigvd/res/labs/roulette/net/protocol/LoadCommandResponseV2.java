package ch.heigvd.res.labs.roulette.net.protocol;

public class LoadCommandResponseV2 {

    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponseV2() {
    }

    public LoadCommandResponseV2(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getnumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public void setnumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }

}
