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
			sender.sendMessage(Lang.HEADERS_MAIN.toString()
					+ Lang.COMMANDS_ONLY_PLAYER.toString());
			return true;
		}
		if(!sender.hasPermission("asgardascension.admin")){
			sender.sendMessage(Lang.HEADERS_MAIN.toString()
					+ Lang.COMMANDS_NO_PERMISSION.toString());
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
				player.sendMessage(Lang.HEADERS_MAIN.toString()
						+ Lang.COMMANDS_UNKNOWN_COMMAND.toString());
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
					player.sendMessage(Lang.HEADERS_MAIN.toString()
							+ Lang.COMMANDS_PLAYER_NOT_FOUND.toString());
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				int amount;
				try{
					amount = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(Lang.HEADERS_MAIN.toString());
					sender.sendMessage(Lang.HEADERS_TOKENS.toString()
							+ Lang.PRESTIGE_SET_NOT_NUMBER.toString());
					return true;
				}
				if(amount < 0 || amount > ConfigFile.getMaxPrestige()) {
					sender.sendMessage(Lang.HEADERS_TOKENS.toString()
							+ Lang.PRESTIGE_SET_NOT_IN_RANGE.toString()
								.replaceAll("%p", String.valueOf(ConfigFile.getMaxPrestige())));
					return true;
				}
				pl.getPlayerManager().setPrestige(target, amount, PrestigeType.COMMAND);
				target.sendMessage(Lang.HEADERS_MAIN.toString()
						+ Lang.PRESTIGE_SET_FOR_TARGET.toString()
							.replaceAll("%p", String.valueOf(amount)));
				player.sendMessage(Lang.HEADERS_MAIN.toString()
						+ Lang.PRESTIGE_SET_FOR_SENDER.toString()
							.replaceAll("%s", target.getDisplayName())
							.replaceAll("%p", String.valueOf(amount)));
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
