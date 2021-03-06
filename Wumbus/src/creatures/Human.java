
package creatures;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import items.*;
import items.Inventory.Item;
import board.Board;

public class Human extends Creature
{
	
	public enum Actions {
		MOVER, ATIRAR, QUEBRAR
	}
	
	private enum States {
		CURIOUS, DESPERATE, LAST_BREATH;
		
		// public States next(int num)
		// {
		// if(ordinal() + num >= values().length)
		// {
		// return values()[values().length-1];
		// }
		// return values()[ordinal() + num];
		// }
	}
	
	public class Possibility
	{
		public String item;
		public Actions action;
		public Direction direction;
		public Point local;
		public boolean possible = false;
		public float heuristic = 0;
	}
	
	public Human(Board board, Monster monster)
	{
		super(board);
		super.posicao = board.getHuman();
		this.monster = monster;
		inventory = new Inventory(board, this, monster);
		state = States.CURIOUS;
		inventory.add(Item.BOW, 1);
		inventory.add(Item.ARROW, 1);
		inventory.add(Item.PICKAXE, 1);
		inventory.check();
		percepcao();
		ai = new AI(this, board, inventory);
	}
	
	private AI ai;
	private States state;
	private Inventory inventory;
	private Monster monster;
	private int numMoves = 0;
	private List<Possibility> possibleActions = new ArrayList<Possibility>();
	private boolean safety_state_change = false;
	public boolean free = false;
	public Possibility action = new Possibility();
		
	public void act()
	{
		int movesThisTurn = 0;
		int totalMoves = (int)speed + (int)(numMoves/4);
		while ((movesThisTurn < totalMoves) && alive && !free)
		{
			interactWithBoard();
			checkPossibilities();
			percepcao();
			ai.update();
			inventory.check();
			action = ai.chooseAction();
			inventory.guaranteeUse();
			switch (action.action)
			{
				case MOVER:
					move(action.local);
					break;
				case ATIRAR:
					if(action.item == "Fire_Arrow")
					{
						shoot(action.direction, Item.FIRE_ARROW);
					}
					else
					{
						shoot(action.direction, Item.ARROW);
					}
				case QUEBRAR:
					mine(action.local);
					break;
			}
			movesThisTurn++;

			if(speed%1 != 0)
			{
				numMoves++;
			}
			if(posicao.equals(dungeon.getSaida()))
			{
				System.out.println("DOBBY IS FREEEEEE!!!!");
				free = true;
			}
		}
	}
	
	private void move(Point location)
	{
		posicao = location;

	}
	
	private void shoot(Direction dir, Item item)
	{
		inventory.shoot(item, dir);
		if(!monster.alive)
		{
			refreshBase(monster.getPosicao());
		}
	}
	
	private void mine(Point local)
	{
		inventory.mine(local);
		refreshBase(local);
	}
	
