package com.nekrosius.asgardascension.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.nekrosius.asgardascension.Main;

public class ChallengeListener implements Listener {
	
	private Main plugin;
	public ChallengeListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onNoteBlockInteract(PlayerInteractEvent event) {
		if(event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.NOTE_BLOCK)){
			if(plugin.getChallenges().getChallenge(event.getPlayer()) == 0)
				return;
			if("fight".equalsIgnoreCase(plugin.getChallengesFile().getType(plugin.getChallenges().getChallenge(event.getPlayer())))) {
				return;
			}
			if(plugin.getChallengesFile().getNoteblockLocation(plugin.getChallenges().getChallenge(event.getPlayer()))
					.equals(event.getClickedBlock().getLocation())){
				event.setCancelled(true);
				plugin.getChallenges().finishChallenge(event.getPlayer(), false);
			}
		}
	}
	
	@EventHandler
	public void onMobDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof Entity){
			Entity e = (Entity) event.getEntity();
			if(e.hasMetadata("challenge")){
				event.getDrops().clear();
				String[] meta = e.getMetadata("challenge").get(0).asString().split(", ");
				final String player = meta[1];
				plugin.getChallenges().addKill(Bukkit.getPlayer(player));
				Bukkit.getPlayer(player).setLevel(plugin.getChallenges().getKillsLeft(Bukkit.getPlayer(player)));
				if(e.getType().equals(EntityType.MAGMA_CUBE) || e.getType().equals(EntityType.SLIME)) {
					e.remove();
				}
				if(plugin.getChallenges().getKillsLeft(Bukkit.getPlayer(player)) == 0){
					plugin.getChallenges().finishChallenge(Bukkit.getPlayer(player), false);
				}
			}
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if(event.getEntity() instanceof Creeper) {
			Entity e = event.getEntity();
			if(e.hasMetadata("challenge")) {
				event.blockList().clear();
				String[] meta = e.getMetadata("challenge").get(0).asString().split(", ");
				final String player = meta[1];
				plugin.getChallenges().addKill(Bukkit.getPlayer(player));
				Bukkit.getPlayer(player).setLevel(plugin.getChallenges().getKillsLeft(Bukkit.getPlayer(player)));
				if(plugin.getChallenges().getKillsLeft(Bukkit.getPlayer(player)) == 0) {
					plugin.getChallenges().finishChallenge(Bukkit.getPlayer(player), false);
				}
			}
		}
	}
	
	@EventHandler
	public void onEXP(PlayerExpChangeEvent event) {
		if(plugin.getChallenges().getChallenge(event.getPlayer()) == 0)
			return;
		event.setAmount(0);
	}
	
	@EventHandler
	public void onDie(PlayerDeathEvent event) {
		if(plugin.getChallenges().getChallenge(event.getEntity()) == 0)
			return;
		plugin.getChallenges().saveInventory(event.getEntity());
		event.getDrops().clear();
		event.getEntity().spigot().respawn();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onRespawn(final PlayerRespawnEvent event) {
		if(plugin.getChallenges().getChallenge(event.getPlayer()) == 0)
			return;
		event.setRespawnLocation(plugin.getChallengesFile().getSpawnpoint(plugin.getChallenges().getChallenge(event.getPlayer())));
		new BukkitRunnable() {
			public void run() {
				plugin.getChallenges().loadInventory(event.getPlayer());
				event.getPlayer().setLevel(plugin.getChallenges().getKillsLeft(event.getPlayer()));
			}
		}.runTaskLater(plugin, 10L);
	}
	
	public Main getPlugin() {
		return plugin;
	}
	
}
