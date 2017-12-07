
package items;

import creatures.*;
import creatures.Creature.Direction;

import java.awt.Point;
import java.util.ArrayList;

import board.*;

public class Inventory
{
	
	public Inventory(Board board, Human human, Monster monster)
	{
		arrow = new Arrow(board, human, monster);
		bow = new Bow(null, null, null);
		fire_arrow = new Fire_Arrow(board, human, monster);
		map = new Map(board, human, null);
		pickaxe = new Pickaxe(board, human, null);
		glasses = new Shortsighted_Glasses(null, human, null);
		torch = new Torch(null, human, null);
	}
	
	public class InventoryInfo
	{
		
		public InventoryInfo(Items item)
		{
			this.item = item;
			possession = item.getPossession();
			number = item.getNumberOf();
		}
		
		private Items item;
		public boolean possession;
		public int number;
		
		public String toString()
		{
			String string = new String();
			if(possession)
			{
				string = number + " de " + item.name;
			}
			return string;
		}
	}
	
	public enum Item {
		ARROW, BOW, FIRE_ARROW, MAP, PICKAXE, GLASSES, TORCH
	};
	
	private Arrow arrow;
	private Bow bow;
	private Fire_Arrow fire_arrow;
	private Map map;
	private Pickaxe pickaxe;
	private Shortsighted_Glasses glasses;
	private Torch torch;
	ArrayList<InventoryInfo> inventory = new ArrayList<InventoryInfo>();
	
	public ArrayList<InventoryInfo> check()
	{
		inventory = new ArrayList<InventoryInfo>();
		InventoryInfo info = new InventoryInfo(arrow);
		inventory.add(info);
		info = new InventoryInfo(bow);
		inventory.add(info);
		info = new InventoryInfo(fire_arrow);
		inventory.add(info);
		info = new InventoryInfo(glasses);
		inventory.add(info);
		info = new InventoryInfo(map);
		inventory.add(info);
		info = new InventoryInfo(pickaxe);
		inventory.add(info);
		info = new InventoryInfo(torch);
		inventory.add(info);
		return inventory;
	}
	
	public void add(Item item, int num)
	{
		switch (item)
		{
			case ARROW:
				arrow.acquire(num);
				break;
			case BOW:
				bow.acquire(num);
				break;
			case FIRE_ARROW:
				fire_arrow.acquire(num);
				break;
			case GLASSES:
				glasses.acquire(num);
				break;
			case MAP:
				map.acquire(num);
				break;
			case PICKAXE:
				pickaxe.acquire(num);
				break;
			case TORCH:
				torch.acquire(num);
				break;
		}
	}
	
	public void mine(Point local)
	{
		pickaxe.effect(local);
	}
	
	@SuppressWarnings("incomplete-switch")
	public void shoot(Item item, Direction dir)
	{
		switch (item)
		{
			case ARROW:
				arrow.effect(dir);
				break;
			case FIRE_ARROW:
				fire_arrow.effect(dir);
		}
	}
	
	private void guaranteeUse()
	{
		if(inventory.get(6).possession)
		{
			torch.effect();
		}
	}
	
	public void print()
	{
		for (int i = 0; i < inventory.size(); i++)
		{
			if(inventory.get(i).possession)
			{
				System.out.println(inventory.get(i).toString());
			}
		}
	}
	
}
