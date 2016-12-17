package com.nekrosius.asgardascension.files;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nekrosius.asgardascension.Main;

public class MessagesFile {
	
	static File file;
	public static FileConfiguration config;

	Main pl;
	public MessagesFile(Main plugin) {
		pl = plugin;
		createConfig();
	}
	
	public void createConfig() {
		(new File("plugins" + File.separator + "AsgardAscension" + File.separator
				+ "")).mkdirs();
		file = new File("plugins" + File.separator + "AsgardAscension",
				"messages.yml");
		config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()){
			config.addDefault("lucky_blocks.lava", "Have a warm bath!");
			config.addDefault("lucky_blocks.explosion", "Enjoy your free resources!");
			config.addDefault("lucky_blocks.zombie", "Bob is going to kill you!");
			config.addDefault("lucky_blocks.trap_chest", "What's that sound?");
			config.addDefault("lucky_blocks.speed_miner.start", "You have found &eSpeed Miner Effect&7!");
			config.addDefault("lucky_blocks.speed_miner.end", "&eSpeed Miner Effect &7has run out!");
			config.addDefault("lucky_blocks.diamond_miner.start", "Every block you break now yield diamonds!");
			config.addDefault("lucky_blocks.diamond_miner.end", "&eDiamond Miner Effect &7has run out!");
			config.addDefault("lucky_blocks.rainbow_miner.start", "You have found &eRainbow Miner Effect&7!");
			config.addDefault("lucky_blocks.rainbow_miner.end", "&eRainbow Miner Effect &7has run out!");
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
	
	public static String getMessage(String path) {
		if(config.get(path) == null) {
			return "Message not found! Inform administrators about missing " + path + " message!";
		}
		String message = config.getString(path);
		return ChatColor.translateAlternateColorCodes('&', message);	
	}

}
