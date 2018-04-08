package ch.heigvd.res.labs.roulette.net.server;

/**
 *
 * @author dorianekaffo
 */
class LoadCommandResponseV2 {

    private int numberOfAddStudents;
    private String status;

    public LoadCommandResponseV2() {
    }

    public LoadCommandResponseV2(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfAddStudents = numberOfNewStudents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getnumberOfAddStudents() {
        return numberOfAddStudents;
    }

    public void setnumberOfAddStudents(int numberOfNewStudents) {
        this.numberOfAddStudents = numberOfNewStudents;
    }

}
