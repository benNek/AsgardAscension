package com.nekrosius.asgardascension.listeners.inventories;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.inventories.GodTokensInventory;
import com.nekrosius.asgardascension.listeners.SetupListener;
import com.nekrosius.asgardascension.objects.Ability;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class GodTokensInventoryListener implements Listener {
	
	Main plugin;
	public GodTokensInventoryListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getCurrentItem() == null)
			return;
		
		if(!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		String title = event.getInventory().getName();
		
		// Main shop
		if(title.equals(ChatColor.BOLD + "God Tokens Shop")) {
			event.setCancelled(true);
			switch(event.getCurrentItem().getType()) {
				// GT withdrawal
				case NETHER_STAR:
					handleGTWithdrawal(player);
					return;
					
				// Temporary abilities
				case WATCH:
					GodTokensInventory.setupAbilitiesMenu(player, true);
					return;
					
				// Permanent abilities
				case END_CRYSTAL:
					GodTokensInventory.setupAbilitiesMenu(player, false);
					return;
					
				// Aditional plot access
				case STONE_SPADE:
					handlePlotPurchase(player);
					return;
					
				// Crate purchase
				case CHEST:
					handleCratePurchase(player);
					return;
					
				// Repair menu
				case ANVIL:
					GodTokensInventory.setupRepairMenu(player);
					return;
				
				// Odin's apple
				case GOLDEN_APPLE:
					handleApplePurchase(player);
					return;
					
				default:
					return;
			}
		}
		
		// Repair menu
		else if(title.equals(ChatColor.BOLD + "Repair Menu")) {
			// Pressed 'Go back!' button
			if(event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)) {
				GodTokensInventory.setupTokensMenu((Player) event.getWhoClicked());
				return;
			}
			// No item in hand
			else if(event.getCurrentItem().getType().equals(Material.STRING)) {
				player.closeInventory();
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
			}
			// Repairing item in hand
			else if(event.getCurrentItem().hasItemMeta()
					&& event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Repair item in main hand")) {
				handleItemRepair(player);
			}
			// Repairing entire inventory
			else if(event.getCurrentItem().getType().equals(Material.CHEST)) {
				handleInventoryRepair(player);
			}
		}
		
		// Temporary abilities
		else if(title.equals(ChatColor.BOLD + "Temporary God Tokens")) {
			String name = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
			Ability ability = plugin.getAbilityManager().getAbility(name);
			handleTemporaryTokenPurchase(player, ability);
		}
		
	}
	
	private void handleTemporaryTokenPurchase(Player player, Ability ability) {
		player.closeInventory();
		if(!plugin.getPlayerManager().hasTokens(player, ability.getTemporaryPrice())) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString()
					+ Lang.TOKENS_BUY_NOT_ENOUGH.toString()
						.replaceAll("%p", String.valueOf(ability.getTemporaryPrice())));
			return;
		}
		
		
		
	}
	
	private void handleGTWithdrawal(Player player) {
		player.closeInventory();
		player.sendMessage(Lang.HEADERS_TOKENS.toString() + "Type amount of GT to withdraw");
		SetupListener.withdrawal.put(player.getName(), true);
		new BukkitRunnable() {
			public void run() {
				if(SetupListener.withdrawal.containsKey(player.getName())) {
					SetupListener.withdrawal.remove(player.getName());
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You didn't withdrawn any GT in time!");
				}
			}
		}.runTaskLater(plugin, 200L);
	}
	
	private void handleCratePurchase(Player player) {
		player.closeInventory();
		if(!plugin.getPlayerManager().hasTokens(player, 8)) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString()
					+ Lang.TOKENS_BUY_NOT_ENOUGH.toString()
						.replaceAll("%p", "8"));
			return;
		}
		plugin.getPlayerManager().setTokens(player, plugin.getPlayerManager().getTokens(player) - 8);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " key 1");
		player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You've bought a crate!");
	}
	
	private void handlePlotPurchase(Player player) {
		player.closeInventory();
		// Already bought the upgrade
		if(!GodTokensInventory.canBuyPlot(player)) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString() + "No more upgrades!");
			return;
		}
		if(!plugin.getPlayerManager().hasTokens(player, 25)) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString()
					+ Lang.TOKENS_BUY_NOT_ENOUGH.toString()
						.replaceAll("%p", "25"));
			return;
		}
		
		plugin.getPlayerManager().setTokens(player, plugin.getPlayerManager().getTokens(player) - 25);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player.getName() + " remove plots.plot.1");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player.getName() + " add plots.plot.2");
		player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You now have one more available plot!");
	}
	
	private void handleApplePurchase(Player player) {
		player.closeInventory();
		if(!plugin.getPlayerManager().hasTokens(player, 1)) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString()
					+ Lang.TOKENS_BUY_NOT_ENOUGH.toString()
						.replaceAll("%p", "1"));
			return;
		}
		
		plugin.getPlayerManager().withdrawTokens(player, 1);
		player.getInventory().addItem(
			ItemStackGenerator.createItem(Material.GOLDEN_APPLE, 0, 1,
					ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + "Odins Apple",
					Arrays.asList(ChatColor.GRAY + "Genuine apple of Odin")));
		player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You've bought a Odins Apple!");
	}
	
	private void handleItemRepair(Player player) {
		player.closeInventory();
		if(!plugin.getPlayerManager().hasTokens(player, 1)) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString()
					+ Lang.TOKENS_BUY_NOT_ENOUGH.toString()
						.replaceAll("%p", "1"));
			return;
		}
		
		player.sendMessage(Lang.HEADERS_TOKENS.toString() + "Your item in hand was successfully repaired!");
		plugin.getPlayerManager().withdrawTokens(player, 1);
		player.getInventory().getItemInMainHand().setDurability((short)0);
	}
	
	private void handleInventoryRepair(Player player) {
		player.closeInventory();
		
		if(!plugin.getPlayerManager().hasTokens(player, 5)) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString()
					+ Lang.TOKENS_BUY_NOT_ENOUGH.toString()
						.replaceAll("%p", "5"));
			return;
		}
		
		// Repairing items in inventory
		for (ItemStack item : player.getInventory().getContents()) {
            if (item == null)
            	continue;
            if (ItemStackGenerator.isRepairable(item))
                item.setDurability((short) 0);
        }
		
		// Repairing items in armor slots
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null)
                item.setDurability((short) 0);
        }
        
        plugin.getPlayerManager().withdrawTokens(player, 5);
        player.sendMessage(Lang.HEADERS_TOKENS.toString() + "All items in your inventory were successfully repaired!");
	}

}
