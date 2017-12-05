
package creatures;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import items.*;
import items.Inventory.Item;
import board.Board;

public class Human extends Creature
{
	
	private enum Actions {
		MOVER, ENTRAR_ATALHO, ATIRAR, QUEBRAR
	}
	
	private enum States {
		CURIOUS, DESPERATE, LAST_BREATH;
		
//		public States next(int num)
//		{
//			if(ordinal() + num >= values().length)
//			{
//				return values()[values().length-1];
//			}
//			return values()[ordinal() + num];
//		}
	}
	
	protected class Possibility
	{
		
		public Actions action;
		public Direction direction;
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
		inventory.add(Item.MAP, 1);
		inventory.check();
//		inventory.print();
		percepcao();
		AI ai = new AI(this, board, inventory);
	}
	
	private States state;
	private Inventory inventory;
	private int numMoves = 0;
	private List<Possibility> possibleActions = new ArrayList<Possibility>();
	
	public void move()
	{
		checkPossibilities();
	}
	
	private void checkPossibilities()
	{
		possibleActions = new ArrayList<Possibility>();
		Possibility possib = new Possibility();
		Point local = new Point();
		
		///MOVER PARA BAIXO
		local = new Point(posicao.x - 1, posicao.y);
		possib.action = Actions.MOVER;
		possib.direction = Direction.BAIXO;
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) != "R")
			{
				possib.possible = true;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		///MOVER PARA CIMA
		possib = new Possibility();
		possib.action = Actions.MOVER;
		possib.direction = Direction.CIMA;
		local = new Point(posicao.x + 1, posicao.y);
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) != "R")
			{
				possib.possible = true;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		///MOVER PARA ESQUERDA
		possib = new Possibility();
		possib.action = Actions.MOVER;
		possib.direction = Direction.ESQUERDA;
		local = new Point(posicao.x, posicao.y - 1);
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) != "R")
			{
				possib.possible = true;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		///MOVER PARA DIREITA
		possib = new Possibility();
		possib.action = Actions.MOVER;
		possib.direction = Direction.DIREITA;
		local = new Point(posicao.x, posicao.y + 1);
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) != "R")
			{
				possib.possible = true;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
				
		///ENTRAR ATALHO
		possib = new Possibility();
		possib.action = Actions.ENTRAR_ATALHO;
		possib.direction = null;
		String quadrante = dungeon.getLocal(posicao);
		for(int i = 0; i < quadrante.length(); i++)
		{
			if(quadrante.charAt(i) == 'A')
			{
				possib.possible = true;
				break;
			}
			else
			{
				possib.possible = false;
			}
		}
		possibleActions.add(possib);
				
		///ATIRAR PARA BAIXO
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.BAIXO;
		possib.possible = true;
		possibleActions.add(possib);		
		
		///ATIRAR PARA CIMA
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.CIMA;
		possib.possible = true;
		possibleActions.add(possib);
		
		///ATIRAR PARA ESQUERDA
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.ESQUERDA;
		possib.possible = true;
		possibleActions.add(possib);
		
		///ATIRAR PARA DIREITA
		possib = new Possibility();
		possib.action = Actions.ATIRAR;
		possib.direction = Direction.DIREITA;
		possib.possible = true;
		possibleActions.add(possib);
		
		///QUEBRAR ROCHA ABAIXO
		possib = new Possibility();
		possib.action = Actions.QUEBRAR;
		possib.direction = Direction.BAIXO;
		local = new Point(posicao.x - 1, posicao.y);
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) == "R")
			{
				possib.possible = true;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
				
		///QUEBRAR ROCHAR ACIMA
		possib = new Possibility();
		possib.action = Actions.QUEBRAR;
		possib.direction = Direction.CIMA;
		local = new Point(posicao.x + 1, posicao.y);
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) == "R")
			{
				possib.possible = true;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
		
		///QUEBRAR ROCHA À ESQUERDA
		possib = new Possibility();
		possib.action = Actions.QUEBRAR;
		possib.direction = Direction.ESQUERDA;
		local = new Point(posicao.x, posicao.y - 1);
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) == "R")
			{
				possib.possible = true;
			}
		}
		else
		{
			possib.possible = false;
		}
		possibleActions.add(possib);
				
		///QUEBRAR ROCHA À DIREITA
		possib = new Possibility();
		possib.action = Actions.QUEBRAR;
		possib.direction = Direction.DIREITA;
		local = new Point(posicao.x, posicao.y+1);
		if(dungeon.validPoint(local))
		{
			if(dungeon.getLocal(local) == "R")
			{
				possib.possible = true;
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
				break;
		}
	}
	
	private void openChest()
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
		 * Caso ele tenha encontrado um Óculos de Miopia
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
