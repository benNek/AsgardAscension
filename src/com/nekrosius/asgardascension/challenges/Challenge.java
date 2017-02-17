package com.nekrosius.asgardascension.challenges;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.utils.Convert;

public class Challenge {
	
	private Map<String, Integer> playerChallenge = new HashMap<>();
	private Map<String, Location> playerLocation = new HashMap<>();
	private Map<String, Boolean> testing = new HashMap<>();
	private Map<String, ItemStack[]> inventory = new HashMap<>();
	private Map<String, ItemStack[]> armor = new HashMap<>();
	private Map<String, Integer> killsLeft = new HashMap<>();
	private Map<String, Float> exp = new HashMap<>();
	private Map<String, Integer> level = new HashMap<>();
	
	private String metaData = "challenge";
	
	Main plugin;
	public Challenge(Main plugin) {
		this.plugin = plugin;
	}

	public void startChallenge(final Player player) {
		final int challenge;
		if(isTesting(player)) {
			challenge = getChallenge(player);
		} else {
			challenge = plugin.getPlayerManager().getRank(player) + 1;
		}
		long price = plugin.getChallengesFile().getPrice(challenge) * (plugin.getPlayerManager().getPrestige(player) + 1);
		if(!plugin.getEconomy().has(player, price)) {
			player.sendMessage(Lang.HEADERS_CHALLENGES.toString() +
					Lang.CHALLENGES_NOT_ENOUGH_MONEY.toString().replaceAll("%d", Convert.toPrice(price)));
			return;
		}
		plugin.getEconomy().withdrawPlayer(player, price);
		saveData(player, challenge, price);
		player.teleport(plugin.getChallengesFile().getSpawnpoint(challenge));
		if("fight".equalsIgnoreCase(plugin.getChallengesFile().getType(challenge))) {
			new BukkitRunnable() {
				public void run() {
					if(plugin.getChallengesFile().getMonsters(challenge) != null) {
						String[] split;
						for(String mob : plugin.getChallengesFile().getMonsters(challenge)) {
							split = mob.split(", ");
							addMobs(player, Integer.parseInt(split[1]));
							Convert.spawnEntity(player.getName(), challenge, split[0], Integer.parseInt(split[1]));
						}
					}
				}
			}.runTaskLater(plugin, 20L);
		}
		if("fight".equalsIgnoreCase(plugin.getChallengesFile().getType(challenge))) {
			player.setLevel(getKillsLeft(player));
			player.setExp(0F);
		}
		player.sendMessage(Lang.HEADERS_CHALLENGES.toString()
				+ Lang.CHALLENGES_START.toString()
					.replaceAll("%s", plugin.getChallengesFile().getTitle(challenge))
					.replaceAll("%d", Convert.toPrice(price)));
	}
	
