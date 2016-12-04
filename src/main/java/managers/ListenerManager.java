package main.java.managers;

import org.bukkit.Bukkit;

import main.java.Main;
import main.java.listeners.ChallengeListener;
import main.java.listeners.GodFoodListener;
import main.java.listeners.GodTokenListener;
import main.java.listeners.InventoryListener;
import main.java.listeners.PlayerListener;
import main.java.listeners.SetupListener;
import main.java.listeners.TribeListener;

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
