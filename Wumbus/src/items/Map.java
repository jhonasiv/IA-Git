
package items;

import board.Board;
import creatures.*;

public class Map extends Items
{
	
	public Map(Board board, Human human, Monster monster)
	{
		super(board, human, monster);
		name = "Map";
		numberOf = 0;
	}
	
	public void effect()
	{
		if(possession)
		{
			human.gatherDungeon();
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
