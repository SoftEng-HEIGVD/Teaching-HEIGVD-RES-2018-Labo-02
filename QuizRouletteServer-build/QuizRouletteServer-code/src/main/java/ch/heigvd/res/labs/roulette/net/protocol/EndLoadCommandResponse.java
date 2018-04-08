package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "ENDOFDATA" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * 
 * @author Jimmy Verdasca
 */
public class EndLoadCommandResponse {
   
   private String status;
   private int numberOfNewStudents;

   public EndLoadCommandResponse(){}
   
   public EndLoadCommandResponse(String status, int numberOfNewStudents) {
      this.status = status;
      this.numberOfNewStudents = numberOfNewStudents;
   }
   
   public int getNumberOfNewStudents() {
      return numberOfNewStudents;
   }

   public void setNumberOfNewStudents(int numberOfNewStudents) {
      this.numberOfNewStudents = numberOfNewStudents;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
