package com.nekrosius.asgardascension.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
	
	private static Random random;

	private Main plugin;
	public RagnorakFile(Main plugin) {
		this.plugin = plugin;
		random = new Random();
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
			config.addDefault("percentage_of_items", 50);
			config.addDefault("vote.minimum_players", 4);
			config.addDefault("vote.timers.restart", 60);
			config.addDefault("vote.timers.successful", 120);
			config.addDefault("vote.timers.unsuccessful", 30);
			config.addDefault("vote.timers.period", 2);
			config.options().copyDefaults(true);
		}
		else {
			if(config.get("percentage_of_items") == null) {
				config.set("percentage_of_items", 50);
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
			return Convert.stringToLocation(config.getStringList("locations").get(index));
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
		locs.add(Convert.locationToString(loc, false));
		config.set("locations", locs);
		saveConfig();
	}
	
	public static int getPercentageOfItems() {
		return config.getInt("percentage_of_items");
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
	
	public static List<ItemStack> getItemsToSpawn() {
		List<ItemStack> items = getItems();
		Collections.shuffle(items);
		int itemsToTake = (int) ((double)getPercentageOfItems() / 100 * getItemsAmount());
		items = items.subList(0, itemsToTake);
		return items;
	}
	
    private static int getRandom(int min, int max){
        return random.nextInt((max - min) + 1) + min;
    }
	
	public Main getPlugin() {
		return plugin;
	}

}
