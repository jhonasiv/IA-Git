

import board.*;
import creatures.*;
import gui.*;

public class Main
{
	
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		Board board = new Board();
		board.setBoardSize(20, 20);
		Monster monster = new Monster(board);
		Human human = new Human(board, monster);
		Gui gui = new Gui(board, monster, human, human.getAI(), human.getInventory());
		gui.initialize();
		monster.getHumanObject(human);
		while (true)
		{
			if(!gui.pause)
			{
				human.act();
			}
			gui.update();
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException intrx)
			{
				System.out.println(intrx);
			}
			if(gui.reset)
			{
				board = new Board();
				board.setBoardSize(20, 20);
				monster = new Monster(board);
				human = new Human(board, monster);
				gui = new Gui(board, monster, human, human.getAI(), human.getInventory());
				gui.initialize();
				monster.getHumanObject(human);
				gui.reset = false;
			}
		}
	}
	
}
