package slick.leWords;
import java.awt.Font;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;
import java.io.*;
import java.net.*;
@SuppressWarnings("deprecation")
public class leWords extends BasicGame {

	//Declaring images
	static Image bg = null;
	static Image field_blank = null;
	static Image field_blank_selected= null;
	static Image field_blank_correct = null;
	static Image field_blank_wrong = null;
	static Image field_blank_known = null;
	static Font font; 

	Socket kkSocket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	DataInputStream dd = null;
	DataOutputStream aa= null;
	static TrueTypeFont ttFont;
	static int fieldsize = 25;
	static int dim = 5;
	Field field[][];
	Vector <Field>selection;
	Vector <Field>latestWord;
	Vector <String>history;
	Vector <String>names;
	Field selected;
	Input input;
	boolean alreadyIn;
	String output;

	int mouseX;
	int mouseY;
	int score;
	float fadeTimer;
	int latestWordState=0;
	char letters[][];
	String[] dict;
	String name;
	ClientThread client;
	public leWords() {
		super("leWords");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		//Load the Fonts
		font = new Font("Arial", Font.PLAIN, 36);
		ttFont =  new TrueTypeFont(font,true);
		output = new String("");
		//Set up the input
		input = container.getInput();
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
		selection = new Vector<Field>();
		latestWord = new Vector<Field>();
		history = new Vector<String>();
		names = new Vector<String>();
		score =0;
		fadeTimer =0;


		//Has to be AFTER initField()! (Danger of being out of synch with the server else!


		openConnection();

		//getName and send it to the server
		try {
			FileInputStream fstream = new FileInputStream("cfg/config.cfg");
			DataInputStream fin = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			name = br.readLine();
			aa.writeInt(name.length());
			for(int i =0; i < name.length(); i++)
				aa.writeChar(name.charAt(i));


		} catch (FileNotFoundException e) {
			System.out.println("confing.cfg is missing");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("could read config.cfg");
			e.printStackTrace();
		}

		loadTextures();
		InitField();
		client.start();


	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {

		//Update mouse position
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
		//Update other clients data
		//names.clear();
		//names = client.names;

		if(input.isMouseButtonDown(0))
		{	
			//Update the fields
			for(int i =0; i < dim; i++)
			{
				for(int j =0; j < dim; j++)
				{
					if(field[i][j].update(mouseX, mouseY))
					{
						if(field[i][j].selected == false)
						{	
							//Check adjacent fields
							if(selected == null)
							{
								field[i][j].selected = true;
								selected = field[i][j];
								selection.add(field[i][j]);
								output += field[i][j].c;
							}
							else if(field[i][j].neighbours.contains(selected))
							{
								field[i][j].selected = true;
								selected = field[i][j];
								selection.add(field[i][j]);
								output += field[i][j].c;
							}
						}
						else if(field[i][j].neighbours.contains(selected))
						{
							selected.selected =false;
							selection.remove(selected);
							field[i][j].selected = true;
							selected = field[i][j];
							output = output.substring(0,output.length()-1);
						}

					}

				}
			}
		}
		else if(selection.size()>0)
		{
			for(int i =0; i < selection.size();i++)
			{
				selection.elementAt(i).selected=false;
			}

			latestWord = (Vector<Field>)selection.clone();
			selection.clear();
			selected = null;
			fadeTimer = 1.5f;

			if(!history.contains(output))
			{
				int points = checkWord(output);
				if(points >0)
				{
					history.add(output);
					score+=points;
					client.score = score;
					latestWordState = 1;
					alreadyIn = false;
				}
				else
				{
					latestWordState =0;
				}
			}
			else
			{
				latestWordState = 2;
				alreadyIn = true;

			}
			output = "";

		}



	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		//Draw Textures
		bg.draw(0,0);
		
		//Draw Fields
		//Adjust fadeTimer
		if(fadeTimer >0)
			fadeTimer -=0.01;
		else
			latestWord.clear();

		for(int i =0; i < dim; i++)
		{
			for(int j =0; j < dim; j++)
			{
				//Draw all the background tiles
				field_blank.draw(field[i][j].x, field[i][j].y);
				ttFont.drawString(field[i][j].x+18, field[i][j].y+12, String.valueOf(field[i][j].c).toUpperCase(), Color.black);	

				if(field[i][j].selected == true)
				{
					field_blank_selected.draw(field[i][j].x, field[i][j].y);
					ttFont.drawString(field[i][j].x+18, field[i][j].y+12, String.valueOf(field[i][j].c).toUpperCase(), Color.black);

				}
				else if(fadeTimer > 0 && latestWord.contains(field[i][j]) )
				{
					switch(latestWordState)
					{
					case 0: 
						field_blank_wrong.setAlpha(fadeTimer);
						field_blank_wrong.draw(field[i][j].x, field[i][j].y);
						break;
					case 1:
						field_blank_correct.setAlpha(fadeTimer);
						field_blank_correct.draw(field[i][j].x, field[i][j].y);
						break;
					case 2:
						field_blank_known.setAlpha(fadeTimer);
						field_blank_known.draw(field[i][j].x, field[i][j].y);
						break;
					}
					ttFont.drawString(field[i][j].x+18, field[i][j].y+12, String.valueOf(field[i][j].c).toUpperCase(), Color.black);

				}
			}
		}

		//Draw other player names:
		int overflow = client.clients.size();
		for(int i =0; i <overflow ; i++)
		{
			overflow = client.clients.size();
			ttFont.drawString(500,100+i*48,client.clients.elementAt(i).name);
			ttFont.drawString(450,100+i*48,String.valueOf(client.clients.elementAt(i).points));
		}

		if (output!="" ){
			if (!alreadyIn){
				ttFont.drawString(100, 500, "Du wählst:", Color.black);
				ttFont.drawString(300, 500, output.toUpperCase(), Color.black);
				ttFont.drawString(100, 550, "für "+ calcWordPoints(output)+ " Punkte.", Color.black);

			}
		}
		if (alreadyIn && output==""){
			ttFont.drawString(100, 500, "Wiederholung!", Color.black);
		}
		if (alreadyIn && output!=""){
			alreadyIn = false;
		}

		

	
		ttFont.drawString(100,00,"Punkte: "+String.valueOf(score), Color.black);
		ttFont.drawString(450,00,"Anzahl Spieler: "+String.valueOf(client.players), Color.black);

		// Edit thomas here: display your 10 last words;
		int to = 10;
		if (history.size()<10){
			to = history.size();
		}
		for (int i=0;i<to;i++){
			// display last 5 words;
			int pos = history.size()-1-i;
			
			ttFont.drawString(450,100+i*50,"#"+(history.size()-i) +": "+history.get(pos).toUpperCase() , Color.black);
			
		}

	}

	public void InitField()
	{

		//Getting the data from the server:
		try {

			dim= dd.readInt();

			letters = new char[dim][dim];
			for(int i =0; i < dim; i++)
			{
				for(int j =0; j < dim; j++)
				{
					letters[i][j] = dd.readChar();
				}
			}
			//Getting the dictionary
			int dictSize = dd.readInt();
			dict = new String[dictSize];
			String word = "";
			for(int i =0; i < dictSize; i++)
			{
				int tmp = dd.readInt();
				for(int j =0; j < tmp; j++)
					word+=dd.readChar();

				dict[i] = word;
				word = "";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}    	

		field = new Field[dim][dim];
		int k =0;
		for(int i =0; i < dim; i++)
		{
			for(int j =0; j < dim; j++)
			{
				field[i][j] = new Field(74+j*(64+8), 68+i*(64+8),i,j,letters[i][j]);
				k++;
			}
		}
		for(int i =0; i < dim; i++)
		{
			for(int j =0; j < dim; j++)
			{
				if(i-1>=0&&j-1>=0)
					field[i][j].neighbours.add(field[i-1][j-1]);
				if(j-1>=0)
					field[i][j].neighbours.add(field[i][j-1]);
				if(j-1>=0&& i+1<dim)
					field[i][j].neighbours.add(field[i+1][j-1]);
				if(i-1>=0)
					field[i][j].neighbours.add(field[i-1][j]);
				if(i+1<dim)
					field[i][j].neighbours.add(field[i+1][j]);
				if(j+1<dim)
					field[i][j].neighbours.add(field[i][j+1]);
				if(j+1<dim&&i-1 >=0)
					field[i][j].neighbours.add(field[i-1][j+1]);
				if(j+1<dim&& i+1<dim)
					field[i][j].neighbours.add(field[i+1][j+1]);
			}
		}
	}
	public void openConnection()
	{
		try {
			//kkSocket = new Socket("217.94.0.124", 5222);
			kkSocket = new Socket("127.0.0.1", 5222);

			client = new ClientThread(kkSocket);
			out = new PrintWriter(kkSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
			dd = new DataInputStream(kkSocket.getInputStream());
			aa = new DataOutputStream(kkSocket.getOutputStream());

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: taranis.");
			//  System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: taranis.");
			//  System.exit(1);
		}
	}

	public static void loadTextures()
			throws SlickException
			{
		bg = new Image("images/background2.png");
		
		field_blank = new Image("images/field_blank.png");
		field_blank_selected = new Image("images/field_blank_selected.png");
		field_blank_correct = new Image("images/field_blank_correct.png");
		field_blank_wrong = new Image("images/field_blank_wrong.png");
		field_blank_known = new Image("images/field_blank_known.png"); 
			}

	public static void main(String[] args) {

		System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
		System.setProperty("net.java.games.input.librarypath", System.getProperty("org.lwjgl.librarypath"));

		try {
			AppGameContainer app = new AppGameContainer(new leWords());
			app.setDisplayMode(800,600,false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public int pointsOf(char c){
		// this is heuristic
		// see http://de.wikipedia.org/wiki/Buchstabenh%C3%A4ufigkeit
		// points by position of frequency

		int points=0;
		switch(c){
		case'e':
		case'n':
		case'i':
		case's': 
			points = 1; 
			break;
		case'r':
		case'a':
		case't':
		case'd':
			points = 2; 
			break;
		case'h':
		case'u':
		case'l':
		case'c':
			points = 3; 
			break;
		case'g':
		case'm':
		case'o':
		case'b':
			points = 4; 
			break;
		case'w':
		case'f':
		case'k':
		case'z':
			points = 5;
			break;
		case'p':
		case'v':
		case'j':
			points = 6;
			break;
		case'y':
		case'x':
		case'q':
			points =7; 
			break;
		}



		return points;
	}

	public int checkWord(String S){
		int p =0 ; // if word doesnt exist : 0 points
		boolean found = false;
		for ( int i=0;i<dict.length &&!found ; i++){
			// for(int i=340;i<390;i++){
			// System.out.println(S );
			//    System.out.println(i );
			//    System.out.println(boardDictionary.dictionary[i] );
			if (dict[i].equalsIgnoreCase(S)){
				// if( boardDictionary.dictionary[i] == S){
				p = calcWordPoints(dict[i]); ; // TODO calc points correctly!
				found = true;
			}
		}


		return p;
	}

	public int calcWordPoints(String S){
		int p  =0 ;
		char[] c = S.toCharArray();
		for (int i=0;i<c.length;i++){
			p+=pointsOf(c[i]);

		}
		return p;


	}
}
