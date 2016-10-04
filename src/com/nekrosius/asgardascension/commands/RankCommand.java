package com.nekrosius.asgardascension.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.nekrosius.asgardascension.Main;

public class RankCommand implements CommandExecutor {

	private Main pl;
	public RankCommand(Main plugin) {
		pl = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!sender.hasPermission("asgardascension.admin")){
			sender.sendMessage(Main.mh + "This command is available only for OPs!");
			return true;
		}
		if(args.length == 1) {
			if(Bukkit.getPlayer(args[0]) == null) {
				sender.sendMessage(Main.mh + "Unknown player!");
				return true;
			}
			sender.sendMessage(Main.mh + Bukkit.getPlayer(args[0]).getName() + " rank is " 
					+ pl.getPlayerManager().getRank(Bukkit.getPlayer(args[0])) + "!");
			return true;
		}
		else if(args.length != 2){
			sender.sendMessage(Main.mh + "Usage: /rank <playerName> <rankId>");
			return true;
		}
		if(Bukkit.getPlayer(args[0]) == null) {
			sender.sendMessage(Main.mh + args[0] + " is offline!");
			return true;
		}
		try{
			Integer.parseInt(args[1]);
		}catch(NumberFormatException e) {
			sender.sendMessage(Main.mh + "Type number for rankId!");
			return true;
		}
		pl.getPlayerManager().setRank(Bukkit.getPlayer(args[0]), Integer.valueOf(args[1]));
		Bukkit.getPlayer(args[0]).sendMessage(pl.getChallenges().mh + "Your rank now is " + args[1] + "!");
		sender.sendMessage(Main.mh + "You've set " + args[0] + " rank to " + args[1] + "!");
		return true;
	}
	
	public Main getPlugin() {
		return pl;
	}

}
