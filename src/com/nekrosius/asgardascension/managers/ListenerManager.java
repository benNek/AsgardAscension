package com.nekrosius.asgardascension.managers;

import org.bukkit.Bukkit;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.listeners.ChallengeListener;
import com.nekrosius.asgardascension.listeners.GodFoodListener;
import com.nekrosius.asgardascension.listeners.GodTokenListener;
import com.nekrosius.asgardascension.listeners.InventoryListener;
import com.nekrosius.asgardascension.listeners.PlayerListener;
import com.nekrosius.asgardascension.listeners.SetupListener;
import com.nekrosius.asgardascension.listeners.TribeListener;

public class ListenerManager{

	private Main pl;
	public ListenerManager(Main plugin) {
		pl = plugin;
		Bukkit.getPluginManager().registerEvents(new InventoryListener(pl), pl);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(pl), pl);
		Bukkit.getPluginManager().registerEvents(new SetupListener(pl), pl);
		Bukkit.getPluginManager().registerEvents(new ChallengeListener(pl), pl);
		Bukkit.getPluginManager().registerEvents(new GodFoodListener(pl), pl);
		Bukkit.getPluginManager().registerEvents(new GodTokenListener(pl), pl);
		Bukkit.getPluginManager().registerEvents(new TribeListener(pl), pl);
	}
	
	public Main getPlugin() {
		return pl;
	}
	
}
