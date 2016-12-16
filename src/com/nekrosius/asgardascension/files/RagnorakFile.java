package com.nekrosius.asgardascension.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.utils.Convert;

public class RagnorakFile {
	
	static File file;
	public static FileConfiguration config;

	private Main pl;
	public RagnorakFile(Main plugin) {
		pl = plugin;
		createConfig();
	}
	
	public void createConfig() {
		(new File("plugins" + File.separator + "AsgardAscension" + File.separator
				+ "")).mkdirs();
		file = new File("plugins" + File.separator + "AsgardAscension",
				"ragnorak.yml");
		config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()) {
			config.addDefault("duration", 10);
			config.addDefault("vote.minimum_players", 4);
			config.addDefault("vote.timers.restart", 60);
			config.addDefault("vote.timers.successful", 120);
			config.addDefault("vote.timers.unsuccessful", 30);
			config.addDefault("vote.timers.period", 2);
			config.options().copyDefaults(true);
		}
		else {
			if(config.get("duration") == null) {
				config.set("duration", 10);
				config.set("vote.minimum_players", 4);
				config.set("vote.timers.restart", 60);
				config.set("vote.timers.successful", 120);
				config.set("vote.timers.unsuccessful", 30);
				config.set("vote.timers.period", 2);
				config.set("repeat", null);
				config.set("percentage", null);
				config.set("interval", null);
			}
		}
		saveConfig();
	}
	
	public static void saveConfig()
	{
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getDuration() {
		return config.getInt("duration");
	}
	
	public static int getItemsAmount() {
		if(config.getConfigurationSection("items") == null) return 0;
		return config.getConfigurationSection("items").getKeys(false).size();
	}
	
	public static List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<ItemStack>();
		if(config.getConfigurationSection("items") != null){
			for(String str : config.getConfigurationSection("items").getKeys(false)){
				items.add(config.getItemStack("items." + str));
			}
		}
		return items;
	}
	
	public static void addItem(ItemStack item) {
		config.set("items." + String.valueOf(getItemsAmount()), item);
		saveConfig();
	}
	
	public static ItemStack getItem(int index) {
		if(config.getConfigurationSection("items") != null){
			for(String str : config.getConfigurationSection("items").getKeys(false)){
				if(Integer.parseInt(str) == index){
					 return config.getItemStack("items." + str);
				}
			}
		}
		return null;
	}
	
	public static Location getLocation() {
		int index = getRandom(0, getLocationsAmount() - 1);
		if(config.getStringList("locations") != null){
			return Convert.StringToLocation(config.getStringList("locations").get(index));
		}
		return null;
	}
	
	public static int getLocationsAmount() {
		return config.getStringList("locations").size();
	}
	
	public static List<String> getLocations() {
		List<String> locs = new ArrayList<String>();
		if(config.getStringList("locations") != null){
			for(String str : config.getStringList("locations")){
				locs.add(str);
			}
		}
		return locs;
	}
	
	public static void addLocation(Location loc) {
		List<String> locs = getLocations();
		locs.add(Convert.LocationToString(loc, false));
		config.set("locations", locs);
		saveConfig();
	}
	
	public static int getMinimumAmountOfPlayers() {
		return config.getInt("vote.minimum_players");
	}
	
	public static int getTimerAfterRestart() {
		return config.getInt("vote.timers.restart");
	}
	
	public static int getTimerAfterSuccessfulVote() {
		return config.getInt("vote.timers.successful");
	}
	
	public static int getTimerAfterUnsuccessfulVote() {
		return config.getInt("vote.timers.unsuccessful");
	}
	
	public static int getTimerForVotingPeriod() {
		return config.getInt("vote.timers.period");
	}
	
    private static int getRandom(int min, int max){
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
	
	public Main getPlugin() {
		return pl;
	}

}
