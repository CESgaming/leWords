//package slick.leWords;
import java.net.*;
import java.io.*;


public class ServerThread extends Thread {
	private Socket socket = null;

	public String word;
	public int score;
	
    public ServerThread(Socket socket) {
	super("ServerThread");
	this.socket = socket;
    }

    public void run() {

	try {
	    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String inputLine;
	    
	    while ((inputLine = in.readLine()) != null) {
		//InputLine contains the string send from the client;
	    	//System.out.println(inputLine);
	    	word = inputLine;
	    	//if(Dictionary.contains(word))
	    	out.println(inputLine);
	    	//out.print(score);
	    	//Now Player interaction is Done
	    	//Send other Players score now:
	    	
	    }
	    out.close();
	    in.close();
	    socket.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}