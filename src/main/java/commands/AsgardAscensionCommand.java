package main.java.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.Main;
import main.java.enums.PrestigeType;
import main.java.files.ConfigFile;
import main.java.handlers.GodTokens;
import main.java.inventories.MainInventory;
import net.md_5.bungee.api.ChatColor;

public class AsgardAscensionCommand implements CommandExecutor {
	
	private Main pl;
	public AsgardAscensionCommand(Main plugin) {
		pl = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(Main.mh + "This command is available only for players!");
			return true;
		}
		if(!sender.hasPermission("asgardascension.admin")){
			sender.sendMessage(Main.mh + "This command is available only for OPs!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 2) {
			if(Bukkit.getPlayer(args[0]) != null) {
				String prefix = args[1];
				prefix = ChatColor.translateAlternateColorCodes('&', prefix);
				Player target = Bukkit.getPlayer(args[0]);
				for(World world : Bukkit.getWorlds()) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() 
							+ " prefix " + prefix + " " + world.getName());
				}
				
				return true;
			}
		}
		else if(args.length == 3) {
			if(args[0].equalsIgnoreCase("prestige")) {
				if(Bukkit.getPlayer(args[1]) == null) {	
					player.sendMessage(Main.mh + "player not found!");
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
				if(amount < 0 || amount > ConfigFile.getMaxPrestige()) {
					sender.sendMessage(GodTokens.mh + "Amount must be greater than -1 and lower than " 
							+ ConfigFile.getMaxPrestige() + "!");
					return true;
				}
				pl.getPlayerManager().setPrestige(target, amount, PrestigeType.COMMAND);
				target.sendMessage(Main.mh + "Your prestige has been set to " + amount + "!");
				player.sendMessage(Main.mh + "You've set " + target.getDisplayName() + ChatColor.GRAY + "'s prestige to " + amount + "!");
				return true;
			}
				
		}
		MainInventory.setupInventory(player);
		return true;
	}
	
	public Main getPlugin() {
		return pl;
	}
	
}
