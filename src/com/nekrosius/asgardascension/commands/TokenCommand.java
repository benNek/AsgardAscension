package com.nekrosius.asgardascension.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.handlers.GodTokens;
import com.nekrosius.asgardascension.inventories.GodTokensInventory;

public class TokenCommand implements CommandExecutor {
	
	private Main pl;
	public TokenCommand(Main plugin) {
		pl = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(args.length == 3) {
			if(args[0].equalsIgnoreCase("give")){
				if(!sender.hasPermission("asgardascension.admin")){
					sender.sendMessage(Main.mh + "This command is available only for OPs!");
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
					sender.sendMessage(GodTokens.mh + "Please type number (Like 6)!");
					return true;
				}
				if(amount <= 0) {
					sender.sendMessage(GodTokens.mh + "Amount must be greater than 0");
					return true;
				}
				pl.getPlayerManager().setTokens(target, pl.getPlayerManager().getTokens(target) + amount);
				if(!sender.getName().equalsIgnoreCase("CONSOLE"))
					target.sendMessage(GodTokens.mh + sender.getName() + ChatColor.GRAY + " has sent you " + ChatColor.RED + amount + ChatColor.GRAY + " tokens!");
				return true;
			}
			else if(args[0].equalsIgnoreCase("remove")){
				if(!sender.hasPermission("asgardascension.admin")){
					sender.sendMessage(Main.mh + "This command is available only for OPs!");
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
					sender.sendMessage(GodTokens.mh + "Please type number (Like 6)!");
					return true;
				}
				if(amount <= 0) {
					sender.sendMessage(GodTokens.mh + "Amount must be greater than 0");
					return true;
				}
				if(pl.getPlayerManager().getTokens(target) - amount < 0) {
					amount = pl.getPlayerManager().getTokens(target);
				}
				pl.getPlayerManager().setTokens(target, pl.getPlayerManager().getTokens(target) - amount);
				target.sendMessage(GodTokens.mh + sender.getName() + ChatColor.GRAY + " has removed your " + ChatColor.RED + amount + ChatColor.GRAY + " tokens!");
				return true;
			}
			else if(args[0].equalsIgnoreCase("set")) {
				if(!sender.hasPermission("asgardascension.admin")){
					sender.sendMessage(Main.mh + "This command is available only for OPs!");
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
					sender.sendMessage(GodTokens.mh + "Please type number (Like 6)!");
					return true;
				}
				if(amount < 0) {
					sender.sendMessage(GodTokens.mh + "Amount must be greater than -1");
					return true;
				}
				pl.getPlayerManager().setTokens(target, amount);
				target.sendMessage(GodTokens.mh + sender.getName() + ChatColor.GRAY + " has set your tokens to " + ChatColor.RED + amount + ChatColor.GRAY + "!");
				return true;
			}
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(GodTokens.mh + "This command is available only for players!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0) {
			if(player.hasPermission("asgardascension.admin")) {
				player.sendMessage(GodTokens.mh + "Admin commands");
				player.sendMessage(ChatColor.RED + "/token give <player> <amount> " + ChatColor.GRAY + "gives specified amount of god tokens to player.");
				player.sendMessage(ChatColor.RED + "/token set <player> <amount> " + ChatColor.GRAY + "sets player's tokens to specified amount");
				player.sendMessage(ChatColor.RED + "/token remove <player> <amount> " + ChatColor.GRAY + "removes specified amount of god tokens from player.");
			}
			if(!(GodTokens.getSkill(player.getName()).equals(""))){
				player.sendMessage(GodTokens.mh + "Your current God Token is being used! Wait for it to end!");
				return true;
			}
			GodTokensInventory.setupTokensMenu(player);
		}
		return true;
	}
	
	public Main getPlugin() {
		return pl;
	}

}
