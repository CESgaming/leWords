package slick.leWords;

import java.net.*;
import java.util.Vector;
import java.io.*;

public class ClientThread extends Thread {

	
	private Socket socket = null;
	public int score;
	public int players=0;
	public Vector<String> names = new Vector<String>();
	public Vector<Client> clients = new Vector<Client>();
	public Vector<Client> remove = new Vector<Client>();
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
		    	//Set all clients to be disconnected
		    	for(int i =0; i < clients.size();i++)
		    		clients.elementAt(i).connected = false;
		    	//Send and receive meta data from server:
		    	//Send points
		    	outStream.writeInt(score);
		    	//Receive number of players
		    	players = inStream.readInt();
		    	//Receive names of all the players;
		    	for(int i =0; i < players; i++)
		    	{
		    		//Read the ID the server wants to send us
		    		int ID = inStream.readInt();
		    		//And his points
		    		int score = inStream.readInt();
		    		boolean known = false;
		    		//Check if we already have the player
		    		for(int j =0; j < clients.size(); j++)
		    		{
		    		
		    			if(clients.elementAt(j).ID == ID)
		    			{
		    				clients.elementAt(j).connected= true;
		    				clients.elementAt(j).points = score;
		    				known = true;
		    				break;
		    			}
		    		}
		    		outStream.writeBoolean(known);
		    		//We dont know that guy
		    		if(!known)
		    		{
		    			Client c= new Client();
		    			c.ID = ID;
		    			String tmp="";
		    			int nameLength = inStream.readInt();
		    			for(int j =0; j < nameLength; j++)
		    			tmp+=inStream.readChar();
		    			c.name = tmp;
		    			c.connected= true;
		    			clients.add(c);
		    		}
		    		//Oh you mean THAT guy...
		    		
		    		

		    	}
		    	//Delete all unknown players
		    	for(int i =0; i < clients.size(); i++)
		    		if(!clients.elementAt(i).connected)
		    			remove.add(clients.elementAt(i));
		    	
		    	for(int i =0; i < remove.size(); i++)
		    		clients.remove(remove.elementAt(i));
		    	remove.clear();
		    	
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
