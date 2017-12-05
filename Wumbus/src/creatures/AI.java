
package creatures;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import creatures.Creature.Knowledge;
import creatures.Human.Possibility;
import items.Inventory;
import items.Inventory.InventoryInfo;
import board.*;

public class AI
{
	
	public AI(Human human, Board board, Inventory inventory)
	{
		this.human = human;
		this.board = board;
		this.inventory = inventory;
		possibilities = human.getPossibilities();
		base = human.getBase();
		inventoryInfo = inventory.check();
		generateRef();
		readBase();
		printRef();
	}
	
	private enum Entity {
		POCO, MONSTRO, OURO
	}
	
	private class Trail
	{
		
		private class LinkEntity
		{
			
			public Entity entity;
			private float probability = 0;
			public int value;
			
			public void setProbability(float val)
			{
				probability = val;
			}
			
			public void setEntity(Entity ent)
			{
				entity = ent;
				if(entity == Entity.POCO)
				{
					value = -100;
				}
				else if(entity == Entity.MONSTRO)
				{
					value = -100;
				}
				else
				{
					value = 75;
				}
			}
			
			public float getProbability()
			{
				return probability;
			}
		}
		
		public Point local = new Point();
		private double distModifier = 0;
		private int baseValue = 1;
		private int infoValue = 1;
		public int heuristic = 0;
		private String info = "";
		public List<LinkEntity> possibleEntities = new ArrayList<LinkEntity>();
		private boolean visited = false;
		private boolean safe = false;
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		
		double towerModifier = 0;
		double fireModifier = 0;
		double chestModifier = 0;
		double goldModifier = 0;
		double exitModifier = 0;
		double monsterModifier = 0;
		double shortcutModifier = 0;
		
		double towerDist = Double.POSITIVE_INFINITY;
		double fireDist = Double.POSITIVE_INFINITY;
		double chestDist = Double.POSITIVE_INFINITY;
		double goldDist = Double.POSITIVE_INFINITY;
		double exitDist = Double.POSITIVE_INFINITY;
		double monsterDist = Double.POSITIVE_INFINITY;
		double shortcutDist = Double.POSITIVE_INFINITY;
		
		public void setInfo(String info)
		{
			this.info = info;
			interpret();
		}
		
