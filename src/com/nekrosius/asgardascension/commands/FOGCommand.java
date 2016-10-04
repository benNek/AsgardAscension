package com.nekrosius.asgardascension.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.files.GodFoodFile;
import com.nekrosius.asgardascension.inventories.MainInventory;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class FOGCommand implements CommandExecutor {

	Main pl;
	public FOGCommand(Main plugin) {
		pl = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		Inventory inv = Bukkit.createInventory(player, MainInventory.getInventorySize(GodFoodFile.getEffectAmount()), ChatColor.BOLD + "FoG");
		for(int i = 1; i <= GodFoodFile.getEffectAmount(); i++) {
			inv.addItem(ItemStackGenerator.createItem(
					GodFoodFile.getFoodType(i), GodFoodFile.getAmount(i), GodFoodFile.getData(i), ChatColor.LIGHT_PURPLE + GodFoodFile.getName(i), null));
		}
		player.openInventory(inv);
		return true;
	}

}
