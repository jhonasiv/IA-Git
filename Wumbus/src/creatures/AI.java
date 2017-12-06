
package creatures;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import creatures.Creature.Knowledge;
import creatures.Human.Actions;
import creatures.Human.Possibility;
import items.Inventory;
import items.Inventory.InventoryInfo;
import board.*;


//TODO: preciso implementar sensaçoes de brisa, fedor etc
// 		arrumar aura de fogo e torre se mantendo depois de serem usados
public class AI
{
	
	public AI(Human human, Board board, Inventory inventory)
	{
		this.human = human;
		this.board = board;
		this.inventory = inventory;
		generateMoveBase();
		update();
		// printMoveBase();
	}
	
	private enum Entity {
		POCO, MONSTRO, OURO
	}
	
	private class MovementChoice
	{
		
		private class ProbEntity
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
		private int infoValue = 1;
		public int heuristic = 0;
		private String info = "";
		public List<ProbEntity> possibleEntities = new ArrayList<ProbEntity>();
		private boolean visited = false;
		private boolean safe = false;
		private int numVisits = 0;
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
		
		public String getInfo()
		{
			return info;
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
					if(inventoryInfo.get(5).possession)
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
					else
					{
						infoValue += -150;
					}
				}
				else if(info.charAt(i) == 'B')
				{
					infoValue += 50;
				}
				else if(info.charAt(i) == 'D')
				{
					if(visited)
					{
						infoValue += 100;
					}
				}
			}
			calculate();
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
			boolean firesFound = false;
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
						firesFound = true;
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
			
