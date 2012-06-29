package slick.leWords;

import java.util.Vector;

public class Field {
	public int x;
	public int y;
	public int i;
	public int j;
	public static int size = 64;
	public char c;
	public Vector<Field> neighbours;
	public boolean selected = false;
	
	public Field(int x,int y,int i, int j, char c)
	{
		this.x = x;
		this.y = y;
		this.i = i;
		this.j = j;
		this.c = c;
		neighbours = new Vector<Field>();
		
	}
	
	public boolean checkIntersection(int mX, int mY)
	{
		int R;
		R = 10;
		if(mX > x+R && mX < x+size-R && mY >y+R && mY < y+size-R)
			return true;
		else
		return false;
	}
	
	public boolean update(int mouseX, int mouseY)
	{
		if(checkIntersection(mouseX, mouseY))
			return true;
		else
			return false;
		
		
	}
	

}
