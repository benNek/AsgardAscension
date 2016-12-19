package com.nekrosius.asgardascension.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.files.TribeFile;
import com.nekrosius.asgardascension.inventories.TribesInventory;
import com.nekrosius.asgardascension.managers.TribeManager;
import com.nekrosius.asgardascension.objects.Tribe;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;
import com.nekrosius.asgardascension.utils.Pager;

public class TribeCommand implements CommandExecutor {
	
	public static Map<String, Boolean> socialSpy;
	
	private Main pl;
	public TribeCommand(Main plugin) {
		pl = plugin;
		socialSpy = new HashMap<String, Boolean>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(TribeManager.MESSAGE_HEADER + "This command is only available for players!");
			return true;
		}
		Player player = (Player) sender;
		if(args.length > 1){
			if(args[0].equalsIgnoreCase("description") || args[0].equalsIgnoreCase("desc")){
				if(!TribeManager.isLeader(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're not leader of tribe!");
					return true;
				}
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				String desc = "";
				for(int i = 1; i < args.length; i++){
					desc += args[i] + " ";
				}
				tribe.setDescription(desc);
				TribeFile.createConfig(tribe.getName());
				TribeFile.setDescription(desc);
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've set description to: " + ChatColor.RED + desc);
				return true;
			}
			else if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("chat")) {
				if(!TribeManager.hasTribe(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You don't have tribe!");
					return true;
				}
				String msg = "";
				for(int i = 1; i < args.length; i++){
					msg += args[i] + " ";
				}
				if(TribeManager.isLeader(player.getName())){
					TribeManager.sendChatMessage(TribeManager.getPlayerTribe(player.getName()), 
							TribeManager.MESSAGE_HEADER + "[" + ChatColor.GREEN + player.getName() + ChatColor.GRAY + "] " +
							ChatColor.RED + msg);
				}
				else {
					TribeManager.sendChatMessage(TribeManager.getPlayerTribe(player.getName()), 
							TribeManager.MESSAGE_HEADER + "[" + ChatColor.RED + player.getName() + ChatColor.GRAY + "] " +
							ChatColor.RED + msg);	
				}
				return true;
			}
		}
		if(args.length == 0) {
			if(TribeManager.hasTribe(player.getName())){
				printTribeInfo(player, TribeManager.getPlayerTribe(player.getName()));
			}else{
				helpList(player);
			}
		}
		else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("leave")){
				if(!TribeManager.hasTribe(player.getName())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You don't have tribe!");
					return true;
				}
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				if(!TribeManager.isLeader(player.getName())){
					tribe.removeMember(player.getUniqueId().toString());
					TribeManager.sendMessage(tribe, TribeManager.MESSAGE_HEADER + ChatColor.RED + player.getName() + ChatColor.GRAY + " has left your tribe!");
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You've left your tribe!");
					return true;
				}
				tribe.delete();
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've disbanded your tribe!");
				return true;
			}
			else if(args[0].equalsIgnoreCase("socialspy") || args[0].equalsIgnoreCase("ss")) {
				if(!(player.hasPermission("asgardascension.admin") || player.hasPermission("asgardascension.staff"))) {
					sender.sendMessage(Main.MESSAGE_HEADER + "This command is only available for staff members!");
					return true;
				}
				if(socialSpy.get(player.getName()) == null) {
					socialSpy.put(player.getName(), true);
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You've turned on Social Spy!");
				}
				else {
					socialSpy.remove(player.getName());
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You've turned off Social Spy!");
				}
				
			}
			else if(args[0].equalsIgnoreCase("list")) {
				Pager pager = new Pager();
				List<String> list = TribeManager.getTribeListDescription();
				player.sendMessage(ChatColor.RED + "****" + ChatColor.GRAY + "Page: 1/" + list.size() / 5 + ChatColor.RED + "****");
				for(String str : pager.getPage(list, 1)) {
					player.sendMessage(str);
				}
				// TODO padaryti kad i argumentus gali ivesti
			}
			else if(args[0].equalsIgnoreCase("type")) {
				if(!TribeManager.isLeader(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're not leader of tribe!");
					return true;
				}
				TribesInventory.setupTypeSelectionMenu(player);
			}
			else if(args[0].equalsIgnoreCase("rankup")){
				if(!TribeManager.isLeader(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're not leader of tribe!");
					return true;
				}
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				if(tribe.getLevel() >= 5) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Your tribe reached maximum level! (5)");
					return true;
				}
				if(!TribeManager.canRankUp(tribe)) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Your tribe can't rankup yet! It costs " + ConfigFile.getRankUpPrice(tribe.getLevel() + 1) + " to rankup!");
					return true;
				}
				tribe.setLevel(tribe.getLevel() + 1);
				TribeFile.createConfig(tribe.getName());
				TribeFile.setLevel(tribe.getLevel());
				TribeManager.sendMessage(tribe, TribeManager.MESSAGE_HEADER + "You tribe has ranked up! It's now has level of " + ChatColor.RED + tribe.getLevel());
			}
			else if(args[0].equalsIgnoreCase("chest")) {
				if(!TribeManager.hasTribe(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You don't have tribe!");
					return true;
				}
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				tribe.openInventory(player);
			}
			else if(args[0].equalsIgnoreCase("desc") || args[0].equalsIgnoreCase("description")) {
				if(!TribeManager.hasTribe(player.getName())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You don't have tribe!");
					return true;
				}
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				player.sendMessage(TribeManager.MESSAGE_HEADER + "" + ChatColor.BOLD + "▲▼▲▼▲▼▲▼▲▼▲▼▲▼");
				player.sendMessage(addLinebreaks(tribe.getDescription(), 20));
			}
			else if(args[0].equalsIgnoreCase("help")) {
				helpList(player);
			}
			else if(args[0].equalsIgnoreCase("info")) {
				if(!TribeManager.hasTribe(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You don't have tribe!");
					return true;
				}
				printTribeInfo(player, TribeManager.getPlayerTribe(player.getName()));
			}
			else if(args[0].equalsIgnoreCase("sword")) {
				if(!TribeManager.hasTribe(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You don't have tribe!");
					return true;
				}
				for(ItemStack item : player.getInventory().getContents()){
					if(item != null) {
						if(item.getType().equals(Material.GOLD_SWORD)){
							if(item.getItemMeta().getDisplayName() != null){
								if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "Ultimate Skill")){
									player.sendMessage(TribeManager.MESSAGE_HEADER + "You already have Tribe's Sword!");
									return true;
								}
							}
						}
					}
				}
				player.getInventory().addItem(ItemStackGenerator.createItem(Material.GOLD_SWORD, 0, 0, ChatColor.GRAY + "Ultimate Skill",
						Arrays.asList(ChatColor.RED + "Sneak + Right-Click to use!")));
			}
			else{
				notFound(player);
			}
		}
		else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("create")) {
				if(TribeManager.hasTribe(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You already have tribe! Leave it to create new one!");
					return true;
				}
				if(!Main.econ.has(player, ConfigFile.getTribePrice())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You don't have enough money! To create tribe it costs " + ConfigFile.getTribePrice());
					return true;
				}
				if(TribeManager.getTribe(args[1]) != null){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "There is already tribe named " + args[1] + ". Choose another name!");
					return true;
				}
				String name = args[1];
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've created new Tribe named " + ChatColor.RED + name);
				Tribe tribe = new Tribe(name);
				tribe.setLeader(player.getUniqueId().toString());
				tribe.addMember(player.getUniqueId().toString());
				tribe.setType("aesir");
				TribeManager.addTribe(tribe);
				TribeFile.createConfig(name);
				TribesInventory.setupTypeSelectionMenu(player);
			}
			else if(args[0].equalsIgnoreCase("invite")) {
				if(!TribeManager.isLeader(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're not leader of tribe!");
					return true;
				}
				if(Bukkit.getPlayer(args[1]) == null) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + args[1] + " is not online!");
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				if(TribeManager.hasTribe(target.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + args[1] + " already has tribe!");
					return true;
				}
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				if(tribe.isInvited(target.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You've already invited " + target.getName() + " to tribe!");
					return true;
				}
				if(tribe.getMembers().size() >= ConfigFile.getMaxMembers(tribe.getLevel())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You reached cap of members! " + ConfigFile.getMaxMembers(tribe.getLevel()) + ". Rankup to get new slots!");
					return true;
				}
				tribe.invitePlayer(target.getName());
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've invited " + ChatColor.RED + args[1] + ChatColor.GRAY + " to your tribe!");
				target.sendMessage(TribeManager.MESSAGE_HEADER + "You've been invited to tribe " + ChatColor.RED + TribeManager.getPlayerTribe(player.getName()).getName());
				target.sendMessage(TribeManager.MESSAGE_HEADER + "Type " + ChatColor.RED + "/tribe accept " + tribe.getName() + ChatColor.GRAY + " or " + ChatColor.RED + "/tribe decline " + tribe.getName());
			}
			else if(args[0].equalsIgnoreCase("list")) {
				int page;
				try{
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Type page number!");
					return true;
				}
				List<String> list = TribeManager.getTribeListDescription();
				if(page < 1 || page > list.size() / 5) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Non-Existant page number! (1-" + list.size() / 5 + ")!");
					return true;
				}
				Pager pager = new Pager();
				player.sendMessage(ChatColor.RED + "****" + ChatColor.GRAY + "Page: " + page + "/" + list.size() / 5 + ChatColor.RED + "****");
				for(String msg : pager.getPage(list, page)) {
					player.sendMessage(msg);
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("kick")) {
				if(!TribeManager.isLeader(player.getName())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're not leader of tribe!");
					return true;
				}
				if(Bukkit.getPlayer(args[1]) == null) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + args[1] + " is not online!");
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				if(!(TribeManager.getPlayerTribe(args[1]).equals(TribeManager.getPlayerTribe(player.getName())))){
					player.sendMessage(TribeManager.MESSAGE_HEADER + args[1] + " is not from your tribe!");
					return true;
				}
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				target.sendMessage(TribeManager.MESSAGE_HEADER + "You've been kicked from tribe!");
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've kicked " + ChatColor.RED + args[1] + ChatColor.GRAY + " from your tribe!");
				tribe.removeMember(target.getUniqueId().toString());
				TribeManager.sendMessage(tribe, TribeManager.MESSAGE_HEADER + ChatColor.RED + args[1] + ChatColor.GRAY + " has been kicked from your tribe!");
				TribeFile.createConfig(tribe.getName());
				TribeFile.removeMember(target.getUniqueId().toString());
				return true;
			}
			else if(args[0].equalsIgnoreCase("promote")){
				if(!TribeManager.isLeader(player.getName())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're not leader of tribe!");
					return true;
				}
				if(Bukkit.getPlayer(args[1]) == null) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + args[1] + " is not online!");
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				if(!(TribeManager.getPlayerTribe(args[1]).equals(TribeManager.getPlayerTribe(player.getName())))){
					player.sendMessage(TribeManager.MESSAGE_HEADER + args[1] + " is not from your tribe!");
					return true;
				}
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				tribe.setLeader(target.getUniqueId().toString());
				target.sendMessage(TribeManager.MESSAGE_HEADER + "You're new leader of your tribe!");
				TribeManager.sendMessage(tribe, TribeManager.MESSAGE_HEADER + ChatColor.RED + target.getName() + ChatColor.GRAY + " is new leader of your tribe!");
				TribeFile.createConfig(tribe.getName());
				TribeFile.setLeader(target.getName());
				return true;
			}
			else if(args[0].equalsIgnoreCase("accept")){
				if(TribeManager.getTribe(args[1]) == null) {
					player.sendMessage(TribeManager.MESSAGE_HEADER+ args[1] + " hasn't invited you!");
					return true;
				}
				Tribe tribe = TribeManager.getTribe(args[1]);
				if(!tribe.isInvited(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + args[1] + " hasn't invited you!");
					return true;
				}
				TribeManager.sendMessage(tribe, ChatColor.RED + player.getName() + ChatColor.GRAY + " has joined your tribe!");
				tribe.removeInvite(player.getName());
				tribe.addMember(player.getUniqueId().toString());
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've joined " + ChatColor.RED + tribe.getName());
				TribeFile.createConfig(tribe.getName());
				TribeFile.addMember(player.getUniqueId().toString());
			}
			else if(args[0].equalsIgnoreCase("decline")){
				if(TribeManager.getTribe(args[1]) == null) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + args[1] + " hasn't invited you!");
					return true;
				}
				Tribe tribe = TribeManager.getTribe(args[1]);
				if(!tribe.isInvited(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + args[1] + " hasn't invited you!");
					return true;
				}
				tribe.removeInvite(player.getName());
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've declined request to join " + ChatColor.RED + tribe.getName());
			}
			else if(args[0].equalsIgnoreCase("balance")) {
				if(TribeManager.getTribe(args[1]) == null){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Tribe " + args[1] + " wasn't found! Check your spelling!");
					return true;
				}
				Tribe tribe = TribeManager.getTribe(args[1]);
				
				player.sendMessage(ChatColor.BOLD + tribe.getName());
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "------------------------------------");
				player.sendMessage(ChatColor.GRAY + "Balance: " + ChatColor.RED + tribe.getBalance());
			}
			else if(args[0].equalsIgnoreCase("pay")) {
				if(!TribeManager.hasTribe(player.getName())){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You don't have tribe!");
					return true;
				}
				double amount;
				try{
					amount = Double.parseDouble(args[1]);
				}catch(NumberFormatException e){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Please type number (Like 50.48)!");
					return true;
				}
				if(amount <= 0) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Amount must be greater than 0");
					return true;
				}
				if(!Main.econ.has(player, amount)){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Insufficient funds! (" + amount + ")");
					return true;
				}
				Main.econ.withdrawPlayer(player, amount);
				Tribe tribe = TribeManager.getPlayerTribe(player.getName());
				tribe.setBalance(tribe.getBalance() + amount);
				TribeFile.createConfig(tribe.getName());
				TribeFile.setBalance(TribeFile.getBalance() + amount);
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've donated " + ChatColor.RED + amount + ChatColor.GRAY + " to " + ChatColor.RED + tribe.getName());
				if(amount > 100){
					TribeManager.sendMessage(tribe, TribeManager.MESSAGE_HEADER + ChatColor.RED + player.getName() + ChatColor.GRAY + " has donated " + ChatColor.RED + amount + ChatColor.GRAY + " to your tribe's balance!");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("ally")) {
				if(!TribeManager.isLeader(player.getName())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're not leader of tribe!");
					return true;
				}
				if(TribeManager.getTribe(args[1]) == null){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Tribe " + args[1] + " wasn't found! Check your spelling!");
					return true;
				}
				Tribe t1 = TribeManager.getPlayerTribe(player.getName());
				Tribe t2 = TribeManager.getTribe(args[1]);
				if(t1.equals(t2)){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You can't be allies with your tribe!");
					return true;
				}
				if(t1.isAllyRequested(t2.getName())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You've already sent ally request to " + t2.getName());
					return true;
				}
				if(t2.isAllyRequested(t1.getName())) {
					t2.addAlly(t1.getName());
					t1.addAlly(t2.getName());
					t2.removeAllyRequest(t1.getName());
					t1.removeAllyRequest(t2.getName());
					TribeManager.sendMessage(t1, TribeManager.MESSAGE_HEADER + "You'are now allies with " + ChatColor.RED + t2.getName());
					TribeManager.sendMessage(t2, TribeManager.MESSAGE_HEADER + "You'are now allies with " + ChatColor.RED + t1.getName());
					TribeFile.createConfig(t1.getName());
					TribeFile.addAlly(t2.getName());
					TribeFile.createConfig(t2.getName());
					TribeFile.addAlly(t1.getName());
					return true;
				}
				if(t1.isAlly(t2)) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're already allies with " + t2.getName());
					return true;
				}
				if(t1.isEnemy(t2)) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're enemies with " + t2.getName() + "! You need to end war firstly!");
					return true;
				}
				if(Bukkit.getPlayer(t2.getLeader()) != null) {
					Bukkit.getPlayer(t2.getLeader()).sendMessage(TribeManager.MESSAGE_HEADER +
									t1.getName() + ChatColor.GRAY + " sent you ally request! Type " + 
									ChatColor.RED + "/tribe ally " + t1.getName() + ChatColor.GRAY + " to accept");
				}
				t1.addAllyRequest(t2.getName());
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've succesfully sent ally request for " + ChatColor.RED + t2.getName());
			}
			else if(args[0].equalsIgnoreCase("neutral")) {
				if(!TribeManager.isLeader(player.getName())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're not leader of tribe!");
					return true;
				}
				if(TribeManager.getTribe(args[1]) == null){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Tribe " + args[1] + " wasn't found! Check your spelling!");
					return true;
				}
				Tribe t1 = TribeManager.getPlayerTribe(player.getName());
				Tribe t2 = TribeManager.getTribe(args[1]);
				if(t1.equals(t2)){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You can't be neutral with your tribe!");
					return true;
				}
				if(t1.isNeutralRequested(t2.getName())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You've already sent neutrality request to " + t2.getName());
					return true;
				}
				if(t2.isNeutralRequested(t1.getName())) {
					t2.removeAlly(t1.getName());
					t1.removeAlly(t2.getName());
					t2.removeEnemy(t1.getName());
					t1.removeEnemy(t2.getName());
					t2.removeNeutralRequest(t1.getName());
					t1.removeNeutralRequest(t2.getName());
					TribeManager.sendMessage(t1, TribeManager.MESSAGE_HEADER + "You're now neutral with " + ChatColor.RED + t2.getName());
					TribeManager.sendMessage(t2, TribeManager.MESSAGE_HEADER + "You're now neutral with " + ChatColor.RED + t1.getName());
					TribeFile.createConfig(t1.getName());
					TribeFile.removeAlly(t2.getName());
					TribeFile.removeEnemy(t2.getName());
					TribeFile.createConfig(t2.getName());
					TribeFile.removeAlly(t1.getName());
					TribeFile.removeEnemy(t1.getName());
					return true;
				}
				if(t1.isAlly(t2)) {
					t2.removeAlly(t1.getName());
					t1.removeAlly(t2.getName());
					t2.removeNeutralRequest(t1.getName());
					t1.removeNeutralRequest(t2.getName());
					TribeManager.sendMessage(t1, TribeManager.MESSAGE_HEADER + "You're no longer allies with " + ChatColor.RED + t2.getName());
					TribeManager.sendMessage(t2, TribeManager.MESSAGE_HEADER + "You're no longer allies with " + ChatColor.RED + t1.getName());
					TribeFile.createConfig(t1.getName());
					TribeFile.removeAlly(t2.getName());
					TribeFile.createConfig(t2.getName());
					TribeFile.removeAlly(t1.getName());
					return true;
				}
				else if(!t1.isEnemy(t2)){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're already at neutral state with " + t2.getName());
					return true;
				}
				if(Bukkit.getPlayer(t2.getLeader()) != null) {
					Bukkit.getPlayer(t2.getLeader()).sendMessage(TribeManager.MESSAGE_HEADER +
									t1.getName() + ChatColor.GRAY + " sent you neutral request! Type " + 
									ChatColor.RED + "/tribe neutral " + t1.getName() + ChatColor.GRAY + " to accept");
				}
				t1.addNeutralRequest(t2.getName());
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've succesfully sent neutrality request for " + ChatColor.RED + t2.getName());
			}
			else if(args[0].equalsIgnoreCase("enemy")) {
				if(!TribeManager.isLeader(player.getName())) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're not leader of tribe!");
					return true;
				}
				if(TribeManager.getTribe(args[1]) == null){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Tribe " + args[1] + " wasn't found! Check your spelling!");
					return true;
				}
				Tribe t1 = TribeManager.getPlayerTribe(player.getName());
				Tribe t2 = TribeManager.getTribe(args[1]);
				if(t1.equals(t2)){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You can't be neutral with your tribe!");
					return true;
				}
				if(t1.isAlly(t2)) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "You're allies with " + t2.getName() + ". You need to become neutral to start war!");
					return true;
				}
				t1.addEnemy(t2.getName());
				t2.addEnemy(t1.getName());
				TribeManager.sendMessage(t1, TribeManager.MESSAGE_HEADER + "You're now at war with " + ChatColor.RED + t2.getName());
				TribeManager.sendMessage(t2, TribeManager.MESSAGE_HEADER + "You're now at war with " + ChatColor.RED + t1.getName());
			}
			else if(args[0].equalsIgnoreCase("info")) {
				if(TribeManager.getTribe(args[1]) == null) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Tribe not found! Check your spelling!");
					return true;
				}
				printTribeInfo(player, TribeManager.getTribe(args[1]));
			}
			else{
				notFound(player);
			}
		}
		else if(args.length == 3) {
			if(args[0].equalsIgnoreCase("pay")) {
				if(TribeManager.getTribe(args[1]) == null){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Tribe " + args[1] + " wasn't found! Check your spelling!");
					return true;
				}
				double amount;
				try{
					amount = Double.parseDouble(args[2]);
				}catch(NumberFormatException e){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Please type number (Like 50.48)!");
					return true;
				}
				if(amount <= 0) {
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Amount must be greater than 0");
					return true;
				}
				if(!Main.econ.has(player, amount)){
					player.sendMessage(TribeManager.MESSAGE_HEADER + "Insufficient funds! (" + amount + ")");
					return true;
				}
				Main.econ.withdrawPlayer(player, amount);
				Tribe tribe = TribeManager.getTribe(args[1]);
				tribe.setBalance(tribe.getBalance() + amount);
				TribeFile.createConfig(tribe.getName());
				TribeFile.setBalance(TribeFile.getBalance() + amount);
				player.sendMessage(TribeManager.MESSAGE_HEADER + "You've donated " + ChatColor.RED + amount + ChatColor.GRAY + " to " + ChatColor.RED + tribe.getName());
				if(amount > 100){
					TribeManager.sendMessage(tribe, TribeManager.MESSAGE_HEADER + ChatColor.RED + player.getName() + ChatColor.GRAY + " has donated " + ChatColor.RED + amount + ChatColor.GRAY + " to your tribe's balance!");
				}
				return true;
			}
		}
		return true;
	}
	
	private void notFound(Player player) {
		player.sendMessage(TribeManager.MESSAGE_HEADER + "Command wans't found! Check your syntax!\nType /tribe help for more info!");
	}
	
	private void helpList(Player player) {
		player.sendMessage(TribeManager.MESSAGE_HEADER + "Tribe Commands");
		player.sendMessage(ChatColor.RED + "/tribe create <tribeName>" + ChatColor.GRAY + " - creates a tribe");
		player.sendMessage(ChatColor.RED + "/tribe list" + ChatColor.GRAY + " - shows all tribes");
		player.sendMessage(ChatColor.RED + "/tribe chat" + ChatColor.GRAY + " - sends message to tribe members");
		player.sendMessage(ChatColor.RED + "/tribe leave" + ChatColor.GRAY + " - leaves tribe");
		player.sendMessage(ChatColor.RED + "/tribe chest" + ChatColor.GRAY + " - opens commnunal chest");
		player.sendMessage(ChatColor.RED + "/tribe sword" + ChatColor.GRAY + " - gives tribe sword to use skills");
		player.sendMessage(ChatColor.RED + "/tribe info <*tribeName> " + ChatColor.GRAY + " - shows information of tribe");
		player.sendMessage(ChatColor.RED + "/tribe description" + ChatColor.GRAY + " - displays tribe description");
		player.sendMessage(ChatColor.RED + "/tribe description <description>" + ChatColor.GRAY + " - sets tribe description");
		player.sendMessage(ChatColor.RED + "/tribe type" + ChatColor.GRAY + " - changes tribe type");
		player.sendMessage(ChatColor.RED + "/tribe rankup" + ChatColor.GRAY + " - rankups tribe to next level");
		player.sendMessage(ChatColor.RED + "/tribe invite <playerName>" + ChatColor.GRAY + " - invites player to tribe");
		player.sendMessage(ChatColor.RED + "/tribe kick <playerName>" + ChatColor.GRAY + " - kicks player forom tribe");
		player.sendMessage(ChatColor.RED + "/tribe promote <playerName>" + ChatColor.GRAY + " - sets player as tribe leader");
		player.sendMessage(ChatColor.RED + "/tribe ally <tribeName>" + ChatColor.GRAY + " - offers tribe to become allies");
		player.sendMessage(ChatColor.RED + "/tribe enemy <tribeName>" + ChatColor.GRAY + " - sets tribe as enemy");
		player.sendMessage(ChatColor.RED + "/tribe neutral <tribeName>" + ChatColor.GRAY + " - offers to become neutral");
		player.sendMessage(ChatColor.RED + "/tribe pay <*tribeName> <amount>" + ChatColor.GRAY + " - adds money to tribe's balance");
		player.sendMessage(ChatColor.RED + "/tribe balance <tribeName>" + ChatColor.GRAY + " - displays tribe's balance");
		player.sendMessage(ChatColor.RED + "* before argument means it's optional!");
	}
	
	public String addLinebreaks(String input, int maxLineLength) {
		// http://stackoverflow.com/questions/7528045/large-string-split-into-lines-with-maximum-length-in-java \\
	    StringTokenizer tok = new StringTokenizer(input, " ");
	    StringBuilder output = new StringBuilder(input.length());
	    int lineLen = 0;
	    while (tok.hasMoreTokens()) {
	        String word = tok.nextToken();

	        if (lineLen + word.length() > maxLineLength) {
	            output.append("\n");
	            lineLen = 0;
	        }
	        output.append(word + " ");
	        lineLen += word.length();
	    }
	    return output.toString();
	}
	
	private void printTribeInfo(Player player, Tribe tribe) {
		if(tribe.getLevel() > 5) {
			TribeManager.sendMessage(tribe, ChatColor.RED + "Sorry for bug with tribes, it is fixed now!");
			TribeManager.sendMessage(tribe, ChatColor.RED + "Also your tribe level was reduced to maximum (5)!");
			TribeManager.sendMessage(tribe, ChatColor.RED + "Good luck playing (BenRush)!");
			tribe.setLevel(5);
		}
		
		int titleLength = tribe.getName().length();
		
		String scores = "";
		for(int i = 0; i < titleLength + 4; i++) scores += '-';
		String title = "    " + tribe.getName() + "  ";
		
		player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "/" + scores + "\\");
		player.sendMessage(ChatColor.BOLD + title);
		player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "\\" + scores + "/");
		player.sendMessage(ChatColor.GRAY + "Type: " + ChatColor.RED + WordUtils.capitalize(tribe.getType()));
		player.sendMessage(ChatColor.GRAY + "Leader: " + ChatColor.RED + tribe.getLeader());
		player.sendMessage(ChatColor.GRAY + "Level: " + ChatColor.RED + tribe.getLevel());
		player.sendMessage(ChatColor.GRAY + "Balance: " + ChatColor.RED + tribe.getBalance());
		player.sendMessage(ChatColor.GRAY + "Members: " + ChatColor.RED + tribe.getMembers().size() + "/" + ConfigFile.getMaxMembers(tribe.getLevel()));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Allies: ");
		if(tribe.getAllies().size() > 0){
			String allies = "";
			for(String ally : tribe.getAllies()){
				allies += ally;
				if(!(ally.equals(tribe.getAllies().get(tribe.getAllies().size()-1)))){
					allies += ", ";
				}
			}
			player.sendMessage(ChatColor.RED + allies);
		}
		player.sendMessage(ChatColor.RED + "Enemies: ");
		if(tribe.getEnemies().size() > 0){
			String enemies = "";
			for(String enemy : tribe.getEnemies()){
				enemies += enemy;
				if(!(enemy.equals(tribe.getEnemies().get(tribe.getEnemies().size()-1)))){
					enemies += ", ";
				}
			}
			player.sendMessage(ChatColor.RED + enemies);
		}
	}
	
	public Main getPlugin() {
		return pl;
	}
	
}
