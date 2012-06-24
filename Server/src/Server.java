
import java.net.*;
import java.util.Vector;
import java.io.*;


public class Server {
    @SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
 
    	  int dim = 5;
    	  int numberOfClients =0;
    	  Board b;
    	  Hypercube h;
    	  Dictionary d;
    	  ServerThread last = null;
          b = new Board(dim);
          h = new Hypercube();
          d = new Dictionary();
    	  h.fillHypercube();
          d.fillCompleteDictionary();
          b.fillBoard();
          b.filterLevelOne(d,h);
          b.filterLevelTwo();
          GameManager gM = new GameManager();
          
    	
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(5222);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 5222.");
            System.exit(1);
        }
        boolean listening = true;
        gM.start();
        
        while(listening)
        {
        	
        	
        	last = new ServerThread(serverSocket.accept(), b);
        	last.start();
        	gM.clients.add(last);
        	
        }
        serverSocket.close();
    }
}