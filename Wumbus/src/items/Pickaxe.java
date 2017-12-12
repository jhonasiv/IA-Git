
package items;

import java.awt.Point;

import board.Board;
import creatures.Human;
import creatures.Monster;

public class Pickaxe extends Items
{
	
	public Pickaxe(Board board, Human human, Monster monster)
	{
		super(board, human, monster);
		name = "Pickaxe";
		numberOf = 0;
	}
	
	public void effect(Point local)
	{
		
		board.modifyBoard(local, 'R');
		human.gatherInfo(local, 0);
		numberOf--;
		if(numberOf == 0)
		{
			possession = false;
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
	}
	
	public int getNumberOf()
	{
		return numberOf;
	}
	
}
