package items;
import board.Board;
import creatures.Human;
import creatures.Monster;

public class Torch extends Items
{
	public Torch(Board board, Human human, Monster monster)
	{
		super(board, human, monster);
		name = "Torch";
		numberOf = 0;
	}
	
	private int numberOf = 0;
	private int durability = 4;
	private boolean using = false;
	
	public void effect()
	{
		if(possession)
		{
			human.setVisao(1);
			using = true;
			if(using)
			{
				wearOut();
			}
		}
		if(durability == 0)
		{
			numberOf--;
			if(numberOf == 0)
			{
				possession = false;
			}
			durability = 4;
			using = false;
			human.setVisao(0);
		}
	}
	
	public boolean getPossession()
	{
		return possession;
	}
	
	public void wearOut()
	{
		durability--;
	}
	
	public void acquire(int num)
	{
		numberOf += num;
		possession = true;
	}
	
	public int getNumberOf()
	{
		return numberOf;
	}
}
