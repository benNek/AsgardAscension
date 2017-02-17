package com.nekrosius.asgardascension.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.objects.Rune;
import com.nekrosius.asgardascension.utils.Cooldowns;
import com.nekrosius.asgardascension.utils.Utility;

public class RuneManager {
	
	List<Rune> runes;
	HashMap<UUID, Rune> activeRunes;
	
	Main plugin;
	public RuneManager(Main plugin) {
		this.plugin = plugin;
		runes = new ArrayList<>();
		activeRunes = new HashMap<>();
		registerRunes();
	}
	
	public void startRune(Player player, Rune rune) {
		switch(rune.getName()) {
			case "Fire Storm":
				handleFireStorm(player, rune);
				break;
			case "Slowdown":
				handleSlowdown(player, rune);
				break;
			case "Detonate":
				handleDetonate(player, rune);
				break;
			default:
				return;
		}
		player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		player.sendMessage(Lang.HEADERS_TOKENS.toString() + Lang.TOKENS_APPLY_RUNE.toString()
			.replaceAll("%s", rune.getName()));
	}
	
	private void handleFireStorm(Player player, Rune rune) {
		plugin.getRuneManager().setActiveRune(player, rune);
		new BukkitRunnable() {
			int iterations = 0;
			public void run() {
				if(!player.isOnline() || iterations * 2 >= rune.getDuration()) {
					this.cancel();
					return;
				}
				
				for(Entity entity : player.getNearbyEntities(2D, 2D, 2D)) {
					if(!(entity instanceof Player)) {
						continue;
					}
					Player target = (Player) entity;
					
					if(Utility.canAttack(player, target)) {
						target.setFireTicks(40);
					}
				}
			}
		}.runTaskTimer(plugin, 10L, 10L);
	}
	
	private void handleSlowdown(Player player, Rune rune) {
		plugin.getRuneManager().setActiveRune(player, rune);
		new BukkitRunnable() {
			int iterations = 0;
			public void run() {
				if(!player.isOnline() || iterations * 2 >= rune.getDuration()) {
					this.cancel();
					return;
				}
				
				for(Entity entity : player.getNearbyEntities(3D, 3D, 3D)) {
					if(!(entity instanceof Player)) {
						continue;
					}
					Player target = (Player) entity;
					
					if(Utility.canAttack(player, target)) {
						target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
					}
				}
			}
		}.runTaskTimer(plugin, 10L, 10L);
	}
	
	private void handleDetonate(Player player, Rune rune) {
		for(Entity entity : player.getNearbyEntities(3D, 3D, 3D)) {
			if(!(entity instanceof Player)) {
				continue;
			}
			Player target = (Player) entity;
			
			if(Utility.canAttack(player, target)) {
				if(target.getHealth() > 6) {
					target.setHealth(target.getHealth() - 6);
				}
				else {
					target.damage(1000000D, player);
				}
			}
		}
		Cooldowns.setCooldown(player, "rune", 15000);
	}
	
	private void registerRunes() {
		Rune rune;
		
		rune = new Rune("Freeze", Material.ICE, 15, -1);
		rune.addDescription(ChatColor.GRAY + "Freezes target");
		rune.addDescription(ChatColor.GRAY + "Range: " + ChatColor.RED + "5 blocks");
		rune.addDescription(ChatColor.GRAY + "Freeze duration: " + ChatColor.RED + "1 second");
		runes.add(rune);
		
		rune = new Rune("Fire Storm", Material.BLAZE_POWDER, 25, 30);
		rune.addDescription(ChatColor.GRAY + "Sets everyone around on fire");
		rune.addDescription(ChatColor.GRAY + "Range: " + ChatColor.RED + "2 blocks");
		rune.addDescription(ChatColor.GRAY + "Burning duration: " + ChatColor.RED + "2 seconds");
		runes.add(rune);
		
		rune = new Rune("Slowdown", Material.WEB, 15, -1);
		rune.addDescription(ChatColor.GRAY + "Slows everyone around");
		rune.addDescription(ChatColor.GRAY + "Range: " + ChatColor.RED + "3 blocks");
		rune.addDescription(ChatColor.GRAY + "Slow Level: " + ChatColor.RED + "I");
		rune.addDescription(ChatColor.GRAY + "Slow duration: " + ChatColor.RED + "5 seconds");
		runes.add(rune);
		
		rune = new Rune("Invisibility", Material.GRASS, 10, 5);
		rune.addDescription(ChatColor.GRAY + "Gives uninterruptible invisibility");
		runes.add(rune);
		
		rune = new Rune("Lightning", Material.FIREWORK, 15, -1);
		rune.addDescription(ChatColor.GRAY + "Strikes lightning at target position");
		rune.addDescription(ChatColor.GRAY + "Range: " + ChatColor.RED + "20 blocks");
		rune.addDescription(ChatColor.GRAY + "Damage: " + ChatColor.RED + "5 hearts");
		runes.add(rune);
		
		rune = new Rune("Detonate", Material.TNT, 20, -1);
		rune.addDescription(ChatColor.GRAY + "Strikes explosion at your location");
		rune.addDescription(ChatColor.GRAY + "Explosion range: " + ChatColor.RED + "4 blocks");
		rune.addDescription(ChatColor.GRAY + "Damage: " + ChatColor.RED + "3 hearts");
		runes.add(rune);
	}
	
	public List<Rune> getRunes() {
		return runes;
	}
	
	public Rune getRune(String name) {
		for(Rune rune : runes) {
			if(rune.getName().equals(name)) {
				return rune;
			}
		}
		return null;
	}
	
	public boolean hasActiveRune(Player player) {
		return activeRunes.get(player.getUniqueId()) != null;
	}
	
	public Rune getActiveRune(Player player) {
		return activeRunes.get(player.getUniqueId());
	}
	
	public void setActiveRune(Player player, Rune rune) {
		activeRunes.put(player.getUniqueId(), rune);
	}
	
	public void removeActiveRune(Player player) {
		activeRunes.remove(player.getUniqueId());
		Cooldowns.setCooldown(player, "rune", 15000);
	}
	
}
