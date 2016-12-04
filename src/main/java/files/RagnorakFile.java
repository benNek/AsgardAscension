package main.java.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import main.java.Main;
import main.java.utils.Convert;

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
		if(!file.exists()){
			config.addDefault("repeat", 240);
			config.addDefault("percentage", 80);
			config.addDefault("interval", 5);
			config.options().copyDefaults(true);
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
	
	public static int getTime() {
		return config.getInt("repeat");
	}
	
	public static double getPercentageMultiplier() {
		return config.getInt("percentage") / 100;
	}
	
	public static int getInterval() {
		return config.getInt("interval");
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
	
    private static int getRandom(int min, int max){
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
	
	public Main getPlugin() {
		return pl;
	}

}
