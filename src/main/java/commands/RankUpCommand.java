package main.java.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.Main;
import main.java.inventories.MainInventory;

public class RankUpCommand implements CommandExecutor {
	
	private Main pl;
	public RankUpCommand(Main plugin) {
		pl = plugin;
	}
	
	public static String mh = ChatColor.GRAY + "[" + ChatColor.RED + "Asgard RankUp" + ChatColor.GRAY + "] ";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(mh + "This command is available only for players!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0){
			if(pl.getChallenges().getChallenge(player) > 0) {
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(mh + "You have already started the challenge!");
				player.sendMessage(mh + "Is it too hard for you? Type " + ChatColor.RED + "/rankup leave");
				player.sendMessage(mh + "To get back your money!");
				return true;
			}
			if(pl.getPlayerManager().getRank(player) >= pl.getChallengesFile().getChallengesAmount()) {
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(mh + "You have completed all challenges!");
				player.sendMessage(mh + "Type /prestige to ascend!");
				return true;
			}
			if(Main.econ.getBalance(player) < pl.getChallengesFile().getPrice(pl.getPlayerManager().getRank(player) + 1) 
					* (pl.getPlayerManager().getPrestige(player) + 1)) {
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(mh + "You don't have enough money for RankUp! ($" 
						+ pl.getChallengesFile().getPrice(pl.getPlayerManager().getRank(player) + 1) 
						* (pl.getPlayerManager().getPrestige(player) + 1) + ")");
				return true;
			}
			MainInventory.setupRankUpMenu(player);
			return true;
		}
		else if(args.length == 1){
			if(args[0].equalsIgnoreCase("leave")) {
				if(pl.getChallenges().getChallenge(player) < 1) {
					player.sendMessage(mh + "You have nothing to quit!");
					return true;
				}
				pl.getChallenges().quitChallenge(player);
				return true;
			}
		}
		return true;
	}

	public Main getPlugin() {
		return pl;
	}
	
}
