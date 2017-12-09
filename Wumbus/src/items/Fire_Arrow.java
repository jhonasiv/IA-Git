package items;

import java.awt.Point;

import board.Board;
import creatures.Creature.Direction;
import creatures.Human;
import creatures.Monster;

public class Fire_Arrow extends Arrow
{
	public Fire_Arrow(Board board, Human human, Monster monster)
	{
		super(board, human, monster);
		name = "Fire_Arrow";
		numberOf = 0;
	}
	
	private int alcance = 3;
	
	
	///TODO: achar um jeito de fazer isso sem passar monster como argumento
	public void effect(Direction dir)
	{
		if (possession)
		{
			Point inicial = new Point(human.getPosicao());
			Point trajetoria = new Point();
			String info = new String();
			OUTER: for (int trajeto = 1; trajeto <= alcance; trajeto++)
			{
				if (dir == Direction.CIMA)
				{
					trajetoria = new Point(inicial.x - trajeto, inicial.y);
					if (board.validPoint(new Point(trajetoria)))
					{
						info = board.getLocal(trajetoria);
						for (int n = 0; n < info.length(); n++)
						{
							if(info.charAt(n) == 'R')
							{
								break OUTER;
							}
						}
						human.gatherInfo(trajetoria, 0);
						if (trajetoria == monster.getPosicao())
						{
							monster.die();
						}
					}
				}
				else if (dir == Direction.BAIXO)
				{
					trajetoria = new Point(inicial.x + trajeto, inicial.y);
					if (board.validPoint(new Point(trajetoria)))
					{
						info = board.getLocal(trajetoria);
						for (int n = 0; n < info.length(); n++)
						{
							if(info.charAt(n) == 'R')
							{
								break OUTER;
							}
						}
						human.gatherInfo(trajetoria, 0);
						if (trajetoria == monster.getPosicao())
						{
							monster.die();
						}
					}
				}
				else if (dir == Direction.ESQUERDA)
				{
					trajetoria = new Point(inicial.x, inicial.y - trajeto);
					if (board.validPoint(new Point(trajetoria)))
					{
						info = board.getLocal(trajetoria);
						for (int n = 0; n < info.length(); n++)
						{
							if(info.charAt(n) == 'R')
							{
								break OUTER;
							}
						}
						human.gatherInfo(trajetoria, 0);
						if (trajetoria == monster.getPosicao())
						{
							monster.die();
						}
					}
				}
				else if (dir == Direction.DIREITA)
				{
					trajetoria = new Point(inicial.x, inicial.y + trajeto);
					if (board.validPoint(new Point(trajetoria)))
					{
						info = board.getLocal(trajetoria);
						for (int n = 0; n < info.length(); n++)
						{
							if(info.charAt(n) == 'R')
							{
								break OUTER;
							}
						}
						human.gatherInfo(trajetoria, 0);
						if (trajetoria == monster.getPosicao())
						{
							monster.die();
						}
					}
				}
			}
			numberOf--;
		}
		if (numberOf == 0)
		{
			possession = false;
		}
	}
	
	public void acquire(int num)
	{
		numberOf++;
		possession = true;
	}
	
	public int getNumberOf()
	{
		return numberOf;
	}
	
	public boolean getPossession()
	{
		return possession;
	}
	
}
