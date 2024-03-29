package com.nekrosius.asgardascension.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.files.RagnorakFile;

public class RagnorakCommand implements CommandExecutor {
	
	private Main plugin;
	public RagnorakCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "This command is available only for players!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0) {
			plugin.getRagnorak().addVote(player);
			return true;
		}
		else if(args.length == 1) {
			if(!sender.isOp()){
				player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "This command is available only for OP!");
				return true;
			}
			if(args[0].equalsIgnoreCase("additem")){
				if(player.getInventory().getItemInMainHand() == null) {
					player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You don't have any item in your hand!");
					return true;
				}
				RagnorakFile.addItem(player.getInventory().getItemInMainHand());
				player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You've succesfully added item to Ragnorak!");
				return true;
			}
			else if(args[0].equalsIgnoreCase("addlocation")) {
				RagnorakFile.addLocation(player.getLocation());
				player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You've succesfully added new drop location!");
				return true;
			}
			else if(args[0].equalsIgnoreCase("start")) {
				player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You've started Ragnorak!");
				plugin.getRagnorak().start();
				return true;
			}
			else{
				player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "Unknown command!");
				return true;
			}
		}else{
			player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "Unknown command!");
			return true;
		}
	}

	public Main getPlugin() {
		return plugin;
	}
	
}
