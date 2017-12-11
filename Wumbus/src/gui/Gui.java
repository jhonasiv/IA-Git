
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
import javax.swing.JCheckBox;

import board.Board;
import creatures.AI;
import creatures.Human;
import items.Inventory;

public class Gui extends JFrame implements ActionListener
{
	
	private Board board;
	private Human human;
	private AI ai;
	private Inventory inventory;
	private JButton[][] buttons;
	private JRadioButton boardView;
	private JRadioButton moveHeuristic;
	private JRadioButton shootHeuristic;
	private JRadioButton mineHeuristic;
	private JPanel mainPanel;
	private JPanel sidePanel;
	private JPanel checkPanel;
	private JButton inventoryButton;
	private ButtonGroup checkGroup;
	private InventoryGUI inventoryGui; 
	private boolean activateInvGui = false;
	
	public Gui(Board board, Human human, AI ai, Inventory inventory)
	{
		this.board = board;
		this.human = human;
		this.ai = ai;
		this.inventory = inventory;
	}
	
	public void initialize()
	{
		checkGroup = new ButtonGroup();
		mainPanel = new JPanel();
		sidePanel = new JPanel();
		checkPanel = new JPanel();
		moveHeuristic = new JRadioButton("Move Heuristic");
		shootHeuristic = new JRadioButton("Shoot Heuristic");
		mineHeuristic = new JRadioButton("Mine Heuristic");
		boardView = new JRadioButton("Board View");
		inventoryButton = new JButton("Inventory");
		buttons = new JButton[board.width][board.height];
		inventoryGui = new InventoryGUI(inventory);
		
		inventoryGui.initialize();
		
		boardView.setSelected(true);
		setLayout(new BorderLayout());
		setResizable(false);
		checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setMaximumSize(new Dimension(800, 600));
		mainPanel.setMinimumSize(new Dimension(800, 600));
		sidePanel.setLayout(new GridLayout(board.width, board.height));
		setSize(1400, 1000);
		for (int i = 0; i < board.width; i++)
		{
			for (int j = 0; j < board.height; j++)
			{
				buttons[i][j] = new JButton(board.getPrintBoard(human.getPosicao()).get(i).get(j));
				buttons[i][j].setFont(new Font("Verdana", Font.BOLD, 12));;
				
				sidePanel.add(buttons[i][j]);
				
			}
			
		}
		checkGroup.add(boardView);
		checkGroup.add(moveHeuristic);
		checkGroup.add(shootHeuristic);
		checkGroup.add(mineHeuristic);
		
		checkPanel.add(boardView);
		checkPanel.add(moveHeuristic);
		checkPanel.add(shootHeuristic);
		checkPanel.add(mineHeuristic);
		checkPanel.add(Box.createVerticalStrut(200));
		checkPanel.add(inventoryButton);
		
		mainPanel.setVisible(true);
		mainPanel.add(sidePanel);
		mainPanel.add(Box.createHorizontalStrut(10));
		mainPanel.add(checkPanel);
		add(mainPanel);
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
					buttons[i][j].setText(board.getPrintBoard(human.getPosicao()).get(i).get(j));
				}
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
		}
		inventoryGui();
		pack();
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