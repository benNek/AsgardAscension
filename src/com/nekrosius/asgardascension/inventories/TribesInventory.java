package com.nekrosius.asgardascension.inventories;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class TribesInventory {

	public static void setupTypeSelectionMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Select Tribe Type");
		inv.setItem(3, ItemStackGenerator.createItem(Material.DIAMOND_SWORD, 0, 0, 
				ChatColor.YELLOW + "" + ChatColor.BOLD + "Aesir", 
				Arrays.asList("Punch damage to everyone in front.")));
		inv.setItem(5, ItemStackGenerator.createItem(Material.FIREBALL, 0, 0, 
				ChatColor.RED + "" + ChatColor.BOLD + "Vanir", 
				Arrays.asList("Shoots massive damage, but slow fireball.")));
		player.openInventory(inv);
	}
	
}
