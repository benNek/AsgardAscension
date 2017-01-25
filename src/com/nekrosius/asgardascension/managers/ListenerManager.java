package com.nekrosius.asgardascension.managers;

import org.bukkit.Bukkit;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.listeners.ChallengeListener;
import com.nekrosius.asgardascension.listeners.CustomEnchantsListener;
import com.nekrosius.asgardascension.listeners.GodFoodListener;
import com.nekrosius.asgardascension.listeners.GodTokenListener;
import com.nekrosius.asgardascension.listeners.InventoryListener;
import com.nekrosius.asgardascension.listeners.PlayerListener;
import com.nekrosius.asgardascension.listeners.SetupListener;
import com.nekrosius.asgardascension.listeners.TribeListener;

public class ListenerManager{

	private Main plugin;
	public ListenerManager(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new SetupListener(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new ChallengeListener(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new GodFoodListener(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new GodTokenListener(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new TribeListener(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new CustomEnchantsListener(plugin), plugin);
	}
	
	public Main getPlugin() {
		return plugin;
	}
	
}
