package com.nekrosius.asgardascension.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.koletar.jj.mineresetlite.Mine;
import com.koletar.jj.mineresetlite.MineResetLite;
import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.handlers.GodTokens;
import com.nekrosius.asgardascension.managers.TribeManager;
import com.nekrosius.asgardascension.objects.Tribe;
import com.nekrosius.asgardascension.utils.Cooldowns;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class PlayerListener implements Listener {
	
	Map<String, Boolean> diamondMiner;
	Map<String, ItemStack> speedMiner;
	Map<String, ItemStack> rainbowMiner;
	private Random randomGen;
	
	private Main pl;
	public PlayerListener(Main plugin) {
		diamondMiner = new HashMap<String, Boolean>();
		speedMiner = new HashMap<String, ItemStack>();
		rainbowMiner = new HashMap<String, ItemStack>();
		randomGen = new Random();
		this.pl = plugin;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Removing Speed Miner effect
		if(speedMiner.get(event.getPlayer().getName()) != null) {
			speedMiner.get(event.getPlayer().getName()).removeEnchantment(Enchantment.DIG_SPEED);
			event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
			event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
			speedMiner.remove(event.getPlayer().getName());
		}
		// Removing Rainbow Miner effect
		if(rainbowMiner.get(event.getPlayer().getName()) != null) {
			rainbowMiner.get(event.getPlayer().getName()).removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
			event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
			rainbowMiner.remove(event.getPlayer().getName());
		}
		// Removing Diamond Miner effect
		if(diamondMiner.get(event.getPlayer().getName()) != null) {
			diamondMiner.remove(event.getPlayer().getName());
		}
		// Quiting challenge in case player is doing it
		if(pl.getChallenges().getChallenge(event.getPlayer()) > 0){
			pl.getChallenges().quitChallenge(event.getPlayer());
		}
		// Finishing god token
		if(!(GodTokens.getSkill(event.getPlayer().getName()).equalsIgnoreCase(""))){
			GodTokens.finish(event.getPlayer().getName());
		}
		pl.getPlayerManager().saveData(event.getPlayer());
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		// Cancelling team damage
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player victim = (Player) event.getEntity();
			Player damager = (Player) event.getDamager();
			if(!TribeManager.canAttack(victim, damager)){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryItemPlace(InventoryClickEvent event) {
		// Removing Speed Miner effect
		if(speedMiner.get(event.getWhoClicked().getName()) != null) {
			ItemStack dropped = event.getCurrentItem();
			if(dropped.getType().equals(speedMiner.get(event.getWhoClicked().getName()).getType() )
					&& dropped.getEnchantmentLevel(Enchantment.DIG_SPEED) == speedMiner.get(event.getWhoClicked().getName()).getEnchantmentLevel(Enchantment.DIG_SPEED))
			{
				event.getWhoClicked().sendMessage(Main.mh + "You can't remove this item from your inventory!");
				event.setCancelled(true);
			}
		}
		// Removing Rainbow Miner effect
		if(rainbowMiner.get(event.getWhoClicked().getName()) != null) {
			ItemStack dropped = event.getCurrentItem();
			if(dropped.getType().equals(rainbowMiner.get(event.getWhoClicked().getName()).getType() )
					&& dropped.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == rainbowMiner.get(event.getWhoClicked().getName()).getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS))
			{
				event.getWhoClicked().sendMessage(Main.mh + "You can't remove this item from your inventory!");
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		// Diamond for every block
		if(diamondMiner.get(event.getPlayer().getName()) != null) {
			if(event.isCancelled()) return;
			event.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
		}
		// Lucky blocks
		if(!event.getBlock().getType().equals(Material.TNT)) return;
		event.setCancelled(true);
		event.getBlock().setType(Material.AIR);
		final Player player = event.getPlayer();
		int random = randomGen.nextInt(100 - 25);
		
		// 5% lava
		if (random <= 29 - 25) {
			event.getBlock().setType(Material.LAVA);
		}
		// 10% 10x10 explosion
		else if (random <= 34 - 25) {
			Mine mine = getTargetMine(event.getBlock().getLocation());
			if(mine == null) return;
			int maxX = (event.getBlock().getLocation().getBlockX() + 5 > mine.getMaxX()) ? mine.getMaxX() : event.getBlock().getLocation().getBlockX() + 5;
			int maxY = (event.getBlock().getLocation().getBlockY() + 5 > mine.getMaxY()) ? mine.getMaxY() : event.getBlock().getLocation().getBlockY() + 5;
			int maxZ = (event.getBlock().getLocation().getBlockZ() + 5 > mine.getMaxZ()) ? mine.getMaxZ() : event.getBlock().getLocation().getBlockZ() + 5;
			
			int minX = (event.getBlock().getLocation().getBlockX() - 5 < mine.getMinX()) ? mine.getMinX() : event.getBlock().getLocation().getBlockX() - 5;
			int minY = (event.getBlock().getLocation().getBlockY() - 5 < mine.getMinY()) ? mine.getMinY() : event.getBlock().getLocation().getBlockY() - 5;
			int minZ = (event.getBlock().getLocation().getBlockZ() - 5 < mine.getMinZ()) ? mine.getMinZ() : event.getBlock().getLocation().getBlockZ() - 5;
			
			for(int height = maxZ; height >= minZ; height--) {
				for(int x = minX; x <= maxX; x++) {
					for(int y = minY; y <= maxY; y++) {
						if(!event.getBlock().getType().equals(Material.TNT))
							player.getInventory().addItem(new ItemStack(event.getBlock().getType()));
						event.getBlock().getLocation().getWorld().getBlockAt(new Location(event.getBlock().getWorld(), x, y, height)).setType(Material.AIR);
					}
				}
			}
			
			
		}
		// 5% BOB
		else if (random <= 44 - 25) {
			Entity bob = event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.ZOMBIE);
			EntityEquipment ee = ((Zombie) bob).getEquipment();
			ee.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			ee.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			ee.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			ee.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			ee.setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
		}
		// 5% decoy chest
		else if(random <= 49 - 25) {
			event.getBlock().setType(Material.CHEST);
			final Player target = event.getPlayer();
			new BukkitRunnable() {
				public void run() {
					if(event.getBlock().getType().equals(Material.CHEST)) {
						event.getBlock().setType(Material.AIR);
						event.getBlock().getWorld().createExplosion(event.getBlock().getLocation().getX(),
								event.getBlock().getLocation().getY(), event.getBlock().getLocation().getZ(),
								0F, false, false);
						if(event.getBlock().getLocation().distance(target.getLocation()) <= 3) {
							target.damage(15D);
						}
					}
				}
			}.runTaskLater(pl, 40L);
		}
		// 10% Command
		else if (random <= 54 - 25) {
			int rnd = randomGen.nextInt(ConfigFile.getLuckyCommands().size());
			String cmd = ConfigFile.getLuckyCommands().get(rnd);
			String msg = Main.mh + ConfigFile.getLuckyCommandMessages().get(rnd);
			cmd = cmd.replaceAll("%player", player.getName());
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
			player.sendMessage(msg);
		}
		// 12% Speed Miner
		else if (random <= 64 - 25) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 2));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1));
			final ItemStack item = player.getInventory().getItemInMainHand();
			final boolean hasTool = ItemStackGenerator.isTool(player.getInventory().getItemInMainHand());
			boolean temp = true;
			if(hasTool) {
				if(item.containsEnchantment(Enchantment.DIG_SPEED)) temp = false;
				else {
					item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
					speedMiner.put(player.getName(), item);
					player.updateInventory();
				}
			}
			final boolean remove = temp;
			player.sendMessage(Main.mh + "You have found " + ChatColor.YELLOW + "Speed Miner Effect" + ChatColor.GRAY + "!");
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if(hasTool) {
						if(!player.isOnline()) return;
						if(remove) {
							item.removeEnchantment(Enchantment.DIG_SPEED);
							speedMiner.remove(player.getName());
							player.updateInventory();
						}
					}
					player.sendMessage(Main.mh + ChatColor.YELLOW + "Speed Miner Effect" + ChatColor.GRAY + " has run out!");
				}
				
			}.runTaskLater(pl, 300L);
		}
		// 12% Diamond miner
		else if(random <= 76 - 25) {
			player.sendMessage(Main.mh + "Every block you break now yield diamonds!");
			diamondMiner.put(player.getName(), true);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if(!player.isOnline()) return;
					diamondMiner.remove(player.getName());
					player.sendMessage(Main.mh + ChatColor.YELLOW + "Diamond Miner Effect" + ChatColor.GRAY + " has run out!");
				}
				
			}.runTaskLater(pl, 300L);
		}
		// 12% Rainbow miner
		else if(random <= 88 - 25) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 3));
			final ItemStack item = player.getInventory().getItemInMainHand();
			final boolean hasTool = ItemStackGenerator.isTool(player.getInventory().getItemInMainHand());
			boolean temp = true;
			if(hasTool) {
				if(item.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) temp = false;
				else {
					item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);
					rainbowMiner.put(player.getName(), item);
					player.updateInventory();
				}
			}
			final boolean remove = temp;
			player.sendMessage(Main.mh + "You have found " + ChatColor.YELLOW + "Rainbow Miner Effect" + ChatColor.GRAY + "!");
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if(hasTool) {
						if(!player.isOnline()) return;
						if(remove) {
							item.removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
							rainbowMiner.remove(player.getName());
							player.updateInventory();
						}
					}
					player.sendMessage(Main.mh + ChatColor.YELLOW + "Rainbow Miner Effect" + ChatColor.GRAY + " has run out!");
				}
				
			}.runTaskLater(pl, 300L);
			
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if(event.getItemDrop() == null) return;
		// Removing Speed Miner effect
		if(speedMiner.get(event.getPlayer().getName()) != null) {
			ItemStack dropped = event.getItemDrop().getItemStack();
			if(dropped.getType().equals(speedMiner.get(event.getPlayer().getName()).getType() )
					&& dropped.getEnchantmentLevel(Enchantment.DIG_SPEED) == speedMiner.get(event.getPlayer().getName()).getEnchantmentLevel(Enchantment.DIG_SPEED))
			{
				ItemStack removed = rainbowMiner.get(event.getPlayer().getName());
				removed.removeEnchantment(Enchantment.DIG_SPEED);
				event.getItemDrop().setItemStack(removed);
				event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
				event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
				speedMiner.remove(event.getPlayer().getName());
			}
		}
		// Removing Rainbow Miner effect
		if(rainbowMiner.get(event.getPlayer().getName()) != null) {
			ItemStack dropped = event.getItemDrop().getItemStack();
			if(dropped.getType().equals(rainbowMiner.get(event.getPlayer().getName()).getType() )
					&& dropped.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == rainbowMiner.get(event.getPlayer().getName()).getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS))
			{
				ItemStack removed = rainbowMiner.get(event.getPlayer().getName());
				removed.removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
				event.getItemDrop().setItemStack(removed);
				event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
				rainbowMiner.remove(event.getPlayer().getName());
			}
		}
		if(event.getItemDrop().getItemStack().getType().equals(Material.GOLD_SWORD)) {
			if(event.getItemDrop().getItemStack().getItemMeta().getDisplayName() == null) return;
			if(event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Ultimate Skill")){
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
			if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WOOD_SWORD) 
					|| event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STONE_SWORD) 
					|| event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD)
					|| event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD) ) {
				Player player = event.getPlayer();
				event.setCancelled(true);
				if(!Main.isPVPEnabled(player)) {
					return;
				}
				if(!TribeManager.hasTribe(player.getName())){
					return;
				}
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
					Location loc = getCenterLocation(player.getLocation(), player.getTargetBlock(new HashSet<Byte>(), 5).getLocation());
					double smashDamage = getSmashDamageByTribeLevel(tribe.getLevel());
					for(Player p : getNearbyPlayers(loc, 2.5)){
						if(p != player) {
							if(p.getHealth() >= smashDamage){
								p.setHealth(p.getHealth() - smashDamage);
							}else{
								p.setHealth(0);
							}
						}
					}
					if(tribe.getLevel() != 5) {
						Cooldowns.setCooldown(player, "ultimate", 3600000);
					}else{
						Cooldowns.setCooldown(player, "ultimate", 1800000);
					}
				}
				else if(tribe.getType().equalsIgnoreCase("vanir")) {
					Fireball fb = player.launchProjectile(Fireball.class);
					fb.setMetadata("ultimate", new FixedMetadataValue(pl, tribe.getLevel()));
					Cooldowns.setCooldown(player, "ultimate", 3600000);
				}
			}
		}
	}
	
	@EventHandler
	public void onIgnite(ExplosionPrimeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent event) {
		if(event.getEntityType().equals(EntityType.PRIMED_TNT)) {
			event.setCancelled(true);
			event.getLocation().getBlock().setType(Material.TNT);
		}
		if(event.getEntityType().equals(EntityType.FIREBALL)){
			Fireball fb = (Fireball) event.getEntity();
			if(fb.hasMetadata("ultimate")){
				int level = fb.getMetadata("ultimate").get(0).asInt();
				double directDamage = getFireballDamageByTribeLevel(level, true);
				double explosionDamage = getFireballDamageByTribeLevel(level, false);
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
	public void onJoin(PlayerJoinEvent event) {
		pl.getPlayerManager().loadData(event.getPlayer());
		
		//fixRank(event.getPlayer());
		
		if(event.getPlayer().hasPermission("asgardascension.staff")) {
			event.getPlayer().setPlayerListName(ChatColor.YELLOW + event.getPlayer().getDisplayName());
		}
	}
	
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		event.setFire(false);
		if (event.getEntity() instanceof Fireball){
			if(event.getEntity().hasMetadata("ultimate")){
				event.setRadius(0);
			}
		}
	}
	
	private double getSmashDamageByTribeLevel(int level) {
		if(level == 1) return 3;
		else if(level == 2) return 4;
		else if(level == 3) return 6;
		else if(level == 4) return 7;
		else return 0;
	}
	
	private double getFireballDamageByTribeLevel(int level, boolean direct) {
		if(direct){
			return 10;
		}
		else{
			if(level == 1 || level == 2) return 4;
			else if(level == 3 || level == 4) return 6;
			else return 4;
		}
	}
	
	private static Location getCenterLocation(Location loc1, Location loc2){
		Location loc = new Location(loc1.getWorld(), 0, 0, 0);
		double x = (loc1.getX() + loc2.getX()) / 2;
		double z = (loc1.getZ() + loc2.getZ()) / 2;
		loc.setX(x);
		loc.setY(loc1.getY());
		loc.setZ(z);
		return loc;
	}
	
	private static List<Player> getNearbyPlayers(Location loc, double radius){
		List<Player> players = new ArrayList<Player>();
		for(Entity e : loc.getWorld().getEntities()){
			if(e instanceof Player){
				if(loc.distance(e.getLocation()) <= radius)
					players.add((Player)e);
			}
		}
		return players;
	}

	@SuppressWarnings("unused")
	private void fixRank(Player player) {
		int rank = pl.getPlayerManager().getRank(player);
		String command = "pex user " + player.getName() + " group add ";
		if(rank == 0) {
			command += "SparkDevil";
		}
		else if(rank == 1) {
			command += "FireDevil";
		}
		else if(rank == 2) {
			command += "SparkDemon";
		}
		else if(rank == 3) {
			command += "FireDemon";
		}
		else if(rank == 4) {
			command += "Eimnir";
		}
		else if(rank == 5) {
			command += "FireMonster";
		}
		else if(rank == 6) {
			command += "InfernoMonster";
		}
		else if(rank == 7) {
			command += "FireGiant";
		}
		else if(rank == 8) {
			command += "InfernoGiant";
		}
		else if(rank == 9) {
			command += "Surt";
		}
		else if(rank == 10) {
			command += "FrostDevil";
		}
		else if(rank == 11) {
			command += "IceDevil";
		}
		else if(rank == 12) {
			command += "FrostDemon";
		}
		else if(rank == 13) {
			command += "IceDemon";
		}
		else if(rank == 14) {
			command += "FrostMonster";
		}
		else if(rank == 15) {
			command += "Jotunn";
		}
		else if(rank == 16) {
			command += "AvalancheMonster";
		}
		else if(rank == 17) {
			command += "IceGiant";
		}
		else if(rank == 18) {
			command += "AvalancheGiant";
		}
		else if(rank == 19) {
			command += "Utgard-Loki";
		}
		else if(rank == 20) {
			command += "Loki";
		}
		else if(rank == 21) {
			command += "Freyja";
		}
		else if(rank == 22) {
			command += "Thor";
		}
		else if(rank == 23) {
			command += "Frigg";
		}
		else if(rank == 24) {
			command += "Odin";
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	private Mine getTargetMine(Location loc) {
		for(Mine mine : MineResetLite.instance.mines) {
			if(mine.isInside(loc)) return mine;
		}
		return null;
	}
	
	public Main getPlugin() {
		return pl;
	}
	
}
