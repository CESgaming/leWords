import java.net.*;
import java.util.Vector;
import java.io.*;


public class GameManager  extends Thread {

	public Vector<ServerThread> clients = new Vector<ServerThread>();
	public Vector<ServerThread> del = new Vector<ServerThread>();
	public Vector<String> names = new Vector<String>();
	public GameManager()
	{}
	

	public void run()
	{
		boolean running = true;
		int numberOfClients=0;
		while(running)
		{
			//Update the player count on all clients:
			numberOfClients = clients.size();
			//Now iterate through all clients and update their information
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(clients.size()>0)
	        	for(int i =0; i < numberOfClients; i++)
	        	{
	        		if(!clients.elementAt(i).isAlive())
	        			{del.add(clients.elementAt(i));continue;}
	        		else
	        		{
	        		if(!names.contains(clients.elementAt(i).name))
	        			{names.add(clients.elementAt(i).name);System.out.println(clients.elementAt(i).name);}
	        		clients.elementAt(i).players = numberOfClients;
	        		clients.elementAt(i).names = names;
	        		}
	        		
	        	}
			for(int i =0; i < del.size(); i++)
			{

				//names.remove(clients.elementAt(i).name);
				clients.remove(del.elementAt(i));
			}
			del.clear();
			
		}
		
	}
	
	/*
        	*/
}
