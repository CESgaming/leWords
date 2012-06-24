import java.net.*;
import java.util.Vector;
import java.io.*;


public class ServerThread extends Thread {
	private Socket socket = null;

	public String word;
	public int score;
	public Board b;

	public boolean disconnected = false;
	public String name = "";
	public int players;
	public Vector<String> names = new Vector<String>();
    public ServerThread(Socket socket,Board b) {
	super("ServerThread");
	this.socket = socket;
	this.b = b;
    }

    public void run() {

	try {
	    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String inputLine;
	    
	    DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
	    DataInputStream inStream = new DataInputStream(socket.getInputStream());
	    //Client Server Handshake and Authentication
	    String tempName = "";
	    int nameSize = inStream.readInt();
	    for(int i =0; i <nameSize ; i++)
	    	tempName+=inStream.readChar();
	    name = tempName;
	    
	    //First of all, send Board Information:
	    outStream.writeInt(5);
	    for(int i =0; i < b.dim; i++)
	    	for(int j =0; j < b.dim; j++)
	    		outStream.writeChar(b.letters[i][j]);
	    
	    //Send board dictionary
	    int dictSize = b.boardDictionary.length;
	    outStream.writeInt(dictSize);
	    for(int i =0; i < dictSize; i++)
	    {
	    	word = b.boardDictionary.dictionary[i];
	    	outStream.writeInt(word.length());
	    	for(int j =0; j <word.length(); j++)
	    		outStream.writeChar(word.charAt(j));
	    		
	    }
	    
	    boolean listening = true;
	    while (listening) 
	    {
	    	//Reading and writing meta data to every client
		    //System.out.println("listening");
	    	//Get points of client
		    score = inStream.readInt();
	    	//Write names to player
		    outStream.writeInt(players);
		    for(int i =0; i < players; i++)
		    {
		    	outStream.writeInt(names.elementAt(i).length());
		    	outStream.writeChars(names.elementAt(i));
		    }
	    }
	    out.close();
	    in.close();
	    socket.close();

	} catch (IOException e) {
	    disconnected = true;
	    System.out.println("Player "+name+" disconnected");
	   // e.printStackTrace();
	}
    }
}