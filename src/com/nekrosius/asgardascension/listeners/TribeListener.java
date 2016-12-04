package com.nekrosius.asgardascension.listeners;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.managers.TribeManager;
import com.nekrosius.asgardascension.objects.Tribe;
import com.nekrosius.asgardascension.utils.Cooldowns;
import com.nekrosius.asgardascension.utils.TribeUtils;

public class TribeListener implements Listener {
	
	private Main plugin;
	private TribeUtils util;
	
	public TribeListener(Main plugin) {
		this.plugin = plugin;
		util = new TribeUtils();
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent event) {
		if(event.getEntityType().equals(EntityType.FIREBALL)){
			Fireball fb = (Fireball) event.getEntity();
			if(fb.hasMetadata("ultimate")){
				int level = fb.getMetadata("ultimate").get(0).asInt();
				double directDamage = util.getFireballDamageByTribeLevel(level, true);
				double explosionDamage = util.getFireballDamageByTribeLevel(level, false);
				Player victim;
				for(Entity e : fb.getNearbyEntities(0.5, 0.5, 0.5)){
					if(e instanceof Player){
						victim = (Player) e;
						if(directDamage == 0){
							victim.setHealth(0);
						}
						else if(victim.getHealth() >= directDamage){
							victim.setHealth(victim.getHealth() - directDamage);
						}else{
							victim.setHealth(0);
						}
					}
				}
				for(Entity e : fb.getNearbyEntities(5, 5, 5)){
					if(e instanceof Player){
						victim = (Player) e;
						if(victim.getHealth() >= explosionDamage){
							victim.setHealth(victim.getHealth() - explosionDamage);
						}else{
							victim.setHealth(0);
						}
					}
				}
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(event.getPlayer().getInventory().getItemInMainHand() == null) {
				return;
			}
			if(!event.getPlayer().isSneaking()){
				return;
			}
			if(!TribeManager.hasTribe(event.getPlayer().getName())){
				return;
			}
			if(!Main.isPVPEnabled(event.getPlayer())) {
				return;
			}
			if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WOOD_SWORD) 
					|| event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STONE_SWORD) 
					|| event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD)
					|| event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD) ) {
				Player player = event.getPlayer();
				event.setCancelled(true);
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				if(Cooldowns.getCooldown(player, "action") <= 0) {
					if(tribe.getLevel() == 2) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 0));
						Cooldowns.setCooldown(player, "action", 7200000);
						player.sendMessage(TribeManager.mh + "You've used Tribe's Action skill!");
					}
					else if(tribe.getLevel() == 3) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 0));
						Cooldowns.setCooldown(player, "action", 7200000);
						player.sendMessage(TribeManager.mh + "You've used Tribe's Action skill!");
					}
					else if(tribe.getLevel() == 4) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 600, 0));
						Cooldowns.setCooldown(player, "action", 7200000);
						player.sendMessage(TribeManager.mh + "You've used Tribe's Action skill!");
					}
					else if(tribe.getLevel() == 5) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 1));
						Cooldowns.setCooldown(player, "action", 7200000);
						player.sendMessage(TribeManager.mh + "You've used Tribe's Action skill!");
					}
				}else{
					player.sendMessage(TribeManager.mh + "You can use Tribe's Action skill in " + Cooldowns.getCooldown(player, "action") / 60000 + " min.");
				}
			}
			else if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GOLD_SWORD)) {
				if(!(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "Ultimate Skill"))){
					return;
				}
				if(!TribeManager.hasTribe(event.getPlayer().getName())) {
					return;
				}
				if(!Main.isPVPEnabled(event.getPlayer())) {
					return;
				}
				Player player = event.getPlayer();
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				if(Cooldowns.getCooldown(player, "ultimate") >= 0){
					player.sendMessage(TribeManager.mh + "You can use Tribe's Ultimate skill in " + Cooldowns.getCooldown(player, "ultimate") / 60000 + " min.");
					return;
				}
				if(tribe.getType().equalsIgnoreCase("aesir")) {
					@SuppressWarnings("deprecation")
					Location loc = util.getCenterLocation(player.getLocation(), player.getTargetBlock(new HashSet<Byte>(), 5).getLocation());
					double smashDamage = util.getSmashDamageByTribeLevel(tribe.getLevel());
					for(Player p : util.getNearbyPlayers(loc, 2.5)){
						if(p != player) {
							if(p.getHealth() >= smashDamage){
								p.setHealth(p.getHealth() - smashDamage);
							}else{
								p.setHealth(0);
							}
						}
					}
					player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
					if(tribe.getLevel() != 5) {
						Cooldowns.setCooldown(player, "ultimate", 3600000);
					} else {
						Cooldowns.setCooldown(player, "ultimate", 1800000);
					}
					
				}
				else if(tribe.getType().equalsIgnoreCase("vanir")) {
					Fireball fb = player.launchProjectile(Fireball.class);
					fb.setMetadata("ultimate", new FixedMetadataValue(plugin, tribe.getLevel()));
					Cooldowns.setCooldown(player, "ultimate", 3600000);
				}
			}
		}
	}

}
