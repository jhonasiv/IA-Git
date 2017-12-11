
package creatures;

import java.awt.Container;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import creatures.Creature.Direction;
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
		generateMoveBase();
		update();
		// printMoveBase();
	}
	
	private enum Entity {
		POCO, MONSTRO, OURO
	}
	
	private class MovementChoice
	{
		
		public void initialize(Point local)
		{
			this.local = local;
			addProbability(Entity.POCO, 0);
			addProbability(Entity.MONSTRO, 0);
			addProbability(Entity.OURO, 0);
		}
		
		private class ProbEntity
		{
			
			public Entity entity;
			private double probability = 0;
			public int value;
			
			public void setProbability(double val)
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
			
			public double getProbability()
			{
				return probability;
			}
			
			public String toString()
			{
				String string = new String();
				if(entity == Entity.POCO)
				{
					string += probability;
				}
				else if(entity == Entity.MONSTRO)
				{
					string += probability;
				}
				else if(entity == Entity.OURO)
				{
					string += probability;
				}
				return string;
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
		private boolean unsafeLock = false;
		private int numVisits = -1;
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		
		private double towerModifier = 0;
		private double fireModifier = 0;
		private double chestModifier = 0;
		private double goldModifier = 0;
		private double exitModifier = 0;
		private double monsterModifier = 0;
		private double shortcutModifier = 0;
		
		private double towerDist = Double.POSITIVE_INFINITY;
		private double fireDist = Double.POSITIVE_INFINITY;
		private double chestDist = Double.POSITIVE_INFINITY;
		private double goldDist = Double.POSITIVE_INFINITY;
		private double exitDist = Double.POSITIVE_INFINITY;
		private double monsterDist = Double.POSITIVE_INFINITY;
		private double shortcutDist = Double.POSITIVE_INFINITY;
		
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
			infoValue = 1;
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
					if(visited)
					{
						infoValue += 1;
					}
					else
					{
						infoValue += 75;
					}
				}
				else if(info.charAt(i) == 'F')
				{
					if(visited)
					{
						infoValue += 1;
					}
					else
					{
						infoValue += 35;
					}
				}
				else if(info.charAt(i) == 'A')
				{
					if(visited)
					{
						infoValue += 10;
					}
					else
					{
						infoValue += 50;
					}
				}
				else if(info.charAt(i) == 'T')
				{
					possibleEntities.get(0).setProbability(0);
					possibleEntities.get(1).setProbability(0);
					safe = true;
					if(visited)
					{
						infoValue += 25;
					}
					else
					{
						infoValue += 75;
					}
				}
				else if(info.charAt(i) == 'R')
				{
					possibleEntities.get(0).setProbability(0);
					possibleEntities.get(1).setProbability(0);
					possibleEntities.get(2).setProbability(0);
					safe = true;
					infoValue += -200;
				}
				else if(info.charAt(i) == 'B')
				{
					if(visited)
					{
						infoValue += 1;
					}
					else
					{
						infoValue += 50;
					}
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
			
			towerModifier = 0;
			fireModifier = 0;
			chestModifier = 0;
			goldModifier = 0;
			exitModifier = 0;
			monsterModifier = 0;
			shortcutModifier = 0;
			
			towerDist = Double.POSITIVE_INFINITY;
			fireDist = Double.POSITIVE_INFINITY;
			chestDist = Double.POSITIVE_INFINITY;
			goldDist = Double.POSITIVE_INFINITY;
			exitDist = Double.POSITIVE_INFINITY;
			monsterDist = Double.POSITIVE_INFINITY;
			shortcutDist = Double.POSITIVE_INFINITY;
			
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
					if(base.get(i).info.charAt(j) == 'T' && !moveBase.get(base.get(i).local.x).get(base.get(i).local.y).visited)
					{
						towerFound = true;
						knownTowers.add(base.get(i).local);
					}
					if(base.get(i).info.charAt(j) == 'F' && !moveBase.get(base.get(i).local.x).get(base.get(i).local.y).visited)
					{
						firesFound = true;
						knownFires.add(base.get(i).local);
					}
					if(base.get(i).info.charAt(j) == 'B' && !moveBase.get(base.get(i).local.x).get(base.get(i).local.y).visited)
					{
						chestFound = true;
						knownChests.add(base.get(i).local);
					}
					if(base.get(i).info.charAt(j) == 'A' && !moveBase.get(base.get(i).local.x).get(base.get(i).local.y).visited)
					{
						shortcutFound = true;
						knownShortcuts.add(base.get(i).local);
					}
					if(base.get(i).info.charAt(j) == 'O' && !moveBase.get(base.get(i).local.x).get(base.get(i).local.y).visited)
					{
						goldFound = true;
						knownGold = base.get(i).local;
					}
					if(base.get(i).info.charAt(j) == 'S' && !moveBase.get(base.get(i).local.x).get(base.get(i).local.y).visited)
					{
						exitFound = true;
						knownExit = base.get(i).local;
					}
					if(base.get(i).info.charAt(j) == 'M' && !moveBase.get(base.get(i).local.x).get(base.get(i).local.y).visited)
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
				towerModifier = (double) (towerWeight / (towerDist + 1));
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
				fireModifier = (double) (fireWeight / (fireDist + 1));
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
				chestModifier = (double) (chestWeight / (chestDist + 1));
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
				shortcutModifier = (double) (shortcutWeight / (shortcutDist + 1));
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
					exitModifier = (double) (exitWeight / (exitDist + .2));
				}
			}
			
			if(monsterFound)
			{
				monsterDist = Math.abs(local.x - knownMonster.x) + Math.abs(local.y - knownMonster.y);
				monsterModifier = (double) (monsterWeight / (monsterDist + 1));
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
			
			heuristic = (int) (distModifier + infoValue);
			if(!visited)
			{
				for (int i = 0; i < possibleEntities.size(); i++)
				{
					heuristic += (int) (possibleEntities.get(i).getProbability() * possibleEntities.get(i).value);
				}
			}
			if(visited)
			{
				heuristic = -Math.abs(heuristic) - (int) (1.5 * numVisits);
			}
		}
		
		private void addProbability(Entity ent, double probability)
		{
			ProbEntity link = new ProbEntity();
			link.setEntity(ent);
			link.setProbability(probability);
			possibleEntities.add(link);
		}
		
		public void presetProbability(Entity ent, double probability)
		{
			if(!safe)
			{
				for (int i = 0; i < possibleEntities.size(); i++)
				{
					if(possibleEntities.get(i).entity == ent)
					{
						possibleEntities.get(i).setProbability(probability);
					}
				}
			}
			
		}
		
		public void changeProbability(Entity ent, double probability)
		{
			if(!safe && !unsafeLock)
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
			if(probability == 1)
			{
				unsafeLock = true;
				checkProbability();
			}
		}
		
		public void checkProbability()
		{
			for (int i = 0; i < possibleEntities.size(); i++)
			{
				if(unsafeLock && possibleEntities.get(i).probability == 1)
				{
					baseAddition(possibleEntities.get(i).entity);
				}
			}
		}
		
		private void baseAddition(Entity ent)
		{
			if(ent == Entity.POCO)
			{
				human.addToBase(local);
			}
			else if(ent == Entity.MONSTRO)
			{
				human.addToBase(local);
			}
			else if(ent == Entity.OURO)
			{
				human.addToBase(local);
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
		
		public String printPossibilities(int ent)
		{
			String string = new String();
			switch (ent)
			{
				case 0:
					string += possibleEntities.get(0).toString();
					return string;
				case 1:
					string += possibleEntities.get(1).toString();
					return string;
				case 2:
					string += possibleEntities.get(2).toString();
					return string;
			}
			return null;
			
		}
	}
	
	private class ShootChoice
	{
		
		private Point local = new Point();
		private Point monsterLocal = new Point();
		private List<Point> shootingPoints = new ArrayList<Point>();
		boolean hasMonster = false;
		private int distToShootingPoint = 9999;
		private List<Integer> directionHeuristic = new ArrayList<Integer>();
		public String item;
		
		public void initialize(Point local)
		{
			this.local = local;
			// BAIXO
			directionHeuristic.add(-100);
			// CIMA
			directionHeuristic.add(-100);
			// ESQUERDA
			directionHeuristic.add(-100);
			// DIREITA
			directionHeuristic.add(-100);
		}
		
		public void update()
		{
			heuristicCalc();
		}
		
		private void distanceCalc()
		{
			for (int i = 0; i < base.size(); i++)
			{
				if(base.get(i).info.contains("M"))
				{
					hasMonster = true;
					monsterLocal = base.get(i).local;
					break;
				}
			}
			if(hasMonster)
			{
				for (int i = 0; i < shootBase.size(); i++)
				{
					for (int j = 0; j < shootBase.get(i).size(); j++)
					{
						if(Math.abs(shootBase.get(i).get(j).local.x - monsterLocal.x) <= 3 && !shootBase.get(i).get(j).local.equals(monsterLocal)
								&& shootBase.get(i).get(j).local.y == monsterLocal.y)
						{
							shootingPoints.add(new Point(i, j));
						}
						else if(Math.abs(shootBase.get(i).get(j).local.y - monsterLocal.y) <= 3 && !shootBase.get(i).get(j).local.equals(monsterLocal)
								&& shootBase.get(i).get(j).local.x == monsterLocal.x)
						{
							shootingPoints.add(new Point(i, j));
						}
					}
				}
				
				filterShootingPoints();
				for (int i = 0; i < shootingPoints.size(); i++)
				{
					if(Math.abs(local.x - shootingPoints.get(i).x) + Math.abs(local.y - shootingPoints.get(i).y) < distToShootingPoint)
					{
						distToShootingPoint = Math.abs(local.x - shootingPoints.get(i).x) + Math.abs(local.y - shootingPoints.get(i).y);
					}
				}
			}
		}
		
		private void directionCalc()
		{
			for (int i = 0; i < shootingPoints.size(); i++)
			{
				int distX = shootingPoints.get(i).x - local.x;
				int distY = shootingPoints.get(i).y - local.y;
				if(distX < 0)
				{
					directionHeuristic.set(1, 400);
				}
				else if(distX > 0)
				{
					directionHeuristic.set(0, 400);
				}
				if(distY < 0)
				{
					directionHeuristic.set(2, 400);
				}
				else if(distY > 0)
				{
					directionHeuristic.set(3, 400);
				}
			}
		}
		
		private void scoutingCalc()
		{
			List<Point> scoutingPoints = new ArrayList<Point>();
			Point auxiliar = new Point();
			boolean hasRock = false;
			int[] blocksInDirection = new int[4];
			for (int alcance = 1; alcance <= 3; alcance++)
			{
				auxiliar = new Point(local.x - alcance, local.y);
				if(board.validPoint(auxiliar))
				{
					for (int i = 0; i < base.size(); i++)
					{
						if(base.get(i).local == auxiliar)
						{
							for (int x = 0; (auxiliar.x + x) < local.x; x++)
							{
								if((auxiliar.x + x) == base.get(i).local.x && base.get(i).info.contains("R"))
								{
									hasRock = true;
									break;
								}
							}
						}
					}
					if(!hasRock)
					{
						scoutingPoints.add(auxiliar);
					}
				}
				auxiliar = new Point(local.x + alcance, local.y);
				hasRock = false;
				if(board.validPoint(auxiliar))
				{
					for (int i = 0; i < base.size(); i++)
					{
						if(base.get(i).local == auxiliar)
						{
							for (int x = 0; (auxiliar.x - x) > local.x; x--)
							{
								if((auxiliar.x - x) == base.get(i).local.x && base.get(i).info.contains("R"))
								{
									hasRock = true;
									break;
								}
							}
						}
					}
					if(!hasRock)
					{
						scoutingPoints.add(auxiliar);
					}
				}
				auxiliar = new Point(local.x, local.y - alcance);
				if(board.validPoint(auxiliar))
				{
					for (int i = 0; i < base.size(); i++)
					{
						if(base.get(i).local == auxiliar)
						{
							for (int y = 0; (auxiliar.y + y) < local.y; y++)
							{
								if((auxiliar.y + y) == base.get(i).local.y && base.get(i).info.contains("R"))
								{
									hasRock = true;
									break;
								}
							}
						}
					}
					if(!hasRock)
					{
						scoutingPoints.add(auxiliar);
					}
				}
				auxiliar = new Point(local.x, local.y + alcance);
				if(board.validPoint(auxiliar))
				{
					for (int i = 0; i < base.size(); i++)
					{
						if(base.get(i).local == auxiliar)
						{
							for (int y = 0; (auxiliar.y - y) > local.y; y--)
							{
								if((auxiliar.y - y) == base.get(i).local.y && base.get(i).info.contains("R"))
								{
									hasRock = true;
									break;
								}
							}
						}
					}
					if(!hasRock)
					{
						scoutingPoints.add(auxiliar);
					}
				}
			}
			for (int i = 0; i < scoutingPoints.size(); i++)
			{
				if(scoutingPoints.get(i).x - local.x < 0)
				{
					blocksInDirection[1]++;
				}
				else if(scoutingPoints.get(i).x - local.x > 0)
				{
					blocksInDirection[0]++;
				}
				else if(scoutingPoints.get(i).y - local.y < 0)
				{
					blocksInDirection[2]++;
				}
				else if(scoutingPoints.get(i).y - local.y > 0)
				{
					blocksInDirection[3]++;
				}
			}
			for (int i = 0; i < directionHeuristic.size(); i++)
			{
				directionHeuristic.set(i, 20 * blocksInDirection[i] - 25);
			}
		}
		
		private void heuristicCalc()
		{
			distanceCalc();
			if(hasMonster && (inventory.check().get(0).possession || inventory.check().get(2).possession))
			{
				// System.out.println(shootingPoints);
				if(distToShootingPoint == 0)
				{
					directionCalc();
					moveBase.get(local.x).get(local.y).heuristic += 50;
					if(inventory.check().get(2).possession)
					{
						item = "Fire_Arrow";
					}
					else
					{
						item = "Arrow";
					}
				}
				else
				{
					moveBase.get(local.x).get(local.y).heuristic += 100 / (distToShootingPoint + 1);
				}
			}
			else if(!hasMonster && inventory.check().get(2).possession && (inventory.check().get(0).number + inventory.check().get(2).number) > 2)
			{
				scoutingCalc();
				item = "Fire_Arrow";
			}
			else
			{
				directionHeuristic.set(0, -100);
				directionHeuristic.set(1, -100);
				directionHeuristic.set(2, -100);
				directionHeuristic.set(3, -100);
			}
		}
		
		public int getDirectionHeuristic(Direction dir)
		{
			switch (dir)
			{
				case BAIXO:
					return directionHeuristic.get(0);
				case CIMA:
					return directionHeuristic.get(1);
				case ESQUERDA:
					return directionHeuristic.get(2);
				case DIREITA:
					return directionHeuristic.get(3);
			}
			return 0;
		}
		
		private void filterShootingPoints()
		{
			for (int n = 0; n < base.size(); n++)
			{
				for (int i = 0; i < shootingPoints.size(); i++)
				{
					if(i < 0)
					{
						i = 0;
					}
					if(base.get(n).local.equals(shootingPoints.get(i)) && base.get(n).info.contains("R"))
					{
						int distToTargetX = 0;
						int distToTargetY = 0;
						distToTargetX = shootingPoints.get(i).x - monsterLocal.x;
						distToTargetY = shootingPoints.get(i).y - monsterLocal.y;
						if(distToTargetX < 0)
						{
							for (int j = 0; j < shootingPoints.size(); j++)
							{
								if(shootingPoints.get(j).x - monsterLocal.x <= distToTargetX)
								{
									shootingPoints.remove(j);
									i--;
									j--;
								}
							}
						}
						else if(distToTargetX > 0)
						{
							for (int j = 0; j < shootingPoints.size(); j++)
							{
								if(shootingPoints.get(j).x - monsterLocal.x >= distToTargetX)
								{
									shootingPoints.remove(j);
									i--;
									j--;
								}
							}
						}
						else if(distToTargetY < 0)
						{
							for (int j = 0; j < shootingPoints.size(); j++)
							{
								if(shootingPoints.get(j).y - monsterLocal.y <= distToTargetY)
								{
									shootingPoints.remove(j);
									i--;
									j--;
								}
							}
						}
						else if(distToTargetY > 0)
						{
							for (int j = 0; j < shootingPoints.size(); j++)
							{
								if(shootingPoints.get(j).y - monsterLocal.y >= distToTargetY)
								{
									shootingPoints.remove(j);
									i--;
									j--;
								}
							}
						}
					}
				}
			}
		}
		
		public String printHeuristic()
		{
			if(local.equals(human.getPosicao()))
			{
				return "H";
			}
			return Integer.toString(Collections.max(directionHeuristic));
			// return Integer.toString(directionHeuristic.get(0));
		}
	}
	
	private class MineChoice
	{
		
		private void initialize(Point local)
		{
			info = "";
			this.local = local;
		}
		
		private String info = new String();
		private Point local = new Point();
		private int heuristic = -100;
		
		public void update(String information)
		{
			this.info = information;
			calculate();
		}
		
		private void calculate()
		{
			if(info.contains("R"))
			{
				if(inventory.check().get(5).possession && inventory.check().get(5).number == 1)
				{
					heuristic = -8;
				}
				else if(inventory.check().get(5).number > 1)
				{
					heuristic = 10;
				}
				else
				{
					heuristic = -100;
				}
			}
		}
		
		public int getHeuristic()
		{
			return heuristic;
		}
	}
	
	private Human human;
	private Board board;
	private Inventory inventory;
	private List<Possibility> possibilities = new ArrayList<Possibility>();
	private List<Knowledge> base = new ArrayList<Knowledge>();
	private List<ArrayList<MovementChoice>> moveBase = new ArrayList<ArrayList<MovementChoice>>();
	private List<ArrayList<ShootChoice>> shootBase = new ArrayList<ArrayList<ShootChoice>>();
	private List<ArrayList<MineChoice>> mineBase = new ArrayList<ArrayList<MineChoice>>();
	private List<InventoryInfo> inventoryInfo = new ArrayList<InventoryInfo>();
	
	private void generateMoveBase()
	{
		ArrayList<MovementChoice> tempMoveBase = new ArrayList<MovementChoice>();
		ArrayList<ShootChoice> tempShootBase = new ArrayList<ShootChoice>();
		ArrayList<MineChoice> tempMineBase = new ArrayList<MineChoice>();
		List<ArrayList<String>> unmovables = board.getUnmovablesBoard();
		for (int i = 0; i < unmovables.size(); i++)
		{
			tempMoveBase = new ArrayList<MovementChoice>();
			tempShootBase = new ArrayList<ShootChoice>();
			tempMineBase = new ArrayList<MineChoice>();
			for (int j = 0; j < unmovables.get(i).size(); j++)
			{
				MovementChoice movementChoice = new MovementChoice();
				ShootChoice shootChoice = new ShootChoice();
				MineChoice mineChoice = new MineChoice();
				shootChoice.local = new Point(i, j);
				movementChoice.local = new Point(i, j);
				tempMoveBase.add(movementChoice);
				tempShootBase.add(shootChoice);
				tempMineBase.add(mineChoice);
			}
			moveBase.add(tempMoveBase);
			shootBase.add(tempShootBase);
			mineBase.add(tempMineBase);
		}
		for (int i = 0; i < moveBase.size(); i++)
		{
			for (int j = 0; j < moveBase.get(i).size(); j++)
			{
				moveBase.get(i).get(j).initialize(new Point(i, j));
				shootBase.get(i).get(j).initialize(new Point(i, j));
				mineBase.get(i).get(j).initialize(new Point(i, j));
				if(moveBase.get(i).get(j).getInfo().equals(""))
				{
					moveBase.get(i).get(j).setInfo("");
				}
			}
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
						// System.out.println(possibilities.get(i).local);
						heuristics.add(shootBase.get(possibilities.get(i).local.x).get(possibilities.get(i).local.y)
								.getDirectionHeuristic(possibilities.get(i).direction));
						possibilities.get(i).item = shootBase.get(possibilities.get(i).local.x).get(possibilities.get(i).local.y).item;
					}
					else
					{
						heuristics.add(-1000);
					}
					break;
				case QUEBRAR:
					if(possibilities.get(i).possible)
					{
						heuristics.add(mineBase.get(possibilities.get(i).local.x).get(possibilities.get(i).local.y).getHeuristic());
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
			choiceIndex = equals.get(ran.nextInt(equals.size()));
		}
		// System.out.println(choiceIndex);
		
		return possibilities.get(choiceIndex);
	}
	
	private void readBase()
	{
		moveBase.get(human.getPosicao().x).get(human.getPosicao().y).visit();
		for (int i = 0; i < base.size(); i++)
		{
			mineBase.get(base.get(i).local.x).get(base.get(i).local.y).update(base.get(i).info);
			moveBase.get(base.get(i).local.x).get(base.get(i).local.y).setInfo(base.get(i).info);
		}
		for (int i = 0; i < shootBase.size(); i++)
		{
			for (int j = 0; j < shootBase.get(i).size(); j++)
			{
				shootBase.get(i).get(j).update();
			}
		}
		for (int i = 0; i < base.size(); i++)
		{
			Point local = new Point();
			boolean pocoPossibility = false;
			boolean ouroPossibility = false;
			boolean monstroPossibility = false;
			
			double probability = 0;
			int[] possiblePlaces = { 0, 0, 0 };
			
				presetAllProbabilities(i, 1);
				
				local = new Point(base.get(i).local);
				local.setLocation(local.x - 1, local.y);
				for (int p = 0; p < possiblePlaces.length; p++)
				{
					possiblePlaces[p] = 0;
					if((board.validPoint(local) && moveBase.get(local.x).get(local.y).possibleEntities.get(p).probability == 0) || !board.validPoint(local)
							|| base.get(i).info.equals(""))
					{
						possiblePlaces[p]++;
					}
					
					local = new Point(base.get(i).local);
					local.setLocation(local.x + 1, local.y);
					if((board.validPoint(local) && moveBase.get(local.x).get(local.y).possibleEntities.get(p).probability == 0) || !board.validPoint(local)
							|| base.get(i).info.equals(""))
					{
						possiblePlaces[p]++;
					}
					
					local = new Point(base.get(i).local);
					local.setLocation(local.x, local.y - 1);
					if((board.validPoint(local) && moveBase.get(local.x).get(local.y).possibleEntities.get(p).probability == 0) || !board.validPoint(local)
							|| base.get(i).info.equals(""))
					{
						possiblePlaces[p]++;
					}
					
					local = new Point(base.get(i).local);
					local.setLocation(local.x, local.y + 1);
					if((board.validPoint(local) && moveBase.get(local.x).get(local.y).possibleEntities.get(p).probability == 0) || !board.validPoint(local)
							|| base.get(i).info.equals(""))
					{
						possiblePlaces[p]++;
					}
				}
				if(base.get(i).info.contains("b"))
				{
					pocoPossibility = true;
					if(possiblePlaces[0] != 4)
					{
						probability = (1 / (4 - (double) possiblePlaces[0]));
					}
					else
					{
						probability = 0;
					}
					setSpecificProbabilities(Entity.POCO, i, probability);
				}
				else if(base.get(i).info.contains("f"))
				{
					monstroPossibility = true;
					if(possiblePlaces[1] != 4)
					{
						probability = (1 / (4 - (double) possiblePlaces[1]));
					}
					else
					{
						probability = 0;
					}
					
					setSpecificProbabilities(Entity.MONSTRO, i, probability);
				}
				else if(base.get(i).info.contains("l"))
				{
					ouroPossibility = true;
					if(possiblePlaces[2] != 4)
					{
						probability = (1 / (4 - (double) possiblePlaces[2]));
					}
					else
					{
						probability = 0;
					}
					
					setSpecificProbabilities(Entity.OURO, i, probability);
				}
			
			if(!pocoPossibility)
			{
				setSpecificProbabilities(Entity.POCO, i, 0);
			}
			if(!monstroPossibility)
			{
				setSpecificProbabilities(Entity.MONSTRO, i, 0);
			}
			if(!ouroPossibility)
			{
				setSpecificProbabilities(Entity.OURO, i, 0);
			}
		}
		
		for (int i = 0; i < moveBase.size(); i++)
		{
			for (int j = 0; j < moveBase.get(i).size(); j++)
			{
				// moveBase.get(i).get(j).checkProbability();
				if(moveBase.get(i).get(j).getInfo().equals(""))
				{
					moveBase.get(i).get(j).setInfo("");
				}
			}
		}
	}
	
	private void presetAllProbabilities(int element, double val)
	{
		Point local = new Point();
		for (int n = 0; n < base.get(element).info.length(); n++)
		{
			if(base.get(element).info.charAt(n) == 'b')
			{
				local = new Point(base.get(element).local);
				local.setLocation(local.x - 1, local.y);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.POCO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x + 1, local.y);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.POCO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y - 1);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.POCO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y + 1);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.POCO, val);
				}
			}
			else if(base.get(element).info.charAt(n) == 'f')
			{
				local = new Point(base.get(element).local);
				local.setLocation(local.x - 1, local.y);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.MONSTRO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x + 1, local.y);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.MONSTRO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y - 1);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.MONSTRO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y + 1);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.MONSTRO, val);
				}
			}
			else if(base.get(element).info.charAt(n) == 'l')
			{
				local = new Point(base.get(element).local);
				local.setLocation(local.x - 1, local.y);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.OURO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x + 1, local.y);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.OURO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y - 1);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.OURO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y + 1);
				if(board.validPoint(local))
				{
					moveBase.get(local.x).get(local.y).presetProbability(Entity.OURO, val);
				}
			}
		}
		
	}
	
	private void setSpecificProbabilities(Entity ent, int element, double val)
	{
		Point local = new Point();
		for (int n = 0; n < base.get(element).info.length(); n++)
		{
			if(ent == Entity.POCO)
			{
				local = new Point(base.get(element).local);
				local.setLocation(local.x - 1, local.y);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.POCO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x + 1, local.y);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.POCO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y - 1);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.POCO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y + 1);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.POCO, val);
				}
			}
			else if(ent == Entity.MONSTRO)
			{
				local = new Point(base.get(element).local);
				local.setLocation(local.x - 1, local.y);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.MONSTRO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x + 1, local.y);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.MONSTRO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y - 1);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.MONSTRO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y + 1);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.MONSTRO, val);
				}
			}
			else if(ent == Entity.OURO)
			{
				local = new Point(base.get(element).local);
				local.setLocation(local.x - 1, local.y);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.OURO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x + 1, local.y);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.OURO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y - 1);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.OURO, val);
				}
				
				local = new Point(base.get(element).local);
				local.setLocation(local.x, local.y + 1);
				if(board.validPoint(local))
				{
					
					moveBase.get(local.x).get(local.y).changeProbability(Entity.OURO, val);
				}
			}
		}
		
	}
	
	public int getMoveHeuristic(Point local)
	{
		return moveBase.get(local.x).get(local.y).heuristic;
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
			System.out.print("]");
			System.out.print("\t\t [");
			for (int j = 0; j < shootBase.get(i).size(); j++)
			{
				System.out.print(shootBase.get(i).get(j).printHeuristic());
				if(j != shootBase.get(i).size() - 1)
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
	
	public void printPossibilites()
	{
		System.out.println("\n\n");
		for (int i = 0; i < moveBase.size(); i++)
		{
			System.out.print(i);
			System.out.print("\t [");
			for (int j = 0; j < moveBase.get(i).size(); j++)
			{
				for (int n = 0; n < 3; n++)
				{
					
					System.out.print(moveBase.get(i).get(j).printPossibilities(n) + ", ");
				}
			}
			System.out.println(" ]");
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
