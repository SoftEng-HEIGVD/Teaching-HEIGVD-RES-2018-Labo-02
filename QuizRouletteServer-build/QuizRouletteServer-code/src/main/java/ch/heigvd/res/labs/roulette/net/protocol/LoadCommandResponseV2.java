package ch.heigvd.res.labs.roulette.net.protocol;

public class LoadCommandResponseV2 {
    private boolean status;
    private int numberOfNewStudents;

    public LoadCommandResponseV2(Boolean status, int numberOfNewStudents) {
        setNumberOfNewStudents(numberOfNewStudents);
        setStatus(status);
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public void setStatus(Boolean status){
        this.status = status;
    }



    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public boolean getStatus() {
        return status;
    }


}
