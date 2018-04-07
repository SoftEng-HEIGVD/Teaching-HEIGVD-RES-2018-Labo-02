package ch.heigvd.res.labs.roulette.net.protocol;

public class LoadCommandResponse {

    private String successStatus;
    private int nbStudents;

    public LoadCommandResponse(String successStatus, int nbStudents){
        this.successStatus = successStatus;
        this.nbStudents = nbStudents;
    }

    public int getNbStudents(){
        return nbStudents;
    }

    public String getSuccessStatus(){
        return successStatus;
    }

    public void setNbStudents(int nbStudents){
        this.nbStudents = nbStudents;
    }

    public void setSuccessStatus(String successStatus){
        this.successStatus = successStatus;
    }
}
