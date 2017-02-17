package com.nekrosius.asgardascension.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.effect.CloudEffect;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.effect.LoveEffect;
import de.slikey.effectlib.util.ParticleEffect;

public class RuneManager {
	
	List<Rune> runes;
	List<UUID> invisiblePlayers;
	Map<UUID, Rune> activeRunes;
	Map<UUID, Effect> effect;
	
	Main plugin;
	public RuneManager(Main plugin) {
		this.plugin = plugin;
		
		runes = new ArrayList<>();
		invisiblePlayers = new ArrayList<>();
		
		activeRunes = new HashMap<>();
		effect = new HashMap<>();
		
		registerRunes();
	}
	
	public void start(Player player, Rune rune) {
		startEffect(player, rune);
		setActiveRune(player, rune);
		switch(rune.getName()) {
			case "Freeze":
				handleFreeze(player, rune);
				return;
			case "Fire Storm":
				handleFireStorm(player, rune);
				break;
			case "Slowdown":
				handleSlowdown(player, rune);
				break;
			case "Invisibility":
				handleInvisibility(player, rune);
				break;
			case "Lightning":
				handleLightning(player, rune);
				break;
			case "Detonate":
				handleDetonate(player, rune);
				break;
			default:
				return;
		}
		player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		player.sendMessage(Lang.HEADERS_TOKENS.toString() + Lang.TOKENS_RUNE_APPLY.toString()
			.replaceAll("%s", rune.getName()));
	}
	
