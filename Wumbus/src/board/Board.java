
package board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board
{
	
	public List<ArrayList<String>> board = new ArrayList<ArrayList<String>>();
	public List<ArrayList<String>> unmovablesBoard = new ArrayList<ArrayList<String>>();
	private List<ManageAtalhos> connections = new ArrayList<ManageAtalhos>();
	public Obstacles rules = new Obstacles();
	
	private int width;
	private int height;
	
	public class ManageAtalhos
	{
		public Point entrada;
		public Point saida;
		
		public boolean equals(Point pos)
		{
			return(pos.equals(entrada));
		}
	}
	
	public void setBoardSize(int rows, int cols)
	{
		this.height = rows;
		this.width = cols;
		rules.getSize(this.width, this.height);
		this.generateBoard();
	}
	
	public boolean validPoint(Point point)
	{
		if(point.x >= 0 && point.x < height)
		{
			if(point.y >= 0 && point.y < width)
			{
				return true;
			}
		}
		return false;
		
	}
	
	private void generateBoard()
	{
		// /TODO: nova estrategia!
		// /TODO: Fazer primeiro os obstaculos, depois se coloca os sentidos!
		StringBuilder result = new StringBuilder();
		StringBuilder resultTmp = new StringBuilder();
		
		for (int i = 0; i < height; i++)
		{
			ArrayList<String> auxBoard = new ArrayList<String>();
			for (int j = 0; j < width; j++)
			{
				auxBoard.add("-");
			}
			this.board.add(auxBoard);
		}
		while (!rules.doneDefining())
		{
			String lastResult = "";
			for (int i = 0; i < height; i++)
			{
				for (int j = 0; j < width; j++)
				{
					result = new StringBuilder();
					resultTmp = new StringBuilder();
					result.append(board.get(i).get(j));
					if(board.get(i).get(j).equals("-"))
					{
						result = new StringBuilder();
					}
					resultTmp.append(rules.enforceRules(i, j, board.get(i).get(j)));
					
					if(!resultTmp.toString().equals("") && !resultTmp.equals(result))
					{
						result.append(resultTmp.toString());
						result = filterStringBuilder(result);
						board.get(i).set(j, result.toString());
						if(!result.toString().equals(lastResult))
						{
							lastResult = result.toString();
							i = 0;
							j = 0;
						}
					}
					
				}
			}
		}
		senses();
		generateUnmovables();
		connectAtalhos();
		// System.out.println(rules.getEntrada() + ", " + rules.getSaida());
	}
	
	private void generateUnmovables()
	{
		ArrayList<String> tempBoard = new ArrayList<String>();
		unmovablesBoard = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < height; i++)
		{
			tempBoard = new ArrayList<String>();
			for (int j = 0; j < width; j++)
			{
				StringBuilder result = new StringBuilder();
				for (int n = 0; n < board.get(i).get(j).length(); n++)
				{
					if(board.get(i).get(j).charAt(n) != 'M' && board.get(i).get(j).charAt(n) != 'f' && board.get(i).get(j).charAt(n) != 'H')
					{
						result.append(board.get(i).get(j).charAt(n));
					}
				}
				if(result.length() == 0)
				{
					result.append("-");
				}
				tempBoard.add(result.toString());
			}
			unmovablesBoard.add(tempBoard);
		}
	}
	
	private void connectAtalhos()
	{
		List<Point> atalhos = rules.getAtalhos();
		for(int i = 0; i < atalhos.size(); i += 2)
		{
			ManageAtalhos connect = new ManageAtalhos();
						
			connect.entrada = atalhos.get(i);
			connect.saida = atalhos.get(i + 1);
			connections.add(connect);
			
			connect.entrada = atalhos.get(i + 1);
			connect.saida = atalhos.get(i);
			connections.add(connect);
			
		}
	}
	
	private void senses()
	{
		ArrayList<Point> pocos = rules.getPocos();
		for (int n = 0; n < pocos.size(); n++)
		{
			writeSenses(pocos.get(n), "b");
		}
		Point monstro = rules.getMonstro();
		writeSenses(monstro, "f");
		Point ouro = rules.getOuro();
		writeSenses(ouro, "l");
		
	}
	
	private void writeSenses(Point ponto, String sense)
	{
		int i = ponto.x;
		int j = ponto.y;
		// /TODO: fazer sense ser uma StringBuilder e evitar que limpe o campo
		// do outros Obstaculos com um Sentido( um poco virar brisa)
		
		StringBuilder senseLeft = new StringBuilder("");
		StringBuilder senseRight = new StringBuilder("");
		StringBuilder senseUp = new StringBuilder("");
		StringBuilder senseDown = new StringBuilder("");
		
		if(i + 1 < height)
		{
			senseUp.append(board.get(i + 1).get(j));
		}
		if(i - 1 >= 0)
		{
			senseDown.append(board.get(i - 1).get(j));
		}
		if(j - 1 >= 0)
		{
			senseLeft.append(board.get(i).get(j - 1));
		}
		if(j + 1 < width)
		{
			senseRight.append(board.get(i).get(j + 1));
		}
		
		senseUp.append(sense);
		senseDown.append(sense);
		senseLeft.append(sense);
		senseRight.append(sense);
		
		filterStringBuilder(senseUp);
		filterStringBuilder(senseDown);
		filterStringBuilder(senseLeft);
		filterStringBuilder(senseRight);
		
		if(i == 0 && j == 0)
		{
			board.get(i + 1).set(j, senseUp.toString());
			board.get(i).set(j + 1, senseRight.toString());
		}
		else if(i == 0 && j == (width - 1))
		{
			board.get(i + 1).set(j, senseUp.toString());
			board.get(i).set(j - 1, senseLeft.toString());
		}
		else if(i == height - 1 && j == 0)
		{
			board.get(i).set(j + 1, senseRight.toString());
			board.get(i - 1).set(j, senseDown.toString());
		}
		else if(i == height - 1 && j == width - 1)
		{
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i - 1).set(j, senseDown.toString());
		}
		else if(i == 0)
		{
			board.get(i + 1).set(j, senseUp.toString());
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i).set(j + 1, senseRight.toString());
		}
		else if(i == height - 1)
		{
			board.get(i - 1).set(j, senseDown.toString());
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i).set(j + 1, senseRight.toString());
		}
		else if(j == 0)
		{
			board.get(i).set(j + 1, senseRight.toString());
			board.get(i - 1).set(j, senseDown.toString());
			board.get(i + 1).set(j, senseUp.toString());
		}
		else if(j == width - 1)
		{
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i - 1).set(j, senseDown.toString());
			board.get(i + 1).set(j, senseUp.toString());
		}
		else
		{
			board.get(i - 1).set(j, senseDown.toString());
			board.get(i + 1).set(j, senseUp.toString());
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i).set(j + 1, senseRight.toString());
		}
	}
	
	public void eraseSense(Point ponto, char sense)
	{
		int i = ponto.x;
		int j = ponto.y;
		// /TODO: fazer sense ser uma StringBuilder e evitar que limpe o campo
		// do outros Obstaculos com um Sentido( um poco virar brisa)
		
		StringBuilder senseLeft = new StringBuilder("");
		StringBuilder senseRight = new StringBuilder("");
		StringBuilder senseUp = new StringBuilder("");
		StringBuilder senseDown = new StringBuilder("");
		
		if(i + 1 < height)
		{
			senseUp.append(board.get(i + 1).get(j));
		}
		if(i - 1 >= 0)
		{
			senseDown.append(board.get(i - 1).get(j));
		}
		if(j - 1 >= 0)
		{
			senseLeft.append(board.get(i).get(j - 1));
		}
		if(j + 1 < width)
		{
			senseRight.append(board.get(i).get(j + 1));
		}
		
		for (int itt = 0; itt < senseUp.length(); itt++)
		{
			if(senseUp.charAt(itt) == sense)
			{
				senseUp.deleteCharAt(itt);
			}
		}
		for (int itt = 0; itt < senseDown.length(); itt++)
		{
			if(senseDown.charAt(itt) == sense)
			{
				senseDown.deleteCharAt(sense);
			}
		}
		for (int itt = 0; itt < senseLeft.length(); itt++)
		{
			if(senseLeft.charAt(itt) == sense)
			{
				senseLeft.deleteCharAt(sense);
			}
		}
		for (int itt = 0; itt < senseRight.length(); itt++)
		{
			if(senseRight.charAt(itt) == sense)
			{
				senseRight.deleteCharAt(sense);
			}
		}
		
		filterStringBuilder(senseUp);
		filterStringBuilder(senseDown);
		filterStringBuilder(senseLeft);
		filterStringBuilder(senseRight);
		
		if(i == 0 && j == 0)
		{
			board.get(i + 1).set(j, senseUp.toString());
			board.get(i).set(j + 1, senseRight.toString());
		}
		else if(i == 0 && j == (width - 1))
		{
			board.get(i + 1).set(j, senseUp.toString());
			board.get(i).set(j - 1, senseLeft.toString());
		}
		else if(i == height - 1 && j == 0)
		{
			board.get(i).set(j + 1, senseRight.toString());
			board.get(i - 1).set(j, senseDown.toString());
		}
		else if(i == height - 1 && j == width - 1)
		{
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i - 1).set(j, senseDown.toString());
		}
		else if(i == 0)
		{
			board.get(i + 1).set(j, senseUp.toString());
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i).set(j + 1, senseRight.toString());
		}
		else if(i == height - 1)
		{
			board.get(i - 1).set(j, senseDown.toString());
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i).set(j + 1, senseRight.toString());
		}
		else if(j == 0)
		{
			board.get(i).set(j + 1, senseRight.toString());
			board.get(i - 1).set(j, senseDown.toString());
			board.get(i + 1).set(j, senseUp.toString());
		}
		else if(j == width - 1)
		{
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i - 1).set(j, senseDown.toString());
			board.get(i + 1).set(j, senseUp.toString());
		}
		else
		{
			board.get(i - 1).set(j, senseDown.toString());
			board.get(i + 1).set(j, senseUp.toString());
			board.get(i).set(j - 1, senseLeft.toString());
			board.get(i).set(j + 1, senseRight.toString());
		}
	}
	
	private StringBuilder filterStringBuilder(StringBuilder str)
	{
		// System.out.println(str.toString());
		for (int i = 0; i < str.length(); i++)
		{
			int repeatition = 0;
			for (int j = 0; j < str.length(); j++)
			{
				if(i != j)
				{
					if(str.charAt(i) == str.charAt(j))
					{
						repeatition++;
						if(repeatition > 0)
						{
							str.deleteCharAt(j);
							j--;
						}
					}
				}
				if(str.charAt(j) == '-')
				{
					str.deleteCharAt(j);
					j--;
				}
			}
		}
		// System.out.println(str.toString());
		return str;
	}
	
	public void modifyBoard(Point local, String change, String newVal)
	{
		board.get(local.x).get(local.y).replace(change, newVal);
		if(board.get(local.x).get(local.y) == "")
		{
			board.get(local.x).set(local.y, "-");
		}
	}
	
	public void printBoard()
	{
		System.out.println("BOARD" );
		System.out.println();
		for (int i = 0; i < this.board.size(); i++)
		{
			System.out.println(i + "\t" + this.board.get(i));
		}
		
//		System.out.println("UNMOVABLE = ");
//		for (int i = 0; i < this.unmovablesBoard.size(); i++)
//		{
//			System.out.println(this.unmovablesBoard.get(i));
//		}
	}
	
	public Point getHuman()
	{
		return rules.getHuman();
	}
	
	public Point getMonster()
	{
		return rules.getMonstro();
	}
	
	public String getLocal(Point local)
	{
		return board.get(local.x).get(local.y);
	}
	
	public String getLocalObstacle(Point local)
	{
		StringBuilder localValue = new StringBuilder();
		localValue.append(board.get(local.x).get(local.y));
		for(int n = 0; n < localValue.length(); n++)
		{
			if(localValue.charAt(n) == 'b' || localValue.charAt(n) == 'l' || localValue.charAt(n) == 'f' || localValue.charAt(n) == 'D')
			{
				localValue.deleteCharAt(n);
			}
		}
		return localValue.toString();
	}
	
	public List<ArrayList<String>> getUnmovablesBoard()
	{
		return unmovablesBoard;
	}
	
	public List<Point> getTowers()
	{
		return rules.getTorres();
	}

	public List<Point> getFires()
	{
		return rules.getFogos();
	}
	
	public List<Point> getShortcuts()
	{
		return rules.getAtalhos();
	}
	
	public Point getExit(Point entrada)
	{
		Point saida = new Point();
		for(int i = 0; i < connections.size(); i++)
		{
			if(connections.get(i).equals(entrada))
			{
				saida = connections.get(i).saida;
			}
		}
		return saida;
	}
}
