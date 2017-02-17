package com.nekrosius.asgardascension.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.PrestigeType;

public class PlayerManager {
	
	Map<String, Integer> rank;
	Map<String, Integer> prestige;
	Map<String, Integer> tokens;
	
	Main plugin;
	public PlayerManager(Main plugin) {
		this.plugin = plugin;
		rank = new HashMap<>();
		prestige = new HashMap<>();
		tokens = new HashMap<>();
	}
	
	public void loadData(Player player) {
		plugin.getPlayerFile().createConfig(player);
		setRank(player, plugin.getPlayerFile().getRank());
		setPrestige(player, plugin.getPlayerFile().getPrestige(), PrestigeType.HIDE);
		setTokens(player, plugin.getPlayerFile().getGodTokens());
	}
	
	public void saveData(Player player) {
		plugin.getPlayerFile().createConfig(player);
		plugin.getPlayerFile().setRank(getRank(player));
		plugin.getPlayerFile().setPrestige(getPrestige(player));
		plugin.getPlayerFile().setGodTokens(getTokens(player));
		plugin.getPlayerFile().saveConfig();
		
		rank.remove(player.getName());
		prestige.remove(player.getName());
		tokens.remove(player.getName());
	}
	
	public int getRank(Player player) {
		if(rank.get(player.getName()) == null)
			return 0;
		return rank.get(player.getName());
	}
	
	public void setRank(Player player, int rank) {
		this.rank.put(player.getName(), rank);
	}
	
	public int getPrestige(Player player) {
		if(prestige.get(player.getName()) == null)
			return 0;
		return prestige.get(player.getName());
	}
	
	public void setPrestige(Player player, int prestige, PrestigeType type) {
		this.prestige.put(player.getName(), prestige);
		if(!type.equals(PrestigeType.HIDE))
			plugin.getLogs().log(player.getName() + " prestige set to " + prestige + " (" + type.toString() + ")");
	}
	
	public int getTokens(Player player) {
		if(player == null)
			return 0;
		if(tokens.get(player.getName()) == null)
			return 0;
		return tokens.get(player.getName());
	}
	
	public void setTokens(Player player, int tokens) {
		this.tokens.put(player.getName(), tokens);
	}
	
	public void withdrawTokens(Player player, int tokens) {
		this.tokens.put(player.getName(), this.tokens.get(player.getName()) - tokens);
	}
	
	public void addTokens(Player player, int tokens) {
		this.tokens.put(player.getName(), this.tokens.get(player.getName()) + tokens);
	}
	
	public boolean hasTokens(Player player, int tokens) {
		return this.tokens.get(player.getName()) >= tokens;
	}

}
