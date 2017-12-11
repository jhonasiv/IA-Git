import java.util.Scanner;

import board.*;
import creatures.*;
import gui.*;

public class Main
{
	
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		Board board = new Board();
		board.setBoardSize(20, 20);
		Monster monster = new Monster(board);
		Human human = new Human(board, monster);
		Gui gui = new Gui(board, human, human.getAI(), human.getInventory());
		gui.initialize();
		monster.getHumanObject(human);
		board.printBoard();
		while (human.alive || monster.alive || !human.free)
		{
			// scanner.next();
			human.act();
			gui.update();
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException intrx)
			{
				System.out.println(intrx);
			}
		}
		// human.printBase();
	}
	
}
