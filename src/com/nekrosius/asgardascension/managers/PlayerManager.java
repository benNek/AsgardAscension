package com.nekrosius.asgardascension.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.PrestigeType;

public class PlayerManager {
	
	Map<String, Integer> rank;
	Map<String, Integer> prestige;
	Map<String, Integer> tokens;
	Map<String, List<String>> purchased;
	
	Main pl;
	public PlayerManager(Main pl) {
		this.pl = pl;
		rank = new HashMap<String, Integer>();
		prestige = new HashMap<String, Integer>();
		tokens = new HashMap<String, Integer>();
		purchased = new HashMap<String, List<String>>();
	}
	
	public void loadData(Player player) {
		pl.getPlayerFile().createConfig(player);
		setRank(player, pl.getPlayerFile().getRank());
		setPrestige(player, pl.getPlayerFile().getPrestige(), PrestigeType.HIDE);
		setTokens(player, pl.getPlayerFile().getGodTokens());
		setPurchasedTokens(player, pl.getPlayerFile().getTokens());
	}
	
	public void saveData(Player player) {
		pl.getPlayerFile().createConfig(player);
		pl.getPlayerFile().setRank(getRank(player));
		pl.getPlayerFile().setPrestige(getPrestige(player));
		pl.getPlayerFile().setGodTokens(getTokens(player));
		pl.getPlayerFile().setTokens(getPurchasedTokens(player));
		pl.getPlayerFile().saveConfig();
		
		rank.remove(player.getName());
		prestige.remove(player.getName());
		tokens.remove(player.getName());
	}
	
	public int getRank(Player player) {
		return rank.get(player.getName());
	}
	
	public void setRank(Player player, int rank) {
		this.rank.put(player.getName(), rank);
	}
	
	public int getPrestige(Player player) {
		return prestige.get(player.getName());
	}
	
	public void setPrestige(Player player, int prestige, PrestigeType type) {
		this.prestige.put(player.getName(), prestige);
		if(!type.equals(PrestigeType.HIDE))
			pl.getLogs().log(player.getName() + " prestige set to " + prestige + " (" + type.toString() + ")");
	}
	
	public int getTokens(Player player) {
		if(tokens.get(player.getName()) == null) return 0;
		return tokens.get(player.getName());
	}
	
	public void setTokens(Player player, int tokens) {
		this.tokens.put(player.getName(), tokens);
	}
	
	public List<String> getPurchasedTokens(Player player) {
		return purchased.get(player.getName());
	}
	
	public boolean hasPurchasedToken(Player player, String token) {
		return purchased.get(player.getName()).contains(token);
	}
	
	public void addPurchasedToken(Player player, String token) {
		List<String> owned = getPurchasedTokens(player);
		owned.add(token);
		setPurchasedTokens(player, owned);
	}
	
	public void setPurchasedTokens(Player player, List<String> tokens) {
		purchased.put(player.getName(), tokens);
	}

}
