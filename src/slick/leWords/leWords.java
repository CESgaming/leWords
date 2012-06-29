package slick.leWords;
import java.awt.Font;
import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.Vector;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
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
import org.newdawn.slick.gui.TextField;

import java.io.*;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
@SuppressWarnings({ "deprecation", "unused" })
public class leWords extends BasicGame {

	//Declaring images
	static Image bg = null;
	static Image field_blank = null;
	static Image field_blank_selected= null;
	static Image field_blank_correct = null;
	static Image field_blank_wrong = null;
	static Image field_blank_known = null;
	static Font font; 
	static Font listfont; 
	
	static Font pointfont; 
	
	static TrueTypeFont pointFont;


	Socket kkSocket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	DataInputStream dd = null;
	DataOutputStream aa= null;
	static TrueTypeFont ttFont;
	static TrueTypeFont listFont;
	static int fieldsize = 25;
	static int dim = 5;
	Field field[][];
	Field oButton;
	Field kButton;
	Vector <Field>selection;
	Vector <Field>latestWord;
	Vector <String>history;
	Vector <String>names;
	Field selected;
	Input input;
	boolean newestBoardRead = true;
	boolean alreadyIn;
	boolean hasName = false;
	boolean hasIP = false;
	String output;
	int xoffset, yoffset;
	int mouseX;
	int mouseY;
	int score;
	
	TextField tf;
	float fadeTimer;
	int latestWordState=0;
	char letters[][];
	String[] dict;
	String name = "";
	String IP = "";
	ClientThread client;
	public leWords() {
		super("leWords");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		//Load the Fonts
		font = new Font("Arial", Font.PLAIN, 36);
		ttFont =  new TrueTypeFont(font,true);
		listfont = new Font("Arial", Font.PLAIN, 16);
		listFont =  new TrueTypeFont(listfont,true);

		pointfont = new Font("Arial", Font.PLAIN,12);
		pointFont = new TrueTypeFont(pointfont,true);
		output = new String("");
		//Set up the input
		input = container.getInput();
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
		selection = new Vector<Field>();
		latestWord = new Vector<Field>();
		history = new Vector<String>();
		names = new Vector<String>();
		oButton = new Field(510 ,600,0,0,'O');
		kButton = new Field(530,600,0,0,'K');
		tf = new TextField(container, ttFont, 350,400,400,100);
		tf.setBackgroundColor(Color.black);
		tf.setTextColor(Color.white);
		tf.setFocus(true);
		tf.setBorderColor(Color.black);
		tf.setText("thomas");
		score =0;
		fadeTimer =0;
		xoffset = 250;
		yoffset = 50;


		//Has to be AFTER initField()! (Danger of being out of synch with the server else!


		//openConnection();

		//getName and send it to the server
		
		loadTextures();
		//DummyField();
		//InitField();
		//client.start();


	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {

		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
	
		if( !hasName)
		{

			
		
			if(input.isMouseButtonDown(0)&&(oButton.update(mouseX,mouseY)|| kButton.update(mouseX, mouseY)) && !tf.getText().equals(""))
			{

				name = tf.getText();
				hasName = true;
				
				tf.setText("");
			
				
			}
			return;
				
		}
		if(!hasIP)
		{
			if(input.isMouseButtonDown(0)&&(oButton.update(mouseX,mouseY)|| kButton.update(mouseX, mouseY)) && (!tf.getText().equals("")) && !tf.getText().equals(""))
			{
				IP = tf.getText();
				hasName = true;
				hasIP = true;
				openConnection();
				try {
					aa.writeInt(name.length());
					for(int i =0; i < name.length(); i++)
					aa.writeChar(name.charAt(i));
					} catch (IOException e) {
					
					e.printStackTrace();
				}
					
				DummyField();
				client.start();
				tf.deactivate();
				
			}
			return;
				
		}
		//Update mouse position
		//Update other clients data
		//names.clear();
		//names = client.names;
		//Check if the round is over and the board gets reshuffled:
		if(client.hasNewBoard)
		{
			InitField();
			client.hasNewBoard = false;
		}
		if(client.time>120)
			return;
	
		
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
							else if( field[i][j].neighbours.contains(selected))
							{
								field[i][j].selected = true;
								selected = field[i][j];
								selection.add(field[i][j]);
								output += field[i][j].c;
							}
						}
						
						
						else if(/*selection.elementAt(selection.size()-1).neighbours.contains(selected))*/field[i][j].neighbours.contains(selected) )
						{ // thomas : bug occours ! you can only go back the LAST one in list, this is not the case
							
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
		//bg.draw(0,0);
		if(!hasName || !hasIP )
		{
			String t = "Name eingeben.";
			if (hasName) t = "IP eingeben.";
			tileStringPrint(t ,400,300,1);
			field_blank_correct.draw(oButton.x,oButton.y);
			field_blank_correct.draw(kButton.x,kButton.y);
			ttFont.drawString(oButton.x, oButton.y, "Ok", Color.black);	
			tf.render(container, g);
			return;
			
		}
		
		//Show the score screen when the round is over:
		if(client.time> 120)
		{
			int overflow = client.clients.size();
			int upto=  10;
			for(int i =0; i <upto && i< overflow ; i++)
			{// TODO add client != me, I am no enemy for myself
				// TODO display only the BEST enemies
				overflow = client.clients.size();

				//ttFont.drawString(450,100+i*48,String.valueOf(client.clients.elementAt(i).points));
				tileStringPrint(client.clients.elementAt(i).name+ " "
						+ String.valueOf(client.clients.elementAt(i).points),
						350,i*30+150,9);
				tileStringPrint(String.valueOf(20-(client.time-120)),466,50,9);
			
			}
			return;
		}

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
				String s = " " +pointsOf(field[i][j].c);
				pointFont.drawString(field[i][j].x+50, field[i][j].y+50, s);
				if(field[i][j].selected == true)
				{
					field_blank_selected.draw(field[i][j].x, field[i][j].y);
					ttFont.drawString(field[i][j].x+18, field[i][j].y+12, String.valueOf(field[i][j].c).toUpperCase(), Color.black);
				//	 s = " " +pointsOf(field[i][j].c);
				//	pointFont.drawString(field[i][j].x+30, field[i][j].y+20, s);

				}
				else if(fadeTimer > 0 && latestWord.contains(field[i][j]) )
				{
					 
					switch(latestWordState)
					{
					case 0: 
						field_blank_wrong.setAlpha(fadeTimer);
						field_blank_wrong.draw(field[i][j].x, field[i][j].y);
					//	 s = " " +pointsOf(field[i][j].c);
					//	pointFont.drawString(field[i][j].x+30, field[i][j].y+20, s);
						break;
					case 1:
						field_blank_correct.setAlpha(fadeTimer);
						field_blank_correct.draw(field[i][j].x, field[i][j].y);
					//	 s = " " +pointsOf(field[i][j].c);
					//	pointFont.drawString(field[i][j].x+30, field[i][j].y+20, s);
						break;
					case 2:
						field_blank_known.setAlpha(fadeTimer);
						field_blank_known.draw(field[i][j].x, field[i][j].y);
					//	 s = " " +pointsOf(field[i][j].c);
					//	pointFont.drawString(field[i][j].x+30, field[i][j].y+20, s);
						break;
					}
					ttFont.drawString(field[i][j].x+18, field[i][j].y+12, String.valueOf(field[i][j].c).toUpperCase(), Color.black);
				//	 s = " " +pointsOf(field[i][j].c);
				//	pointFont.drawString(field[i][j].x+30, field[i][j].y+20, s);

				}
			}
		}