	private void checkPossibilities()
	{
		possibleActions = new ArrayList<Possibility>();
		Possibility possib = new Possibility();
		Point local = new Point();
		
		/// MOVER PARA BAIXO
		local = new Point(posicao.x + 1, posicao.y);
		possib.action = Actions.MOVER;
		possib.direction = Direction.BAIXO;
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) != "R")
			{
				possib.possible = true;
			}
			else
			{
				possib.possible = false;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		/// MOVER PARA CIMA
		possib = new Possibility();
		possib.action = Actions.MOVER;
		possib.direction = Direction.CIMA;
		local = new Point(posicao.x - 1, posicao.y);
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) != "R")
			{
				possib.possible = true;
			}
			else
			{
				possib.possible = false;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		/// MOVER PARA ESQUERDA
		possib = new Possibility();
		possib.action = Actions.MOVER;
		possib.direction = Direction.ESQUERDA;
		local = new Point(posicao.x, posicao.y - 1);
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) != "R")
			{
				possib.possible = true;
			}
			else
			{
				possib.possible = false;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		/// MOVER PARA DIREITA
		possib = new Possibility();
		possib.action = Actions.MOVER;
		possib.direction = Direction.DIREITA;
		local = new Point(posicao.x, posicao.y + 1);
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) != "R")
			{
				possib.possible = true;
			}
			else
			{
				possib.possible = false;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		/// ATIRAR PARA BAIXO
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.BAIXO;
		if(inventory.check().get(0).possession || inventory.check().get(2).possession)
		{
			possib.possible = true;
		}
		else
		{
			possib.possible = false;
		}
		possib.local = new Point(posicao);
		possibleActions.add(possib);
		
		/// ATIRAR PARA CIMA
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.CIMA;
		if(inventory.check().get(0).possession || inventory.check().get(2).possession)
		{
			possib.possible = true;
		}
		else
		{
			possib.possible = false;
		}
		possib.local = new Point(posicao);
		possibleActions.add(possib);
		
		/// ATIRAR PARA ESQUERDA
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.ESQUERDA;
		if(inventory.check().get(0).possession || inventory.check().get(2).possession)
		{
			possib.possible = true;
		}
		else
		{
			possib.possible = false;
		}
		possib.local = new Point(posicao);
		possibleActions.add(possib);
		
		/// ATIRAR PARA DIREITA
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.DIREITA;
		if(inventory.check().get(0).possession || inventory.check().get(2).possession)
		{
			possib.possible = true;
		}
		else
		{
			possib.possible = false;
		}
		possib.local = new Point(posicao);
		possibleActions.add(possib);
		
		/// QUEBRAR ROCHA ABAIXO
		possib = new Possibility();
		possib.action = Actions.QUEBRAR;
		possib.direction = Direction.BAIXO;
		local = new Point(posicao.x + 1, posicao.y);
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local).contains("R"))
			{
				possib.possible = true;
			}
			else
			{
				possib.possible = false;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		/// QUEBRAR ROCHAR ACIMA
		possib = new Possibility();
		possib.action = Actions.QUEBRAR;
		possib.direction = Direction.CIMA;
		local = new Point(posicao.x - 1, posicao.y);
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local).contains("R"))
			{
				possib.possible = true;
			}
			else
			{
				possib.possible = false;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		/// QUEBRAR ROCHA � ESQUERDA
		possib = new Possibility();
		possib.action = Actions.QUEBRAR;
		possib.direction = Direction.ESQUERDA;
		local = new Point(posicao.x, posicao.y - 1);
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local).contains("R"))
			{
				possib.possible = true;
			}
			else
			{
				possib.possible = false;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		/// QUEBRAR ROCHA � DIREITA
		possib = new Possibility();
		possib.action = Actions.QUEBRAR;
		possib.direction = Direction.DIREITA;
		local = new Point(posicao.x, posicao.y + 1);
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local).contains("R"))
			{
				possib.possible = true;
			}
			else
			{
				possib.possible = false;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
	}
	
	private void changeState(States nextState)
	{
		state = nextState;
		statesMachine();
	}
	
	private void statesMachine()
	{
		if(!safety_state_change)
		{
			switch (state)
			{
				case CURIOUS:
					speed = 1;
					break;
				case DESPERATE:
					speed += 0.25;
					break;
				case LAST_BREATH:
					speed += 1;
					safety_state_change = true;
					break;
			}
		}
	}
	
	private void interactWithBoard()
	{
		checkHealth();
		seeRock();
		takeGold();
		takeTorch();
		openChest();
		goUpTower();
		enterShortcut();
	}
	
	private void checkHealth()
	{
		for (int n = 0; n < dungeon.getLocal(posicao).length(); n++)
		{
			if(dungeon.getLocal(posicao).charAt(n) == 'M' || dungeon.getLocal(posicao).charAt(n) == 'P')
			{
				die();
			}
		}
	}
	
	private void enterShortcut()
	{
		percepcao();
		ai.update();
		for (int n = 0; n < dungeon.getLocal(posicao).length(); n++)
		{
			if(dungeon.getLocal(posicao).charAt(n) == 'A')
			{
				move(dungeon.getExit(posicao));
			}
		}
	}
	
	private void takeGold()
	{
		for (int n = 0; n < dungeon.getLocal(posicao).length(); n++)
		{
			if(dungeon.getLocal(posicao).charAt(n) == 'O')
			{
				changeState(States.LAST_BREATH);
				dungeon.modifyBoard(posicao, 'O');
				refreshBase(posicao);
			}
		}
	}
	
	private void takeTorch()
	{
		for (int n = 0; n < dungeon.getFires().size(); n++)
		{
			if((Math.abs(posicao.x - dungeon.getFires().get(n).x) <= 2 && Math.abs(posicao.y - dungeon.getFires().get(n).y) == 0)
					|| (Math.abs(posicao.x - dungeon.getFires().get(n).x) == 0 && Math.abs(posicao.y - dungeon.getFires().get(n).y) <= 2))
			{
				gatherInfo(dungeon.getFires().get(n), 0);
			}
		}
		
			if(dungeon.getLocal(posicao).contains("F"))
			{
				inventory.add(Item.TORCH, 1);
				inventory.guaranteeUse();
				dungeon.modifyBoard(posicao, 'F');
			}
		refreshBase(posicao);
	}
	
	private void goUpTower()
	{
		for (int n = 0; n < dungeon.getTowers().size(); n++)
		{
			if(Math.sqrt(Math.pow(posicao.x - dungeon.getTowers().get(n).x, 2) + Math.pow(posicao.y - dungeon.getTowers().get(n).y, 2)) <= 3)
			{
				gatherInfo(dungeon.getTowers().get(n), 0);
			}
		}
		
		for (int i = 0; i < dungeon.getLocal(posicao).length(); i++)
		{
			if(dungeon.getLocal(posicao).charAt(i) == 'T')
			{
				Point local = new Point();
				for (int quadrante = 1; quadrante <= 3; quadrante++)
				{
					local = new Point(posicao.x - quadrante, posicao.y);
					gatherInfo(local, quadrante);
					
					local = new Point(posicao.x + quadrante, posicao.y);
					gatherInfo(local, quadrante);
					
					local = new Point(posicao.x, posicao.y - quadrante);
					gatherInfo(local, quadrante);
					
					local = new Point(posicao.x, posicao.y + quadrante);
					gatherInfo(local, quadrante);
					
					local = new Point(posicao.x - quadrante, posicao.y - quadrante);
					gatherInfo(local, quadrante);
					
					local = new Point(posicao.x + quadrante, posicao.y + quadrante);
					gatherInfo(local, quadrante);
					
					local = new Point(posicao.x - quadrante, posicao.y + quadrante);
					gatherInfo(local, quadrante);
					
					local = new Point(posicao.x + quadrante, posicao.y - quadrante);
					gatherInfo(local, quadrante);
				}
				
			}
		}
		
	}
	
	private void openChest()
	{
		for (int i = 0; i < dungeon.getLocal(posicao).length(); i++)
		{
			if(dungeon.getLocal(posicao).charAt(i) == 'B')
			{
				/**
				 * Caso ele tenha encontrado uma Flecha
				 */
				if(Math.random() < 0.4)
				{
					if(Math.random() < 0.4)
					{
						if(Math.random() < 0.4)
						{
							inventory.add(Item.ARROW, 3);
						}
						else
						{
							inventory.add(Item.ARROW, 2);
						}
					}
					else
					{
						inventory.add(Item.ARROW, 1);
					}
				}
				/**
				 * Caso ele tenha encontrado uma picareta
				 */
				if(Math.random() < 0.25)
				{
					if(Math.random() < 0.25)
					{
						inventory.add(Item.PICKAXE, 2);
					}
					else
					{
						inventory.add(Item.PICKAXE, 1);
					}
				}
				/*
				 * Caso ele tenha encontrado um �culos de Miopia
				 */
				if(Math.random() < 0.15)
				{
					inventory.add(Item.GLASSES, 1);
				}
				/**
				 * Caso ele tenha ativado o alarme
				 */
				if(Math.random() < 0.1)
				{
					// Ativar o alarme aqui
				}
				/**
				 * Caso ele tenha encontrado um mapa
				 */
				if(Math.random() < 0.05)
				{
					inventory.add(Item.MAP, 1);
				}
				dungeon.modifyBoard(posicao, 'B');
			}
		}
		refreshBase(posicao);
	}
	
	public AI getAI()
	{
		return ai;
	}
	
	public Inventory getInventory()
	{
		return inventory;
	}
	public Point getPosicao()
	{
		return posicao;
	}
	
	public Possibility getAction()
	{
		return action;
	}
	
	public List<Possibility> getPossibilities()
	{
		return possibleActions;
	}
}
