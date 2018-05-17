package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * 
 * @author Jimmy Verdasca
 */
public class ByeCommandResponse {
   
   /**
    * state of the request success or fail
    */
   private String status = RouletteV2Protocol.SUCCESS;
   
   /**
    * number of commands sent by the client
    */
   private int numberOfCommands;

   /**
    * Constructor
    * 
    * @param numberOfCommands number of commands sent by the client
    */
   public ByeCommandResponse(int numberOfCommands) {
      this.numberOfCommands = numberOfCommands;
   }
   
   /**
    * return the state of the request success or fail
    * @return the state of the request success or fail
    */
   public String getStatus() {
      return status;
   }

   /**
    * update the state of the request success or fail
    * 
    * @param status new state of the request success or fail
    */
   public void setStatus(String status) {
      this.status = status;
   }

   /**
    * return the number of commands sent by the client
    * 
    * @return the number of commands sent by the client
    */
   public int getNumberOfCommands() {
      return numberOfCommands;
   }

   /**
    * update the number of commands sent by the client
    * 
    * @param numberOfCommands new number of commands sent by the client
    */
   public void setNumberOfCommands(int numberOfCommands) {
      this.numberOfCommands = numberOfCommands;
   }
   
   
}