		if (output!="" ){
			if (!alreadyIn){
		
			//	ttFont.drawString(350, 600, output.toUpperCase(), Color.white);
			//	ttFont.drawString(350, 650, "für "+ calcWordPoints(output)+ " Punkte.", Color.white);
				tileStringPrint(output.toUpperCase(),350,500,1);
				tileStringPrint("gibt "+ calcWordPoints(output)+ " Punkte",350,550,1);
			}
		}
		if (alreadyIn && output==""){
			ttFont.drawString(350, 600, "Wiederholung!", Color.white);
		}
		if (alreadyIn && output!=""){
			alreadyIn = false;
		}
		
		if ((120-client.time )>0){
			// 2 digits, 466 is exactly above third latter
		//	Color tc = new Color(255-255/60*(120-client.time),255/60*(120-client.time),0);
			tileStringPrint(String.valueOf(120-client.time),466,50,2);
			
		//} else{
		
		//	tileStringPrint(String.valueOf(120-client.time),452,50,9);
		}


		// Edit thomas here: display your 10 last words;
		int to = 15;
		if (history.size()<to){
			to = history.size();
		}
		tileStringPrint("Deine Treffer",450+xoffset,150,1);
		for (int i=0;i<to;i++){
			// display last 5 words;
			int pos = history.size()-1-i;
			if (calcWordPoints(history.get(pos))>9){
			tileStringPrint( calcWordPoints(history.get(pos))+"  " +history.get(pos).toUpperCase(), 450+xoffset,200+i*30,0);
			}else{ // one more space inserted
				tileStringPrint( calcWordPoints(history.get(pos))+"   " +history.get(pos).toUpperCase(), 450+xoffset,200+i*30,0);
			}


		}


