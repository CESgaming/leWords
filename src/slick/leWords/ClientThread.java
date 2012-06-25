package slick.leWords;

import java.net.*;
import java.util.Collections;
import java.util.Vector;
import java.io.*;

public class ClientThread extends Thread {

	
	private Socket socket = null;
	public int score;
	public int time=0;
	public int players=0;
	int dim=0;
	boolean hasNewBoard = false;
	char letters[][];
	String[] dict;
	public int GAMESTATE =0;
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
		    	int GAMESTATE = inStream.readInt();
		    	//System.out.println(tmpG);
		    	//if(tmpG != 2 && !wait)
		    	//	GAMESTATE = tmpG;
		    	
		    	switch(GAMESTATE)
		    	{
		    	case 1:
		    		
		    		//Getting the data from the server:
		    		try {
		    			dim= inStream.readInt();

		    			letters = new char[dim][dim];
		    			for(int i =0; i < dim; i++)
		    			{
		    				for(int j =0; j < dim; j++)
		    				{
		    					letters[i][j] = inStream.readChar();
		    				}
		    			}
		    			//Getting the dictionary
		    			int dictSize = inStream.readInt();
		    			dict = new String[dictSize];
		    			String word = "";
		    			for(int i =0; i < dictSize; i++)
		    			{
		    				int tmp = inStream.readInt();
		    				for(int j =0; j < tmp; j++)
		    					word+=inStream.readChar();

		    				dict[i] = word;
		    				word = "";
		    			}
		    			hasNewBoard = true;
		    			score = 0;

		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		} 
		    		break;
		    		
		    	case 2:
		    	//Set all clients to be disconnected
		    	for(int i =0; i < clients.size();i++)
		    		clients.elementAt(i).connected = false;
		    	//Send and receive meta data from server:
		    	time = inStream.readInt();
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
		    				if(clients.elementAt(j).name.equalsIgnoreCase("")){known = false;break;}
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
		    	Collections.sort(clients);
		    	break;
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
