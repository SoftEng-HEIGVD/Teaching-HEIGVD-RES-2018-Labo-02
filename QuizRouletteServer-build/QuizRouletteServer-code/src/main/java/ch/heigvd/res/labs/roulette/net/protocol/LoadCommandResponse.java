package ch.heigvd.res.labs.roulette.net.protocol;

public class LoadCommandResponse {
    private String status;
    private int nbStudents;

    public LoadCommandResponse(String status, int nbStudents){
        this.status = status;
        this.nbStudents = nbStudents;
    }

    public String getStatus(){
        return status;
    }

    public int getNbStudents(){
        return nbStudents;
    }
}
