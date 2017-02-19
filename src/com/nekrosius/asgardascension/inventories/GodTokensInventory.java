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
import com.nekrosius.asgardascension.objects.Rune;
import com.nekrosius.asgardascension.utils.Convert;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class GodTokensInventory {
	
	private static Main plugin = (Main)Bukkit.getPluginManager().getPlugin("AsgardAscension");
	
	public static void setupTokensMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 18, ChatColor.BOLD + "God Tokens Shop");
		
		ItemStack tokensAmount = ItemStackGenerator.createItem(Material.STAINED_GLASS_PANE, 1, 3,
				ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "|"
						+ ChatColor.DARK_PURPLE + "GT: " + ChatColor.LIGHT_PURPLE
						+ plugin.getPlayerManager().getTokens(player)
						+ ChatColor.MAGIC + "|", null);
		
		ItemStack withdraw = ItemStackGenerator.createItem(Material.NETHER_STAR,
				ChatColor.LIGHT_PURPLE + "Withdraw ... GT", null);
		
		// 1st Row
		inv.setItem(1, ItemStackGenerator.createItem(Material.WATCH, ChatColor.LIGHT_PURPLE + "Temporary Tokens",
				Arrays.asList("Click for more information!")));
		inv.setItem(2, ItemStackGenerator.createItem(Material.END_CRYSTAL, ChatColor.LIGHT_PURPLE + "Permanent Tokens",
				Arrays.asList("Click for more information!")));
		inv.setItem(4, withdraw);
		inv.setItem(6, ItemStackGenerator.createItem(Material.ENCHANTED_BOOK, ChatColor.LIGHT_PURPLE + "Runes",
				Arrays.asList("Click for more information!"), true));
		
		// 2nd Row
		if(canBuyPlot(player)) {
			inv.setItem(10, ItemStackGenerator.createItem(Material.STONE_SPADE, ChatColor.LIGHT_PURPLE + "Additional plot access", 
					Arrays.asList("Price: " + ChatColor.LIGHT_PURPLE + "25 GT"), true));
		}
		inv.setItem(11, ItemStackGenerator.createItem(Material.CHEST, ChatColor.LIGHT_PURPLE + "Crate", 
				Arrays.asList("Price: " + ChatColor.LIGHT_PURPLE + "8 GT")));
		inv.setItem(13, withdraw);
		inv.setItem(15, ItemStackGenerator.createItem(Material.ANVIL, ChatColor.LIGHT_PURPLE + "Repair", 
				Arrays.asList("Click for more information!")));
		inv.setItem(16, ItemStackGenerator.createItem(Material.GOLDEN_APPLE, 0, 1, ChatColor.LIGHT_PURPLE + "Odins Apple", 
				Arrays.asList("Price: " + ChatColor.LIGHT_PURPLE + "1 GT")));
		
		for(int i = 0; i < inv.getSize(); i++) {
			if(inv.getItem(i) != null)
				continue;
			inv.setItem(i, tokensAmount);
		}
		
		player.openInventory(inv);
	}
	
	public static void setupAbilitiesMenu(Player player, boolean temporary) {
		String title = temporary ? "Temporary" : "Permanent";
		Inventory inventory = Bukkit.createInventory(player, Convert.getInventorySize(plugin.getAbilityManager().getAbilities().size()),
				ChatColor.BOLD + title + " God Tokens");
		for(Ability ability : plugin.getAbilityManager().getAbilities()) {
			List<String> description = new ArrayList<>(ability.getDescription());
			
			// Adding price depending whetver ability is permanent or temporary
			if(temporary) {
				description.add(ChatColor.GRAY + "Duration: " + ChatColor.RED + "15 minutes");
			}
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
		for(Rune rune : plugin.getRuneManager().getRunes()) {
			
			List<String> description = new ArrayList<>(rune.getDescription());
			
			if(rune.getDuration() != -1) {
				description.add(ChatColor.GRAY + "Duration: " + ChatColor.RED + rune.getDuration() + " seconds");
			}
			
			description.add(ChatColor.GRAY + "Price: " + ChatColor.RED + rune.getPrice() + " GT");
			
			inventory.addItem(ItemStackGenerator.createItem(rune.getIcon(), ChatColor.RED + rune.getName(),
					description));
		}
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
