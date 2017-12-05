package items;

import board.Board;
import creatures.Human;
import creatures.Monster;

public class Shortsighted_Glasses extends Items
{
	public Shortsighted_Glasses(Board board, Human human, Monster monster)
	{
		super(board, human, monster);
		name = "Shortsighted_Glasses";
		numberOf = 0;
	}
	
	private int numberOf = 0;
	
	public void effect()
	{
		if(possession)
		{
			human.setVisao(1);
		}
	}

	public boolean getPossession()
	{
		return possession;
	}

	public void acquire(int num)
	{
		numberOf += num;
		possession = true;
		effect();
	}

	public int getNumberOf()
	{
		return numberOf;
	}
	
}
