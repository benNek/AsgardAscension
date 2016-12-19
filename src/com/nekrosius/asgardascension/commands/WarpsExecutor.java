package com.nekrosius.asgardascension.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.files.WarpsFile;

import net.md_5.bungee.api.ChatColor;

public class WarpsExecutor implements CommandExecutor {
	
	Main plugin;
	public WarpsExecutor(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(Main.MESSAGE_HEADER + "This command is only available for players!");
			return true;
		}
		if(!sender.hasPermission("asgardascension.admin")){
			sender.sendMessage(Main.MESSAGE_HEADER + "This command is available only for OPs!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 1) {
			String title = args[0];
			if(!WarpsFile.warpExists(title)) {
				player.sendMessage(Main.MESSAGE_HEADER + title + " warp does not exist!");
				return true;
			}
			else {
				if(!player.hasPermission("tokenwarp." + title)) {
					player.sendMessage(Main.MESSAGE_HEADER + "You do not have permission to use this warp!");
					return true;
				}
				else {
					if(!plugin.getPlayerManager().hasTokens(player, WarpsFile.getPrice(title))) {
						player.sendMessage(Main.MESSAGE_HEADER + "You do not have enough GT for this warp!");
						return true;
					}
					else {
						plugin.getPlayerManager().withdrawTokens(player, WarpsFile.getPrice(title));
						player.teleport(WarpsFile.getLocation(title));
						return true;
					}
				}
			}
		}
		if(!sender.hasPermission("asgardascension.admin")){
			sender.sendMessage(Main.MESSAGE_HEADER + "This command is available only for OPs!");
			return true;
		}
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("setWarp")) {
				String title = args[0];
				WarpsFile.setLocation(title, player.getLocation());
				player.sendMessage(Main.MESSAGE_HEADER + "You've set the warp location of " + title);
				return true;
			}
		}
		else if(args.length == 3) {
			if(args[0].equalsIgnoreCase("setWarp")) {
				String title = args[0];
				int price;
				try {
					price = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.sendMessage(Main.MESSAGE_HEADER + "Price can only be an integer!");
					return true;
				}
				WarpsFile.setPrice(title, price);
				player.sendMessage(Main.MESSAGE_HEADER + "You've set the price of " + title + " to " + price + " GT");
				return true;
			}
		}
		player.sendMessage(Main.MESSAGE_HEADER + "Commands: ");
		player.sendMessage(ChatColor.RED + "/tokenwarp setWarp <WarpName> " + ChatColor.GRAY + " - sets location of warp");
		player.sendMessage(ChatColor.RED + "/tokenwarp setWarp <WarpName> <Price>" + ChatColor.GRAY + " - sets price of warp");
		return true;
	}

}
