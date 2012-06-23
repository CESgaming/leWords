//package slick.leWords;
import java.net.*;
import java.util.Vector;
import java.io.*;
 
public class Server {
    public static void main(String[] args) throws IOException {
 
    	Vector<ServerThread> clients = new Vector<ServerThread>();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(5222);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 5222.");
            System.exit(1);
        }
        boolean listening = true;
 
        while(listening)
        {
        	ServerThread tmp = new ServerThread(serverSocket.accept());
        	clients.add(tmp);
        	tmp.start();
        	
        }
        serverSocket.close();
    }
}