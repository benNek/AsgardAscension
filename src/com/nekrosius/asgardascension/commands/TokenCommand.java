package com.nekrosius.asgardascension.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.inventories.GodTokensInventory;

public class TokenCommand implements CommandExecutor {
	
	private Main plugin;
	public TokenCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(args.length == 3) {
			if(args[0].equalsIgnoreCase("give")){
				if(!sender.hasPermission("asgardascension.admin")){
					sender.sendMessage(Lang.HEADERS_MAIN.toString() + "This command is available only for OPs!");
					return true;
				}
				if(Bukkit.getPlayer(args[1]) == null) {
					sender.sendMessage("Player not found!");
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				int amount;
				try{
					amount = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(Lang.HEADERS_TOKENS.toString() + "Please type number (Like 6)!");
					return true;
				}
				if(amount <= 0) {
					sender.sendMessage(Lang.HEADERS_TOKENS.toString() + "Amount must be greater than 0");
					return true;
				}
				plugin.getPlayerManager().setTokens(target, plugin.getPlayerManager().getTokens(target) + amount);
				if(!sender.getName().equalsIgnoreCase("CONSOLE"))
					target.sendMessage(Lang.HEADERS_TOKENS.toString() + sender.getName() + ChatColor.GRAY + " has sent you " + ChatColor.RED + amount + ChatColor.GRAY + " tokens!");
				return true;
			}
			else if(args[0].equalsIgnoreCase("remove")){
				if(!sender.hasPermission("asgardascension.admin")){
					sender.sendMessage(Lang.HEADERS_MAIN.toString() + "This command is available only for OPs!");
					return true;
				}
				if(Bukkit.getPlayer(args[1]) == null) {
					sender.sendMessage("Player not found!");
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				int amount;
				try{
					amount = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(Lang.HEADERS_TOKENS.toString() + "Please type number (Like 6)!");
					return true;
				}
				if(amount <= 0) {
					sender.sendMessage(Lang.HEADERS_TOKENS.toString() + "Amount must be greater than 0");
					return true;
				}
				if(plugin.getPlayerManager().getTokens(target) - amount < 0) {
					amount = plugin.getPlayerManager().getTokens(target);
				}
				plugin.getPlayerManager().setTokens(target, plugin.getPlayerManager().getTokens(target) - amount);
				target.sendMessage(Lang.HEADERS_TOKENS.toString() + sender.getName() + ChatColor.GRAY + " has removed your " + ChatColor.RED + amount + ChatColor.GRAY + " tokens!");
				return true;
			}
			else if(args[0].equalsIgnoreCase("set")) {
				if(!sender.hasPermission("asgardascension.admin")){
					sender.sendMessage(Lang.HEADERS_MAIN.toString() + "This command is available only for OPs!");
					return true;
				}
				if(Bukkit.getPlayer(args[1]) == null) {
					sender.sendMessage("Player not found!");
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				int amount;
				try{
					amount = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(Lang.HEADERS_TOKENS.toString() + "Please type number (Like 6)!");
					return true;
				}
				if(amount < 0) {
					sender.sendMessage(Lang.HEADERS_TOKENS.toString() + "Amount must be greater than -1");
					return true;
				}
				plugin.getPlayerManager().setTokens(target, amount);
				target.sendMessage(Lang.HEADERS_TOKENS.toString() + sender.getName() + ChatColor.GRAY + " has set your tokens to " + ChatColor.RED + amount + ChatColor.GRAY + "!");
				return true;
			}
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(Lang.HEADERS_TOKENS.toString() + "This command is available only for players!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0) {
			if(player.hasPermission("asgardascension.admin")) {
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "Admin commands");
				player.sendMessage(ChatColor.RED + "/token give <player> <amount> " + ChatColor.GRAY + "gives specified amount of god tokens to player.");
				player.sendMessage(ChatColor.RED + "/token set <player> <amount> " + ChatColor.GRAY + "sets player's tokens to specified amount");
				player.sendMessage(ChatColor.RED + "/token remove <player> <amount> " + ChatColor.GRAY + "removes specified amount of god tokens from player.");
			}
			GodTokensInventory.setupTokensMenu(player);
		}
		return true;
	}
	
	public Main getPlugin() {
		return plugin;
	}

}
