package creatures;

import java.awt.Point;

import board.Board;

public class Monster extends Creature
{
	private enum States
	{
		ASLEEP, ALERT, BERSERK;
		public States next()
		{
			return values()[ordinal()+1];
		}
	}
	
	public Monster(Board board)
	{
		super(board);
		super.posicao = dungeon.getMonster();
	}	
	
	private States states;
	private int speed = 1;
	private Human human;
	
	public void getHumanObject(Human human)
	{
		this.human = human;
	}
	
	public void die()
	{
		alive = false;
		System.out.println("MONSTRO ESTA MORTO");
		dungeon.modifyBoard(posicao, "M", "");
		dungeon.eraseSense(posicao, 'f');
	}
	
	private void changeState()
	{
		states.next();
	}
	
	public Point getPosicao()
	{
		return posicao;
	}
}
