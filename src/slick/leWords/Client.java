package slick.leWords;

public class Client implements Comparable<Client>{

	public int ID;
	public String name = "";
	public int points;
	public boolean connected = true;
	public Client()
	{}
	
	@Override
	public int compareTo(Client o)
	{
		return o.points-this.points;
	}
	
}
