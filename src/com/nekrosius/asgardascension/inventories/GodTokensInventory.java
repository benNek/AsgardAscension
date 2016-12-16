package com.nekrosius.asgardascension.inventories;

import java.util.Arrays;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.TokenType;
import com.nekrosius.asgardascension.handlers.GodTokens;
import com.nekrosius.asgardascension.objects.GodToken;
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
	
	public static void setupTokensShopMenu(Player player, TokenType type) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "God Tokens");
		for(GodToken token : GodTokens.tokens) {
			if(token.getType().equals(type)) {
				inv.addItem(ItemStackGenerator.createItem(token.getIcon(),
						0, 0, ChatColor.LIGHT_PURPLE + token.getName(),
						token.getDescription(), true));
			}
		}
		inv.setItem(8, ItemStackGenerator.createItem(Material.REDSTONE_BLOCK, 0, 0, ChatColor.RED + "Go back!", null));
		player.openInventory(inv);
	}
	
	public static void setupTokensMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 18, ChatColor.BOLD + "God Tokens Type");
		inv.setItem(0, ItemStackGenerator.createItem(Material.BOOK, 0, 0,
				ChatColor.GRAY + "GT: " + ChatColor.RED + plugin.getPlayerManager().getTokens(player), null));
		inv.setItem(9, ItemStackGenerator.createItem(Material.BOOK, 0, 0,
				ChatColor.GRAY + "GT: " + ChatColor.RED + plugin.getPlayerManager().getTokens(player), null));
		inv.setItem(8, ItemStackGenerator.createItem(Material.NETHER_STAR, 0, 0,
				ChatColor.LIGHT_PURPLE + "Withdraw 1 GT", null));
		inv.setItem(17, ItemStackGenerator.createItem(Material.NETHER_STAR, 0, 0,
				ChatColor.LIGHT_PURPLE + "Withdraw 1 GT", null));
		
		if(canBuyPlot(player)) {
			inv.setItem(1, ItemStackGenerator.createItem(Material.STONE_SPADE, 0, 0, ChatColor.LIGHT_PURPLE + "Additional plot access", 
					Arrays.asList("Price: " + ChatColor.RED + "25 GT"), true));
		}

		//inv.setItem(2, ItemStackGenerator.createItem(Material.CHEST, 0, 0, ChatColor.LIGHT_PURPLE + "Donator rank", 
		//		Arrays.asList(ChatColor.GREEN + "Price: " + ChatColor.RED + "8 god tokens")));
		Material values[] = {Material.GOLD_SWORD, Material.GOLD_PICKAXE, Material.DOUBLE_PLANT};
		int i = 0;
		for(TokenType type : TokenType.values()) {
			inv.setItem(i + 3, ItemStackGenerator.createItem(values[i], 0, 0,
					ChatColor.LIGHT_PURPLE + WordUtils.capitalize(type.name().toLowerCase()), null, true));
			i++;
		}
		inv.setItem(12, ItemStackGenerator.createItem(Material.CHEST, 0, 0, ChatColor.LIGHT_PURPLE + "Crate", 
				Arrays.asList("Price: " + ChatColor.RED + "8 GT")));
		inv.setItem(13, ItemStackGenerator.createItem(Material.ANVIL, 0, 0, ChatColor.LIGHT_PURPLE + "Repair", 
				Arrays.asList("Click for more information!")));
		inv.setItem(14, ItemStackGenerator.createItem(Material.GOLDEN_APPLE, 0, 1, ChatColor.LIGHT_PURPLE + "Odins Apple", 
				Arrays.asList("Price: " + ChatColor.RED + "1 GT")));
		
		player.openInventory(inv);
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