	public void finishChallenge(Player player, boolean buy) {
		if(!buy) {
			for(Entity e : player.getWorld().getEntities()) {
				if(e.hasMetadata(metaData) && e.getMetadata(metaData).get(0).asString().contains(player.getName())) {
					e.remove();
				}
			}
			player.teleport(plugin.getChallengesFile().getVictorySpawnpoint(getChallenge(player)));
			loadData(player);
			player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE, 1F, 1F);
		}
		for(String cmd : plugin.getChallengesFile().getCommands(getChallenge(player))) {
			cmd = cmd.replaceAll("%s", player.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
		if(!isTesting(player)) {
			plugin.getPlayerManager().setRank(player, getChallenge(player));
			player.sendMessage(Lang.HEADERS_CHALLENGES.toString() 
					+ Lang.CHALLENGES_COMPLETE.toString()
						.replaceAll("%s", plugin.getChallengesFile().getTitle(getChallenge(player))));
		} else {
			player.sendMessage(Lang.HEADERS_CHALLENGES.toString()
					+ Lang.CHALLENGES_TEST_COMPLETE.toString());
		}
		playerChallenge.remove(player.getName());
	}
	
	public void quitChallenge(Player player) {
		for(Entity e : player.getWorld().getEntities()){
			if(e.hasMetadata("") && e.getMetadata(metaData).get(0).asString().contains(player.getName())) {
				e.remove();
			}
		}
		player.teleport(getLocation(player));
		if(!isTesting(player)){
			plugin.getEconomy().depositPlayer(player, plugin.getChallengesFile().getPrice(getChallenge(player)) 
					* (plugin.getPlayerManager().getPrestige(player) + 1));
			player.sendMessage(Lang.HEADERS_CHALLENGES.toString()
					+ Lang.CHALLENGES_LEAVE.toString()
						.replaceAll("%s", plugin.getChallengesFile().getTitle(getChallenge(player))));
		} else {
			player.sendMessage(Lang.HEADERS_CHALLENGES.toString()
					+ Lang.CHALLENGES_TEST_COMPLETE.toString());
		}
		loadData(player);
		playerChallenge.remove(player.getName());
	}
	
	private void saveData(Player player, int challenge, long price) {
		setChallenge(player, challenge);
		setLocation(player, player.getLocation());
		setLevel(player);
		plugin.getPlayerFile().createConfig(player);
		plugin.getPlayerFile().setChallengeLocation(player.getLocation());
		plugin.getPlayerFile().setChallengeLevel(player.getLevel());
		plugin.getPlayerFile().setChallengeExperience(player.getExp());
		plugin.getPlayerFile().setChallengePrice(price);
		plugin.getPlayerFile().saveConfig();
	}
	
	private void loadData(Player player) {
		player.setFireTicks(0);
		player.setHealth(20);
		player.setLevel(level.get(player.getName()));
		player.setExp(exp.get(player.getName()));
		playerLocation.remove(player.getName());
		testing.remove(player.getName());
		armor.remove(player.getName());
		inventory.remove(player.getName());
		killsLeft.remove(player.getName());
		exp.remove(player.getName());
		level.remove(player.getName());
		plugin.getPlayerFile().createConfig(player);
		plugin.getPlayerFile().removeChallenge();
		plugin.getPlayerFile().saveConfig();
	}
	
	public void setChallenge(Player player, int challenge) {
		playerChallenge.put(player.getName(), challenge);
	}
	
	public int getChallenge(Player player) {
		if(playerChallenge.get(player.getName()) == null)
			return 0;
		return playerChallenge.get(player.getName());
	}
	
	public void setLocation(Player player, Location loc) {
		playerLocation.put(player.getName(), loc);
	}
	
	public Location getLocation(Player player) {
		return playerLocation.get(player.getName());
	}
	
	public void setTesting(Player player, boolean bool) {
		testing.put(player.getName(), bool);
	}
	
	public boolean isTesting(Player player) {
		if(testing.get(player.getName()) == null)
			return false;
		return testing.get(player.getName());
	}
	
	public void saveInventory(Player player) {
		inventory.put(player.getName(), player.getInventory().getContents());
		armor.put(player.getName(), player.getInventory().getArmorContents());
	}
	
	public void loadInventory(Player player) {
		if(inventory.get(player.getName()) != null)
			player.getInventory().setContents(inventory.get(player.getName()));
		if(armor.get(player.getName()) != null)
			player.getInventory().setArmorContents(armor.get(player.getName()));
	}
	
	public void setKillsLeft(Player player, int kills) {
		killsLeft.put(player.getName(), kills);
	}
	
	public void addMobs(Player player, int mobs) {
		if(killsLeft.get(player.getName()) == null)
			killsLeft.put(player.getName(), 0);
		killsLeft.put(player.getName(), killsLeft.get(player.getName()) + mobs);
	}
	
	public void addKill(Player player) {
		if(player == null || !player.isOnline())
			return;
		killsLeft.put(player.getName(), killsLeft.get(player.getName()) - 1);
	}
	
	public int getKillsLeft(Player player) {
		if(killsLeft.get(player.getName()) == null)
			return 0;
		return killsLeft.get(player.getName());
	}
	
	public void setLevel(Player player) {
		level.put(player.getName(), player.getLevel());
		exp.put(player.getName(), player.getExp());
	}
	
}
