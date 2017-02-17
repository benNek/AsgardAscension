package com.nekrosius.asgardascension.files;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.utils.Convert;

public class PlayerFile {
	
	static File file;
	public static FileConfiguration config;
	
	Main plugin;
	public PlayerFile(Main plugin) {
		this.plugin = plugin;
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
			config.options().copyDefaults(true);
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
	
	public boolean isInChallenge() {
		return config.get("challenge.location") != null;
	}
	
	public void setChallengeLocation(Location location) {
		config.set("challenge.location", Convert.locationToString(location, true));
	}
	
	public Location getChallengeLocation() {
		return Convert.stringToLocation(config.getString("challenge.location"));
	}
	
	public void setChallengeLevel(int level) {
		config.set("challenge.level", level);
	}
	
	public int getChallengeLevel() {
		return config.getInt("challenge.level");
	}
	
	public void setChallengeExperience(float exp) {
		config.set("challenge.experience", (double) exp);
	}
	
	public float getChallengeExperience() {
		return (float) config.getDouble("challenge.exp");
	}
	
	public void setChallengePrice(long price) {
		config.set("challenge.price", price);
	}
	
	public long getChallengePrice() {
		return config.getLong("challenge.price");
	}
	
	public void removeChallenge() {
		config.set("challenge", null);
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
