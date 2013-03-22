package com.crossge.hungergames;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Players
{
	Game g = new Game();
	Stats s = new Stats();
	private static ArrayList<String> alive = new ArrayList<String>();
	private static ArrayList<String> origalive = new ArrayList<String>();
	private static ArrayList<String> dead = new ArrayList<String>();
	private static ArrayList<String> queued = new ArrayList<String>();
	private static ArrayList<String> spectating = new ArrayList<String>();
	private File customConfigFile = new File("plugins/Hunger Games", "spawns.yml");
   	private YamlConfiguration customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	public Players()
	{
		
	}
	
	public String leftAlive()
	{
		String temp = "";
		for(int i = 0; i < alive.size(); i++)
		{
			temp += alive.get(i) + ", ";
		}
		temp = temp.trim();
		temp = temp.substring(0, temp.length() - 2);
		temp += ".";
		return temp;
	}
	public boolean gameGoing()
	{
		return alive.size() > 1;
	}
	public boolean isAlive(String name)
	{
		return alive.contains(name);
	}
	public boolean isSpectating(String name)
	{
		return spectating.contains(name);
	}
	public void addSpectating(String name)
	{
		String world = g.getNext();
		spectating.add(name);
		String pathx = world + ".s25.x";
		String pathy = world + ".s25.y";
		String pathz = world + ".s25.z";
		customConfig.get(pathx);
		customConfig.get(pathy);
		customConfig.get(pathz);
		hideSpec();
		Player p = Bukkit.getPlayer(name);
		p.setGameMode(GameMode.ADVENTURE);
		p.setFoodLevel(20);
		p.setHealth(20);
		p.setFlying(true);
		p.setCanPickupItems(false);
		p.teleport(new Location(Bukkit.getWorld(world), customConfig.getInt(pathx), customConfig.getInt(pathy), customConfig.getInt(pathz)));
	}
	public void delSpectating(String name)
	{
		spectating.remove(name);
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(!p.canSee(Bukkit.getPlayer(name)))
				p.hidePlayer(Bukkit.getPlayer(name));
		}
		Player p = Bukkit.getPlayer(name);
		p.setGameMode(GameMode.SURVIVAL);
		p.setFoodLevel(20);
		p.setHealth(20);
		p.setFlying(false);
		p.setCanPickupItems(true);
		p.performCommand("/spawn");
	}
	public void addDead(String name)
	{
		alive.remove(name);
		dead.add(name);
		Player p = Bukkit.getPlayer(name);
		p.setGameMode(GameMode.SURVIVAL);
		p.setFoodLevel(20);
		p.setHealth(20);
		p.setFlying(false);
		p.setCanPickupItems(true);
		p.performCommand("/spawn");
	}
	public String deceased()
	{
		if(dead.isEmpty())
			return "none";
		String temp = "";
		for(int i = 0; i < dead.size(); i++)
		{
			temp += dead.get(i) + ", ";
		}
		temp = temp.trim();
		temp = temp.substring(0, temp.length() - 1);
		temp += ".";
		return temp;
	}
	public String breathing()
	{
		if(alive.isEmpty())
			return "none";
		String temp = "";
		for(int i = 0; i < alive.size(); i++)
		{
			temp += alive.get(i) + ", ";
		}
		temp = temp.trim();
		temp = temp.substring(0, temp.length() - 1);
		temp += ".";
		return temp;
	}
	public String amount()
	{
		try
		{
			return Integer.toString(alive.size()) + " alive out of " + Integer.toString(alive.size() + dead.size()) + " total.";
		}
		catch(Exception e)
		{
			return "Game not started";
		}
	}
	public boolean onePlayerLeft()
	{
		return alive.size() == 1;
	}
	public String winner()
	{
		s.addWin(alive.get(0));
		s.addPoints(alive.get(0), 20);
		Player p = Bukkit.getPlayer(alive.get(0));
		p.setGameMode(GameMode.SURVIVAL);
		p.setFoodLevel(20);
		p.setHealth(20);
		p.setFlying(false);
		p.setCanPickupItems(true);
		p.performCommand("/spawn");
		return alive.get(0);
	}
	public void hideSpectators(Player p)
	{
		if(spectating.size() == 0)
			return;
		for(int i = 0; i < spectating.size(); i++)
		{
			p.hidePlayer(Bukkit.getPlayer(spectating.get(i)));
		}
	}
	public void unhideSpectators(Player p)
	{
		if(spectating.size() == 0)
			return;
		for(int i = 0; i < spectating.size(); i++)
		{
			if(!p.canSee(Bukkit.getPlayer(spectating.get(i))))
				p.showPlayer(Bukkit.getPlayer(spectating.get(i)));
		}
	}
	private void hideSpec()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			for(int i = 0; i < spectating.size(); i++)
			{
				p.hidePlayer(Bukkit.getPlayer(spectating.get(i)));
			}
		}
	}
	private void unhideSpec()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			for(int i = 0; i < spectating.size(); i++)
			{
				p.showPlayer(Bukkit.getPlayer(spectating.get(i)));
			}
		}
	}
	public void endGame()
	{
		alive.clear();
		origalive.clear();
		dead.clear();
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.setCanPickupItems(true);
		}
		unhideSpec();
		spectating.clear();
		g.end();
	}
	public void addToQueue(String name)
	{
		if(g.getNext().equals(""))
		{
		//EMPTY IF :D	
		}
		queued.add(name);
	}
	public void removeFromQueue(String name)
	{
		queued.remove(name);
	}
	public boolean queueFull()
	{
		return queued.size() == 24;
	}
	public int posInQueue(String name)
	{
		return queued.indexOf(name) + 1;
	}
	public String district(String name)
	{
		for(int i = 0; i < origalive.size(); i++)
		{
			if(origalive.get(i).equals(name))
			{
				int temp = i + 1;
				if(temp + 1 > 12)
					temp = temp - 11;
				return Integer.toString(temp);
			}
		}
		return null;
	}
	public void gameStart()
	{
		alive.clear();
		origalive.clear();
		dead.clear();
		spectating.clear();
		for(int i = 0; i < queued.size(); i++)
		{
			alive.add(queued.get(i));
			origalive.add(queued.get(i));
		}
		queued.clear();
		joinGame();
	}
	private void joinGame()
	{
		Player temp;
		for(int i = 0; i < alive.size(); i++)
		{
			temp = Bukkit.getPlayer(alive.get(i));
			temp.setGameMode(GameMode.ADVENTURE);
			temp.setFoodLevel(20);
			temp.setHealth(20);
			temp.setFlying(false);
			temp.teleport(loc(i + 1));
			temp.getInventory().clear();
			temp.getEquipment().clear();
			temp.setExp(0);
			s.addGame(alive.get(i));
		}
	}
	private Location loc(int number)
	{
		String world = g.getNext();
		String pathx = world + ".s" + Integer.toString(number) + ".x";//temporarily only have support for one map name
		String pathy = world + ".s" + Integer.toString(number) + ".y";//temporarily only have support for one map name
		String pathz = world + ".s" + Integer.toString(number) + ".z";//temporarily only have support for one map name
		customConfig.get(pathx);
		customConfig.get(pathy);
		customConfig.get(pathz);
		return new Location(Bukkit.getWorld(world), customConfig.getInt(pathx), customConfig.getInt(pathy), customConfig.getInt(pathz));
	}
}