
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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


import board.Board;
import creatures.AI;
import creatures.Human;
import creatures.Human.Actions;
import creatures.Monster;
import items.Inventory;

@SuppressWarnings("serial")
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
	private JPanel controlBox;
	private JPanel checkPanel;
	private JPanel newsPanel;
	private JLabel actionLabel;
	private JTextArea newsLabel;
	private News news;
	private JButton inventoryButton;
	private ButtonGroup checkGroup;
	private InventoryGUI inventoryGui;
	public boolean pause = false;
	private JButton pauseButton;
	private JButton resetButton;
	private Point lastPosition = new Point();
	public boolean reset = false;
	
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
		controlBox = new JPanel();
		actionBox = new JPanel();
		newsPanel = new JPanel();
		moveHeuristic = new JRadioButton("Move Heuristic");
		shootHeuristic = new JRadioButton("Shoot Heuristic");
		mineHeuristic = new JRadioButton("Mine Heuristic");
		boardView = new JRadioButton("Board View");
		baseView = new JRadioButton("Base View");
		inventoryButton = new JButton("Inventory");
		actionLabel = new JLabel("");
		newsLabel = new JTextArea("Que os jogos comecem!");
		buttons = new JButton[board.width][board.height];
		pauseButton = new JButton("Pause");
		resetButton = new JButton("Reset");
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
		newsPanel.setLayout(new BoxLayout(newsPanel, BoxLayout.Y_AXIS));
		newsPanel.setMaximumSize(new Dimension(300, 400));
		checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
		mainPanel.setLayout(new GridBagLayout());
		actionBox.setLayout(new BoxLayout(actionBox, BoxLayout.Y_AXIS));
		controlBox.setLayout(new BoxLayout(controlBox, BoxLayout.X_AXIS));
		buttonPanel.setMaximumSize(new Dimension(1650, 750));
		buttonPanel.setMinimumSize(new Dimension(1650, 750));
		actionBox.setMaximumSize(new Dimension(1650, 750));
		actionBox.setMinimumSize(new Dimension(1650, 750));
		mainPanel.setMaximumSize(new Dimension(1650, 750));
		mainPanel.setMinimumSize(new Dimension(1650, 750));
		buttonPanel.setLayout(new GridLayout(board.width, board.height));
		setMinimumSize(new Dimension(1750, 750));
		setMaximumSize(new Dimension(1750, 750));
		
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.weightx = 1;
		// setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		for (int i = 0; i < board.width; i++)
		{
			for (int j = 0; j < board.height; j++)
			{
				buttons[i][j] = new JButton(board.getPrintBoard(human.getPosicao()).get(i).get(j));
				buttons[i][j].setFont(new Font("Verdana", Font.BOLD, 12));;
				buttons[i][j].setToolTipText("[" + i + ", " + j + "]");
				buttons[i][j].setFocusable(false);
				buttonPanel.add(buttons[i][j]);
				
			}
			
		}
		newsPanel.add(Box.createHorizontalStrut(20));
		newsPanel.add(newsLabel);
		mainPanel.add(newsPanel);
		mainPanel.add(Box.createHorizontalStrut(50));
		actionLabel.setFont(new Font("Verdana", Font.BOLD, 16));
		newsLabel.setFont(new Font("Verdana", Font.BOLD, 14));
		
		checkGroup.add(boardView);
		checkGroup.add(baseView);
		checkGroup.add(moveHeuristic);
		checkGroup.add(shootHeuristic);
		checkGroup.add(mineHeuristic);
		
		actionBox.add(buttonPanel, BoxLayout.X_AXIS);
		// actionBox.setBounds(150,20, 1850,900);
		actionBox.add(Box.createVerticalStrut(20));
		actionLabel.setAlignmentX(CENTER_ALIGNMENT);
		actionBox.add(actionLabel);
		actionBox.add(Box.createVerticalStrut(50));
		controlBox.add(pauseButton);
		controlBox.add(Box.createHorizontalStrut(20));
		controlBox.add(resetButton);
		actionBox.add(controlBox);
		checkPanel.add(boardView);
		checkPanel.add(baseView);
		checkPanel.add(moveHeuristic);
		checkPanel.add(shootHeuristic);
		checkPanel.add(mineHeuristic);
		checkPanel.add(Box.createVerticalStrut(200));
		checkPanel.add(inventoryButton);
		
		mainPanel.setVisible(true);
		mainPanel.add(actionBox, constraint);
		mainPanel.add(Box.createHorizontalStrut(10));
		mainPanel.add(checkPanel);
		
		add(mainPanel, BorderLayout.CENTER);
		inventoryButton.addActionListener(this);
		pauseButton.addActionListener(this);
		resetButton.addActionListener(this);
		pauseButton.setFocusable(false);
		resetButton.setFocusable(false);
		boardView.setFocusable(false);
		baseView.setFocusable(false);
		shootHeuristic.setFocusable(false);
		moveHeuristic.setFocusable(false);
		mineHeuristic.setFocusable(false);
		inventoryButton.setFocusable(false);
		// pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setTitle("Wumbus");
		
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
					if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("O"))
					{
						buttons[i][j].setBackground(Color.YELLOW);
						buttons[i][j].setForeground(Color.BLACK);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("T"))
					{
						buttons[i][j].setBackground(Color.BLUE);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("B"))
					{
						buttons[i][j].setForeground(Color.BLACK);
						buttons[i][j].setBackground(Color.PINK);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("S"))
					{
						buttons[i][j].setBackground(Color.CYAN);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("E"))
					{
						buttons[i][j].setBackground(Color.BLACK);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("F"))
					{
						buttons[i][j].setForeground(Color.RED);
					}
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
					if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("O"))
					{
						buttons[i][j].setForeground(Color.BLACK);
						buttons[i][j].setBackground(Color.YELLOW);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("T"))
					{
						buttons[i][j].setBackground(Color.BLUE);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("B"))
					{
						buttons[i][j].setForeground(Color.BLACK);
						buttons[i][j].setBackground(Color.PINK);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("S"))
					{
						buttons[i][j].setBackground(Color.CYAN);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("E"))
					{
						buttons[i][j].setBackground(Color.BLACK);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("F"))
					{
						buttons[i][j].setForeground(Color.RED);
					}
					buttons[i][j].setText("  ");
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
					if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("O"))
					{
						buttons[i][j].setBackground(Color.YELLOW);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("T"))
					{
						buttons[i][j].setBackground(Color.BLUE);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("B"))
					{
						buttons[i][j].setBackground(Color.PINK);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("S"))
					{
						buttons[i][j].setBackground(Color.CYAN);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("E"))
					{
						buttons[i][j].setBackground(Color.BLACK);
					}
					else if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("F"))
					{
						buttons[i][j].setForeground(Color.RED);
					}
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
					if(board.getPrintBoard(new Point(i, j)).get(i).get(j).contains("R"))
					{
						buttons[i][j].setBackground(Color.ORANGE);
					}
					buttons[i][j].setText(Integer.toString(ai.getMineHeuristic(new Point(i, j))));
					
				}
			}
			buttons[human.getPosicao().x][human.getPosicao().y].setForeground(Color.BLACK);
			buttons[human.getPosicao().x][human.getPosicao().y].setBackground(Color.GREEN);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setForeground(Color.BLACK);
			buttons[monster.getPosicao().x][monster.getPosicao().y].setBackground(Color.RED);
		}
		actionLabel.setText(human.getAction().action.toString() + " para " + human.getAction().direction.toString());
		if(!human.getPosicao().equals(lastPosition) || human.getAction().action == Actions.ATIRAR)
		{
			if(!human.alive)
			{
				news.addNews("O humano foi morto!\n");
			}
			else if(human.getAction().action == Actions.ATIRAR && !monster.alive)
			{
				news.addNews("O humano matou o Monstro com uma flecha no peito");
			}
			else if(human.free)
			{
				news.addNews("O humano escapou da caverna!\n");
			}
			else if(board.getLocal(human.getPosicao()).contains("O"))
			{
				news.addNews("O humano encontrou o Ouro! Ele esta rico!\n");
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
				news.addNews("O humano achou fogo! Esta evoluindo..\n");
			}
			else if(board.getLocal(human.getPosicao()).contains("B"))
			{
				news.addNews("O humano achou um bau! O que sera que tem dentro dele?\n");
			}
			else if(board.getLocal(human.getPosicao()).contains("D"))
			{
				news.addNews("Cuidado! A sala pode desmoronar a qualquer instante!\n");
			}
			else if(board.getLocal(human.getPosicao()).contains("A"))
			{
				news.addNews("O humano pegou carona num atalho!\n");
			}
		}
		newsLabel.setText("\n" + news.feed());
		lastPosition = new Point(human.getPosicao());
		inventoryGui();
		pack();
	}
	
	private void inventoryGui()
	{
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
		else if(e.getSource().equals(pauseButton))
		{
			pause = !pause;
			if(pause)
			{
				news.addNews("Jogo pausado!\n");
				pauseButton.setText("Continue");
			}
			else
			{
				pauseButton.setText("Pause");
				news.addNews("Que o jogo continue!\n");
			}
			
		}
		else if(e.getSource().equals(resetButton))
		{
			reset = true;
		}
	}
	
}