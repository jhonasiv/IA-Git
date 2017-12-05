import board.*;
import creatures.*;

public class Main 
{
	@SuppressWarnings("unused")
	public static void main(String[] args) 
	{
		Board board = new Board();
		board.setBoardSize(20, 20);
		Monster monster = new Monster(board);
		Human human = new Human(board, monster);
		monster.getHumanObject(human);
		board.printBoard();
//		human.printBase();
	}

}
