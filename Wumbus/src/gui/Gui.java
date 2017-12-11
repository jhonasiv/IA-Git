
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.jar.Pack200.Unpacker;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

import board.Board;
import creatures.AI;
import creatures.Human;

public class Gui extends JFrame
{
	
	private Board board;
	private Human human;
	private AI ai;
	private JButton[][] buttons;
	private JCheckBox moveHeuristic;
	private JCheckBox shootHeuristic;
	private JPanel mainPanel;
	private JPanel sidePanel;
	private JPanel checkPanel;
	
	public Gui(Board board, Human human, AI ai)
	{
		this.board = board;
		this.human = human;
		this.ai = ai;
	}
	
	public void initialize()
	{
		mainPanel = new JPanel();
		sidePanel = new JPanel();
		checkPanel = new JPanel();
		moveHeuristic = new JCheckBox("Move Heuristic");
		shootHeuristic = new JCheckBox("Shoot Heuristic");
		checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
		buttons = new JButton[board.width][board.height];
		setLayout(new BorderLayout());
		
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setSize(800, 700);
		sidePanel.setLayout(new GridLayout(board.width, board.height));
		setSize(1200, 800);
		for (int i = 0; i < board.width; i++)
		{
			for (int j = 0; j < board.height; j++)
			{
				buttons[i][j] = new JButton(board.getPrintBoard(human.getPosicao()).get(i).get(j));
				buttons[i][j].setFont(new Font("Verdana", Font.BOLD, 12));;
				
				sidePanel.add(buttons[i][j]);
				
			}
			
		}
		
		checkPanel.add(moveHeuristic);
		checkPanel.add(shootHeuristic);
		
		mainPanel.setVisible(true);
		mainPanel.add(sidePanel);
		mainPanel.add(checkPanel);
		add(mainPanel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	public void update()
	{
		if(!moveHeuristic.isSelected())
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
		else
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
		pack();
	}
	
}