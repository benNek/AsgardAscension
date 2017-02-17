package com.nekrosius.asgardascension.inventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.ItemType;
import com.nekrosius.asgardascension.objects.Ability;
import com.nekrosius.asgardascension.objects.GodToken;
import com.nekrosius.asgardascension.utils.Convert;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class GodTokensInventory {
	
	private static Main plugin = (Main)Bukkit.getPluginManager().getPlugin("AsgardAscension");

	public static void setupTokensDurationMenu(Player player, GodToken token) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "BUY " + token.getName());
		inv.setItem(2, ItemStackGenerator.createItem(Material.PAPER, 0, 0, ChatColor.RED + "Temporary (" + ChatColor.GOLD + token.getTempPrice() + " GT"  + ChatColor.RED + ")", null));
		inv.setItem(4, ItemStackGenerator.createItem(token.getIcon(), 0, 0, ChatColor.RED + token.getName(), token.getDescription(), true));
		inv.setItem(6, ItemStackGenerator.createItem(Material.BOOK, 0, 0, ChatColor.RED + "Permanent Access (" + ChatColor.GOLD + token.getPermPrice() + " GT"  + ChatColor.RED + ")", null));
		inv.setItem(8, ItemStackGenerator.createItem(Material.REDSTONE_BLOCK, 0, 0, ChatColor.RED + "Go back!", null));
		player.openInventory(inv);
	}
	
	public static void setupTokensMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 18, ChatColor.BOLD + "God Tokens Shop");
		
		ItemStack tokensAmount = ItemStackGenerator.createItem(Material.PAPER,
				ChatColor.DARK_PURPLE + "GT: " + ChatColor.LIGHT_PURPLE + plugin.getPlayerManager().getTokens(player), null);
		ItemStack withdraw = ItemStackGenerator.createItem(Material.NETHER_STAR, ChatColor.LIGHT_PURPLE + "Withdraw ... GT", null);
		
		inv.setItem(0, tokensAmount);
		inv.setItem(9, tokensAmount);
		inv.setItem(8, withdraw);
		inv.setItem(17, withdraw);
		
		// 1st Row
		inv.setItem(3, ItemStackGenerator.createItem(Material.WATCH, ChatColor.LIGHT_PURPLE + "Temporary Tokens",
				Arrays.asList("Click for more information!")));
		inv.setItem(4, ItemStackGenerator.createItem(Material.END_CRYSTAL, ChatColor.LIGHT_PURPLE + "Permanent Tokens",
				Arrays.asList("Click for more information!")));
		inv.setItem(5, ItemStackGenerator.createItem(Material.ENCHANTED_BOOK, ChatColor.LIGHT_PURPLE + "Runes",
				Arrays.asList("Click for more information!"), true));
		
		// 2nd Row
		if(canBuyPlot(player)) {
			inv.setItem(10, ItemStackGenerator.createItem(Material.STONE_SPADE, ChatColor.LIGHT_PURPLE + "Additional plot access", 
					Arrays.asList("Price: " + ChatColor.LIGHT_PURPLE + "25 GT"), true));
		}
		inv.setItem(12, ItemStackGenerator.createItem(Material.CHEST, ChatColor.LIGHT_PURPLE + "Crate", 
				Arrays.asList("Price: " + ChatColor.LIGHT_PURPLE + "8 GT")));
		inv.setItem(13, ItemStackGenerator.createItem(Material.ANVIL, ChatColor.LIGHT_PURPLE + "Repair", 
				Arrays.asList("Click for more information!")));
		inv.setItem(14, ItemStackGenerator.createItem(Material.GOLDEN_APPLE, 0, 1, ChatColor.LIGHT_PURPLE + "Odins Apple", 
				Arrays.asList("Price: " + ChatColor.LIGHT_PURPLE + "1 GT")));
		
		player.openInventory(inv);
	}
	
	public static void setupAbilitiesMenu(Player player, boolean temporary) {
		String title = temporary ? "Temporary" : "Permanent";
		Inventory inventory = Bukkit.createInventory(player, Convert.getInventorySize(plugin.getAbilityManager().getAbilities().size()),
				ChatColor.BOLD + title + " God Tokens");
		for(Ability ability : plugin.getAbilityManager().getAbilities()) {
			List<String> description = new ArrayList<>(ability.getDescription());
			
			// Adding price depending whetver ability is permanent or temporary
			int price = temporary ? ability.getTemporaryPrice() : ability.getPermanentPrice();
			String line = ChatColor.GRAY + "Price: " + ChatColor.RED + price + " GT";
			description.add(line);
			
			description.add(ChatColor.GRAY + "Supported items:");
			for(ItemType item : ability.getItems()) {
				description.add(ChatColor.RED + "- " + WordUtils.capitalize(item.toString().replaceAll("_", "").toLowerCase()));
			}
			
			inventory.addItem(ItemStackGenerator.createItem(ability.getIcon(), 0, 0, ChatColor.RED + ability.getName(), description, true));
		}
		player.openInventory(inventory);
	}
	
	public static void setupRunesMenu(Player player) {
		Inventory inventory = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Runes");
		
		player.openInventory(inventory);
	}
	
	public static void setupRepairMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Repair Menu");
		
		if(player.getInventory().getItemInMainHand().getType() != Material.AIR) {
			if(ItemStackGenerator.isRepairable(player.getInventory().getItemInMainHand())) {
				if(player.getInventory().getItemInMainHand().getDurability() < 2) {
					inv.setItem(3, ItemStackGenerator.createItem(Material.STRING, 1, 0,
							ChatColor.LIGHT_PURPLE + "Your item in hand doesn't need a repair!", 
							null));
				}
				else {
					ItemStack item = player.getInventory().getItemInMainHand();
					inv.setItem(3, ItemStackGenerator.createItem(item.getType(), item.getAmount(), item.getDurability(),
							ChatColor.LIGHT_PURPLE + "Repair item in main hand", 
							Arrays.asList("Price: " + ChatColor.RED + "1 GT")));
				}
			}
			else {
				inv.setItem(3, ItemStackGenerator.createItem(Material.STRING, 1, 0,
						ChatColor.LIGHT_PURPLE + "Your item in hand can't be repaired!", 
						null));
			}
		}
		else {
			inv.setItem(3, ItemStackGenerator.createItem(Material.STRING, 1, 0,
					ChatColor.LIGHT_PURPLE + "You don't have any item in hand!", 
					null));
		}
		inv.setItem(4, ItemStackGenerator.createItem(Material.CHEST, 1, 0, 
				ChatColor.LIGHT_PURPLE + "Repair entire inventory",
				Arrays.asList("Price: " + ChatColor.RED + "5 GT")));
		
		inv.setItem(8, ItemStackGenerator.createItem(Material.REDSTONE_BLOCK, 0, 0, ChatColor.RED + "Go back!", null));
		player.openInventory(inv);
	}
	
	public static boolean canBuyPlot(Player player) {
		return !player.hasPermission("plots.plot.2");
	}
	
}
