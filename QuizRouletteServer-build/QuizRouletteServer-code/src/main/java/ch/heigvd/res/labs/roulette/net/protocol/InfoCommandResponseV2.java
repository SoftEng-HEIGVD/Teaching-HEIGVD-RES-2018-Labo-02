package ch.heigvd.res.labs.roulette.net.protocol;

public class InfoCommandResponseV2 {
    private String status;
    private int numberOfNewStudents;

    public InfoCommandResponseV2(Boolean status, int numberOfNewStudents) {
        setNumberOfNewStudents(numberOfNewStudents);
        setStatus(status);
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public void setStatus(Boolean status){
        this.status = status? "success" : "failure";
    }



    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public String getStatus() {
        return status;
    }


}
