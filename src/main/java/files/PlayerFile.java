package main.java.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import main.java.Main;

public class PlayerFile {
	
	static File file;
	public static FileConfiguration config;
	
	Main pl;
	public PlayerFile(Main pl) {
		this.pl = pl;
	}
	
	public void createConfig(Player player) {
		(new File("plugins" + File.separator + "AsgardAscension" + File.separator
				+ "players" + File.separator + "")).mkdirs();
		file = new File("plugins" + File.separator + "AsgardAscension" + File.separator+ "players",
				player.getUniqueId().toString() + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()){
			config.addDefault("rank", 0);
			config.addDefault("prestige", 0);
			config.addDefault("god_tokens", 0);
			config.addDefault("purchased_tokens", new ArrayList<String>());
			config.options().copyDefaults(true);
		}
		else {
			if(config.get("purchased_tokens") == null) {
				config.set("purchased_tokens", new ArrayList<String>());
			}
		}
		saveConfig();
	}
	
	public int getRank() {
		return config.getInt("rank");
	}
	
	public void setRank(int rank) {
		config.set("rank", rank);
	}
	
	public int getPrestige() {
		return config.getInt("prestige");
	}
	
	public void setPrestige(int prestige) {
		config.set("prestige", prestige);
	}
	
	public int getGodTokens() {
		return config.getInt("god_tokens");
	}
	
	public void setGodTokens(int tokens) {
		config.set("god_tokens", tokens);
	}
	
	public List<String> getTokens() {
		return config.getStringList("purchased_tokens");
	}
	
	public void setTokens(List<String> tokens) {
		config.set("purchased_tokens", tokens);
	}
	
	public void saveConfig()
	{
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
