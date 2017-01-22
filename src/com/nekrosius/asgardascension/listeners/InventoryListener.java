package com.nekrosius.asgardascension.listeners;

import java.util.Arrays;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.challenges.Challenge;
import com.nekrosius.asgardascension.challenges.ChallengeSetup;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.enums.TokenType;
import com.nekrosius.asgardascension.files.GodFoodFile;
import com.nekrosius.asgardascension.files.RagnorakFile;
import com.nekrosius.asgardascension.handlers.FoodSetup;
import com.nekrosius.asgardascension.handlers.GodTokens;
import com.nekrosius.asgardascension.inventories.GodTokensInventory;
import com.nekrosius.asgardascension.inventories.MainInventory;
import com.nekrosius.asgardascension.managers.TribeManager;
import com.nekrosius.asgardascension.objects.GodToken;
import com.nekrosius.asgardascension.objects.Tribe;
import com.nekrosius.asgardascension.utils.Convert;
import com.nekrosius.asgardascension.utils.Cooldowns;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class InventoryListener implements Listener {
	
	private Main pl;
	public InventoryListener(Main plugin) {
		pl = plugin;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(event.getCurrentItem() == null)
			return;
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Asgard Ascension")) {
			event.setCancelled(true);
			if(event.getCurrentItem().getType().equals(Material.NETHER_STAR)){
				MainInventory.setupChallengesInventory(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.DIAMOND)) {
				MainInventory.setupRagnorakInventory(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLDEN_CARROT)){
				MainInventory.setupGodFoodInventory(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)){
				pl.setupFiles();
				player.closeInventory();
				player.sendMessage(Lang.HEADERS_MAIN.toString() + "You've reloaded config files!");
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Rank-Up")) { 
			event.setCancelled(true);
			if(event.getCurrentItem().getType().equals(Material.DIAMOND_HELMET)) {
				pl.getChallenges().startChallenge(player);
				player.closeInventory();
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLD_INGOT)) {
				player.closeInventory();
				if(!Main.econ.has(player, pl.getChallengesFile().getPrice(pl.getPlayerManager().getRank(player) + 1) 
						* 2 * (pl.getPlayerManager().getPrestige(player) + 1))){
					player.sendMessage(Challenge.MESSAGE_HEADER + "You don't have enough money to buy rank-up! (" 
						+ pl.getChallengesFile().getPrice(pl.getPlayerManager().getRank(player) + 1) 
						* 2 * (pl.getPlayerManager().getPrestige(player) + 1) + ")");
					return;
				}
				Main.econ.withdrawPlayer(player, pl.getChallengesFile().getPrice(pl.getPlayerManager().getRank(player) + 1) * 2 * (pl.getPlayerManager().getPrestige(player) + 1));
				pl.getChallenges().setChallenge(player, pl.getPlayerManager().getRank(player) + 1);
				pl.getChallenges().finishChallenge(player, true);
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Rank-ups and Challenges")) {
			event.setCancelled(true);
			if(ChallengeSetup.getStep(player) > 0){
				player.sendMessage(Challenge.MESSAGE_HEADER + "You are already in ChallengeSetup of challenge!");
				return;
			}
			if(event.getCurrentItem().getType().equals(Material.SLIME_BALL)) {
					MainInventory.setupChallengesChoseMenu(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.BOOK_AND_QUILL)) {
				MainInventory.setupEditMenu(player);
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Ragnorak")) {
			event.setCancelled(true);
			if(event.getCurrentItem().getType().equals(Material.LEVER)) {
				player.closeInventory();
				if(pl.getRagnorak().eventStarted){
					player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "Ragnorak has already started!");
				}else{
					player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You've force started Ragnorak!");
					pl.getRagnorak().start();
				}
			}
			else if(event.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
				player.closeInventory();
				if(player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
					player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You don't have any item in your hand!");
				}else{
					RagnorakFile.addItem(player.getInventory().getItemInMainHand());
					player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You've succesfully added item to Ragnorak!");
				}
			}
			else if(event.getCurrentItem().getType().equals(Material.CHEST)) {
				player.closeInventory();
				for(ItemStack item : player.getInventory()) {
					if(item != null) {
						RagnorakFile.addItem(item);
					}
				}
				player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You've added all your items from inventory to Ragnorak!");
			}
			else if(event.getCurrentItem().getType().equals(Material.COAL)) {
				player.closeInventory();
				RagnorakFile.addLocation(player.getLocation());
				player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You've succesfully added new drop location!");
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Challenge type")) {
			event.setCancelled(true);
			if(event.getCurrentItem().getType().equals(Material.GOLD_BOOTS)){
				player.closeInventory();
				pl.getChallengesFile().addChallenge("parkour");
				ChallengeSetup.setStep(player, 1);
				ChallengeSetup.setChallenge(player, pl.getChallengesFile().getChallengesAmount());
				ChallengeSetup.setType(player, "parkour");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(
						Challenge.MESSAGE_HEADER + "You've created " + ChatColor.RED 
						+ pl.getChallengesFile().getChallengesAmount() + Convert.getOrdinalFor(pl.getChallengesFile().getChallengesAmount())
						+ " Parkour"
						+ ChatColor.GRAY +  " challenge");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type amount of price for this challenge!");
				player.sendMessage(Challenge.MESSAGE_HEADER + "(number, like 5002.5)");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type cancel to stop ChallengeSetup of Challenge!");
			}
			else if(event.getCurrentItem().getType().equals(Material.TORCH)){
				player.closeInventory();
				pl.getChallengesFile().addChallenge("maze");
				ChallengeSetup.setStep(player, 1);
				ChallengeSetup.setChallenge(player, pl.getChallengesFile().getChallengesAmount());
				ChallengeSetup.setType(player, "maze");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(
						Challenge.MESSAGE_HEADER + "You've created " + ChatColor.RED 
						+ pl.getChallengesFile().getChallengesAmount() + Convert.getOrdinalFor(pl.getChallengesFile().getChallengesAmount())
						+ " Maze"
						+ ChatColor.GRAY +  " challenge");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type amount of price for this challenge!");
				player.sendMessage(Challenge.MESSAGE_HEADER + "(number, like 5002.5)");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type cancel to stop ChallengeSetup of Challenge!");
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLD_SWORD)){
				player.closeInventory();
				pl.getChallengesFile().addChallenge("fight");
				ChallengeSetup.setStep(player, 1);
				ChallengeSetup.setChallenge(player, pl.getChallengesFile().getChallengesAmount());
				ChallengeSetup.setType(player, "fight");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(
						Challenge.MESSAGE_HEADER + "You've created " + ChatColor.RED 
						+ pl.getChallengesFile().getChallengesAmount() + Convert.getOrdinalFor(pl.getChallengesFile().getChallengesAmount())
						+ " Fight"
						+ ChatColor.GRAY +  " challenge");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type amount of price for this challenge!");
				player.sendMessage(Challenge.MESSAGE_HEADER + "(number, like " + ChatColor.RED + "5002.5" + ChatColor.GRAY + ")");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type cancel to stop ChallengeSetup of Challenge!");
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Edit Challenge")){
			if(event.getCurrentItem().getType().equals(Material.GOLD_BOOTS) 
				|| event.getCurrentItem().getType().equals(Material.TORCH)
					|| event.getCurrentItem().getType().equals(Material.GOLD_SWORD)){
				event.setCancelled(true);
				int id = event.getSlot() + 1;
				MainInventory.setupChallengeEditMenu((Player)event.getWhoClicked(), id);
			}
		}
		else if(event.getInventory().getName().endsWith("Challenge Edit Menu")){
			event.setCancelled(true);
			String invName = event.getInventory().getName().substring(2);
			String challengeId = "";
			for(char s : invName.toCharArray()){
				if(s == ' '){
					break;
				}
				challengeId += s;
			}
			int challenge;
			try{
				challenge = Integer.parseInt(challengeId);
			}catch(NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			if(event.getCurrentItem().getType().equals(Material.IRON_BARDING)){
				pl.getChallenges().setTesting(player, true);
				pl.getChallenges().setChallenge(player, challenge);
				pl.getChallenges().startChallenge(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLD_INGOT)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 1);
				ChallengeSetup.setType(player, pl.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(pl.getChallengesFile().getSpawnpoint(challenge));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type amount of price for this challenge!");
				player.sendMessage(Challenge.MESSAGE_HEADER + "(number, like " + ChatColor.RED + "5002.5" + ChatColor.GRAY + ")");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type cancel to stop ChallengeSetup of Challenge!");
			}
			else if(event.getCurrentItem().getType().equals(Material.COAL)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 2);
				ChallengeSetup.setType(player, pl.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(pl.getChallengesFile().getSpawnpoint(challenge));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Right-Click with Iron Axe to set spawnpoint");
				player.getInventory().addItem(ItemStackGenerator.createItem(Material.IRON_AXE, 0, 0,
					ChatColor.GRAY + "Add Spawnpoint",
					Arrays.asList(ChatColor.RED + "Right-Click to add spawnpoint of " + WordUtils.capitalize(ChallengeSetup.getType(player)) + " challenge!")));
			}
			else if(event.getCurrentItem().getType().equals(Material.NOTE_BLOCK)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 3);
				ChallengeSetup.setType(player, pl.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(pl.getChallengesFile().getSpawnpoint(challenge));
				player.getInventory().setItemInMainHand(ItemStackGenerator.createItem(Material.IRON_AXE, 0, 0,
						ChatColor.GRAY + "Select a Noteblock", 
						Arrays.asList(ChatColor.RED + "Click a block to make it noteblock!")));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Right-Click a block to make it Noteblock!");
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLD_HELMET)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 4);
				ChallengeSetup.setType(player, pl.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(pl.getChallengesFile().getSpawnpoint(challenge));
				player.getInventory().setItemInMainHand(ItemStackGenerator.createItem(Material.IRON_AXE, 0, 0,
						ChatColor.GRAY + "Set Victory Spawnpoint", 
						Arrays.asList(ChatColor.RED + "Right-Click to set spawnpoint upon victory!")));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Right-Click to set spawnpoint upon victory!");
			}
			else if(event.getCurrentItem().getType().equals(Material.WATER_LILY)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 5);
				ChallengeSetup.setType(player, pl.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(pl.getChallengesFile().getSpawnpoint(challenge));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type commands that are going to be executed after completion");
				player.sendMessage(Challenge.MESSAGE_HEADER + "%s " + ChatColor.GRAY + " - player; don't add '/' before command");
				player.sendMessage(Challenge.MESSAGE_HEADER + "For example: " + ChatColor.RED + "ban %s");
				player.sendMessage(Challenge.MESSAGE_HEADER + "To finish ChallengeSetup type " + ChatColor.RED + "finish");
			}
			else if(event.getCurrentItem().getType().equals(Material.BLAZE_POWDER)){
				player.closeInventory();
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "You've removed all Commands!");
				pl.getChallengesFile().removeCommands(challenge);
			}
			else if(event.getCurrentItem().getType().equals(Material.STICK)) {
				player.closeInventory();
				ChallengeSetup.setStep(player, 6);
				ChallengeSetup.setType(player, pl.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				ChallengeSetup.setStep(player, 6);
				player.getInventory().setItemInMainHand(ItemStackGenerator.createItem(Material.IRON_AXE, 1, 0,
					ChatColor.GRAY + "Select Mobs' spawning region",
					Arrays.asList(ChatColor.RED + "Select 2 points")));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Select 2 points for area");
			}
			else if(event.getCurrentItem().getType().equals(Material.MOB_SPAWNER)) {
				player.closeInventory();
				ChallengeSetup.setStep(player, 7);
				ChallengeSetup.setType(player, pl.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(pl.getChallengesFile().getSpawnpoint(challenge));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Type anything to open selection menu");
				player.sendMessage(Challenge.MESSAGE_HEADER + "To finish ChallengeSetup type " + ChatColor.RED + "finish");
				MainInventory.setupMobSpawnInventory(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.BLAZE_ROD)){
				player.closeInventory();
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "You've removed all Mob Spawns!");
				pl.getChallengesFile().removeMonsterSpawns(challenge);
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "FoG")) {
			event.setCancelled(true);
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Food of the Gods")) {
			event.setCancelled(true);
			if(FoodSetup.getStep(player) > 0) {
				player.sendMessage(Lang.HEADERS_FOG.toString() + "You are already in setup of Food of the Gods!");
				return;
			}
			if(event.getCurrentItem().getType().equals(Material.SLIME_BALL)) {
				player.closeInventory();
				FoodSetup.setStep(player, 1);
				FoodSetup.setFoodIndex(player, GodFoodFile.getEffectAmount() + 1);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type the name of this food!");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type cancel to stop setup of Food of the Gods!");
			}
			else if(event.getCurrentItem().getType().equals(Material.BOOK_AND_QUILL)) {
				MainInventory.setupFoodEditListMenu(player);
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Edit Food of the Gods")) {
			event.setCancelled(true);
			if(event.getSlotType().equals(SlotType.CONTAINER)){
				MainInventory.setupFoodEditMenu(player, event.getSlot() + 1);
			}
		}
		
		else if(event.getInventory().getName().endsWith("Edit Menu")) {
			event.setCancelled(true);
			String invName = event.getInventory().getName().substring(2);
			String food = "";
			for(char s : invName.toCharArray()){
				if(s == ' '){
					break;
				}
				food += s;
			}
			int foodId;
			try{
				foodId = Integer.parseInt(food);
			}catch(NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			if(event.getCurrentItem().getType().equals(Material.PAPER)) {
				player.closeInventory();
				FoodSetup.setEditing(player, true);
				FoodSetup.setFoodIndex(player, foodId);
				FoodSetup.setStep(player, 1);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type the name of this food!");
			}
			else if(event.getCurrentItem().getType().equals(Material.WORKBENCH)) {
				player.closeInventory();
				FoodSetup.setEditing(player, true);
				FoodSetup.setFoodIndex(player, foodId);
				FoodSetup.setStep(player, 2);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type ID of item, which represents this Food of Gods!");
			}
			else if(event.getCurrentItem().getType().equals(Material.ARROW)) {
				player.closeInventory();
				FoodSetup.setEditing(player, true);
				FoodSetup.setFoodIndex(player, foodId);
				FoodSetup.setStep(player, 3);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type amount of this required to be used!");
			}
			else if(event.getCurrentItem().getType().equals(Material.STICK)) {
				player.closeInventory();
				FoodSetup.setEditing(player, true);
				FoodSetup.setFoodIndex(player, foodId);
				FoodSetup.setStep(player, 4);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type data value of item (0 for default)!");
			}
			else if(event.getCurrentItem().getType().equals(Material.WATER_LILY)) {
				player.closeInventory();
				FoodSetup.setEditing(player, true);
				FoodSetup.setFoodIndex(player, foodId);
				FoodSetup.setStep(player, 5);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Select Effect Type!");
				MainInventory.setupFoodEffectTypeInventory(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.BLAZE_POWDER)) {
				GodFoodFile.removeEffects(foodId);
			}
			else if(event.getCurrentItem().getType().equals(Material.IRON_BARDING)) {
				player.closeInventory();
				player.getInventory().addItem(
						ItemStackGenerator.createItem(
								GodFoodFile.getFoodType(foodId), 
								GodFoodFile.getAmount(foodId),
								GodFoodFile.getData(foodId),
								ChatColor.LIGHT_PURPLE + GodFoodFile.getName(foodId), null));
			}
		}
		else if(event.getInventory().getName().equals(ChatColor.BOLD + "Select Tribe Type")) {
			event.setCancelled(true);
			Tribe tribe = TribeManager.getPlayerTribe(player.getName());
			if(event.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)){
				player.closeInventory();
				tribe.setType("aesir");
				player.sendMessage(Lang.HEADERS_TRIBES.toString() + "You've set tribe's type to " + ChatColor.RED + "Aesir");
			}
			else if(event.getCurrentItem().getType().equals(Material.FIREBALL)){
				player.closeInventory();
				tribe.setType("vanir");
				player.sendMessage(Lang.HEADERS_TRIBES.toString() + "You've set tribe's type to " + ChatColor.RED + "Vanir");
			}
		}
		// GOD TOKENS TYPE
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "God Tokens Type")) {
			event.setCancelled(true);
			if(event.getCurrentItem().getItemMeta() == null)
				return;
			int tokens = pl.getPlayerManager().getTokens(player);
			if(event.getCurrentItem().getType().equals(Material.BOOK)) {
				return;
			}
			if(event.getCurrentItem().getType().equals(Material.NETHER_STAR)) {
				player.closeInventory();
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "Type amount of GT to withdraw");
				SetupListener.withdrawal.put(player.getName(), true);
				new BukkitRunnable() {
					public void run() {
						if(SetupListener.withdrawal.containsKey(player.getName())) {
							SetupListener.withdrawal.remove(player.getName());
							player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You didn't withdrawn any GT in time!");
						}
					}
				}.runTaskLater(pl, 200L);
			}
			else if(event.getCurrentItem().getType().equals(Material.STONE_SPADE)) {
				player.closeInventory();
				if(!GodTokensInventory.canBuyPlot(player)) {
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "No more upgrades!");
					return;
				}
				if(tokens < 25) {
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You don't have enough god tokens! (" + tokens + ")");
					return;
				}
				pl.getPlayerManager().setTokens(player, tokens - 25);
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player.getName() + " remove plots.plot.1");
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player.getName() + " add plots.plot.2");
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You now have one more available plot!");
			}
			else if(event.getCurrentItem().getType().equals(Material.CHEST)) {	
				player.closeInventory();
				if(tokens < 8) {
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You don't have enough god tokens! (" + tokens + ")");
					return;
				}
				pl.getPlayerManager().setTokens(player, tokens - 8);
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " key 1");
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You've bought a crate!");
			}
			else if(event.getCurrentItem().getType().equals(Material.ANVIL)) {
				GodTokensInventory.setupRepairMenu(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLDEN_APPLE)) {	
				player.closeInventory();
				if(tokens < 1) {
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You don't have enough god tokens! (" + tokens + ")");
					return;
				}
				pl.getPlayerManager().withdrawTokens(player, 1);
				player.getInventory().addItem(
					ItemStackGenerator.createItem(Material.GOLDEN_APPLE, 0, 1,
							ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + "Odins Apple",
							Arrays.asList(ChatColor.GRAY + "Genuine apple of Odin")));
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You've bought a Odins Apple!");
			}
			else {
				GodTokensInventory.setupTokensShopMenu((Player) event.getWhoClicked(),
						TokenType.valueOf(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()));
			}
		}
		// GOD TOKENS SELECTION
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "God Tokens")) {
			event.setCancelled(true);
			if(event.getCurrentItem().getItemMeta() == null)
				return;
			if(event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)) {
				GodTokensInventory.setupTokensMenu((Player) event.getWhoClicked());
				return;
			}
			GodToken token = GodTokens.findToken(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
			if("Fortune".equalsIgnoreCase(token.getName())) {
				event.getWhoClicked().sendMessage(Lang.HEADERS_TOKENS.toString() + "Currently disabled!");
				return;
			}
			if(Cooldowns.getCooldown((Player) event.getWhoClicked(), token.getName()) > 0) {
				event.getWhoClicked().closeInventory();
				event.getWhoClicked().sendMessage(Lang.HEADERS_TOKENS.toString() + "You can use this token in " 
						+ ChatColor.RED + Convert.timeToString((int)(Cooldowns.getCooldown((Player) event.getWhoClicked(), token.getName()) / 1000))
						+ ChatColor.GRAY + "!");
				return;
			}
			if(pl.getPlayerManager().hasPurchasedToken((Player) event.getWhoClicked(), token.getName())) {
				event.getWhoClicked().closeInventory();
				GodTokens.startSkill(event.getWhoClicked().getName(), token);
			}
			else {
				GodTokensInventory.setupTokensDurationMenu((Player) event.getWhoClicked(), token);
			}
		}
		// GOD TOKEN DURATION
		else if(event.getInventory().getName().startsWith(ChatColor.BOLD + "BUY")) {
			event.setCancelled(true);
			if(event.getCurrentItem().getItemMeta() == null)
				return;
			GodToken token = GodTokens.findToken(event.getInventory().getName().substring(6));
			if(event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)) {
				GodTokensInventory.setupTokensShopMenu((Player) event.getWhoClicked(), token.getType());
				return;
			}
			if(event.getCurrentItem().getType().equals(Material.PAPER)) {
				player.closeInventory();
				if(pl.getPlayerManager().getTokens(player) < token.getTempPrice()) {
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You don't have enough tokens! (" + token.getTempPrice() + ")");
					return;
				}
				pl.getPlayerManager().setTokens(player, pl.getPlayerManager().getTokens(player) - token.getTempPrice());
				GodTokens.startSkill(event.getWhoClicked().getName(), token);
			}
			else if(event.getCurrentItem().getType().equals(Material.BOOK)) {
				if(pl.getPlayerManager().getTokens(player) < token.getPermPrice()) {
					player.closeInventory();
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You don't have enough tokens! (" + token.getPermPrice() + ")");
					return;
				}
				player.closeInventory();
				pl.getPlayerManager().setTokens(player, pl.getPlayerManager().getTokens(player) - token.getPermPrice());
				pl.getPlayerManager().addPurchasedToken(player, token.getName());
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You've permanently bought " + ChatColor.RED + token.getName() + ChatColor.GRAY + " ability!");
			}
			
		}
		// GOD TOKEN REPAIR MENU
		else if(event.getInventory().getName().equals(ChatColor.BOLD + "Repair Menu")) {
			if(event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)) {
				GodTokensInventory.setupTokensMenu((Player) event.getWhoClicked());
				return;
			}
			else if(event.getCurrentItem().getType().equals(Material.STRING)) {
				player.closeInventory();
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
			}
			else if(event.getCurrentItem().hasItemMeta()
					&& event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Repair item in main hand")) {
				player.closeInventory();
				if(pl.getPlayerManager().hasTokens(player, 1)) {
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "Your item in hand was successfully repaired!");
					pl.getPlayerManager().withdrawTokens(player, 1);
					player.getInventory().getItemInMainHand().setDurability((short)0);
				}
				else {
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You don't have enough GT! It costs" + ChatColor.RED + " 1 GT");
				}
			}
			else if(event.getCurrentItem().getType().equals(Material.CHEST)) {
				player.closeInventory();
				if(pl.getPlayerManager().hasTokens(player, 5)) {
					for (ItemStack item : player.getInventory().getContents()) {
	                    if (item != null) {
	                        if (ItemStackGenerator.isRepairable(item))
	                            item.setDurability((short) 0);
	                    }
	                }
	                for (ItemStack item : player.getInventory().getArmorContents()) {
	                    if (item != null)
	                        item.setDurability((short) 0);
	                }
	                pl.getPlayerManager().withdrawTokens(player, 5);
	                player.sendMessage(Lang.HEADERS_TOKENS.toString() + "All items in your inventory were successfully repaired!");
				}
				else {
					player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You don't have enough GT! It costs" + ChatColor.RED + " 5 GT");
				}
			}
		}
		
		// CHALLENGES MENU
		else if(event.getInventory().getName().equals(ChatColor.BOLD + "Challenges")) {
			event.setCancelled(true);
			int challenge = event.getRawSlot() + 1;
			int level = pl.getPlayerManager().getRank((Player)event.getWhoClicked());
			if(challenge > level + 1) {
				event.getWhoClicked().sendMessage(Challenge.MESSAGE_HEADER + "Your rank is too low for this challenge!");
			}
			else if(challenge <= level) {
				event.getWhoClicked().sendMessage(Challenge.MESSAGE_HEADER + "You have already completed this challenge!");
			}
			else {
				MainInventory.setupRankUpMenu((Player) event.getWhoClicked());
			}
		}
	}

	public Main getPlugin() {
		return pl;
	}
	
}
