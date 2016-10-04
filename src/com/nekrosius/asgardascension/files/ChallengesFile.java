package com.nekrosius.asgardascension.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.challenges.ChallengeSetup;
import com.nekrosius.asgardascension.utils.Convert;

public class ChallengesFile {
	
	File file;
	public FileConfiguration config;

	private Main pl;
	public ChallengesFile(Main plugin) {
		pl = plugin;
		createConfig();
	}
	
	public void createConfig() {
		(new File("plugins" + File.separator + "AsgardAscension" + File.separator
				+ "")).mkdirs();
		file = new File("plugins" + File.separator + "AsgardAscension",
				"challenges.yml");
		config = YamlConfiguration.loadConfiguration(file);
		saveConfig();
	}
	
	public void saveConfig()
	{
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void remove(int challenge) {
		config.set(String.valueOf(challenge), null);
		saveConfig();
	}
	
	public void removeCommands(int challenge) {
		List<String> cmds = new ArrayList<String>();
		config.set(String.valueOf(challenge) + ".commands", cmds);
		saveConfig();
	}
	
	public void removeMonsterSpawns(int challenge) {
		config.set(String.valueOf(challenge) + ".mobs", new ArrayList<String>());
		saveConfig();
	}
	
	public int getChallengesAmount() {
		return config.getKeys(false).size();
	}
	
	public void addChallenge(String type) {
		config.set(String.valueOf(getChallengesAmount() + 1) + ".type", type);
		saveConfig();
	}
	
	public void setPrice(int challenge, long price) {
		config.set(String.valueOf(challenge) + ".price", price);
		saveConfig();
	}
	
	public void setSpawnpoint(Player player, int challenge) {
		config.set(String.valueOf(challenge) + ".spawnpoint", Convert.LocationToString(player.getLocation(), true));
		saveConfig();
	}
	
	public void setNoteblock(Player player, int challenge, Location loc) {
		config.set(String.valueOf(challenge) + ".noteblock", Convert.LocationToString(loc, false));
		saveConfig();
	}
	
	public void setVictorySpawnpoint(Player player, int challenge) {
		config.set(String.valueOf(challenge) + ".victory", Convert.LocationToString(player.getLocation(), true));
		saveConfig();
	}
	
	public void addCommand(int challenge, String command) {
		List<String> cmds = config.getStringList(String.valueOf(challenge) + ".commands");
		cmds.add(command);
		config.set(String.valueOf(challenge) + ".commands", cmds);
		saveConfig();
	}
	
	public void addMonster(int challenge, String mob, Location loc) {
		List<String> mobs = config.getStringList(String.valueOf(challenge) + ".monsters");
		mobs.add(mob + ", " + Convert.LocationToString(loc, true));
		config.set(String.valueOf(challenge) + ".monsters", mobs);
		saveConfig();
	}
	
	public void setMobsLocation(int challenge, Player player) {
		config.set(String.valueOf(challenge) + ".spawns.first",
				Convert.LocationToString(ChallengeSetup.getFirstLocation(player), false));
		config.set(String.valueOf(challenge) + ".spawns.second",
				Convert.LocationToString(ChallengeSetup.getSecondLocation(player), false));
		saveConfig();
	}
	
	public void addMob(int challenge, String mob, int amount) {
		List<String> mobs = config.getStringList(String.valueOf(challenge) + ".mobs");
		mobs.add(mob + ", " + amount);
		config.set(String.valueOf(challenge) + ".mobs", mobs);
		saveConfig();
	}
	
	public String getType(int challenge) {
		return config.getString(String.valueOf(challenge) + ".type");
	}
	
	public Material getTypeMaterial(int challenge) {
		String type = getType(challenge);
		if(type.equalsIgnoreCase("parkour")) return Material.GOLD_BOOTS;
		else if(type.equalsIgnoreCase("maze")) return Material.TORCH;
		else return Material.GOLD_SWORD;
	}
	
	public Location getSpawnpoint(int challenge) {
		return Convert.StringToLocation(config.getString(String.valueOf(challenge) + ".spawnpoint")); 
	}
	
	public Long getPrice(int challenge) {
		return config.getLong(String.valueOf(challenge) + ".price");
	}
	
	public Location getNoteblockLocation(int challenge) {
		return Convert.StringToLocation(config.getString(String.valueOf(challenge) + ".noteblock")); 
	}
	
	public Location getVictorySpawnpoint(int challenge) {
		return Convert.StringToLocation(config.getString(String.valueOf(challenge) + ".victory")); 
	}
	
	public List<String> getCommands(int challenge) {
		return config.getStringList(String.valueOf(challenge) + ".commands"); 
	}
	
	public List<String> getMonsters(int challenge) {
		return config.getStringList(String.valueOf(challenge) + ".mobs"); 
	}
	
	public Location getFirstLocation(int challenge) {
		return Convert.StringToLocation(config.getString(String.valueOf(challenge) + ".spawns.first"));
	}
	
	public Location getSecondLocation(int challenge) {
		return Convert.StringToLocation(config.getString(String.valueOf(challenge) + ".spawns.second"));
	}
	
	public Main getPlugin() {
		return pl;
	}

}
