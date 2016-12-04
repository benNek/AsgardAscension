package com.nekrosius.asgardascension.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.files.RagnorakFile;
import com.nekrosius.asgardascension.handlers.Ragnorak;

public class RagnorakCommand implements CommandExecutor {
	
	private Main pl;
	public RagnorakCommand(Main plugin) {
		pl = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Ragnorak.mh + "This command is available only for players!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0) {
			if(Ragnorak.hasStarted()){
				player.sendMessage(Ragnorak.mh + "Ragnorak already started!");
				return true;
			}
			else if(Ragnorak.onCooldown()){
				player.sendMessage(Ragnorak.mh + "Ragnorak occured not so long ago!");
				player.sendMessage(Ragnorak.mh + "Next Ragnorak in " + ChatColor.RED + (RagnorakFile.getTime() - Ragnorak.minutes) + ChatColor.GRAY + " minutes!");
				return true;
			}
			else if(Ragnorak.hasVoted(player.getName())) {
				player.sendMessage(Ragnorak.mh + "You've already voted!");
				return true;
			}
			int toStart = (int)(Bukkit.getOnlinePlayers().size() * RagnorakFile.getPercentageMultiplier()) - Ragnorak.getVotes();
			if(toStart > 0){
				player.sendMessage(Ragnorak.mh + "You've voted for " + ChatColor.RED 
						+ "Ragnorak" + ChatColor.GRAY + " to start! " + toStart + " mores votes to start!");
			}else{
				player.sendMessage(Ragnorak.mh + "You've voted for " + ChatColor.RED 
						+ "Ragnorak" + ChatColor.GRAY + " to start!");
			}
			Ragnorak.setVoted(player.getName(), true);
			return true;
		}
		else if(args.length == 1) {
			if(!sender.isOp()){
				player.sendMessage(Ragnorak.mh + "This command is available only for OP!");
				return true;
			}
			if(args[0].equalsIgnoreCase("additem")){
				if(player.getInventory().getItemInMainHand() == null) {
					player.sendMessage(Ragnorak.mh + "You don't have any item in your hand!");
					return true;
				}
				RagnorakFile.addItem(player.getInventory().getItemInMainHand());
				player.sendMessage(Ragnorak.mh + "You've succesfully added item to Ragnorak!");
				return true;
			}
			else if(args[0].equalsIgnoreCase("addlocation")) {
				RagnorakFile.addLocation(player.getLocation());
				player.sendMessage(Ragnorak.mh + "You've succesfully added new drop location!");
				return true;
			}
			else if(args[0].equalsIgnoreCase("start")) {
				player.sendMessage(Ragnorak.mh + "You've started Ragnorak!");
				Ragnorak.start();
				return true;
			}
			else{
				player.sendMessage(Ragnorak.mh + "Unknown command!");
				return true;
			}
		}else{
			player.sendMessage(Ragnorak.mh + "Unknown command!");
			return true;
		}
	}

	public Main getPlugin() {
		return pl;
	}
	
}
