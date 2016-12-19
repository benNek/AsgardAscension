package com.nekrosius.asgardascension.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.PrestigeType;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.utils.Convert;

public class PrestigeCommand implements CommandExecutor {
	
	private Main pl;
	public PrestigeCommand(Main plugin) {
		pl = plugin;
	}

	public static String MESSAGE_HEADER = ChatColor.GRAY + "[" + ChatColor.RED + "Asgard RankUp" + ChatColor.GRAY + "] ";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(MESSAGE_HEADER + "This command is available only for players!");
			return true;
		}
		Player player = (Player) sender;
		
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("location")) {
				if(!sender.hasPermission("asgardascension.admin")){
					sender.sendMessage(Main.MESSAGE_HEADER + "This command is available only for OPs!");
					return true;
				}
				ConfigFile.setPrestigeLocation(Convert.locationToString(player.getLocation(), true));
				player.sendMessage(Main.MESSAGE_HEADER + "You've succesfully added prestige teleport location!");
				return true;
			}
		}
		
		if(pl.getPlayerManager().getRank(player) != pl.getChallengesFile().getChallengesAmount()) {
			player.sendMessage(MESSAGE_HEADER + "Your Rank is too low for Prestige! Maximum Rank level is "
					+ ChatColor.RED + pl.getChallengesFile().getChallengesAmount());
			return true;
		}
		if(pl.getPlayerManager().getPrestige(player) >= ConfigFile.getMaxPrestige()){
			player.sendMessage(MESSAGE_HEADER + "You've reached maximum level of Prestige!");
			return true;
		}
		if(args.length == 0){
			player.sendMessage(MESSAGE_HEADER + "You're ready to ascend! Type " + ChatColor.RED + "/prestige confirm" + ChatColor.GRAY + " to ascend!");
			return true;
		}
		if(args.length == 1){
			if(!args[0].equalsIgnoreCase("confirm")){
				player.sendMessage(MESSAGE_HEADER + "You're ready to ascend! Type " + ChatColor.RED + "/prestige confirm" + ChatColor.GRAY + " to ascend!");
				return true;
			}
			else {
				pl.getPlayerManager().setPrestige(player, pl.getPlayerManager().getPrestige(player) + 1, PrestigeType.SELF);
				pl.getPlayerManager().setRank(player, 0);
				pl.getPlayerManager().setTokens(player, pl.getPlayerManager().getTokens(player) + ConfigFile.getTokensReward());
				player.teleport(ConfigFile.getPrestigeLocation());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player.getName() + " group remove Odin");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player.getName() + " group add A");
				String comm = ConfigFile.getPrestigeCommand();
				comm = comm.replaceAll("%player", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comm);
				player.sendMessage(MESSAGE_HEADER + "You've ascended! Your Prestige is " + ChatColor.RED 
						+ pl.getPlayerManager().getPrestige(player) + ChatColor.GRAY + "!");
				if(ConfigFile.getTokensReward() > 0) {
					player.sendMessage(MESSAGE_HEADER + "As a reward for hard work you got " + ConfigFile.getTokensReward() 
						+ " token " + Convert.addSuffix(ConfigFile.getTokensReward()) + "!");
				}
			}
			return true;
		}
		return true;
	}

	public Main getPlugin() {
		return pl;
	}
	
}
