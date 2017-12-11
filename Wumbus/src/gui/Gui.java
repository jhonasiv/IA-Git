
package gui;
//

import java.awt.Button;

//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JTextField;
//
//import java.awt.*;
//public class Gui extends JFrame
//{ 
//	private JButton button;
//	private JLabel label;
//	private JTextField field;
//	
//	public void initialize()
//	{
//		setLayout(new FlowLayout());
//		
//		field = new JTextField(10);
//		add(field);
//		
//		label = new JLabel("ROLA");
//		add(label);
//		
//		button = new JButton("TORTONA PRA ESQUERDA");
//		add(button);
//
//		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		setSize(800,600);
//		setVisible(true);
//		setTitle("Wumbus");
//		
//	}
//}

//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.GridLayout;
//import javax.swing.*;
//
//@SuppressWarnings("serial")
//public class Gui extends JPanel
//{
//	
//	private static final int SML_SIDE = 10;
//	private static final int SIDE = SML_SIDE * SML_SIDE;
//	private static final int GAP = 3;
//	private static final Color BG = Color.BLACK;
//	private static final Dimension BTN_PREF_SIZE = new Dimension(80, 80);
//	private JButton[][] buttons = new JButton[SIDE][SIDE];
//	
//	public Gui()
//	{
////		setBackground(BG);
//		setLayout(new GridLayout(SML_SIDE, SML_SIDE));
//		setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
//		JPanel smallPanels = new JPanel();
////		
//		smallPanels = new JPanel(new GridLayout(SML_SIDE, SML_SIDE));
////		
//		add(smallPanels);
//		for (int i = 0; i < buttons.length; i++)
//		{
//			int panelI = i / SML_SIDE;
//			for (int j = 0; j < buttons[i].length; j++)
//			{
//				int panelJ = j / SML_SIDE;
//				String text = String.format("[%d, %d]", j, i);
//				buttons[i][j] = new JButton(text);
//				buttons[i][j].setPreferredSize(BTN_PREF_SIZE);
//				add(buttons[i][j]);
//			}
//		}
////		smallPanels.add(buttons);
//	}
//	
//	public void initialize()
//	{
////		Gui mainPanel = new Gui();
//		
//		JFrame frame = new JFrame("JPanelGrid");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().add(this);
//		frame.pack();
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
//	}
//}

import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.jar.Pack200.Unpacker;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

import board.Board;
import creatures.Human;

public class Gui extends JFrame
{
	
	private Board board;
	private Human human;	
	private JButton[][] buttons;
	private JCheckBox activateHeuristic;
	private JPanel mainPanel;
	private JPanel sidePanel;
	private JPanel checkPanel;
	
	public void initialize()
	{
		mainPanel = new JPanel();
		sidePanel = new JPanel();
		checkPanel = new JPanel();
		activateHeuristic = new JCheckBox("Heuristic Mode");
//		getContentPane();
		buttons = new JButton[board.width][board.height];
//		setLayout(new );
//		setLayout(new GridLayout(board.width, board.height));
		mainPanel.setLayout(new GridLayout(1000, 700));
		sidePanel.setLayout(new GridLayout(board.width, board.height));
		setSize(1200, 800);
//		Point pos = new Point(10,10);
		for (int i = 0; i < board.width; i++)
		{
			for (int j = 0; j < board.height; j++)
			{
				buttons[i][j] = new JButton(board.getPrintBoard(human.getPosicao()).get(i).get(j));
				buttons[i][j].setFont(new Font("Times New Roman", Font.BOLD, 11));;
//				buttons[i][j].setSize(100, 100);
				sidePanel.add(buttons[i][j]);
//				pos = new Point(pos.x,pos.y+20);
			}
//			pos = new Point(pos.x+20,10);
		}
		
		checkPanel.add(activateHeuristic);
//		activateHeuristic.setLocation(new Point(900,900));
		mainPanel.add(sidePanel);
		mainPanel.add(checkPanel);
		add(mainPanel);
//		add(activateHeuristic);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		pack();
		setVisible(true);
	}
	
	public void update()
	{
		for (int i = 0; i < board.width; i++)
		{
			for (int j = 0; j < board.height; j++)
			{
				buttons[i][j].setText(board.getPrintBoard(human.getPosicao()).get(i).get(j));
			}
		}
//		pack();
	}
	
	public Gui(Board board, Human human)
	{
		this.board = board;
		this.human = human;
	}
	
}