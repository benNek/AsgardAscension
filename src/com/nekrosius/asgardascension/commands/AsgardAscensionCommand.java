package com.nekrosius.asgardascension.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.enums.PrestigeType;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.inventories.MainInventory;

import net.md_5.bungee.api.ChatColor;

public class AsgardAscensionCommand implements CommandExecutor {
	
	private Main pl;
	public AsgardAscensionCommand(Main plugin) {
		pl = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(Lang.HEADERS_MAIN.toString() + "This command is available only for players!");
			return true;
		}
		if(!sender.hasPermission("asgardascension.admin")){
			sender.sendMessage(Lang.HEADERS_MAIN.toString() + "This command is available only for OPs!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 1) {
			if("info".equalsIgnoreCase(args[0])) {
				player.sendMessage(ChatColor.GRAY + "----------------------");
				player.sendMessage(ChatColor.RED + pl.getDescription().getName());
				player.sendMessage(ChatColor.GRAY + "Author: " + ChatColor.RED + pl.getDescription().getAuthors().get(0));
				player.sendMessage(ChatColor.GRAY + "Version: " + ChatColor.RED + pl.getDescription().getVersion());
				player.sendMessage(ChatColor.GRAY + "Description: " + ChatColor.RED + pl.getDescription().getDescription());
				player.sendMessage(ChatColor.GRAY + "----------------------");
			}
			else {
				player.sendMessage(Lang.HEADERS_MAIN.toString() + "Unknown command! Are you looking for /" + cmdLabel + " info?");
			}
			return true;
		}
		else if(args.length == 2) {
			if(Bukkit.getPlayer(args[0]) != null) {
				String prefix = args[1];
				prefix = ChatColor.translateAlternateColorCodes('&', prefix);
				Player target = Bukkit.getPlayer(args[0]);
				for(World world : Bukkit.getWorlds()) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() 
							+ " prefix " + prefix + " " + world.getName());
				}
				
				return true;
			}
		}
		else if(args.length == 3) {
			if("prestige".equalsIgnoreCase(args[0])) {
				if(Bukkit.getPlayer(args[1]) == null) {	
					player.sendMessage(Lang.HEADERS_MAIN.toString() + "Player not found!");
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				int amount;
				try{
					amount = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(Lang.HEADERS_MAIN.toString());
					sender.sendMessage(Lang.HEADERS_TOKENS.toString() + "Please type number (Like 6)!");
					return true;
				}
				if(amount < 0 || amount > ConfigFile.getMaxPrestige()) {
					sender.sendMessage(Lang.HEADERS_TOKENS.toString() + "Amount must be greater than -1 and lower than " 
							+ ConfigFile.getMaxPrestige() + "!");
					return true;
				}
				pl.getPlayerManager().setPrestige(target, amount, PrestigeType.COMMAND);
				target.sendMessage(Lang.HEADERS_MAIN.toString() + "Your prestige has been set to " + amount + "!");
				player.sendMessage(Lang.HEADERS_MAIN.toString() + "You've set " + target.getDisplayName() + ChatColor.GRAY + "'s prestige to " + amount + "!");
				return true;
			}
				
		}
		MainInventory.setupInventory(player);
		return true;
	}
	
	public Main getPlugin() {
		return pl;
	}
	
}
