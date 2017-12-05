package items;

import board.*;
import creatures.*;

public abstract class Items
{
	public Items(Board board,Human human, Monster monster)
	{
		this.board = board;
		this.human = human;
		this.monster = monster;
	}
	
	protected Board board = new Board();
	protected Human human;
	protected Monster monster = new Monster(board);
	
	public String name;
	protected int numberOf;
	protected boolean possession;
	
	public void effect() {};
	public boolean getPossession()
	{
		return possession;
	};
	public abstract void acquire(int num);
	public int getNumberOf()
	{
		return numberOf;
	}
	
}