		private void getModifier()
		{
			
			int towerWeight = 45;
			int fireWeight = 20;
			int chestWeight = 40;
			int goldWeight = 75;
			int exitWeight = 100;
			int monsterWeight = -50;
			int shortcutWeight = 35;
			
			boolean towerFound = false;
			boolean fireFound = false;
			boolean chestFound = false;
			boolean goldFound = false;
			boolean exitFound = false;
			boolean monsterFound = false;
			boolean shortcutFound = false;
			
			List<Point> knownTowers = new ArrayList<Point>();
			List<Point> knownFires = new ArrayList<Point>();
			List<Point> knownChests = new ArrayList<Point>();
			List<Point> knownShortcuts = new ArrayList<Point>();
			Point knownGold = new Point();
			Point knownExit = new Point();
			Point knownMonster = new Point();
			
			for (int i = 0; i < base.size(); i++)
			{
				for (int j = 0; j < base.get(i).info.length(); j++)
				{
					if(base.get(i).info.charAt(j) == 'T')
					{
						towerFound = true;
						knownTowers.add(base.get(i).local);
					}
					if(base.get(i).info.charAt(j) == 'F')
					{
						fireFound = true;
						knownFires.add(base.get(i).local);
					}
					if(base.get(i).info.charAt(j) == 'B')
					{
						chestFound = true;
						knownChests.add(base.get(i).local);
					}
					if(base.get(i).info.charAt(j) == 'A')
					{
						shortcutFound = true;
						knownShortcuts.add(base.get(i).local);
					}
					if(base.get(i).info.charAt(j) == 'O')
					{
						goldFound = true;
						knownGold = base.get(i).local;
					}
					if(base.get(i).info.charAt(j) == 'S')
					{
						exitFound = true;
						knownExit = base.get(i).local;
					}
					if(base.get(i).info.charAt(j) == 'M')
					{
						monsterFound = true;
						knownMonster = base.get(i).local;
					}
				}
			}
			if(towerFound)
			{
				for (int i = 0; i < knownTowers.size(); i++)
				{
					if(Math.abs(local.x - knownTowers.get(i).x + local.y - knownTowers.get(i).y) < towerDist)
					{
						towerDist = Math.abs(local.x - knownTowers.get(i).x) + Math.abs(local.y - knownTowers.get(i).y);
					}
				}
				towerModifier = (double) (towerWeight / (towerDist));
			}
			
			if(fireFound)
			{
				for (int i = 0; i < knownFires.size(); i++)
				{
					if(Math.abs(local.x - knownFires.get(i).x + local.y - knownFires.get(i).y) < fireDist)
					{
						fireDist = Math.abs(local.x - knownFires.get(i).x) + Math.abs(local.y - knownFires.get(i).y);
					}
				}
				fireModifier = (double) (fireWeight / (fireDist));
			}
			if(chestFound)
			{
				for (int i = 0; i < knownChests.size(); i++)
				{
					if(Math.abs(local.x - knownChests.get(i).x + local.y - knownChests.get(i).y) < chestDist)
					{
						chestDist = Math.abs(local.x - knownChests.get(i).x) + Math.abs(local.y - knownChests.get(i).y);
					}
				}
				chestModifier = (double) (chestWeight / (chestDist));
			}
			if(shortcutFound)
			{
				for (int i = 0; i < knownShortcuts.size(); i++)
				{
					if(Math.abs(local.x - knownShortcuts.get(i).x + local.y - knownShortcuts.get(i).y) < shortcutDist)
					{
						shortcutDist = Math.abs(local.x - knownShortcuts.get(i).x) + Math.abs(local.y - knownShortcuts.get(i).y);
					}
				}
				shortcutModifier = (double) (shortcutWeight / (shortcutDist));
			}
			
			if(goldFound)
			{
				goldDist = Math.abs(local.x - knownGold.x) + Math.abs(local.y - knownGold.y);
				goldModifier = (double) (goldWeight / (goldDist));
			}
			if(exitFound)
			{
				exitDist = Math.abs(local.x - knownExit.x) + Math.abs(local.y - knownExit.y);
				if(exitDist != 0)
				{
					exitModifier = (double) (exitWeight / (exitDist + 1));
				}
			}
			
			if(monsterFound)
			{
				monsterDist = Math.abs(local.x - knownMonster.x) + Math.abs(local.y - knownMonster.y);
				monsterModifier = (double) (monsterWeight / (monsterDist));
			}
			
			if(towerDist == 0)
			{
				towerModifier = towerWeight;
			}
			if(fireDist == 0)
			{
				fireModifier = fireWeight;
			}
			if(chestDist == 0)
			{
				chestModifier = chestWeight;
			}
			if(goldDist == 0)
			{
				goldModifier = goldWeight;
			}
			if(exitDist == 0)
			{
				exitModifier = exitWeight;
			}
			if(monsterDist == 0)
			{
				monsterModifier = monsterWeight;
			}
			if(shortcutDist == 0)
			{
				shortcutModifier = shortcutWeight;
			}
			distModifier = towerModifier + fireModifier + chestModifier + goldModifier + exitModifier + monsterModifier + shortcutModifier;
		}
		
		private void interpret()
		{
			for (int i = 0; i < info.length(); i++)
			{
				if(info.charAt(i) == 'P')
				{
					infoValue += -100;
				}
				else if(info.charAt(i) == 'M')
				{
					infoValue += -100;
				}
				else if(info.charAt(i) == 'S')
				{
					infoValue += 100;
				}
				else if(info.charAt(i) == 'O')
				{
					infoValue += 75;
				}
				else if(info.charAt(i) == 'F')
				{
					infoValue += 35;
				}
				else if(info.charAt(i) == 'A')
				{
					infoValue += 50;
				}
				else if(info.charAt(i) == 'T')
				{
					infoValue += 75;
				}
				else if(info.charAt(i) == 'R')
				{
					if(inventoryInfo.get(5).number > 1)
					{
						infoValue += 5;
					}
					else
					{
						infoValue += -5;
					}
				}
				else if(info.charAt(i) == 'B')
				{
					infoValue += 50;
				}
				else if(info.charAt(i) == 'D')
				{
					infoValue += 100;
				}
			}
			calculate();
		}
		
		private void calculate()
		{
			getModifier();
			if(possibleEntities.isEmpty())
			{
				heuristic = (int) (baseValue * distModifier) + infoValue;
			}
			else
			{
				for (int i = 0; i < possibleEntities.size(); i++)
				{
					heuristic += baseValue * possibleEntities.get(i).getProbability() * possibleEntities.get(i).value;
				}
			}
		}
		
