package com.nekrosius.asgardascension.commands;

import java.util.ArrayList;
import java.util.List;

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
import com.nekrosius.asgardascension.utils.Convert;
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
			
			List<String> effects = new ArrayList<>();
			for(String str : GodFoodFile.GetEffects(i)) {
				String[] values = str.split(", ");
				effects.add(ChatColor.RED + values[0] + " " + Convert.arabicToRome(Integer.parseInt(values[2])) + ChatColor.LIGHT_PURPLE + " " + values[1] + " sec.");
			}
			
			inv.addItem(ItemStackGenerator.createItem(
					GodFoodFile.getFoodType(i), GodFoodFile.getAmount(i), GodFoodFile.getData(i), ChatColor.LIGHT_PURPLE + GodFoodFile.getName(i), effects));
		}
		player.openInventory(inv);
		return true;
	}

}
