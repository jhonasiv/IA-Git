
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.jar.Pack200.Unpacker;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.JCheckBox;

import board.Board;
import creatures.AI;
import creatures.Human;
import creatures.Human.Actions;
import creatures.Monster;
import items.Inventory;

public class Gui extends JFrame implements ActionListener
{
	
	private Board board;
	private Monster monster;
	private Human human;
	private AI ai;
	private Inventory inventory;
	private JButton[][] buttons;
	private JRadioButton boardView;
	private JRadioButton baseView;
	private JRadioButton moveHeuristic;
	private JRadioButton shootHeuristic;
	private JRadioButton mineHeuristic;
	private JPanel mainPanel;
	private JPanel buttonPanel;
	private JPanel actionBox;
	private JPanel checkPanel;
	private JPanel newsPanel;
	private JLabel actionLabel;
	private JTextArea newsLabel;
	private News news;
	private JButton inventoryButton;
	private ButtonGroup checkGroup;
	private InventoryGUI inventoryGui;
	private boolean activateInvGui = false;
	
	public Gui(Board board, Monster monster, Human human, AI ai, Inventory inventory)
	{
		this.board = board;
		this.human = human;
		this.monster = monster;
		this.ai = ai;
		this.inventory = inventory;
	}
	
	public void initialize()
	{
		news = new News();
		checkGroup = new ButtonGroup();
		mainPanel = new JPanel();
		buttonPanel = new JPanel();
		checkPanel = new JPanel();
		actionBox = new JPanel();
		newsPanel = new JPanel();
		moveHeuristic = new JRadioButton("Move Heuristic");
		shootHeuristic = new JRadioButton("Shoot Heuristic");
		mineHeuristic = new JRadioButton("Mine Heuristic");
		boardView = new JRadioButton("Board View");
		baseView = new JRadioButton("Base View");
		inventoryButton = new JButton("Inventory");
		actionLabel = new JLabel("");
		newsLabel = new JTextArea();
		buttons = new JButton[board.width][board.height];
		inventoryGui = new InventoryGUI(inventory);
		
		inventoryGui.initialize();
		
		newsLabel.setWrapStyleWord(true);
		newsLabel.setLineWrap(true);
		newsLabel.setOpaque(false);
		newsLabel.setEditable(true);
		newsLabel.setFocusable(false);
		newsLabel.setBackground(UIManager.getColor("Label.background"));
		newsLabel.setFont(UIManager.getFont("Label.font"));
		newsLabel.setBorder(UIManager.getBorder("Label.border"));
		
		boardView.setSelected(true);
		setLayout(new BorderLayout());
		// setResizable(false);
		newsPanel.setLayout(new BoxLayout(newsPanel, BoxLayout.Y_AXIS));
//		newsPanel.setMinimumSize(new Dimension(0, 400));
		newsPanel.setMaximumSize(new Dimension(200, 400));
		checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
		mainPanel.setLayout(new GridBagLayout());
		actionBox.setLayout(new BoxLayout(actionBox, BoxLayout.Y_AXIS));
		buttonPanel.setMaximumSize(new Dimension(1650, 600));
		buttonPanel.setMinimumSize(new Dimension(1550, 600));
		mainPanel.setMaximumSize(new Dimension(1850, 600));
		mainPanel.setMinimumSize(new Dimension(1850, 600));
		buttonPanel.setLayout(new GridLayout(board.width, board.height));
		setMinimumSize(new Dimension(1550, 600));
		setMaximumSize(new Dimension(2850, 1200));
		for (int i = 0; i < board.width; i++)
		{
			for (int j = 0; j < board.height; j++)
			{
				buttons[i][j] = new JButton(board.getPrintBoard(human.getPosicao()).get(i).get(j));
				buttons[i][j].setFont(new Font("Verdana", Font.BOLD, 12));;
				
				buttonPanel.add(buttons[i][j]);
				
			}
			
		}
		
		newsPanel.add(newsLabel);
		mainPanel.add(newsPanel);
		mainPanel.add(Box.createHorizontalStrut(150));
		actionLabel.setFont(new Font("Verdana", Font.BOLD, 16));
		newsLabel.setFont(new Font("Verdana", Font.BOLD, 14));
		
		checkGroup.add(boardView);
		checkGroup.add(baseView);
		checkGroup.add(moveHeuristic);
		checkGroup.add(shootHeuristic);
		checkGroup.add(mineHeuristic);
		
		actionBox.add(buttonPanel);
		actionBox.add(actionLabel);
		checkPanel.add(boardView);
		checkPanel.add(baseView);
		checkPanel.add(moveHeuristic);
		checkPanel.add(shootHeuristic);
		checkPanel.add(mineHeuristic);
		checkPanel.add(Box.createVerticalStrut(200));
		checkPanel.add(inventoryButton);
		
		mainPanel.setVisible(true);
		mainPanel.add(actionBox);
		mainPanel.add(Box.createHorizontalStrut(10));
		mainPanel.add(checkPanel);
		add(mainPanel);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setTitle("Wumbus");
		
		newsLabel.setText("Que os jogos comecem!");
	}
	
