package slick.leWords;

import java.net.*;
import java.util.Vector;
import java.io.*;

public class ClientThread extends Thread {

	
	private Socket socket = null;
	public int score;
	public int players=0;
	public Vector<String> names = new Vector<String>();
	
	public ClientThread(Socket socket)
	{
	super("ClientThread");

	this.socket = socket;
	}
	
	
	public void run()
	{
		try{
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
		    DataInputStream inStream = new DataInputStream(socket.getInputStream());
		    
		    boolean listening = true;
		    while(listening)
		    {
		    	//Send and receive meta data from server:
		    	//Send points
		    	outStream.writeInt(score);
		    	//Receive number of players
		    	players = inStream.readInt();
		    	//Receive names of all the players;
		    	for(int i =0; i < players; i++)
		    	{
		    		int nameLength = inStream.readInt();

		    		String tmp="";
		    		for(int j =0; j < nameLength; j++)
		    			tmp+=inStream.readChar();
		    		names.clear();
		    		names.add(tmp);
		    		
		    	}
		    	
		    }
		    
			outStream.close();
			inStream.close();
			socket.close();
		}catch(IOException e)
		{
			e.printStackTrace();
			
		}
	}
	
}
