package com.nekrosius.asgardascension.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.objects.Ability;
import com.nekrosius.asgardascension.utils.Convert;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;
import com.nekrosius.asgardascension.utils.Utility;

public class AbilityListener implements Listener {
	
	Main plugin;
	public AbilityListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if(item == null)
			return;
		if(!plugin.getAbilityManager().hasAbility(item))
			return;
		
		Ability ability = plugin.getAbilityManager().getAbility(item);
		switch(ability.getName()) {
			case "AoE":
				handleAOE(event);
				return;
			case "Magnet":
				handleMagnet(event);
				return;
			case "Lucky Repair":
				handleLuckyRepair(event);
				return;
			default:
				return;
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
			return;
		
		Player player = (Player) event.getDamager();
		Player victim = (Player) event.getEntity();
		
		if(!Utility.canAttack(player, victim))
			return;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item == null)
			return;
		if(!plugin.getAbilityManager().hasAbility(item))
			return;
		
		Ability ability = plugin.getAbilityManager().getAbility(item);
		
		switch(ability.getName()) {
			case "Poison":
				handlePoison(player, victim);
				return;
			case "Flame":
				handleFlame(player, victim);
				return;
			default:
				return;
		}
	}
	
	@EventHandler
	private void handleAOE(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		BlockFace playerDir = Convert.yawToFace(player.getLocation().getYaw());
		Block block = event.getBlock();
		Block up = block.getRelative(BlockFace.UP);
		Block left = block.getRelative(BlockFace.EAST);
		Block right = block.getRelative(BlockFace.WEST);
				
		if(playerDir.equals(BlockFace.EAST) || playerDir.equals(BlockFace.WEST)) {
			left = block.getRelative(BlockFace.NORTH);
			right = block.getRelative(BlockFace.SOUTH);
		}
		if(Utility.canBuild(player, block)) {
			player.getInventory().addItem(new ItemStack(block.getType()));
			block.setType(Material.AIR);
		}
		if(Utility.canBuild(player, up)) {
			player.getInventory().addItem(new ItemStack(up.getType()));
			up.setType(Material.AIR);
		}
		if(Utility.canBuild(player, left)) {
			player.getInventory().addItem(new ItemStack(left.getType()));
			left.setType(Material.AIR);
		}
		if(Utility.canBuild(player, right)) {
			player.getInventory().addItem(new ItemStack(right.getType()));
			right.setType(Material.AIR);
		}
	}
	
	// Removing temporary tokens
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		if(!plugin.getAbilityManager().hasAbility(item) || !plugin.getAbilityManager().isTemporaryItem(item))
			return;
		
		Ability ability = plugin.getAbilityManager().getAbility(item);
		event.getItemDrop().remove();
		event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getEyeLocation(), plugin.getAbilityManager().removeItemLore(item, ability));
	}
	
	@EventHandler
	public void onRepair(InventoryClickEvent event) {
		if(event.isCancelled() || !(event.getWhoClicked() instanceof Player) || !(event.getInventory() instanceof AnvilInventory))
			return;
		
		InventoryView view = event.getView();
		int rawSlot = event.getRawSlot();
		
		if(rawSlot != view.convertSlot(rawSlot) || rawSlot != 2)
			return;
		
		ItemStack item = event.getInventory().getItem(0);
		if(!plugin.getAbilityManager().hasAbility(item) || !plugin.getAbilityManager().isTemporaryItem(item))
			return;
		
		Ability ability = plugin.getAbilityManager().getAbility(item);
		event.getInventory().setItem(0, plugin.getAbilityManager().removeItemLore(item, ability));
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		for(int i = 0; i < 32; i++) {
			ItemStack item = player.getInventory().getItem(i);
			if(item == null || !plugin.getAbilityManager().hasAbility(item) || !plugin.getAbilityManager().isTemporaryItem(item))
				continue;
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setItem(i, plugin.getAbilityManager().removeItemLore(item, ability));
		}
		
		ItemStack item = player.getInventory().getHelmet();
		if(item != null && plugin.getAbilityManager().hasAbility(item) && plugin.getAbilityManager().isTemporaryItem(item)) {
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setHelmet(plugin.getAbilityManager().removeItemLore(item, ability));
		}
		
		item = player.getInventory().getChestplate();
		if(item != null && plugin.getAbilityManager().hasAbility(item) && plugin.getAbilityManager().isTemporaryItem(item)) {
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setHelmet(plugin.getAbilityManager().removeItemLore(item, ability));
		}
		
		item = player.getInventory().getLeggings();
		if(item != null && plugin.getAbilityManager().hasAbility(item) && plugin.getAbilityManager().isTemporaryItem(item)) {
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setHelmet(plugin.getAbilityManager().removeItemLore(item, ability));
		}
		
		item = player.getInventory().getBoots();
		if(item != null && plugin.getAbilityManager().hasAbility(item) && plugin.getAbilityManager().isTemporaryItem(item)) {
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setHelmet(plugin.getAbilityManager().removeItemLore(item, ability));
		}
	}
		
	// Handling
	
	private void handleMagnet(BlockBreakEvent event) {
		if(Utility.getRandom(1, 10000) > 2) {
			return;
		}
		String command = ConfigFile.getMagnetCommand().replaceAll("%player", event.getPlayer().getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		event.getPlayer().sendMessage(Lang.HEADERS_TOKENS.toString() + Lang.TOKENS_MAGNET.toString());
	}
	
	private void handleLuckyRepair(BlockBreakEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if(Utility.getRandom(1, 10000) > 2 || item == null ||!ItemStackGenerator.isRepairable(item)) {
			return;
		}
		item.setDurability((short)0);
	}
	
	private void handlePoison(Player damager, Player target) {
		if(target.hasPotionEffect(PotionEffectType.POISON))
			return;
		
		target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
		target.sendMessage(Lang.HEADERS_TOKENS.toString()
				+ Lang.TOKENS_POISONED.toString()
					.replaceAll("%s", damager.getName()));
	}
	
	private void handleFlame(Player damager, Player target) {
		if(target.getFireTicks() > 0)
			return;
		
		target.setFireTicks(100);
		target.sendMessage(Lang.HEADERS_TOKENS.toString()
				+ Lang.TOKENS_FLAMED.toString()
					.replaceAll("%s", damager.getName()));
	}
	
}
