package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author Jimmy Verdasca
 */
public class ByeCommandResponse {
   private String status = "success";
   private int numberOfCommands;

   public ByeCommandResponse(int numberOfCommands) {
      this.numberOfCommands = numberOfCommands;
   }
   
   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public int getNumberOfCommands() {
      return numberOfCommands;
   }

   public void setNumberOfCommands(int numberOfCommands) {
      this.numberOfCommands = numberOfCommands;
   }
   
   
}
