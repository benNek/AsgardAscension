package com.nekrosius.asgardascension.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.files.TribeFile;
import com.nekrosius.asgardascension.objects.Tribe;

public class TribeManager {
	
	private static List<Tribe> tribes = new ArrayList<Tribe>();
	private static Map<String, Tribe> playerTribe = new HashMap<String, Tribe>();
	
	public static String mh = ChatColor.GRAY + "[" + ChatColor.RED + "Asgard Tribes" + ChatColor.GRAY + "] ";
	
	private Main pl;
	public TribeManager(Main plugin) {
		pl = plugin;
		File file = new File("plugins" + File.separator + "AsgardAscension" + File.separator
				+ "tribes" + File.separator);
		String[] files = file.list();
		if(files != null){
			for(String str : files){
				if(str.length() - 4 <= 0) continue;
				String name = str.substring(0, str.length() - 4);
				TribeFile.createConfig(name);
				Tribe tribe = new Tribe(name);
				tribe.setType(TribeFile.getType());
				tribe.setBalance(TribeFile.getBalance());
				tribe.setLeader(TribeFile.getLeader());
				tribe.setMembers(TribeFile.getMembers());
				tribe.setLevel(TribeFile.getLevel());
				tribe.setDescription(TribeFile.getDescription());
				tribe.setAllies(TribeFile.getAllies());
				tribe.setEnemies(TribeFile.getEnemies());
				addTribe(tribe);
			}
		}
	}
	
	public static List<Tribe> getTribes() {
		return tribes;
	}
	
	public static void addTribe(Tribe tribe) {
		tribes.add(tribe);
	}
	
	public static void removeTribe(Tribe tribe) {
		tribes.remove(tribe);
	}
	
	public static Tribe getTribe(String name) {
		for(Tribe tribe : getTribes()) {
			if(tribe.getName().equalsIgnoreCase(name)) {
				return tribe;
			}
		}
		return null;
	}
	
	public static boolean hasTribe(String player) {
		if(playerTribe.get(player) != null) return true;
		return false;
	}
	
	public static Tribe getPlayerTribe(String player) {
		return playerTribe.get(player);
	}
	
	public static void setPlayerTribe(String player, Tribe tribe) {
		playerTribe.put(player, tribe);
	}
	
	public static void removePlayerTribe(String player) {
		playerTribe.remove(player);
	}
	
	public static boolean isLeader(String player) {
		if(getPlayerTribe(player) == null) return false;
		return getPlayerTribe(player).getLeader().equals(player);
	}
	
	public static void sendMessage(Tribe tribe, String message) {
		for(String p : tribe.getMembers()) {
			if(Bukkit.getPlayer(p) != null){
				Bukkit.getPlayer(p).sendMessage(message);
			}
		}
	}
	
	public static boolean isAllies(Tribe one, Tribe two) {
		if(one.getAllies().contains(two.getName())) return true;
		return false;
	}
	
	public static boolean canAttack(Player one, Player two) {
		if(!hasTribe(one.getName()) || !hasTribe(two.getName())) {
			return true;
		}
		if(getPlayerTribe(one.getName()).equals(getPlayerTribe(two.getName()))){
			return false;
		}
		if(isAllies(getPlayerTribe(one.getName()), getPlayerTribe(two.getName()))) {
			return false;
		}
		return Main.isPVPEnabled(one) && Main.isPVPEnabled(two);
	}
	
	public static boolean canRankUp(Tribe tribe) {
		return tribe.getBalance() >= ConfigFile.getRankUpPrice(tribe.getLevel() + 1);
	}
	
	public static List<String> getTribeListDescription() {
		List<String> desc = new ArrayList<String>();
		ChatColor color;
		int online;
		for(Tribe tribe : getTribes()) {
			online = 0;
			if(tribe.getType().equals("vanir")) color = ChatColor.RED;
			else color = ChatColor.YELLOW;
			for(String str : tribe.getMembers()) {
				if(Bukkit.getPlayer(str) != null) online++;
			}
			desc.add(color + tribe.getName() + ChatColor.GRAY + " " + online + "/" + tribe.getMembers().size() + " online, $" + tribe.getBalance());
		}
		return desc;
	}
	
	public Main getPlugin() {	
		return pl;
	}
}
