package com.nekrosius.asgardascension.handlers;

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

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.files.RagnorakFile;
import com.nekrosius.asgardascension.utils.Convert;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.ShieldEffect;

public class Ragnorak {
	
	/*
	private static int votes = 0;
	public static int minutes = 0;
	private static boolean started = false;
	private static boolean cooldown = false;
	private static Map<String, Boolean> voted = new HashMap<String, Boolean>();
	*/
	
	public boolean voteStarted;
	public boolean eventStarted;
	public int minutesLeft = -1;
	public Map<String, Boolean> voted;
	
	private static List<Effect> effects;
	
	public static String mh = ChatColor.GRAY + "[" + ChatColor.RED + "Asgard Ragnorak" + ChatColor.GRAY + "] ";
	
	private Main plugin;
	public Ragnorak(Main plugin) {
		this.plugin = plugin;
		voted = new HashMap<String, Boolean>();
		voteStarted = false;
		eventStarted = false;
		minutesLeft = RagnorakFile.getTimerAfterRestart();
		startTimer();
	}
	
	public void addVote(Player player) {
		if(eventStarted) {
			player.sendMessage(mh + "Ragnorak is already in progress!");
			return;
		}
		if(!voteStarted) {
			player.sendMessage(mh + "Voting is currently not in progress!" + 
					" The voting will start in " + minutesLeft + " min.");
			return;
		}
		if(voted.containsKey(player.getName())) {
			player.sendMessage(mh + "You've already voted! Current progress is " + voted.size() + "/" + Bukkit.getOnlinePlayers().size());
			return;
		}
		voted.put(player.getName(), true);
		int required = Math.max(Bukkit.getOnlinePlayers().size() / 2 - 1, RagnorakFile.getMinimumAmountOfPlayers());
		player.sendMessage(mh + "You've sucessfully voted! (" + voted.size() + "/" + required + ")");
		checkForTrigger();
	}
	
	private void startTimer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				minutesLeft--;
				if(minutesLeft == 0) {
					startVoting();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1200L, 1200L);
	}
	
	private void startVoting() {
		voteStarted = true;
		final int minCount = RagnorakFile.getMinimumAmountOfPlayers();
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(mh + "Are you prepared for epic Ragnorak? Type /ragnorak to vote!");
			p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1F, 1F);
		}
		
		// Voting period
		new BukkitRunnable() {
			@Override
			public void run() {
				if(eventStarted || !voteStarted) {
					this.cancel();
					return;
				}
				minutesLeft = RagnorakFile.getTimerAfterUnsuccessfulVote();
				for(Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(mh + "Voting has been unsuccessful! Next voting will begin in " + minutesLeft + " min.");
				}
				eventStarted = false;
				voteStarted = false;
				startTimer();
			}		
		}.runTaskLater(plugin, RagnorakFile.getTimerForVotingPeriod() * 1200);
		
		// Display information about current vote every 20 seconds
		new BukkitRunnable() {
			int previousCount = -1;
			@Override
			public void run() {
				if(eventStarted || !voteStarted) {
					this.cancel();
					return;
				}
				if(voted.size() != previousCount) {		
					previousCount = voted.size();
					int required = Bukkit.getOnlinePlayers().size() / 2 - 1;
					required = Math.max(required, minCount);
					String base = mh + previousCount + "/" + required + " players have already voted!";
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(!hasVoted(p)) {
							base += " Type /ragnorak to vote!";
						}
						p.sendMessage(base);
					}
				}
			}
		}.runTaskTimer(plugin, 400, 400);
	}
	
	private void checkForTrigger() {
		int required = Math.max(Bukkit.getOnlinePlayers().size() / 2 - 1, RagnorakFile.getMinimumAmountOfPlayers());
		if(voted.size() >= required) {
			start();
		}
	}
	
	private boolean hasVoted(Player player) {
		return voted.containsKey(player.getName());
	}
	
	public void start() {
		effects = new ArrayList<Effect>();
		
		if(eventStarted){
			return;
		}
		voted.clear();
		eventStarted = true;
		voteStarted = false;
	    
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
		
		minutesLeft = 0;
		new BukkitRunnable() {
			public void run() {
				if(minutesLeft >= RagnorakFile.getDuration()) {
					eventStarted = false;
					minutesLeft = RagnorakFile.getTimerAfterSuccessfulVote();
					startTimer();
					cancel();
				}
				minutesLeft++;
			}
		}.runTaskTimer(plugin, 1200, 1200);
		
	}
	
}