		//Draw other player names:
		int overflow = client.clients.size();
		int upto=  10;
		for(int i =0; i <upto && i< overflow ; i++)
		{// TODO add client != me, I am no enemy for myself
			// TODO display only the BEST enemies
			overflow = client.clients.size();

			//ttFont.drawString(450,100+i*48,String.valueOf(client.clients.elementAt(i).points));
			tileStringPrint(client.clients.elementAt(i).name+ " "
					+ String.valueOf(client.clients.elementAt(i).points),
					20,i*30+150,2);
		}


	}
	public void tileStringPrint(String s, int x, int y,int p,Color co){
		float scale = 0.3f;
		for (int i=0;i<s.length();i++){
			char c = s.charAt(i);
			if (c != ' '){
			int dx = 22;
			
			switch (p){
			case 0:

				field_blank_known.getScaledCopy(scale).draw(x+i*dx, y);
				listFont.drawString(x+i*dx+4, y, s.substring(i, i+1).toUpperCase(), co);	
				break;
			case 1:
				field_blank_correct.getScaledCopy(scale).draw(x+i*dx, y);
				listFont.drawString(x+i*dx+4, y, s.substring(i, i+1).toUpperCase(), co);	
				break;

			case 2:
				field_blank_wrong.getScaledCopy(scale).draw(x+i*dx, y);
				listFont.drawString(x+i*dx+4, y, s.substring(i, i+1).toUpperCase(), co);	
				break;
			case 9: // only the time 
				field_blank_wrong.getScaledCopy(0.5f).draw(x+i*36, y);
				ttFont.drawString(x+i*36+8, y-4, s.substring(i, i+1).toUpperCase(), co);	
				break;
			
			}
			
			}
		}
		

	}

	public void tileStringPrint(String s, int x, int y,int p){
		float scale = 0.3f;
		for (int i=0;i<s.length();i++){
			char c = s.charAt(i);
			if (c != ' '){
			int dx = 22;
			
			switch (p){
			case 0:

				field_blank_known.getScaledCopy(scale).draw(x+i*dx, y);
				listFont.drawString(x+i*dx+4, y, s.substring(i, i+1).toUpperCase(), Color.black);	
				break;
			case 1:
				field_blank_correct.getScaledCopy(scale).draw(x+i*dx, y);
				listFont.drawString(x+i*dx+4, y, s.substring(i, i+1).toUpperCase(), Color.black);	
				break;

			case 2:
				field_blank_wrong.getScaledCopy(scale).draw(x+i*dx, y);
				listFont.drawString(x+i*dx+4, y, s.substring(i, i+1).toUpperCase(), Color.black);	
				break;
			case 9: // only the time 
				field_blank_wrong.getScaledCopy(0.5f).draw(x+i*36, y);
				ttFont.drawString(x+i*36+8, y-4, s.substring(i, i+1).toUpperCase(), Color.black);	
				break;
			
			}
			
			}
		}
		

	}



	public void DummyField()
	{
		dim = 5;
		letters = new char[dim][dim];
		for(int i =0; i < dim; i++)
		{
			for(int j =0; j < dim; j++)
			{
				letters[i][j] = ' ';
			}
		}
		//Getting the dictionary
		int dictSize = 0;
		dict = new String[dictSize];

		field = new Field[dim][dim];


		for(int i =0; i < dim; i++)
		{
			for(int j =0; j < dim; j++)
			{
				field[i][j] = new Field(74+j*(64+8)+xoffset, 68+i*(64+8)+yoffset,i,j,letters[i][j]);

			}
		}
	}

	public void InitField()
	{
		dim = client.dim;
		letters = client.letters;
		dict = client.dict;


		field = new Field[dim][dim];


		for(int i =0; i < dim; i++)
		{
			for(int j =0; j < dim; j++)
			{
				field[i][j] = new Field(74+j*(64+8)+xoffset, 68+i*(64+8)+yoffset,i,j,letters[i][j]);

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
		//Reset Score and Everything else
		score =0;
		history.clear();
	}
	public void openConnection()
	{
		try {
			//kkSocket = new Socket("217.94.0.124", 5222);
			kkSocket = new Socket(IP, 5222);

			client = new ClientThread(kkSocket);
			out = new PrintWriter(kkSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
			dd = new DataInputStream(kkSocket.getInputStream());
			aa = new DataOutputStream(kkSocket.getOutputStream());

		} catch (UnknownHostException e) {
			Sys.alert("Server error", "IP "+IP+" could not be resolved!");
		} catch (IOException e) {
			Sys.alert("Server error", "Server on "+IP+" is not responding!");
		}
	}

	public static void loadTextures()
			throws SlickException
			{
		bg = new Image("images/background.png");
		bg = bg.getScaledCopy(2);

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
			app.setDisplayMode(1024,768,false);
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
				p = calcWordPoints(dict[i]); 
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
		p = p+c.length;
		return p;


	}
}
