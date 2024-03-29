package com.nekrosius.asgardascension.inventories;

import java.util.Arrays;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.utils.Convert;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class ChallengesMenu {
	
	private static Main plugin = (Main)Bukkit.getPluginManager().getPlugin("AsgardAscension");
	
	public static void setup(Player player) {
		Inventory inv = Bukkit.createInventory(player, Convert.getInventorySize(plugin.getChallengesFile().getChallengesAmount()), ChatColor.BOLD + "Challenges");
		String status = ChatColor.GREEN + " [COMPLETED]";
		int level = plugin.getPlayerManager().getRank(player);
		int prestige = plugin.getPlayerManager().getPrestige(player);
		for(int i = 1; i <= plugin.getChallengesFile().getChallengesAmount(); i++) {
			if(level + 1 == i)
				status = ChatColor.YELLOW + " [CURRENT]";
			else if(i > level)
				status = ChatColor.RED + " [LOCKED]";
			inv.addItem(ItemStackGenerator.createItem(plugin.getChallengesFile().getTypeMaterial(i), 0, 0, ChatColor.GOLD + "" + i + Convert.getOrdinalFor(i) + " challenge" + status,
					Arrays.asList(ChatColor.GRAY + "Type: " + ChatColor.RED + WordUtils.capitalize(plugin.getChallengesFile().getType(i)),
							ChatColor.GRAY + "Cost: " + Convert.toPrice(plugin.getChallengesFile().getPrice(i) * (prestige + 1), true)), true));
		}
		player.openInventory(inv);
	}

}
