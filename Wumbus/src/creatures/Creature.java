
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
	
	public class Knowledge
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
	public boolean alive = true;
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
				gatherInfo(local, 0);
			}
		}
		filterBase();
	}
	
	public void refreshBase(Point local)
	{
		for(int i = 0; i < base.size(); i++)
		{
			if(base.get(i).sameLocation(local))
			{
				base.get(i).info = dungeon.getLocal(local);
			}
		}
	}
	
	public void addToBase(Point local)
	{
		boolean exists = true;
		int element = 0;
		for (int i = 0; i < base.size(); i++)
		{
			exists = base.get(i).sameLocation(local);
			if(exists)
			{
				element = i;
			}
		}
		if(exists)
		{
			base.get(element).info = dungeon.getLocal(local);
		}
		else
		{
			Knowledge knowledge = new Knowledge();
			knowledge.info = dungeon.getLocal(local);
			knowledge.local = local;
			base.add(knowledge);
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
		for(int i = 0; i < base.size(); i++)
		{
			StringBuilder info = new StringBuilder();
			info.append(base.get(i).info);
			for(int n = 0; n < info.length(); n++)
			{
				int repeatition = 0;
				for (int j = 0; j < info.length(); j++)
				{
					if(n != j)
					{
						if(info.charAt(n) == info.charAt(j))
						{
							repeatition++;
							if(repeatition > 0)
							{
								info.deleteCharAt(j);
								j--;
							}
						}
					}
				}
			}
			base.get(i).info = info.toString();
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
			boolean exists = false;
			int element = 0;
			for (int i = 0; i < base.size(); i++)
			{
				exists = base.get(i).sameLocation(local);
				if(exists)
				{
					element = i;
					break;
				}
			}
			if(!exists)
			{
				Knowledge sabedoria = new Knowledge();
				sabedoria.local = local;
				if(quadrante == 0)
				{
					sabedoria.info = dungeon.getLocal(local);
				}
				else
				{
					sabedoria.info = dungeon.getLocal(local);
				}
				base.add(sabedoria);
			}
			else
			{
				if(quadrante == 0)
				{
					base.get(element).info += dungeon.getLocal(local);
				}
				else
				{
					base.get(element).info += dungeon.getLocal(local);
				}
			}
		}
		filterBase();
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
		System.out.println("Morreu");
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
