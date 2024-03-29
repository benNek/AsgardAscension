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
	FileConfiguration config;

	private Main plugin;
	public ChallengesFile(Main plugin) {
		this.plugin = plugin;
		createConfig();
		for(String key : config.getKeys(false)) {
			if(config.get(key + ".title") == null) {
				config.set(key + ".title", key);
			}
			else {
				break;
			}
		}
		saveConfig();
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
		List<String> cmds = new ArrayList<>();
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
	
	public void setTitle(int challenge, String title) {
		config.set(String.valueOf(challenge) + ".title", title);
		saveConfig();
	}
	
	public void setPrice(int challenge, long price) {
		config.set(String.valueOf(challenge) + ".price", price);
		saveConfig();
	}
	
	public void setSpawnpoint(Player player, int challenge) {
		config.set(String.valueOf(challenge) + ".spawnpoint", Convert.locationToString(player.getLocation(), true));
		saveConfig();
	}
	
	public void setNoteblock(int challenge, Location loc) {
		config.set(String.valueOf(challenge) + ".noteblock", Convert.locationToString(loc, false));
		saveConfig();
	}
	
	public void setVictorySpawnpoint(Player player, int challenge) {
		config.set(String.valueOf(challenge) + ".victory", Convert.locationToString(player.getLocation(), true));
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
		mobs.add(mob + ", " + Convert.locationToString(loc, true));
		config.set(String.valueOf(challenge) + ".monsters", mobs);
		saveConfig();
	}
	
	public void setMobsLocation(int challenge, Player player) {
		config.set(String.valueOf(challenge) + ".spawns.first",
				Convert.locationToString(ChallengeSetup.getFirstLocation(player), false));
		config.set(String.valueOf(challenge) + ".spawns.second",
				Convert.locationToString(ChallengeSetup.getSecondLocation(player), false));
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
	
	public String getTitle(int challenge) {
		if(config.get(String.valueOf(challenge) + ".title") == null) 
			return "A";
		return config.getString(String.valueOf(challenge) + ".title");
	}
	
	public boolean hasTitle(int challenge) {
		return config.get(String.valueOf(challenge) + ".title") != null;
	}
	
	public int getChallengeId(String title) {
		for(String key : config.getKeys(false)) {
			int id = Integer.parseInt(key);
			if(title.equalsIgnoreCase(getTitle(id))) {
				return id;
			}
		}
		return -1;
	}
	
	public Material getTypeMaterial(int challenge) {
		String type = getType(challenge);
		if("parkour".equalsIgnoreCase(type))
			return Material.GOLD_BOOTS;
		else if("maze".equalsIgnoreCase(type))
			return Material.TORCH;
		else return Material.GOLD_SWORD;
	}
	
	public Location getSpawnpoint(int challenge) {
		return Convert.stringToLocation(config.getString(String.valueOf(challenge) + ".spawnpoint")); 
	}
	
	public Long getPrice(int challenge) {
		return config.getLong(String.valueOf(challenge) + ".price");
	}
	
	public Location getNoteblockLocation(int challenge) {
		return Convert.stringToLocation(config.getString(String.valueOf(challenge) + ".noteblock")); 
	}
	
	public Location getVictorySpawnpoint(int challenge) {
		return Convert.stringToLocation(config.getString(String.valueOf(challenge) + ".victory")); 
	}
	
	public List<String> getCommands(int challenge) {
		return config.getStringList(String.valueOf(challenge) + ".commands"); 
	}
	
	public List<String> getMonsters(int challenge) {
		return config.getStringList(String.valueOf(challenge) + ".mobs"); 
	}
	
	public Location getFirstLocation(int challenge) {
		return Convert.stringToLocation(config.getString(String.valueOf(challenge) + ".spawns.first"));
	}
	
	public Location getSecondLocation(int challenge) {
		return Convert.stringToLocation(config.getString(String.valueOf(challenge) + ".spawns.second"));
	}
	
	public Main getPlugin() {
		return plugin;
	}

}
