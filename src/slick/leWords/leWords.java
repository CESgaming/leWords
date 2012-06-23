//package slick.leWords;
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
    static Font font; 
    
    Socket kkSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    
	static TrueTypeFont ttFont;
	static int fieldsize = 25;
	static int dim = 5;
	Field field[][];
	Vector <Field>selection;
	Field selected;
	Input input;
	String output;
	int mouseX;
	int mouseY;
	int score;
	ClientThread client;
  Board b;
  Hypercube h;
  Dictionary d;

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
    	score =0;
    	//Load Textures
      // Thomas part
      //
      b = new Board(dim);
      h = new Hypercube();
      d = new Dictionary();
      h.fillHypercube();
      d.fillCompleteDictionary();
      b.fillBoard();
      b.filterLevelOne(d,h);
      b.filterLevelTwo();




    	openConnection();
        loadTextures();
        InitField();
    	
    }

    @Override
    public void update(GameContainer container, int delta)
            throws SlickException {
    	
    	//Update mouse position
    	mouseX = input.getMouseX();
    	mouseY = input.getMouseY();
    	
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
    		selected = null;
    		selection.clear();
    		//client.right = output;
    		//out.println(output);
        score+= b.checkWord(output);
        

        

    		output = "";
    	/*	if(client.getPoints == true)
    		{
    			score+=100;
    			client.getPoints = false;
    			
    		}
    		*/
    		
    	}
    	
    	
    	
    }

    @Override
    public void render(GameContainer container, Graphics g)
            throws SlickException {
    	//Draw Textures
    	bg.draw(0,0);
    	//Draw Fields
    	for(int i =0; i < dim; i++)
    	{
    		for(int j =0; j < dim; j++)
    	{
    		if(field[i][j].selected == true)
    		{
    			field_blank_selected.draw(field[i][j].x, field[i][j].y);
        		ttFont.drawString(field[i][j].x+18, field[i][j].y+12, String.valueOf(field[i][j].c), Color.black);

    		}
    		else
    		{
    			field_blank.draw(field[i][j].x, field[i][j].y);
        		ttFont.drawString(field[i][j].x+18, field[i][j].y+12, String.valueOf(field[i][j].c), Color.white);	
    		}
    		
    	}
    	}
    	ttFont.drawString(100, 600, output);
    	ttFont.drawString(100,700,String.valueOf(score));
    	
    }
    
    public void InitField()
    {
    field = new Field[dim][dim];
    int k =0;
    	for(int i =0; i < dim; i++)
    	{
    		for(int j =0; j < dim; j++)
    	{
    	field[i][j] = new Field(74+j*(64+8), 68+i*(64+8),i,j,b.letters[i][j]);
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
         //   client.start();
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
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
    	bg = new Image("images/background.png");
    	field_blank = new Image("images/field_blank.png");
    	field_blank_selected = new Image("images/field_blank_selected.png");
    }

    public static void main(String[] args) {
    	
    	System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
    	System.setProperty("net.java.games.input.librarypath", System.getProperty("org.lwjgl.librarypath"));
    	
        try {
            AppGameContainer app = new AppGameContainer(new leWords());
            app.setDisplayMode(500,800,false);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
