package ch.heigvd.res.labs.roulette.net.protocol;
/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification.
 * 
 * @author Yosra Harbaoui
 */


public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents;
    
    public LoadCommandResponse(){
    }
    
    public LoadCommandResponse(String status, int numberOfNewStudents){
      this.status = status;
      this.numberOfNewStudents = numberOfNewStudents;
    }
    
    public String getStatus(){
        return status;
    }
    public int getNumberOfNewStudents(){
        return numberOfNewStudents; 
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    public void setNumberOfOfNewStudents(int numberOfStudentsAdded){
        this.numberOfNewStudents = numberOfStudentsAdded;
    }
}
