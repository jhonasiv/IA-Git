
package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import items.Inventory;
import items.Inventory.InventoryInfo;

@SuppressWarnings("serial")
public class InventoryGUI extends JFrame implements ActionListener
{
	
	private Inventory inventory;
	private JLabel[] labels;
	private ArrayList<InventoryInfo> informations;
	private JPanel labelsPanel;
	private JButton exit;
	public boolean putDown = false;
	
	public InventoryGUI(Inventory inventory)
	{
		this.inventory = inventory;
	}
	
	public void initialize()
	{
		if(!isVisible())
		{
			exit = new JButton("Exit");
			labelsPanel = new JPanel();
			informations = new ArrayList<InventoryInfo>();
			informations = inventory.check();
			labels = new JLabel[informations.size()];
			labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
			
			setSize(400,300);
			setLayout(new BorderLayout());
			
			for (int i = 0; i < informations.size(); i++)
			{
				labels[i] = new JLabel(informations.get(i).toString());
				labelsPanel.add(labels[i]);
			}
			labelsPanel.add(Box.createVerticalStrut(50));
			labelsPanel.add(exit);
			add(labelsPanel);
			
			exit.addActionListener(this);
			
//			setVisible(true);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		}
		
	}
	
	public void update()
	{
		informations = inventory.check();
		for (int i = 0; i < informations.size(); i++)
		{
			labels[i].setText(informations.get(i).toString());
//			labelsPanel.add(labels[i]);
		}
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(exit))
		{
			putDown = true;
		}
	}
	
	public void close()
	{
		setVisible(false);
	}
	// public void clo
}
