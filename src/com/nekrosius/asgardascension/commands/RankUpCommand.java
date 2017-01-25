package com.nekrosius.asgardascension.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.inventories.MainInventory;

public class RankUpCommand implements CommandExecutor {
	
	private Main plugin;
	public RankUpCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	public static String MESSAGE_HEADER = ChatColor.GRAY + "[" + ChatColor.RED + "Asgard RankUp" + ChatColor.GRAY + "] ";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(MESSAGE_HEADER + "This command is available only for players!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0){
			if(plugin.getChallenges().getChallenge(player) > 0) {
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(MESSAGE_HEADER + "You have already started the challenge!");
				player.sendMessage(MESSAGE_HEADER + "Is it too hard for you? Type " + ChatColor.RED + "/rankup leave");
				player.sendMessage(MESSAGE_HEADER + "To get back your money!");
				return true;
			}
			if(plugin.getPlayerManager().getRank(player) >= plugin.getChallengesFile().getChallengesAmount()) {
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(MESSAGE_HEADER + "You have completed all challenges!");
				player.sendMessage(MESSAGE_HEADER + "Type /prestige to ascend!");
				return true;
			}
			if(plugin.getEconomy().getBalance(player) < plugin.getChallengesFile().getPrice(plugin.getPlayerManager().getRank(player) + 1) 
					* (plugin.getPlayerManager().getPrestige(player) + 1)) {
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(MESSAGE_HEADER + "You don't have enough money for RankUp! ($" 
						+ plugin.getChallengesFile().getPrice(plugin.getPlayerManager().getRank(player) + 1) 
						* (plugin.getPlayerManager().getPrestige(player) + 1) + ")");
				return true;
			}
			MainInventory.setupRankUpMenu(player);
			return true;
		}
		else if(args.length == 1){
			if(args[0].equalsIgnoreCase("leave")) {
				if(plugin.getChallenges().getChallenge(player) < 1) {
					player.sendMessage(MESSAGE_HEADER + "You have nothing to quit!");
					return true;
				}
				plugin.getChallenges().quitChallenge(player);
				return true;
			}
		}
		return true;
	}

	public Main getPlugin() {
		return plugin;
	}
	
}
