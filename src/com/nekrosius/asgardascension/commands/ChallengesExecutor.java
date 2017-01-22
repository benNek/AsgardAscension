package com.nekrosius.asgardascension.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.inventories.ChallengesMenu;

public class ChallengesExecutor implements CommandExecutor {
	
	Main pl;
	public ChallengesExecutor(Main pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(Lang.HEADERS_MAIN.toString() + "This command is available only for players!");
			return true;
		}
		ChallengesMenu.setup((Player) sender);
		return true;
	}

}
