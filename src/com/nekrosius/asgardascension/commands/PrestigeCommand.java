package com.nekrosius.asgardascension.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.enums.PrestigeType;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.utils.Convert;

public class PrestigeCommand implements CommandExecutor {
	
	private Main plugin;
	public PrestigeCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Lang.HEADERS_CHALLENGES.toString() + Lang.COMMANDS_ONLY_PLAYER.toString());
			return true;
		}
		Player player = (Player) sender;
		
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("location")) {
				if(!sender.hasPermission("asgardascension.admin")){
					sender.sendMessage(Lang.HEADERS_MAIN.toString() + Lang.COMMANDS_NO_PERMISSION.toString());
					return true;
				}
				ConfigFile.setPrestigeLocation(Convert.locationToString(player.getLocation(), true));
				player.sendMessage(Lang.HEADERS_MAIN.toString() + Lang.PRESTIGE_SET_LOCATION.toString());
				return true;
			}
		}
		if(plugin.getPlayerManager().getPrestige(player) >= ConfigFile.getMaxPrestige()){
			player.sendMessage(Lang.HEADERS_CHALLENGES.toString()
					+ Lang.PRESTIGE_REACHED_MAX.toString());
			return true;
		}
		if(plugin.getPlayerManager().getRank(player) != plugin.getChallengesFile().getChallengesAmount()) {
			player.sendMessage(Lang.HEADERS_CHALLENGES.toString()
					+ Lang.PRESTIGE_RANK_INSUFFICIENT.toString()
						.replaceAll("%s", plugin.getChallengesFile().getTitle(plugin.getChallengesFile().getChallengesAmount())));
			return true;
		}
		if(args.length == 0){
			player.sendMessage(Lang.HEADERS_CHALLENGES.toString()
					+ Lang.PRESTIGE_READY.toString());
			return true;
		}
		if(args.length == 1){
			if(!args[0].equalsIgnoreCase("confirm")){
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString()
						+ Lang.PRESTIGE_READY.toString());
				return true;
			}
			else {
				plugin.getPlayerManager().setPrestige(player, plugin.getPlayerManager().getPrestige(player) + 1, PrestigeType.SELF);
				plugin.getPlayerManager().setRank(player, 0);
				plugin.getPlayerManager().setTokens(player, plugin.getPlayerManager().getTokens(player) + ConfigFile.getTokensReward());
				player.teleport(ConfigFile.getPrestigeLocation());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player.getName() + " group remove Odin");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player.getName() + " group add A");
				String comm = ConfigFile.getPrestigeCommand();
				comm = comm.replaceAll("%player", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comm);
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() 
						+ Lang.PRESTIGE_ASCENDED.toString()
							.replaceAll("%d", String.valueOf(plugin.getPlayerManager().getPrestige(player))));
				if(ConfigFile.getTokensReward() > 0) {
					player.sendMessage(Lang.HEADERS_CHALLENGES.toString() 
							+ Lang.PRESTIGE_TOKEN_REWARD.toString()
								.replaceAll("%d", String.valueOf(ConfigFile.getTokensReward())));
				}
			}
			return true;
		}
		return true;
	}

	public Main getPlugin() {
		return plugin;
	}
	
}