		public void addPossibility(Entity ent, float probability)
		{
			LinkEntity link = new LinkEntity();
			link.setEntity(ent);
			link.setProbability(probability);
			possibleEntities.add(link);
		}
		
		public void changeProbability(Entity ent, float probability)
		{
			if(!safe)
			{
				safe = true;
				for (int i = 0; i < possibleEntities.size(); i++)
				{
					if(possibleEntities.get(i).entity == ent)
					{
						possibleEntities.get(i).setProbability(probability);
					}
					if(possibleEntities.get(i).getProbability() != 0)
					{
						safe = false;
					}
				}
			}
		}
		
		public void visit()
		{
			visited = true;
			changeBase();
		}
		
		private void changeBase()
		{
			
			baseValue = -1;
		}
		
		public String printHeuristic()
		{
			return Integer.toString(heuristic);
		}
		
		public String printModifier()
		{
			return numberFormat.format(distModifier);
		}
		
		public String printSpecificModifier(int mod)
		{
			switch (mod)
			{
				case 1:
					return numberFormat.format(towerModifier);
				case 2:
					return numberFormat.format(fireModifier);
				case 3:
					return numberFormat.format(chestModifier);
				case 4:
					return numberFormat.format(goldModifier);
				case 5:
					return numberFormat.format(exitDist);
				case 6:
					return numberFormat.format(monsterModifier);
			}
			return null;
		}
	}
	
	private Human human;
	private Board board;
	private Inventory inventory;
	private List<Possibility> possibilities;
	private List<Knowledge> base;
	private List<ArrayList<Trail>> refBase = new ArrayList<ArrayList<Trail>>();
	private List<InventoryInfo> inventoryInfo = new ArrayList<InventoryInfo>();
	
	private void generateRef()
	{
		ArrayList<Trail> tempBase = new ArrayList<Trail>();
		List<ArrayList<String>> unmovables = board.getUnmovablesBoard();
		for (int i = 0; i < unmovables.size(); i++)
		{
			tempBase = new ArrayList<Trail>();
			for (int j = 0; j < unmovables.get(i).size(); j++)
			{
				Trail trail = new Trail();
				trail.local = new Point(i, j);
				tempBase.add(trail);
			}
			refBase.add(tempBase);
		}
		
	}
	
	public void update()
	{
		base.clear();
		possibilities.clear();
		base = human.getBase();
		possibilities = human.getPossibilities();
		inventoryInfo = inventory.check();
		readBase();
	}
	
	private void readBase()
	{
		refBase.get(human.getPosicao().x).get(human.getPosicao().y).visit();
		for (int i = 0; i < base.size(); i++)
		{
			refBase.get(base.get(i).local.x).get(base.get(i).local.y).setInfo(base.get(i).info);
		}
	}
	
	public void printRef()
	{
		System.out.println("\n\nHEURISTIC ");
		for (int i = 0; i < refBase.size(); i++)
		{
			System.out.print("[");
			for (int j = 0; j < refBase.get(i).size(); j++)
			{
				System.out.print(refBase.get(i).get(j).printHeuristic());
				if(j != refBase.get(i).size() - 1)
				{
					System.out.print(", ");
				}
			}
			System.out.println("]");
		}
		
		System.out.println("\n\nDISTANCE MODIFIERS ");
		for (int i = 0; i < refBase.size(); i++)
		{
			System.out.print("[");
			for (int j = 0; j < refBase.get(i).size(); j++)
			{
				System.out.print(refBase.get(i).get(j).printModifier());
				if(j != refBase.get(i).size() - 1)
				{
					System.out.print("    ");
				}
			}
			System.out.println("]");
		}
	}
	public void printSpecificModifier()
	{
		System.out.println("\n\n");
		for (int n = 1; n <= 5; n++)
		{
			switch (n)
			{
				case 1:
					System.out.println("TORRE");
					break;
				case 2:
					System.out.println("FIRE");
					break;
				case 3:
					System.out.println("CHEST");
					break;
				case 4:
					System.out.println("GOLD");
					break;
				case 5:
					System.out.println("EXIT");
					break;
				case 6:
					System.out.println("MONSTER");
					break;
			}
			for (int i = 0; i < refBase.size(); i++)
			{
				System.out.print("[");
				for (int j = 0; j < refBase.get(i).size(); j++)
				{
					System.out.print(refBase.get(i).get(j).printSpecificModifier(n));
					if(j != refBase.get(i).size() - 1)
					{
						System.out.print("    ");
					}
				}
				System.out.println("]");
			}
		}
		
	}
}