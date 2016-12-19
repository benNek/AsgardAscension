package com.nekrosius.asgardascension.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.utils.Convert;

public class ConfigFile {
	
	static File file;
	public static FileConfiguration config;

	private Main pl;
	public ConfigFile(Main plugin) {
		pl = plugin;
		createConfig();
	}
	
	public void createConfig() {
		(new File("plugins" + File.separator + "AsgardAscension" + File.separator
				+ "")).mkdirs();
		file = new File("plugins" + File.separator + "AsgardAscension",
				"config.yml");
		config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()){
			config.addDefault("prestige.tokens_reward", 2);
			config.addDefault("prestige.command", "give %player dirt");
			config.addDefault("prestige.location", "world, 0, 0, 0, 0, 0");
			config.addDefault("rankup.max_prestige", 2);
			config.addDefault("rankup.fight_max_mobs", 20);
			List<String> cmds = new ArrayList<String>();
			cmds.add("give %player dirt");
			cmds.add("give %player iron_ingot");
			config.addDefault("lucky_blocks.commands", cmds);
			List<String> msgs = new ArrayList<String>();
			msgs.add("Better luck next time!");
			msgs.add("You've won special iron ingot!");
			config.addDefault("lucky_blocks.messages", msgs);
			config.addDefault("tribes.price", 10000);
			config.addDefault("tribes.1.members", 5);
			config.addDefault("tribes.2.price", 50000);
			config.addDefault("tribes.2.members", 10);
			config.addDefault("tribes.3.price", 100000);
			config.addDefault("tribes.3.members", 15);
			config.addDefault("tribes.4.price", 250000);
			config.addDefault("tribes.4.members", 20);
			config.addDefault("tribes.5.price", 500000);
			config.addDefault("tribes.5.members", 25);
			config.options().copyDefaults(true);
		}
		else {
			if(config.get("lucky_blocks.command") != null) {
				List<String> cmds = new ArrayList<String>();
				cmds.add("give %player dirt");
				cmds.add("give %player iron_ingot");
				config.set("lucky_blocks.commands", cmds);
				List<String> msgs = new ArrayList<String>();
				msgs.add("Better luck next time!");
				msgs.add("You've won special iron ingot!");
				config.set("lucky_blocks.messages", msgs);
				config.set("lucky_blocks.command", null);
			}
			if(config.get("prestige.tokens_reward") == null) {
				config.set("prestige.tokens_reward", 2);
			}
			if(config.get("prestige.command") == null) {
				config.set("prestige.command", "give %player dirt");
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
	
	public static String getPrestigeCommand() {
		return config.getString("prestige.command");
	}
	
	public static double getRankUpPrice(int level) {
		return config.getInt("tribes." + String.valueOf(level) + ".price");
	}
	
	public static int getMaxMembers(int level) {
		return config.getInt("tribes." + String.valueOf(level) + ".members");
	}
	
	public static int getMaxMonsters() {
		return config.getInt("rankup.fight_max_mobs");
	}
	
	public static double getTribePrice() {
		return config.getDouble("tribes.price");
	}
	
	public static int getMaxPrestige() {
		return config.getInt("rankup.max_prestige");
	}
	
	public static int getTokensReward() {
		return config.getInt("prestige.tokens_reward");
	}
	
	public static List<String> getLuckyCommands() {
		return config.getStringList("lucky_blocks.commands");
	}
	
	public static List<String> getLuckyCommandMessages() {
		return config.getStringList("lucky_blocks.messages");
	}
	
	public static Location getPrestigeLocation() {
		return Convert.stringToLocation(config.getString("prestige.location"));
	}
	
	public static void setPrestigeLocation(String location) {
		config.set("prestige.location", location);
		saveConfig();
	}
	
	public Main getPlugin() {
		return pl;
	}
}
