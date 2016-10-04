package com.nekrosius.asgardascension.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.nekrosius.asgardascension.managers.TribeManager;
import com.nekrosius.asgardascension.objects.Tribe;

public class TribeFile {
	
	static File file;
	public static FileConfiguration config;
	
	public static void createConfig(String name) {
		(new File("plugins" + File.separator + "AsgardAscension" + File.separator
				+ "tribes" + File.separator + "")).mkdirs();
		file = new File("plugins" + File.separator + "AsgardAscension" + File.separator + "tribes",
				name + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()){
			Tribe tribe = TribeManager.getTribe(name);
			if(tribe == null) return;
			config.addDefault("name", name);
			config.addDefault("type", tribe.getType());
			config.addDefault("level", tribe.getLevel());
			config.addDefault("leader", tribe.getLeader());
			config.addDefault("balance", tribe.getBalance());
			config.addDefault("members", tribe.getMembers());
			// CONTAINER
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
	
	public static void delete(String name) {
		file.delete();
	}
	
	public static String getName() {
		return config.getString("name");
	}
	
	public static String getType() {
		return config.getString("type");
	}
	
	public static int getLevel() {
		return config.getInt("level");
	}
	
	public static String getLeader() {
		return config.getString("leader");
	}
	
	public static Double getBalance() {
		return config.getDouble("balance");
	}
	
	public static List<String> getMembers() {
		return config.getStringList("members");
	}
	
	public static String getDescription() {
		return config.getString("description");
	}
	
	public static List<String> getAllies() {
		return config.getStringList("allies");
	}
	
	public static List<String> getEnemies() {
		return config.getStringList("enemies");
	}
	
	public static void setName(String name) {
		config.set("name", name);
		saveConfig();
	}
	
	public static void setType(String type) {
		config.set("type", type);
		saveConfig();
	}
	
	public static void setLevel(int level) {
		config.set("level", level);
		saveConfig();
	}
	
	public static void setLeader(String name) {
		config.set("leader", name);
		saveConfig();
	}
	
	public static void setBalance(double balance) {
		config.set("balance", balance);
		saveConfig();
	}
	
	public static void setMembers(List<String> members) {
		config.set("members", members);
		saveConfig();
	}
	
	public static void addMember(String player) {
		List<String> members = getMembers();
		members.add(player);
		config.set("members", members);
		saveConfig();
	}
	
	public static void removeMember(String player) {
		List<String> members = getMembers();
		members.remove(player);
		config.set("members", members);
		saveConfig();
	}
	
	public static void setDescription(String desc) {
		config.set("description", desc);
		saveConfig();
	}
	
	public static void setAllies(List<String> allies) {
		config.set("allies", allies);
		saveConfig();
	}
	
	public static void addAlly(String ally) {
		List<String> allies = getAllies();
		allies.add(ally);
		config.set("allies", allies);
		saveConfig();
	}
	
	public static void removeAlly(String ally) {
		List<String> allies = getAllies();
		allies.remove(ally);
		config.set("allies", allies);
		saveConfig();
	}
	
	public static void setEnemies(List<String> enemies) {
		config.set("enemies", enemies);
		saveConfig();
	}
	
	public static void addEnemy(String enemy) {
		List<String> enemies = getEnemies();
		enemies.add(enemy);
		config.set("enemies", enemies);
		saveConfig();
	}
	
	public static void removeEnemy(String enemy) {
		List<String> enemies = getEnemies();
		enemies.remove(enemy);
		config.set("enemies", enemies);
		saveConfig();
	}
	
	public static List<ItemStack> getChest() {
		List<?> list = config.getList("chest");
		if(list == null) return null;
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(Object item : list) {
			items.add((ItemStack) item);
		}
		return items;
	}
	
	public static void setChest(ItemStack[] items) {
		config.set("chest", items);
		saveConfig();
	}

}