	public void finish(Player player, boolean message) {
		if(!player.isOnline())
			return;
		if(message) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString() + Lang.TOKENS_RUNE_EXPIRE.toString()
					.replaceAll("%s", getActiveRune(player).getName()));
		}
		
		if(getActiveRune(player).getName().equals("Invisibility")) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(player);
			}
			removeInvisiblePlayer(player);
		}
		
		Cooldowns.setCooldown(player, "rune", 15000);
		removeActiveRune(player);
		removeEffect(player);
	}
	
	private void handleFreeze(Player player, Rune rune) {
		Player target = Utility.getTargetPlayer(player, 10);
		if(target == null || !Utility.canAttack(player, target)) {
			return;
		}
		
		LineEffect eff = new LineEffect(plugin.getEffectManager());
		eff.setEntity(player);
		eff.setTargetEntity(target);
		eff.particle = ParticleEffect.SNOW_SHOVEL;
		eff.start();
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 6));
		target.playSound(target.getLocation(), Sound.ENTITY_SNOWMAN_DEATH, 1F, 1F);
		
		player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		player.sendMessage(Lang.HEADERS_TOKENS.toString() + Lang.TOKENS_RUNE_APPLY.toString()
			.replaceAll("%s", rune.getName()));
		target.sendMessage(Lang.HEADERS_TOKENS.toString() + Lang.TOKENS_FROZEN.toString()
				.replaceAll("%s", player.getName()));
		finish(player, false);
	}
	
	private void handleFireStorm(Player player, Rune rune) {
		new BukkitRunnable() {
			int iterations = 0;
			public void run() {
				if(!player.isOnline() || iterations / 2 >= rune.getDuration()) {
					finish(player, true);
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
				iterations++;
			}
		}.runTaskTimer(plugin, 0L, 10L);
	}
	
	private void handleSlowdown(Player player, Rune rune) {
		new BukkitRunnable() {
			int iterations = 0;
			public void run() {
				if(!player.isOnline() || iterations / 2 >= rune.getDuration()) {
					finish(player, true);
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
				iterations++;
			}
		}.runTaskTimer(plugin, 0L, 10L);
	}
	
	private void handleInvisibility(Player player, Rune rune) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.hidePlayer(player);
		}
		new BukkitRunnable() {
			public void run() {
				if(player != null && player.isOnline()) {
					finish(player, true);
				}
			}
		}.runTaskLater(plugin, 100L);
	}
	
	private void handleLightning(Player player, Rune rune) {
		@SuppressWarnings("deprecation")
		Block block = player.getTargetBlock((HashSet<Byte>) null, 15);
		if(!Utility.isPVPEnabled(block.getLocation())) {
			return;
		}
		
		block.getWorld().strikeLightning(block.getLocation());
		for(Entity e : block.getWorld().getEntities()) {
			if(!(e instanceof Player)) {
				continue;
			}
			Player target = (Player) e;
			if(!Utility.canAttack(player, target)) {
				continue;
			}
			double distance = target.getLocation().distance(block.getLocation());
			double damage = 0;
			if(distance < 1) {
				damage = 16;
			}
			else if(distance <= 3) {
				damage = 10;
			}
			
			if(damage < 1)
				continue;
			
			if(target.getHealth() > damage)
				target.setHealth(target.getHealth() - damage);
			else
				target.damage(1000000, player);
			target.sendMessage(Lang.HEADERS_TOKENS.toString() + Lang.TOKENS_LIGHTNING.toString()
				.replaceAll("%s", player.getName()));
		}
		finish(player, false);
	}
	
	private void handleDetonate(Player player, Rune rune) {
		player.spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);
		player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1F, 1F);
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
		finish(player, false);
	}
	
	private void registerRunes() {
		Rune rune;
		
		rune = new Rune("Freeze", Material.ICE, 15, -1);
		rune.addDescription(ChatColor.GRAY + "Freezes target");
		rune.addDescription(ChatColor.GRAY + "Range: " + ChatColor.RED + "10 blocks");
		rune.addDescription(ChatColor.GRAY + "Freeze duration: " + ChatColor.RED + "3 second");
		runes.add(rune);
		
		rune = new Rune("Fire Storm", Material.BLAZE_POWDER, 25, 30);
		rune.addDescription(ChatColor.GRAY + "Sets everyone around on fire");
		rune.addDescription(ChatColor.GRAY + "Range: " + ChatColor.RED + "2 blocks");
		rune.addDescription(ChatColor.GRAY + "Burning duration: " + ChatColor.RED + "2 seconds");
		runes.add(rune);
		
		rune = new Rune("Slowdown", Material.WEB, 15, 30);
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
	
	public void startEffect(Player player, Rune rune) {
		if("Fire Storm".equals(rune.getName())) {
			AnimatedBallEffect eff = new AnimatedBallEffect(plugin.getEffectManager());
			eff.setEntity(player);
			eff.particle = ParticleEffect.FLAME;
			eff.size = 0.7F;
			eff.speed = 0.2F;
			eff.yOffset = -0.8F;
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
		else if("Slowdown".equals(rune.getName())) {
			AnimatedBallEffect eff = new AnimatedBallEffect(plugin.getEffectManager());
			eff.setEntity(player);
			eff.particle = ParticleEffect.SNOW_SHOVEL;
			eff.size = 0.7F;
			eff.speed = 0.2F;
			eff.yOffset = -0.8F;
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
	}
	
	public void startHoldingEffect(Player player, Rune rune) {
		if(rune.getName().equals("Freeze")) {
			LoveEffect eff = new LoveEffect(plugin.getEffectManager());
			eff.particle = ParticleEffect.SPELL_WITCH;
			eff.setEntity(player);
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
		else if(rune.getName().equals("Lightning")) {
			CloudEffect eff = new CloudEffect(plugin.getEffectManager());
			eff.yOffset = 1.5;
			eff.setEntity(player);
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
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
	
	public List<UUID> getInvisiblePlayers() {
		return invisiblePlayers;
	}
	
	public boolean isInvisible(Player player) {
		return invisiblePlayers.contains(player.getUniqueId());
	}
	
	public void addInvisiblePlayer(Player player) {
		invisiblePlayers.add(player.getUniqueId());
	}
	
	public void removeInvisiblePlayer(Player player) {
		invisiblePlayers.remove(player.getUniqueId());
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
	}
	
	public Effect getEffect(Player player) {
		return effect.get(player.getUniqueId());
	}
	
	public void setEffect(Player player, Effect effect) {
		this.effect.put(player.getUniqueId(), effect);
	}
	
	public void removeEffect(Player player) {
		if(getEffect(player) != null)
			getEffect(player).cancel();
		effect.remove(player.getUniqueId());
	}
	
}
