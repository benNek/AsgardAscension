package com.nekrosius.asgardascension.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.koletar.jj.mineresetlite.Mine;
import com.koletar.jj.mineresetlite.MineResetLite;
import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.files.MessagesFile;
import com.nekrosius.asgardascension.handlers.GodTokens;
import com.nekrosius.asgardascension.managers.TribeManager;
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
				event.getWhoClicked().sendMessage(Main.MESSAGE_HEADER + "You can't remove this item from your inventory!");
				event.setCancelled(true);
			}
		}
		// Removing Rainbow Miner effect
		if(rainbowMiner.get(event.getWhoClicked().getName()) != null) {
			ItemStack dropped = event.getCurrentItem();
			if(dropped.getType().equals(rainbowMiner.get(event.getWhoClicked().getName()).getType() )
					&& dropped.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == rainbowMiner.get(event.getWhoClicked().getName()).getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS))
			{
				event.getWhoClicked().sendMessage(Main.MESSAGE_HEADER + "You can't remove this item from your inventory!");
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		// Diamond for every block
		if(diamondMiner.get(event.getPlayer().getName()) != null) {
			if(event.isCancelled()) return;
			
			ItemStack itemToAdd = new ItemStack(Material.DIAMOND);
			if (!ItemStackGenerator.isInventoryFull(event.getPlayer(), itemToAdd)) {
				event.getPlayer().getInventory().addItem(itemToAdd);
			}
			else {
				event.getPlayer().sendMessage(Main.MESSAGE_HEADER + "Your inventory is full!");
			}
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
			player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.lava"));
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
						Block block = event.getBlock().getLocation().getWorld().getBlockAt(new Location(event.getBlock().getWorld(), x, y, height));
						if(!block.getType().equals(Material.TNT))
							player.getInventory().addItem(new ItemStack(block.getType()));
						block.setType(Material.AIR);
					}
				}
			}
			
			player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.explosion"));
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
			player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.zombie"));
		}
		// 5% decoy chest
		else if(random <= 49 - 25) {
			event.getBlock().setType(Material.CHEST);
			final Player target = event.getPlayer();
			player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.trap_chest"));
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
			String msg = Main.MESSAGE_HEADER + ConfigFile.getLuckyCommandMessages().get(rnd);
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
			player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.speed_miner.start"));
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
					player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.speed_miner.end"));
				}
				
			}.runTaskLater(pl, 300L);
		}
		// 12% Diamond miner
		else if(random <= 76 - 25) {
			player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.diamond_miner.start"));
			diamondMiner.put(player.getName(), true);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if(!player.isOnline()) return;
					diamondMiner.remove(player.getName());
					player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.diamond_miner.end"));
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
			player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.rainbow_miner.start"));
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
					player.sendMessage(Main.MESSAGE_HEADER + MessagesFile.getMessage("lucky_blocks.rainbow_miner.end"));
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
				event.getItemDrop().remove();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent event) {
		if(event.getEntityType().equals(EntityType.PRIMED_TNT)) {
			event.setCancelled(true);
			event.getLocation().getBlock().setType(Material.TNT);
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