	public void update()
	{
		if(boardView.isSelected())
		{
			for (int i = 0; i < board.width; i++)
			{
				for (int j = 0; j < board.height; j++)
				{
					buttons[i][j].setForeground(Color.WHITE);
					buttons[i][j].setBackground(Color.DARK_GRAY);
					buttons[i][j].setText(board.getPrintBoard(human.getPosicao()).get(i).get(j));
				}
			}
			buttons[human.getPosicao().x][human.getPosicao().y].setForeground(Color.BLACK);
			buttons[human.getPosicao().x][human.getPosicao().y].setBackground(Color.GREEN);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setForeground(Color.BLACK);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setBackground(Color.RED);
		}
		else if(baseView.isSelected())
		{
			for (int i = 0; i < board.width; i++)
			{
				for (int j = 0; j < board.height; j++)
				{
					buttons[i][j].setForeground(Color.WHITE);
					buttons[i][j].setBackground(Color.DARK_GRAY);
					buttons[i][j].setText("");
				}
			}
			for (int i = 0; i < human.getBase().size(); i++)
			{
				buttons[human.getBase().get(i).local.x][human.getBase().get(i).local.y].setText(human.getBase().get(i).info);
			}
			buttons[human.getPosicao().x][human.getPosicao().y].setForeground(Color.BLACK);
			buttons[human.getPosicao().x][human.getPosicao().y].setBackground(Color.GREEN);
		}
		else if(moveHeuristic.isSelected())
		{
			for (int i = 0; i < board.width; i++)
			{
				for (int j = 0; j < board.height; j++)
				{
					buttons[i][j].setBackground(Color.DARK_GRAY);
					buttons[i][j].setForeground(Color.WHITE);
					buttons[i][j].setText(Integer.toString(ai.getMoveHeuristic(new Point(i, j))));
					
				}
			}
			buttons[human.getPosicao().x][human.getPosicao().y].setForeground(Color.BLACK);
			buttons[human.getPosicao().x][human.getPosicao().y].setBackground(Color.GREEN);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setForeground(Color.BLACK);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setBackground(Color.RED);
		}
		else if(shootHeuristic.isSelected())
		{
			for (int i = 0; i < board.width; i++)
			{
				for (int j = 0; j < board.height; j++)
				{
					buttons[i][j].setBackground(Color.DARK_GRAY);
					buttons[i][j].setForeground(Color.WHITE);
					buttons[i][j].setText(Integer.toString(ai.getShootHeuristic(new Point(i, j))));
					
				}
			}
			buttons[human.getPosicao().x][human.getPosicao().y].setForeground(Color.BLACK);
			buttons[human.getPosicao().x][human.getPosicao().y].setBackground(Color.GREEN);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setForeground(Color.BLACK);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setBackground(Color.RED);
		}
		else if(mineHeuristic.isSelected())
		{
			for (int i = 0; i < board.width; i++)
			{
				for (int j = 0; j < board.height; j++)
				{
					buttons[i][j].setBackground(Color.DARK_GRAY);
					buttons[i][j].setForeground(Color.WHITE);
					buttons[i][j].setText(Integer.toString(ai.getMineHeuristic(new Point(i, j))));
					
				}
			}
			buttons[human.getPosicao().x][human.getPosicao().y].setForeground(Color.BLACK);
			buttons[human.getPosicao().x][human.getPosicao().y].setBackground(Color.GREEN);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setForeground(Color.BLACK);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setBackground(Color.RED);
		}
		actionLabel.setText(human.getAction().action.toString() + " para " + human.getAction().direction.toString());
		if(!human.alive)
		{
			news.addNews("O humano foi morto!\n");
		}
		else if(human.getAction().action == Actions.ATIRAR && !monster.alive)
		{
			news.addNews("O humano matou o Monstro com uma flecha no coração");
		}
		else if(human.free)
		{
			news.addNews("O humano escapou da caverna!\n");
		}
		else if(board.getLocal(human.getPosicao()).contains("O"))
		{
			news.addNews("O humano encontrou o Ouro! Ele está rico!\n");
		}
		else if(board.getLocal(human.getPosicao()).contains("T"))
		{
			news.addNews("O humano subiu numa enorme Torre!\n");
		}
		else if(human.getAction().action == Actions.ATIRAR)
		{
			news.addNews("O humano atirou para a " + human.getAction().direction.toString() + "\n");
		}
		else if(board.getLocal(human.getPosicao()).contains("F"))
		{
			news.addNews("O humano achou fogo! Está evoluindo..\n");
		}
		else if(board.getLocal(human.getPosicao()).contains("B"))
		{
			news.addNews("O humano achou um baú! O que será que tem dentro dele?\n");
		}
		else if(board.getLocal(human.getPosicao()).contains("D"))
		{
			news.addNews("Cuidado! A sala pode desmoronar a qualquer instante!\n");
		}
		else if(board.getLocal(human.getPosicao()).contains("A"))
		{
			news.addNews("O humano pegou carona num atalho!\n");
		}
		newsLabel.setText("\n" + news.feed());
		inventoryGui();
		// pack();
	}
	
	private void inventoryGui()
	{
		inventoryButton.addActionListener(this);
		if(inventoryGui.isVisible())
		{
			inventoryGui.update();
		}
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(inventoryButton))
		{
			inventoryGui.setVisible(true);;
		}
	}
}