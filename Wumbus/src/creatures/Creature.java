
package creatures;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import board.*;

public class Creature
{
	
	public Creature(Board board)
	{
		dungeon = board;
	}
	
	protected class Knowledge
	{
		
		public Point local = new Point();
		public String info = new String();
		
		public boolean compare(Knowledge instance)
		{
			if(this.local.equals(instance.local))
			{
				if(this.info.equals(instance.info))
				{
					return true;
				}
			}
			return false;
		}
		
		public boolean sameLocation(Point point)
		{
			return this.local.equals(point);
		}
	};
	
	/// No ato de atirar eu devo mandar a posicao inicial e a direcão
	public enum Direction {
		CIMA, BAIXO, ESQUERDA, DIREITA
	}
	
	protected Board dungeon = new Board();
	
	protected List<Knowledge> base = new ArrayList<Knowledge>();
	
	protected int visao = 0;
	protected Point posicao = new Point();
	protected boolean alive = true;
	protected double speed = 1;
	
	// \TODO: no futuro será private
	protected void percepcao()
	{
		Point local = new Point();
		for (int alcance = 0; alcance <= visao; alcance++)
		{
			if(alcance != 0)
			{
				if(dungeon.validPoint(new Point(posicao.x - alcance, posicao.y)))
				{
					local = new Point(posicao.x - alcance, posicao.y);
					gatherInfo(local, alcance);
				}
				if(dungeon.validPoint(new Point(posicao.x + alcance, posicao.y)))
				{
					local = new Point(posicao.x + alcance, posicao.y);
					gatherInfo(local, alcance);
				}
				if(dungeon.validPoint(new Point(posicao.x, posicao.y - alcance)))
				{
					local = new Point(posicao.x, posicao.y - alcance);
					gatherInfo(local, alcance);
					
				}
				if(dungeon.validPoint(new Point(posicao.x, posicao.y + alcance)))
				{
					local = new Point(posicao.x, posicao.y + alcance);
					gatherInfo(local, alcance);
				}
			}
			else
			{
				local = new Point(posicao);
				System.out.println("local = " + local);
				gatherInfo(local, 0);
			}
		}
		filterBase();
	}
	
	private void filterBase()
	{
		for (int i = 0; i < base.size(); i++)
		{
			for (int j = 0; j < base.size(); j++)
			{
				if(i != j)
				{
					if(base.get(i).compare(base.get(j)))
					{
						base.remove(j);
						j--;
					}
				}
			}
		}
	}
	
	protected void seeRock()
	{
		Point local = new Point();
		if(dungeon.validPoint(new Point(posicao.x - 1, posicao.y)))
		{
			local = new Point(posicao.x - 1, posicao.y);
			for (int i = 0; i < dungeon.getLocal(local).length(); i++)
			{
				if(dungeon.getLocal(local).charAt(i) == 'R')
				{
					gatherInfo(local, 0);
				}
			}
		}
		if(dungeon.validPoint(new Point(posicao.x + 1, posicao.y)))
		{
			local = new Point(posicao.x + 1, posicao.y);
			for (int i = 0; i < dungeon.getLocal(local).length(); i++)
			{
				if(dungeon.getLocal(local).charAt(i) == 'R')
				{
					gatherInfo(local, 0);
				}
			}
		}
		if(dungeon.validPoint(new Point(posicao.x, posicao.y - 1)))
		{
			local = new Point(posicao.x, posicao.y - 1);
			for (int i = 0; i < dungeon.getLocal(local).length(); i++)
			{
				if(dungeon.getLocal(local).charAt(i) == 'R')
				{
					gatherInfo(local, 0);
				}
			}
			
		}
		if(dungeon.validPoint(new Point(posicao.x, posicao.y + 1)))
		{
			local = new Point(posicao.x, posicao.y + 1);
			for (int i = 0; i < dungeon.getLocal(local).length(); i++)
			{
				if(dungeon.getLocal(local).charAt(i) == 'R')
				{
					gatherInfo(local, 0);
				}
			}
		}
	}
	
	public void gatherInfo(Point local, int quadrante)
	{
		if(dungeon.validPoint(local))
		{
			Knowledge sabedoria = new Knowledge();
			sabedoria.local = local;
			if(quadrante == 0)
			{
				sabedoria.info = dungeon.getLocal(local);
			}
			else
			{
				sabedoria.info = dungeon.getLocalObstacle(local);
			}
			base.add(sabedoria);
		}
	}
	
	public void gatherDungeon()
	{
		Knowledge sabedoria = new Knowledge();
		Point local = new Point();
		List<ArrayList<String>> unmovables = new ArrayList<ArrayList<String>>(dungeon.getUnmovablesBoard());
		// System.out.println("TESTE: ");
		for (int i = 0; i < unmovables.size(); i++)
		{
			//
			// System.out.println(unmovables.get(i));
			// }
			for (int j = 0; j < unmovables.get(i).size(); j++)
			{
				sabedoria = new Knowledge();
				local = new Point(i, j);
				sabedoria.info = unmovables.get(i).get(j);
				sabedoria.local = local;
				base.add(sabedoria);
			}
		}
		
	}
	
	public void die()
	{
		alive = false;
	}
	
	public void setVisao(int novoValor)
	{
		visao = novoValor;
	}
	
	public void printBase()
	{
		for (int i = 0; i < base.size(); i++)
		{
			System.out.println("Local: " + base.get(i).local + "\t Info: " + base.get(i).info);
		}
	}
	
	public List<Knowledge> getBase()
	{
		return base;
	}
}
