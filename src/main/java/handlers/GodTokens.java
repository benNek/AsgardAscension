package main.java.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.effect.LoveEffect;
import de.slikey.effectlib.effect.ShieldEffect;
import de.slikey.effectlib.util.ParticleEffect;
import main.java.Main;
import main.java.enums.TokenType;
import main.java.managers.TribeManager;
import main.java.objects.GodToken;
import main.java.utils.Cooldowns;
import main.java.utils.ItemStackGenerator;

public class GodTokens {
	
	private static Main plugin = (Main)Bukkit.getPluginManager().getPlugin("AsgardAscension");
	
	private static Map<String, String> skill = new HashMap<String, String>();
	private static Map<String, Effect> effect = new HashMap<String, Effect>();
	
	public static String mh = ChatColor.GRAY + "[" + ChatColor.RED + "Asgard Tokens" + ChatColor.GRAY + "] ";

	public static List<GodToken> tokens;
	
	public static void startSkill(final String player, final GodToken token) {
		setSkill(player, token.getName());
		startEffect(player, token.getName());
		Bukkit.getPlayer(player).sendMessage(mh + ChatColor.RED + token.getName() + ChatColor.GRAY + " is now activated!");
		
		if(token.getDuration() != -1) {
			new BukkitRunnable() {
				public void run() {
					finish(player);
				}
			}.runTaskLater(plugin, token.getDuration() * 20);
		}
		
		final String skill = token.getName();
		final Player p = Bukkit.getPlayer(player);
		if(skill.equalsIgnoreCase("Fire")) {
			final double radius = 5;
			new BukkitRunnable() {
				public void run() {
					if(getSkill(player) == null) this.cancel();
					else if(!getSkill(player).equals("Fire")) this.cancel();
					for(Entity e : p.getNearbyEntities(radius, radius, radius)){
						if(e instanceof Player) {
							if(TribeManager.canAttack(p, (Player) e)) {
								e.setFireTicks(60);
							}
						}
					}
				}
			}.runTaskTimer(plugin, 30L, 30L);
		}
		else if(skill.equalsIgnoreCase("Slowness")) {
			new BukkitRunnable() {
				public void run() {
					for(Entity e : p.getNearbyEntities(5D, 5D, 5D)){
						if(getSkill(player) == null) this.cancel();
						else if(!getSkill(player).equals("Slowness")) this.cancel();
						if(e instanceof Player) {
							if(TribeManager.canAttack(p, (Player) e)) {
								((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 1));
							}
						}
					}
				}
			}.runTaskTimer(plugin, 30L, 30L);
		}
		else if(skill.equalsIgnoreCase("Regeneration")) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 3));
		}
		else if(skill.equalsIgnoreCase("Efficiency")) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 6000, 2));
		}
		else {
			p.getInventory().addItem(ItemStackGenerator.createItem(Material.GOLD_SWORD, 0, 0, ChatColor.RED + token.getName(), Arrays.asList(ChatColor.GRAY + "Right-Click to use this!"), true));
		}
	}
	
	public static GodToken findToken(String name) {
		for(GodToken token : tokens) {
			if(token.getName().equals(name)) return token;
		}
		return null;
	}
	
	public static String getSkill(String player) {
		if(skill.get(player) == null) return "";
		return skill.get(player);
	}
	
	public static void setSkill(String player, String skillName) {
		skill.put(player, skillName);
	}
	
	public static void finish(String player) {
		if(Bukkit.getPlayer(player) == null) return;
		if(!Bukkit.getPlayer(player).isOnline()) return;
		Bukkit.getPlayer(player).sendMessage(mh + ChatColor.RED + getSkill(player) 
			+ ChatColor.GRAY + " has ended! You can use it again in " + findToken(getSkill(player)).getCooldown() + " minutes!");
		Cooldowns.setCooldown(Bukkit.getPlayer(player), getSkill(player), (long)findToken(getSkill(player)).getCooldown() * 60000);
		skill.remove(player);
		if(getEffect(player) != null)
			getEffect(player).cancel();
		effect.remove(player);
	}
	
	public static void setupTokens() {
		tokens = new ArrayList<GodToken>();
		GodToken token;
		// LIGHTNING
		token = new GodToken("Lightning");
		token.setType(TokenType.PVP);
		token.setIcon(Material.HOPPER);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Strike a lightning at targeting location",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "3/10 tokens",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "15 minutes",
				ChatColor.GRAY + "Range: " + ChatColor.RED + "15 blocks",
				ChatColor.GRAY + "Direct damage: " + ChatColor.RED + "8 hearts",
				ChatColor.GRAY + "Radius: " + ChatColor.RED + "3 blocks",
				ChatColor.GRAY + "Radius damage: " + ChatColor.RED + "5 hearts"));
		token.setTempPrice(3);
		token.setPermPrice(15);
		token.setCooldown(15);
		tokens.add(token);
		// REGENERATION
		token = new GodToken("Regeneration");
		token.setType(TokenType.PVP);
		token.setIcon(Material.GOLDEN_APPLE);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Fully heal in 10 seconds",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "3/20 tokens",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "30 minutes"));
		token.setTempPrice(3);
		token.setPermPrice(20);
		token.setCooldown(30);
		token.setDuration(10);
		tokens.add(token);
		// DODGE
		token = new GodToken("Dodge");
		token.setType(TokenType.PVP);
		token.setIcon(Material.LEAVES);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Dodge some of the incoming attacks",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "2/10 tokens",
				ChatColor.GRAY + "Dodge chance: " + ChatColor.RED + "30%",
				ChatColor.GRAY + "Duration: " + ChatColor.RED + "60 seconds",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "20 minutes"));
		token.setTempPrice(3);
		token.setPermPrice(15);
		token.setCooldown(20);
		token.setDuration(60);
		tokens.add(token);
		// SLOWNESS
		token = new GodToken("Slowness");
		token.setType(TokenType.PVP);
		token.setIcon(Material.SHEARS);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Slow down nearby players",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "2/10 tokens",
				ChatColor.GRAY + "Effect: " + ChatColor.RED + "Slowness II",
				ChatColor.GRAY + "Radius: " + ChatColor.RED + "5 blocks",
				ChatColor.GRAY + "Duration: " + ChatColor.RED + "20 seconds",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "15 minutes"));
		token.setTempPrice(1);
		token.setPermPrice(10);
		token.setCooldown(15);
		token.setDuration(20);
		tokens.add(token);
		// FIRE
		token = new GodToken("Fire");
		token.setType(TokenType.PVP);
		token.setIcon(Material.BLAZE_POWDER);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Set nearby players on fire",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "1/10 tokens",
				ChatColor.GRAY + "Radius: " + ChatColor.RED + "5 blocks",
				ChatColor.GRAY + "Duration: " + ChatColor.RED + "30 seconds",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "10 minutes"));
		token.setTempPrice(1);
		token.setPermPrice(15);
		token.setCooldown(10);
		token.setDuration(30);
		tokens.add(token);
		// FIREBALL
		token = new GodToken("Fireball");
		token.setType(TokenType.PVP);
		token.setIcon(Material.FIREBALL);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Launch a fireball",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "5/50 tokens",
				ChatColor.GRAY + "Radius damage: " + ChatColor.RED + "5 hearts",
				ChatColor.GRAY + "Radius: " + ChatColor.RED + "6 blocks",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "1 hour"));
		token.setTempPrice(5);
		token.setPermPrice(50);
		token.setCooldown(60);
		tokens.add(token);
		// FREEZE
		token = new GodToken("Freeze");
		token.setType(TokenType.PVP);
		token.setIcon(Material.ICE);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Freeze a target player",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "1/10 tokens",
				ChatColor.GRAY + "Duration: " + ChatColor.RED + "5 seconds",
				ChatColor.GRAY + "Radius: " + ChatColor.RED + "15 blocks",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "20 minutes"));
		token.setTempPrice(1);
		token.setPermPrice(15);
		token.setCooldown(20);
		tokens.add(token);
		// EFFICIENCY
		token = new GodToken("Efficiency");
		token.setType(TokenType.MINING);
		token.setIcon(Material.DIAMOND_PICKAXE);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Become more efficient while mining",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "2/15 tokens",
				ChatColor.GRAY + "Effect: " + ChatColor.RED + "Haste III",
				ChatColor.GRAY + "Duration: " + ChatColor.RED + "5 minutes",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "35 minutes"));
		token.setTempPrice(2);
		token.setPermPrice(25);
		token.setCooldown(35);
		token.setDuration(300);
		tokens.add(token);
		// EXPLOSIVE
		token = new GodToken("Explosive");
		token.setType(TokenType.MINING);
		token.setIcon(Material.TNT);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Explode blocks around you",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "3/20 tokens",
				ChatColor.GRAY + "Radius: " + ChatColor.RED + "2 blocks",
				ChatColor.GRAY + "Duration: " + ChatColor.RED + "3 minutes",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "23 minutes"));
		token.setTempPrice(3);
		token.setPermPrice(20);
		token.setCooldown(23);
		token.setDuration(180);
		tokens.add(token);
		// FORTUNE
		token = new GodToken("Fortune");
		token.setType(TokenType.EXTRA);
		token.setIcon(Material.RAW_FISH);
		token.setDescription(Arrays.asList(	ChatColor.GRAY + "Increase your fortune while fishing",
				ChatColor.GRAY + "Cost (temp/perm): " + ChatColor.RED + "3/25 tokens",
				ChatColor.GRAY + "Effect: " + ChatColor.RED + "Luck III",
				ChatColor.GRAY + "Duration: " + ChatColor.RED + "10 minutes",
				ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "40 minutes"));
		token.setTempPrice(3);
		token.setPermPrice(20);
		token.setCooldown(23);
		token.setDuration(600);
		tokens.add(token);
	}
	
	public static int getCooldown(String player) {
		return (int)Cooldowns.getCooldown(Bukkit.getPlayer(player), "god_token");
	}
	
	public static int getCooldownSeconds(String player) {
		return (int)Cooldowns.getCooldown(Bukkit.getPlayer(player), "god_token") / 1000;
	}
	
	public static void addCooldown(String player, int time) {
		Cooldowns.setCooldown(Bukkit.getPlayer(player), "god_token", time * 1000);
	}
	
	public static boolean canUse(String player) {
		return Cooldowns.getCooldown(Bukkit.getPlayer(player), "god_token") <= 0;
	}
	
	public static Effect getEffect(String player) {
		return effect.get(player);
	}
	
	public static void setEffect(String player, Effect eff) {
		effect.put(player, eff);
	}
	
	
	public static void startEffect(String player, String skill) {
		if(skill.equalsIgnoreCase("Fire")) {
			AnimatedBallEffect eff = new AnimatedBallEffect(Main.em);
			eff.setEntity(Bukkit.getPlayer(player));
			eff.particle = ParticleEffect.FLAME;
			eff.size = 0.7F;
			eff.speed = 0.2F;
			eff.yOffset = -0.8F;
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
		else if(skill.equalsIgnoreCase("Fireball")) {
			LoveEffect eff = new LoveEffect(Main.em);
			eff.setEntity(Bukkit.getPlayer(player));
			eff.particle = ParticleEffect.LAVA;
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
		else if(skill.equalsIgnoreCase("Ice")) {
			LoveEffect eff = new LoveEffect(Main.em);
			eff.setEntity(Bukkit.getPlayer(player));
			eff.particle = ParticleEffect.SNOW_SHOVEL;
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
		else if(skill.equalsIgnoreCase("Slowness")) {
			AnimatedBallEffect eff = new AnimatedBallEffect(Main.em);
			eff.setEntity(Bukkit.getPlayer(player));
			eff.particle = ParticleEffect.ENCHANTMENT_TABLE;
			eff.size = 0.7F;
			eff.speed = 0.2F;
			eff.yOffset = -0.8F;
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
		else if(skill.equalsIgnoreCase("Dodge")) {
			ShieldEffect eff = new ShieldEffect(Main.em);
			eff.setEntity(Bukkit.getPlayer(player));
			eff.sphere = true;
			eff.particle = ParticleEffect.TOWN_AURA;
			eff.radius = 2;
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
		else if(skill.equalsIgnoreCase("Lightning")) {
			LoveEffect eff = new LoveEffect(Main.em);
			eff.setEntity(Bukkit.getPlayer(player));
			eff.particle = ParticleEffect.CRIT_MAGIC;
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
		else if(skill.equalsIgnoreCase("Regeneration")) {
			LoveEffect eff = new LoveEffect(Main.em);
			eff.setEntity(Bukkit.getPlayer(player));
			eff.infinite();
			eff.start();
			setEffect(player, eff);
		}
	}
	
}
