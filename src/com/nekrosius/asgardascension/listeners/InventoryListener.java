package com.nekrosius.asgardascension.listeners;

import java.util.Arrays;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.challenges.ChallengeSetup;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.files.GodFoodFile;
import com.nekrosius.asgardascension.files.RagnorakFile;
import com.nekrosius.asgardascension.handlers.FoodSetup;
import com.nekrosius.asgardascension.inventories.MainInventory;
import com.nekrosius.asgardascension.managers.TribeManager;
import com.nekrosius.asgardascension.objects.Tribe;
import com.nekrosius.asgardascension.utils.Convert;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class InventoryListener implements Listener {
	
	private Main plugin;
	public InventoryListener(Main plugin) {
		this.plugin = plugin;
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
				plugin.setupFiles();
				player.closeInventory();
				player.sendMessage(Lang.HEADERS_MAIN.toString() + "You've reloaded config files!");
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Rank-Up")) { 
			event.setCancelled(true);
			if(event.getCurrentItem().getType().equals(Material.DIAMOND_HELMET)) {
				plugin.getChallenges().startChallenge(player);
				player.closeInventory();
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLD_INGOT)) {
				player.closeInventory();
				if(!plugin.getEconomy().has(player, plugin.getChallengesFile().getPrice(plugin.getPlayerManager().getRank(player) + 1) 
						* 2 * (plugin.getPlayerManager().getPrestige(player) + 1))){
					player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "You don't have enough money to buy rank-up! (" 
						+ plugin.getChallengesFile().getPrice(plugin.getPlayerManager().getRank(player) + 1) 
						* 2 * (plugin.getPlayerManager().getPrestige(player) + 1) + ")");
					return;
				}
				plugin.getEconomy().withdrawPlayer(player, plugin.getChallengesFile().getPrice(plugin.getPlayerManager().getRank(player) + 1) * 2 * (plugin.getPlayerManager().getPrestige(player) + 1));
				plugin.getChallenges().setChallenge(player, plugin.getPlayerManager().getRank(player) + 1);
				plugin.getChallenges().finishChallenge(player, true);
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Rank-ups and Challenges")) {
			event.setCancelled(true);
			if(ChallengeSetup.getStep(player) > 0){
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "You are already in ChallengeSetup of challenge!");
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
				if(plugin.getRagnorak().eventStarted){
					player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "Ragnorak has already started!");
				}else{
					player.sendMessage(Lang.HEADERS_RAGNORAK.toString() + "You've force started Ragnorak!");
					plugin.getRagnorak().start();
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
				plugin.getChallengesFile().addChallenge("parkour");
				ChallengeSetup.setStep(player, 1);
				ChallengeSetup.setChallenge(player, plugin.getChallengesFile().getChallengesAmount());
				ChallengeSetup.setType(player, "parkour");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(
						Lang.HEADERS_CHALLENGES.toString() + "You've created " + ChatColor.RED 
						+ plugin.getChallengesFile().getChallengesAmount() + Convert.getOrdinalFor(plugin.getChallengesFile().getChallengesAmount())
						+ " Parkour"
						+ ChatColor.GRAY +  " challenge");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type amount of price for this challenge!");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "(number, like 5002.5)");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type cancel to stop ChallengeSetup of Challenge!");
			}
			else if(event.getCurrentItem().getType().equals(Material.TORCH)){
				player.closeInventory();
				plugin.getChallengesFile().addChallenge("maze");
				ChallengeSetup.setStep(player, 1);
				ChallengeSetup.setChallenge(player, plugin.getChallengesFile().getChallengesAmount());
				ChallengeSetup.setType(player, "maze");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(
						Lang.HEADERS_CHALLENGES.toString() + "You've created " + ChatColor.RED 
						+ plugin.getChallengesFile().getChallengesAmount() + Convert.getOrdinalFor(plugin.getChallengesFile().getChallengesAmount())
						+ " Maze"
						+ ChatColor.GRAY +  " challenge");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type amount of price for this challenge!");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "(number, like 5002.5)");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type cancel to stop ChallengeSetup of Challenge!");
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLD_SWORD)){
				player.closeInventory();
				plugin.getChallengesFile().addChallenge("fight");
				ChallengeSetup.setStep(player, 1);
				ChallengeSetup.setChallenge(player, plugin.getChallengesFile().getChallengesAmount());
				ChallengeSetup.setType(player, "fight");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(
						Lang.HEADERS_CHALLENGES.toString() + "You've created " + ChatColor.RED 
						+ plugin.getChallengesFile().getChallengesAmount() + Convert.getOrdinalFor(plugin.getChallengesFile().getChallengesAmount())
						+ " Fight"
						+ ChatColor.GRAY +  " challenge");
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type amount of price for this challenge!");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "(number, like " + ChatColor.RED + "5002.5" + ChatColor.GRAY + ")");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type cancel to stop ChallengeSetup of Challenge!");
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
				plugin.getChallenges().setTesting(player, true);
				plugin.getChallenges().setChallenge(player, challenge);
				plugin.getChallenges().startChallenge(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLD_INGOT)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 1);
				ChallengeSetup.setType(player, plugin.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(plugin.getChallengesFile().getSpawnpoint(challenge));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type amount of price for this challenge!");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "(number, like " + ChatColor.RED + "5002.5" + ChatColor.GRAY + ")");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type cancel to stop ChallengeSetup of Challenge!");
			}
			else if(event.getCurrentItem().getType().equals(Material.COAL)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 2);
				ChallengeSetup.setType(player, plugin.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(plugin.getChallengesFile().getSpawnpoint(challenge));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Right-Click with Iron Axe to set spawnpoint");
				player.getInventory().addItem(ItemStackGenerator.createItem(Material.IRON_AXE, 0, 0,
					ChatColor.GRAY + "Add Spawnpoint",
					Arrays.asList(ChatColor.RED + "Right-Click to add spawnpoint of " + WordUtils.capitalize(ChallengeSetup.getType(player)) + " challenge!")));
			}
			else if(event.getCurrentItem().getType().equals(Material.NOTE_BLOCK)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 3);
				ChallengeSetup.setType(player, plugin.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(plugin.getChallengesFile().getSpawnpoint(challenge));
				player.getInventory().setItemInMainHand(ItemStackGenerator.createItem(Material.IRON_AXE, 0, 0,
						ChatColor.GRAY + "Select a Noteblock", 
						Arrays.asList(ChatColor.RED + "Click a block to make it noteblock!")));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Right-Click a block to make it Noteblock!");
			}
			else if(event.getCurrentItem().getType().equals(Material.GOLD_HELMET)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 4);
				ChallengeSetup.setType(player, plugin.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(plugin.getChallengesFile().getSpawnpoint(challenge));
				player.getInventory().setItemInMainHand(ItemStackGenerator.createItem(Material.IRON_AXE, 0, 0,
						ChatColor.GRAY + "Set Victory Spawnpoint", 
						Arrays.asList(ChatColor.RED + "Right-Click to set spawnpoint upon victory!")));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Right-Click to set spawnpoint upon victory!");
			}
			else if(event.getCurrentItem().getType().equals(Material.WATER_LILY)){
				player.closeInventory();
				ChallengeSetup.setStep(player, 5);
				ChallengeSetup.setType(player, plugin.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(plugin.getChallengesFile().getSpawnpoint(challenge));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type commands that are going to be executed after completion");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "%s " + ChatColor.GRAY + " - player; don't add '/' before command");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "For example: " + ChatColor.RED + "ban %s");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "To finish ChallengeSetup type " + ChatColor.RED + "finish");
			}
			else if(event.getCurrentItem().getType().equals(Material.BLAZE_POWDER)){
				player.closeInventory();
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "You've removed all Commands!");
				plugin.getChallengesFile().removeCommands(challenge);
			}
			else if(event.getCurrentItem().getType().equals(Material.STICK)) {
				player.closeInventory();
				ChallengeSetup.setStep(player, 6);
				ChallengeSetup.setType(player, plugin.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				ChallengeSetup.setStep(player, 6);
				player.getInventory().setItemInMainHand(ItemStackGenerator.createItem(Material.IRON_AXE, 1, 0,
					ChatColor.GRAY + "Select Mobs' spawning region",
					Arrays.asList(ChatColor.RED + "Select 2 points")));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Select 2 points for area");
			}
			else if(event.getCurrentItem().getType().equals(Material.MOB_SPAWNER)) {
				player.closeInventory();
				ChallengeSetup.setStep(player, 7);
				ChallengeSetup.setType(player, plugin.getChallengesFile().getType(challenge));
				ChallengeSetup.setEditing(player, true);
				ChallengeSetup.setChallenge(player, challenge);
				player.teleport(plugin.getChallengesFile().getSpawnpoint(challenge));
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Type anything to open selection menu");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "To finish ChallengeSetup type " + ChatColor.RED + "finish");
				MainInventory.setupMobSpawnInventory(player);
			}
			else if(event.getCurrentItem().getType().equals(Material.BLAZE_ROD)){
				player.closeInventory();
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_CHALLENGES.toString() + "You've removed all Mob Spawns!");
				plugin.getChallengesFile().removeMonsterSpawns(challenge);
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
		
		// CHALLENGES MENU
		else if(event.getInventory().getName().equals(ChatColor.BOLD + "Challenges")) {
			event.setCancelled(true);
			int challenge = event.getRawSlot() + 1;
			int level = plugin.getPlayerManager().getRank((Player)event.getWhoClicked());
			if(challenge > level + 1) {
				event.getWhoClicked().sendMessage(Lang.HEADERS_CHALLENGES.toString() + "Your rank is too low for this challenge!");
			}
			else if(challenge <= level) {
				event.getWhoClicked().sendMessage(Lang.HEADERS_CHALLENGES.toString() + "You have already completed this challenge!");
			}
			else {
				MainInventory.setupRankUpMenu((Player) event.getWhoClicked());
			}
		}
	}

	public Main getPlugin() {
		return plugin;
	}
	
}
