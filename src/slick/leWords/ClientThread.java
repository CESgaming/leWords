//package slick.leWords;

import java.net.*;
import java.io.*;

public class ClientThread extends Thread {

	
	private Socket socket = null;
	public String right = "";
	boolean getPoints = false;
	public ClientThread(Socket socket)
	{
	super("ClientThread");

	this.socket = socket;
	}
	
	
	public void run()
	{
		try{
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String inputLine, outputLine;
			while((inputLine = in.readLine())!= null)
			{
				System.out.println(inputLine);
				System.out.println(right);
				if(right.compareTo(inputLine)==0)
				{
					getPoints = true;
					System.out.println("getPoints");
				}
				//if(inputLine == right)
				//	right= true;
				
			}
			out.close();
			in.close();
			socket.close();
			
		}catch(IOException e)
		{
			e.printStackTrace();
			
		}
		
		
		
	}
}
