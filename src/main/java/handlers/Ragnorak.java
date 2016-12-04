package main.java.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.ShieldEffect;
import main.java.Main;
import main.java.files.RagnorakFile;
import main.java.utils.Convert;

public class Ragnorak {
	
	private static Main plugin = (Main)Bukkit.getPluginManager().getPlugin("AsgardAscension");
	
	private static int votes = 0;
	public static int minutes = 0;
	private static boolean started = false;
	private static boolean cooldown = false;
	private static Map<String, Boolean> voted = new HashMap<String, Boolean>();
	
	private static List<Effect> effects;
	
	public static String mh = ChatColor.GRAY + "[" + ChatColor.RED + "Asgard Ragnorak" + ChatColor.GRAY + "] ";
	
	public static void start() {
		effects = new ArrayList<Effect>();
		if(hasStarted()){
			return;
		}
		voted = new HashMap<String, Boolean>();
		setStarted(true);
	    
		/* TODO find alternative
		TitleBar title = new TitleBar(ChatColor.RED + "" + ChatColor.BOLD + "Ragnorak!",
				ChatColor.GRAY + "Get to the pvp zone to get awesome loot!");
		title.broadcast();
		*/
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 1F, 1F);
			p.sendMessage(mh + "Ragnorak" + ChatColor.GRAY + " has started!");
		}
		for(String locString : RagnorakFile.getLocations()){
			Location loc = Convert.StringToLocation(locString);
			loc.getBlock().setType(Material.CHEST);
			ShieldEffect he = new ShieldEffect(Main.em);
			he.setLocation(loc);
			he.start();
			effects.add(he);
		}
		for(int itemIndex = 0; itemIndex < RagnorakFile.getItemsAmount(); itemIndex++){
			Location loc = RagnorakFile.getLocation();
			if(loc.getBlock().getState() instanceof Chest){
				Chest block = (Chest) loc.getBlock().getState();
				if(RagnorakFile.getItem(itemIndex) == null) continue;
				block.getInventory().addItem(RagnorakFile.getItem(itemIndex));
			}
		}
		new BukkitRunnable(){
			public void run(){
				for(Player p : Bukkit.getOnlinePlayers()){
					p.sendMessage(mh + "Ragnorak has finished!");
				}
				for(String locString : RagnorakFile.getLocations()){
					Location lloc = Convert.StringToLocation(locString);
					if(lloc.getBlock().getState() instanceof Chest) {
						Chest chest = (Chest) lloc.getBlock().getState();
						chest.getInventory().clear();
					}
					lloc.getBlock().setType(Material.AIR);
				}
			}
		}.runTaskLater(plugin, 2400);
		setStarted(false);
		cooldown = true;
		new BukkitRunnable() {
			public void run() {
				if(minutes >= RagnorakFile.getTime()) {
					cooldown = false;
					minutes = 0;
					cancel();
				}
				minutes++;
			}
		}.runTaskTimer(plugin, 0, 1200);
	}
	
	public static boolean canStart() {
		return votes >= Bukkit.getOnlinePlayers().size() * RagnorakFile.getPercentageMultiplier();
	}
	
	public static boolean hasStarted() {
		return started;
	}
	
	public static void setStarted(boolean status) {
		started = status;
	}

	public static int getVotes() {
		return votes;
	}
	
	public static void addVote() {
		votes++;
	}

	public static void setVotes(int vot) {
		votes = vot;
	}
	
	public static boolean hasVoted(String player) {
		if(voted.get(player) == null) return false;
		return voted.get(player);
	}
	
	public static void setVoted(String player, boolean status) {
		voted.put(player, status);
		if(status){
			addVote();
			if(canStart()) {
				start();
			}
		}
	}
	
	public static boolean onCooldown() {
		return cooldown;
	}
	
	public static void setOnCooldown(boolean status) {
		cooldown = status;
	}

	// 80 percents of player
	
}
