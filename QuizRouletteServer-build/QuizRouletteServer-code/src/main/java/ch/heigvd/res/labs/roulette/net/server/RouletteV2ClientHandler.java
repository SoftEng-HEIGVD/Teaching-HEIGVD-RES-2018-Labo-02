package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;


/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 * 
 * @author Lemdjo Marie
 * @author Kengne Francine
 */
public class RouletteV2ClientHandler implements IClientHandler {

  static final Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());
  private IStudentsStore store;
  private int numberOfCommands;

  public RouletteV2ClientHandler(IStudentsStore store) {
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of gen  
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