			if(firesFound)
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

			
			if(visited && info.contains("T"))
			{
				towerModifier = 0;
			}
			if(visited && info.contains("F"))
			{
				fireModifier = 0;
			}
			if(visited && info.contains("A"))
			{
				shortcutModifier = 0;
			}
			if(visited && info.contains("B"))
			{
				chestModifier = 0;
			}
			if(visited && info.contains("S"))
			{
				exitModifier = 0;
			}
			if(visited && info.contains("M"))
			{
				monsterModifier = 0;
			}
			
			
			distModifier = towerModifier + fireModifier + chestModifier + goldModifier + exitModifier + monsterModifier + shortcutModifier;
		}
		
		private void calculate()
		{
			getModifier();
			if(possibleEntities.isEmpty())
			{
				heuristic = (int) (distModifier + infoValue);
			}
			else
			{
				for (int i = 0; i < possibleEntities.size(); i++)
				{
					heuristic += (int) (possibleEntities.get(i).getProbability() * possibleEntities.get(i).value);
				}
			}
			if(visited)
			{
				heuristic = -Math.abs(heuristic) - numVisits;
			}
		}
		
		public void addPossibility(Entity ent, float probability)
		{
			ProbEntity link = new ProbEntity();
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
			numVisits++;
			if(human.alive)
			{
				safe = true;
			}
		}
		
		public String printHeuristic()
		{
			if(local.equals(human.getPosicao()))
			{
				return "H";
			}
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
					return numberFormat.format(exitModifier);
				case 6:
					return numberFormat.format(shortcutModifier);
				case 7:
					return numberFormat.format(monsterModifier);
			}
			return null;
		}
	}
	
	private class ShootChoice
	{
		
	}
	
	private Human human;
	private Board board;
	private Inventory inventory;
	private List<Possibility> possibilities = new ArrayList<Possibility>();
	private List<Knowledge> base = new ArrayList<Knowledge>();
	private List<ArrayList<MovementChoice>> moveBase = new ArrayList<ArrayList<MovementChoice>>();
	private List<InventoryInfo> inventoryInfo = new ArrayList<InventoryInfo>();
	
	private void generateMoveBase()
	{
		ArrayList<MovementChoice> tempBase = new ArrayList<MovementChoice>();
		List<ArrayList<String>> unmovables = board.getUnmovablesBoard();
		for (int i = 0; i < unmovables.size(); i++)
		{
			tempBase = new ArrayList<MovementChoice>();
			for (int j = 0; j < unmovables.get(i).size(); j++)
			{
				MovementChoice movementChoice = new MovementChoice();
				movementChoice.local = new Point(i, j);
				tempBase.add(movementChoice);
			}
			moveBase.add(tempBase);
		}
		
	}
	
	public void update()
	{
		base = new ArrayList<Creature.Knowledge>();
		possibilities = new ArrayList<Human.Possibility>();
		base = human.getBase();
		possibilities = human.getPossibilities();
		inventoryInfo = inventory.check();
		readBase();
	}
	
	public Possibility chooseAction()
	{
		int choiceIndex = 0;
		List<Integer> heuristics = new ArrayList<Integer>();
		List<Integer> equals = new ArrayList<Integer>();
		for (int i = 0; i < possibilities.size(); i++)
		{
			switch (possibilities.get(i).action)
			{
				case MOVER:
					if(possibilities.get(i).possible)
					{
						heuristics.add(moveBase.get(possibilities.get(i).local.x).get(possibilities.get(i).local.y).heuristic);
					}
					else
					{
						heuristics.add(-1000);
					}
					break;
				case ATIRAR:
					if(possibilities.get(i).possible)
					{
						heuristics.add(-1000);
					}
					else
					{
						heuristics.add(-1000);
					}
					break;
				case QUEBRAR:
					if(possibilities.get(i).possible)
					{
						heuristics.add(-1000);
					}
					else
					{
						heuristics.add(-1000);
					}
			}
		}
		
		int biggestValue = -9999;
		for (int i = 0; i < heuristics.size(); i++)
		{
			if(biggestValue < heuristics.get(i))
			{
				choiceIndex = i;
				biggestValue = heuristics.get(i);
			}
		}
		for (int i = 0; i < heuristics.size(); i++)
		{
			if(biggestValue == heuristics.get(i))
			{
				equals.add(i);
			}
		}
		Random ran = new Random();
		if(equals.size() > 1)
		{
			choiceIndex = equals.get(ran.nextInt(equals.size() - 1));
		}
		System.out.println(choiceIndex);
		
		return possibilities.get(choiceIndex);
	}
	
	private void readBase()
	{
		moveBase.get(human.getPosicao().x).get(human.getPosicao().y).visit();
		for (int i = 0; i < base.size(); i++)
		{
			moveBase.get(base.get(i).local.x).get(base.get(i).local.y).setInfo(base.get(i).info);
		}
		for (int i = 0; i < moveBase.size(); i++)
		{
			for (int j = 0; j < moveBase.get(i).size(); j++)
			{
				if(moveBase.get(i).get(j).getInfo().equals(""))
				{
					moveBase.get(i).get(j).setInfo("");
				}
			}
		}
	}
	
	public void printMoveBase()
	{
		System.out.println("\n\nHEURISTIC ");
		for (int i = 0; i < moveBase.size(); i++)
		{
			System.out.print(i + "\t [");
			for (int j = 0; j < moveBase.get(i).size(); j++)
			{
				System.out.print(moveBase.get(i).get(j).printHeuristic());
				if(j != moveBase.get(i).size() - 1)
				{
					System.out.print("  ");
				}
			}
			System.out.println("]");
		}
		
	}
	
	public void printDistanceModifier()
	{
		System.out.println("\n\nDISTANCE MODIFIERS ");
		for (int i = 0; i < moveBase.size(); i++)
		{
			System.out.print(i + "\t [");
			for (int j = 0; j < moveBase.get(i).size(); j++)
			{
				System.out.print(moveBase.get(i).get(j).printModifier());
				if(j != moveBase.get(i).size() - 1)
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
		for (int n = 1; n <= 7; n++)
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
					System.out.println("SHORTCUTS");
					break;
				case 7:
					System.out.println("MONSTER");
					break;
			}
			for (int i = 0; i < moveBase.size(); i++)
			{
				System.out.print(i + "\t [");
				for (int j = 0; j < moveBase.get(i).size(); j++)
				{
					System.out.print(moveBase.get(i).get(j).printSpecificModifier(n));
					if(j != moveBase.get(i).size() - 1)
					{
						System.out.print("    ");
					}
				}
				System.out.println("]");
			}
		}
		
	}
}
