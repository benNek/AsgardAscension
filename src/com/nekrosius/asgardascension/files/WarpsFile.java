package com.nekrosius.asgardascension.files;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.utils.Convert;

public class WarpsFile {
	
	static File file;
	public static FileConfiguration config;
	
	Main plugin;
	public WarpsFile(Main plugin) {
		this.plugin = plugin;
		createConfig();
	}
	
	public void createConfig() {
		(new File("plugins" + File.separator + "AsgardAscension" + File.separator
				+ "")).mkdirs();
		file = new File("plugins" + File.separator + "AsgardAscension",
				"warps.yml");
		config = YamlConfiguration.loadConfiguration(file);
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
	
	public static boolean warpExists(String title) {
		return config.get(title + ".location") != null;
	}
	
	public static void setLocation(String title, Location location) {
		config.set(title + ".location", Convert.locationToString(location, true));
		if(config.get(title + ".price") == null) {
			config.set(title + ".price", 1);
		}
		saveConfig();
	}
	
	public static void setPrice(String title, int price) {
		config.set(title + ".price", price);
		saveConfig();
	}
	
	public static Location getLocation(String title) {
		return Convert.stringToLocation(config.getString(title + ".location"));
	}
	
	public static int getPrice(String title) {
		return config.getInt(title + ".price");
	}

}
