package com.nekrosius.asgardascension.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.challenges.Challenge;
import com.nekrosius.asgardascension.enums.Lang;

public class RankCommand implements CommandExecutor {

	private Main pl;
	public RankCommand(Main plugin) {
		pl = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!sender.hasPermission("asgardascension.admin")){
			sender.sendMessage(Lang.HEADERS_MAIN.toString() + "This command is available only for OPs!");
			return true;
		}
		if(args.length == 1) {
			if(Bukkit.getPlayer(args[0]) == null) {
				sender.sendMessage(Lang.HEADERS_MAIN.toString() + "Unknown player!");
				return true;
			}
			sender.sendMessage(Lang.HEADERS_MAIN.toString() + Bukkit.getPlayer(args[0]).getName() + " rank is " 
					+ pl.getChallengesFile().getTitle(pl.getPlayerManager().getRank(Bukkit.getPlayer(args[0]))) + "!");
			return true;
		}
		else if(args.length != 2){
			sender.sendMessage(Lang.HEADERS_MAIN.toString() + "Usage: /rank <playerName> <rankId>");
			return true;
		}
		if(Bukkit.getPlayer(args[0]) == null) {
			sender.sendMessage(Lang.HEADERS_MAIN.toString() + args[0] + " is offline!");
			return true;
		}
		int rankId;
		try{
			rankId = Integer.parseInt(args[1]);
		}catch(NumberFormatException e) {
			String rankName = args[1];
			rankId = pl.getChallengesFile().getChallengeId(rankName);
			if(rankId == -1) {
				sender.sendMessage(Lang.HEADERS_MAIN.toString() + "Unknown rank name or id!");
				return true;
			}
			pl.getPlayerManager().setRank(Bukkit.getPlayer(args[0]), rankId);
			Bukkit.getPlayer(args[0]).sendMessage(Challenge.MESSAGE_HEADER + "Your rank now is " + args[1] + "!");
			sender.sendMessage(Lang.HEADERS_MAIN.toString() + "You've set " + args[0] + " rank to " + args[1] + "!");
			return true;
		}
		pl.getPlayerManager().setRank(Bukkit.getPlayer(args[0]), rankId);
		Bukkit.getPlayer(args[0]).sendMessage(Challenge.MESSAGE_HEADER + "Your rank now is " + args[1] + "!");
		sender.sendMessage(Lang.HEADERS_MAIN.toString() + "You've set " + args[0] + " rank to " + args[1] + "!");
		return true;
	}
	
	public Main getPlugin() {
		return pl;
	}

}
