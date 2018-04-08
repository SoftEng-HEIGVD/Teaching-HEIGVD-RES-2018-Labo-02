package ch.heigvd.res.labs.roulette.net.protocol;

public class LoadCommandResponseV2 {
    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponseV2() {
    }

    public LoadCommandResponseV2(String status, int numberOfNewStudents) {
        setNumberOfNewStudents(numberOfNewStudents);
        setStatus(status);
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public void setStatus(String status){
        this.status = status;
    }



    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public String getStatus() {
        return status;
    }


}
