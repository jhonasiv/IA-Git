package items;

import board.Board;
import creatures.Human;
import creatures.Monster;

public class Bow extends Items
{
	
	public Bow(Board board, Human human, Monster monster)
	{
		super(board, human, monster);
		name = "Bow";
		numberOf = 0;
	}
	
	public void acquire(int number)
	{
		numberOf += number;
		effect();
	}
	
	public void effect()
	{
		if (numberOf != 0)
		{
			possession = true;
		}
	}
	
	public boolean getPossession()
	{
		return possession;
	}
	
	public int getNumberOf()
	{
		return numberOf;
	}
}
