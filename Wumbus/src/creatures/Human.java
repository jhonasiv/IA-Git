
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
	private int numMoves = 0;
	private List<Possibility> possibleActions = new ArrayList<Possibility>();
	private boolean safety_state_change = false;
	
	public void act()
	{
		Possibility action = new Possibility();
		int movesThisTurn = 0;
		int appr = 0;
		int totalMoves = (int)speed + (int)(numMoves/4);
		while ((movesThisTurn < totalMoves) && alive)
		{
			checkPossibilities();
			percepcao();
			ai.update();
//			ai.printSpecificModifier();
			inventory.print();
			ai.printPossibilites();
			dungeon.printBoard();
			ai.printMoveBase();
			printBase();
			
			System.out.print("Posicao inicial: " + posicao);
			action = ai.chooseAction();
			switch (action.action)
			{
				case MOVER:
					move(action.local);
					break;
				case ATIRAR:
					if(inventory.check().get(2).possession)
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
			interactWithBoard();
			System.out.println("\tMoveu " + movesThisTurn + " casas " + "\tPosicao final : " + posicao);
			if(speed%1 != 0)
			{
				numMoves++;
			}
		}
	}
	
	private void move(Point location)
	{
		posicao = location;
		// System.out.println(posicao);
	}
	
	private void shoot(Direction dir, Item item)
	{
		inventory.shoot(item, dir);
	}
	
	private void mine(Point local)
	{
		inventory.mine(local);
		for (int i = 0; i < base.size(); i++)
		{
			if(base.get(i).sameLocation(local))
			{
				base.get(i).info = dungeon.getLocal(local);
			}
		}
	}
	
	private void checkPossibilities()
	{
		possibleActions = new ArrayList<Possibility>();
		Possibility possib = new Possibility();
		Point local = new Point();
		
		/// MOVER PARA BAIXO
		local = new Point(posicao.x - 1, posicao.y);
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
		local = new Point(posicao.x + 1, posicao.y);
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
		possib.possible = true;
		possib.local = local;
		possibleActions.add(possib);
		
		/// ATIRAR PARA CIMA
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.CIMA;
		possib.possible = true;
		possib.local = local;
		possibleActions.add(possib);
		
		/// ATIRAR PARA ESQUERDA
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.ESQUERDA;
		possib.possible = true;
		possib.local = local;
		possibleActions.add(possib);
		
		/// ATIRAR PARA DIREITA
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.DIREITA;
		possib.possible = true;
		possib.local = local;
		possibleActions.add(possib);
		
		/// QUEBRAR ROCHA ABAIXO
		possib = new Possibility();
		possib.action = Actions.QUEBRAR;
		possib.direction = Direction.BAIXO;
		local = new Point(posicao.x - 1, posicao.y);
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) == "R")
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
		local = new Point(posicao.x + 1, posicao.y);
		possib.local = local;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) == "R")
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
			if(dungeon.getLocal(local) == "R")
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
			if(dungeon.getLocal(local) == "R")
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
				dungeon.modifyBoard(posicao, "O", "");
				for (int i = 0; i < base.size(); i++)
				{
					if(base.get(i).sameLocation(posicao))
					{
						base.get(i).info = dungeon.getLocal(posicao);
					}
				}
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
		
		for (int n = 0; n < dungeon.getLocal(posicao).length(); n++)
		{
			if(dungeon.getLocal(posicao).charAt(n) == 'F')
			{
				dungeon.modifyBoard(posicao, "F", "");
				inventory.add(Item.TORCH, 1);
			}
		}
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
				dungeon.modifyBoard(posicao, "T", "");
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
				dungeon.modifyBoard(posicao, "B", "");
			}
		}
	}
	
	public Point getPosicao()
	{
		return posicao;
	}
	
	public List<Possibility> getPossibilities()
	{
		return possibleActions;
	}
}
