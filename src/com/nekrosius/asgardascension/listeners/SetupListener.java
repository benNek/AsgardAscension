package com.nekrosius.asgardascension.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.challenges.Challenge;
import com.nekrosius.asgardascension.challenges.ChallengeSetup;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.files.GodFoodFile;
import com.nekrosius.asgardascension.handlers.FoodSetup;
import com.nekrosius.asgardascension.inventories.MainInventory;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class SetupListener implements Listener {
	
	private Main pl;
	public SetupListener(Main plugin) {
		pl = plugin;
	}
	
	public static Map<String, Boolean> withdrawal = new HashMap<>();
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if(ChallengeSetup.getStep(player) > 0){
			if(event.getMessage().equals("cancel")){
				if(!ChallengeSetup.isEditing(player)){
					pl.getChallengesFile().remove(ChallengeSetup.getChallenge(player));
				}
				ChallengeSetup.finish(player);
				event.setCancelled(true);
				player.sendMessage(Challenge.MESSAGE_HEADER + "You've cancelled setup of Challenge!");
			}
		}
		if(FoodSetup.getStep(player) > 0){
			if(event.getMessage().equals("cancel")){
				if(!FoodSetup.isEditing(player)){
					GodFoodFile.remove(FoodSetup.getFoodIndex(player));
				}
				FoodSetup.finish(player);
				event.setCancelled(true);
				player.sendMessage(Lang.HEADERS_FOG.toString() + "You've cancelled setup of Food of Gods!");
			}
		}
		if(withdrawal.containsKey(player.getName())) {
			event.setCancelled(true);
			int amount;
			try {
				amount = Integer.parseInt(event.getMessage());
			} catch (NumberFormatException e) {
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "Type only a number!");
				return;
			}
			if(amount < 1) {
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You can not withdraw less than 1 token!");
				return;
			}
			if(amount > 20) {
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You can only withdraw up to 20 GT!");
				return;
			}
			if(!pl.getPlayerManager().hasTokens(player, amount)) {
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You don't have " + amount + " tokens! (" + pl.getPlayerManager().getTokens(player) + ")");
				return;
			}
			ItemStack item = ItemStackGenerator.createItem(Material.NETHER_STAR, amount, 0, 
					ChatColor.LIGHT_PURPLE + "God Token",
					Arrays.asList(ChatColor.RED + "Right-Click to deposit GT"));
			if(ItemStackGenerator.isInventoryFull(player, item)) {
				player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You don't have enough inventory space for " + amount + " GT!");
				return;
			}
			player.getInventory().addItem(item);
			player.sendMessage(Lang.HEADERS_TOKENS.toString() + "You've successfully withdrawn " + amount + " GT!");
			pl.getPlayerManager().withdrawTokens(player, amount);
			withdrawal.remove(player.getName());
		}
		
		// CHALLENGES
		
		if(ChallengeSetup.getStep(player) == 1) {
			event.setCancelled(true);
			long price;
			try{
				price = Long.parseLong(event.getMessage());
			}catch(NumberFormatException e){
				player.sendMessage(Challenge.MESSAGE_HEADER + "Please type number!");
				return;
			}
			pl.getChallengesFile().setPrice(ChallengeSetup.getChallenge(player), price);
			if(!pl.getChallengesFile().hasTitle(ChallengeSetup.getChallenge(player))) {
				pl.getChallengesFile().setTitle(ChallengeSetup.getChallenge(player), String.valueOf(ChallengeSetup.getChallenge(player)));
			}
			ChallengeSetup.setStep(player, 2);
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Challenge.MESSAGE_HEADER + "Price to enter this challenge will be " + ChatColor.RED + price);
			if(ChallengeSetup.isEditing(player)){
				ChallengeSetup.finish(player);
			}else{
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Challenge.MESSAGE_HEADER + "Right-Click with Iron Axe to set spawnpoint");
				player.getInventory().addItem(ItemStackGenerator.createItem(Material.IRON_AXE, 0, 0,
					ChatColor.GRAY + "Add Spawnpoint",
					Arrays.asList(Challenge.MESSAGE_HEADER + "Right-Click to add spawnpoint of " + WordUtils.capitalize(ChallengeSetup.getType(player)) + " challenge!")));
			}
		}
		else if(ChallengeSetup.getStep(player) == 5) {
			event.setCancelled(true);
			if(event.getMessage().equalsIgnoreCase("finish")){
				if(!ChallengeSetup.getType(player).equalsIgnoreCase("fight")){
					player.sendMessage(
							Challenge.MESSAGE_HEADER + "You have finished " 
							+ ChatColor.RED + WordUtils.capitalize(ChallengeSetup.getType(player)) + " Challenge" 
							+ ChatColor.GRAY + " setup!");
					ChallengeSetup.finish(player);
				}else{
					if(ChallengeSetup.isEditing(player)){
						ChallengeSetup.finish(player);
						player.sendMessage(
								Challenge.MESSAGE_HEADER + "You have finished " 
								+ ChatColor.RED + WordUtils.capitalize(ChallengeSetup.getType(player)) + " Challenge" 
								+ ChatColor.GRAY + " setup!");
					}
					else{
						ChallengeSetup.setStep(player, 6);
						player.getInventory().setItemInMainHand(ItemStackGenerator.createItem(Material.IRON_AXE, 1, 0,
							ChatColor.GRAY + "Select Mobs' spawning region",
							Arrays.asList(ChatColor.RED + "Select 2 points")));
						player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
						player.sendMessage(Challenge.MESSAGE_HEADER + "Select 2 points for area");
					}
				}
			}else{
				pl.getChallengesFile().addCommand(ChallengeSetup.getChallenge(player), event.getMessage());
				player.sendMessage(Challenge.MESSAGE_HEADER + "You've added a new command:");
				player.sendMessage(event.getMessage());
			}
		}
		else if(ChallengeSetup.getStep(player) == 6) {
			if(event.getMessage().equalsIgnoreCase("finish")){
				event.setCancelled(true);
				player.sendMessage(
						Challenge.MESSAGE_HEADER + "You have finished " 
						+ ChatColor.RED + WordUtils.capitalize(ChallengeSetup.getType(player)) + " Challenge" 
						+ ChatColor.GRAY + " setup!");
				ChallengeSetup.finish(player);
				return;
			}
		}
		else if(ChallengeSetup.getStep(player) == 7) {
			event.setCancelled(true);
			if(event.getMessage().equalsIgnoreCase("finish")){
				player.sendMessage(
						Challenge.MESSAGE_HEADER + "You have finished " 
						+ ChatColor.RED + WordUtils.capitalize(ChallengeSetup.getType(player)) + " Challenge" 
						+ ChatColor.GRAY + " setup!");
				ChallengeSetup.finish(player);
				return;
			}
			player.sendMessage(ChatColor.GRAY + "Type " + ChatColor.RED + "finish" + ChatColor.GRAY + " to finish setup");
			MainInventory.setupMobSpawnInventory(player);
		}
		else if(ChallengeSetup.getStep(player) == 8){
			event.setCancelled(true);
			int amount;
			try{
				amount = Integer.parseInt(event.getMessage());
			}catch(NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Type minimum amount of mobs to be alive!");
				return;
			}
			pl.getChallengesFile().addMob(ChallengeSetup.getChallenge(player), ChallengeSetup.getMob(player), amount);
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
							+ ChatColor.RED + WordUtils.capitalize(ChallengeSetup.getMob(player).toLowerCase())
							+ ChatColor.GRAY + " to spawn in area!");
			ChallengeSetup.setStep(player, 7);
			MainInventory.setupMobSpawnInventory(player);
		}
		
		// FOOD OF GODS
		
		if(FoodSetup.getStep(player) > 4) {
			if(event.getMessage().equalsIgnoreCase("finish")){
				FoodSetup.finish(player);
				event.setCancelled(true);
				player.sendMessage(Lang.HEADERS_FOG.toString() + "You've finished setup of Food of the Gods!");
			}
		}
		if(FoodSetup.getStep(player) == 1) {
			event.setCancelled(true);
			GodFoodFile.setName(FoodSetup.getFoodIndex(player), event.getMessage());
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Lang.HEADERS_FOG.toString() + "Name of Food of the Goods is " + ChatColor.RED + event.getMessage());
			if(FoodSetup.isEditing(player)){
				FoodSetup.finish(player);
				player.sendMessage(Lang.HEADERS_FOG.toString() + "You've finished setup of Food of the Gods!");
			}else{
				FoodSetup.setStep(player, 2);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type ID of item, which represents this Food of Gods!");
			}
		}
		else if(FoodSetup.getStep(player) == 2) {
			event.setCancelled(true);
			int id;
			try{
				id = Integer.parseInt(event.getMessage());
			}catch(NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Type ID of item!");
				return;
			}
			GodFoodFile.setItemId(FoodSetup.getFoodIndex(player), id);
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Lang.HEADERS_FOG.toString() + "You've set ID to " + ChatColor.RED + id);
			if(FoodSetup.isEditing(player)){
				FoodSetup.finish(player);
				player.sendMessage(Lang.HEADERS_FOG.toString()+ "You've finished setup of Food of the Gods!");
			}else{
				FoodSetup.setStep(player, 3);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type data value of item (0 for default)!");
			}
		}
		else if(FoodSetup.getStep(player) == 3) {
			event.setCancelled(true);
			int data;
			try{
				data = Integer.parseInt(event.getMessage());
			}catch(NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Type data value of item (0 for no data)!");
				return;
			}
			GodFoodFile.setData(FoodSetup.getFoodIndex(player), data);
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Lang.HEADERS_FOG.toString() + "You've set item's data value to " + ChatColor.RED + data);
			if(FoodSetup.isEditing(player)){
				FoodSetup.finish(player);
				player.sendMessage(Lang.HEADERS_FOG.toString()+ "You've finished setup of Food of the Gods!");
			}else{
				FoodSetup.setStep(player, 4);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type amount of this required to be used!");
			}
		}
		
		else if(FoodSetup.getStep(player) == 4) {
			event.setCancelled(true);
			int amount;
			try{
				amount = Integer.parseInt(event.getMessage());
			}catch(NumberFormatException e) {
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type required amount of item!");
				return;
			}
			GodFoodFile.setAmount(FoodSetup.getFoodIndex(player), amount);
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Lang.HEADERS_FOG.toString() + "You've set required amount of item to " + ChatColor.RED + amount);
			if(FoodSetup.isEditing(player)){
				FoodSetup.finish(player);
				player.sendMessage(Lang.HEADERS_FOG.toString() + "You've finished setup of Food of the Gods!");
			}else{
				FoodSetup.setStep(player, 5);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Select Effect Type!");
				MainInventory.setupFoodEffectTypeInventory(player);
			}
		}
		else if(FoodSetup.getStep(player) == 5) {
			MainInventory.setupFoodEffectTypeInventory(player);
		}
		else if(FoodSetup.getStep(player) == 6) {
			event.setCancelled(true);
			int duration;
			try{
				duration = Integer.parseInt(event.getMessage());
			}catch(NumberFormatException e) {
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type duration of effect in seconds!");
				return;
			}
			FoodSetup.setDuration(player, duration);
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Lang.HEADERS_FOG.toString() + "You've set effect duration to " + ChatColor.RED + duration);
			FoodSetup.setStep(player, 7);
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Lang.HEADERS_FOG.toString() + "Type Amplifier of this effect!");
			player.sendMessage(Lang.HEADERS_FOG.toString() + "(number, like " + ChatColor.RED + "2" + ChatColor.GRAY + ")");
		}
		else if(FoodSetup.getStep(player) == 7) {
			event.setCancelled(true);
			int amplifier;
			try{
				amplifier = Integer.parseInt(event.getMessage());
			}catch(NumberFormatException e) {
				player.sendMessage(Lang.HEADERS_FOG.toString() + "Type amplifier of effect!");
				return;
			}
			FoodSetup.setAmplifier(player, amplifier);
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Lang.HEADERS_FOG.toString() + "You've set effect amplifier to " + ChatColor.RED + amplifier);
			GodFoodFile.addEffect(player, FoodSetup.getFoodIndex(player));
			FoodSetup.setStep(player, 5);
			player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
			player.sendMessage(Lang.HEADERS_FOG.toString() + "Select Effect Type!");
			MainInventory.setupFoodEffectTypeInventory(player);
			player.sendMessage(Lang.HEADERS_FOG.toString() + "Type " + ChatColor.RED + "finish" + ChatColor.RED + " to finish setup!");
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getPlayer().getInventory().getItemInMainHand() != null) {
			if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE)) {
				Player player = event.getPlayer();
				if(ChallengeSetup.getStep(player) == 6) {
					// LEFT  - 1
					// RIGHT - 2
					if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
						event.setCancelled(true);
						ChallengeSetup.setFirstLocation(player, event.getClickedBlock().getLocation());
						player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've set 1st location!");
						if(ChallengeSetup.isReady(player)){
							pl.getChallengesFile().setMobsLocation(ChallengeSetup.getChallenge(player), player);
							if(ChallengeSetup.isEditing(player)){
								player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								player.sendMessage(
										Challenge.MESSAGE_HEADER + "You have finished " 
										+ ChatColor.RED + WordUtils.capitalize(ChallengeSetup.getType(player)) + " Challenge" 
										+ ChatColor.GRAY + " setup!");
								ChallengeSetup.finish(player);
								return;
							}
							ChallengeSetup.setStep(player, 7);
							player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
							player.sendMessage(Lang.HEADERS_FOG.toString() + "Select mob type!");
							MainInventory.setupMobSpawnInventory(player);
						}
					}
					else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						event.setCancelled(true);
						ChallengeSetup.setSecondLocation(player, event.getClickedBlock().getLocation());
						player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've set 2nd location!");
						if(ChallengeSetup.isReady(player)) {
							pl.getChallengesFile().setMobsLocation(ChallengeSetup.getChallenge(player), player);
							if(ChallengeSetup.isEditing(player)){
								player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								player.sendMessage(
										Challenge.MESSAGE_HEADER + "You have finished " 
										+ ChatColor.RED + WordUtils.capitalize(ChallengeSetup.getType(player)) + " Challenge" 
										+ ChatColor.GRAY + " setup!");
								ChallengeSetup.finish(player);
								return;
							}
							ChallengeSetup.setStep(player, 7);
							player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
							player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
							player.sendMessage(Lang.HEADERS_FOG.toString() + "Select mob type!");
							MainInventory.setupMobSpawnInventory(player);
						}
					}
				}
			}
		}
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(event.getPlayer().getInventory().getItemInMainHand() != null){
				if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE)){
					Player player = event.getPlayer();
					if(ChallengeSetup.getStep(player) == 2){
						if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()
								.getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "Add Spawnpoint")){
							event.setCancelled(true);
							pl.getChallengesFile().setSpawnpoint(player, ChallengeSetup.getChallenge(player));
							ChallengeSetup.setStep(player, 3);
							player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
							player.sendMessage(Challenge.MESSAGE_HEADER + "You've added spawnpoint!");
							if(ChallengeSetup.isEditing(player)){
								ChallengeSetup.finish(player);
							}else{
								player.getInventory().setItemInMainHand(ItemStackGenerator.createItem(Material.IRON_AXE, 0, 0,
										ChatColor.GRAY + "Select a Noteblock", 
										Arrays.asList(ChatColor.RED + "Click a block to make it noteblock!")));
								player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
								player.sendMessage(Challenge.MESSAGE_HEADER + "Right-Click a block to make it Noteblock!");
							}
						}
					}
					else if(ChallengeSetup.getStep(player) == 3) {
						if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
							if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()
									.getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "Select a Noteblock")){
								event.setCancelled(true);
								pl.getChallengesFile().setNoteblock(ChallengeSetup.getChallenge(player), event.getClickedBlock().getLocation());
								event.getClickedBlock().setType(Material.NOTE_BLOCK);
								ChallengeSetup.setStep(player, 4);
								player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
								player.sendMessage(Challenge.MESSAGE_HEADER + "You've added Noteblock!");
								if(ChallengeSetup.isEditing(player)){
									ChallengeSetup.finish(player);
								}else{
									player.getInventory().setItemInMainHand(ItemStackGenerator.createItem(Material.IRON_AXE, 0, 0,
											ChatColor.GRAY + "Set Victory Spawnpoint", 
											Arrays.asList(ChatColor.RED + "Right-Click to set spawnpoint upon victory!")));
									player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
									player.sendMessage(Challenge.MESSAGE_HEADER + "Right-Click to set spawnpoint upon victory!");
								}
							}
						}
					}
					else if(ChallengeSetup.getStep(player) == 4) {
						if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()
								.getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "Set Victory Spawnpoint")){
							event.setCancelled(true);
							pl.getChallengesFile().setVictorySpawnpoint(player, ChallengeSetup.getChallenge(player));
							ChallengeSetup.setStep(player, 5);
							player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
							player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
							player.sendMessage(ChatColor.GRAY + "You've added Victory Spawnpoint!");
							if(ChallengeSetup.isEditing(player)){
								ChallengeSetup.finish(player);
							}else{
								player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
								player.sendMessage(Challenge.MESSAGE_HEADER + "Type commands that are going to be executed after completion");
								player.sendMessage(Challenge.MESSAGE_HEADER + "%s " + ChatColor.GRAY + " - player; don't add '/' before command");
								player.sendMessage(Challenge.MESSAGE_HEADER + "For example: " + ChatColor.RED + "ban %s");
								player.sendMessage(Challenge.MESSAGE_HEADER + "To finish setup type " + ChatColor.RED + "finish");
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		
		// CHALLENGES
		
		if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Select Mob Type")){
			if(event.getCurrentItem() != null){
				if(event.getCurrentItem().getType().equals(Material.MONSTER_EGG)){
					Player player = (Player) event.getWhoClicked();		
					event.setCancelled(true);
					if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Creeper")){
						ChallengeSetup.setMob(player, "CREEPER");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Creeper" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Skeleton")){
						ChallengeSetup.setMob(player, "SKELETON");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Skeleton" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Spider")){
						ChallengeSetup.setMob(player, "SPIDER");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Spider" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Zombie")){
						ChallengeSetup.setMob(player, "ZOMBIE");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Zombie" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Slime")){
						ChallengeSetup.setMob(player, "SLIME");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Slime" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Ghast")){
						ChallengeSetup.setMob(player, "GHAST");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Ghast" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Pigman")){
						ChallengeSetup.setMob(player, "PIG_ZOMBIE");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Pigman" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Enderman")){
						ChallengeSetup.setMob(player, "ENDERMAN");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Enderman" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Cave Spider")){
						ChallengeSetup.setMob(player, "CAVE_SPIDER");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Cave Spider" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Silverfish")){
						ChallengeSetup.setMob(player, "SILVERFISH");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Silverfish" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Blaze")){
						ChallengeSetup.setMob(player, "BLAZE");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Blaze" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Magma Cube")){
						ChallengeSetup.setMob(player, "MAGMA_CUBE");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Magma Cube" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Bat")){
						ChallengeSetup.setMob(player, "BAT");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Bat" 
								+ ChatColor.GRAY + " spawn");
					}
					else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Witch")){
						ChallengeSetup.setMob(player, "WITCH");
						player.sendMessage(Challenge.MESSAGE_HEADER + "You've added "
								+ ChatColor.RED + "Witch" 
								+ ChatColor.GRAY + " spawn!");
					}
					player.closeInventory();
					ChallengeSetup.setStep(player, 8);
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "Type minimum amount of mobs to stay in this area");
				}
			}
		}
		
		// FOOD OF THE GODS
		
		else if(event.getInventory().getName().equalsIgnoreCase(ChatColor.BOLD + "Food Effect Type")) {
			if(event.getCurrentItem() != null) {
				Player player = (Player) event.getWhoClicked();
				if(event.getCurrentItem().getType().equals(Material.GOLD_BLOCK)) {
					FoodSetup.setType(player, "ABSORPTION");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Absorption");
				}
				else if(event.getCurrentItem().getType().equals(Material.SPIDER_EYE)) {
					FoodSetup.setType(player, "BLINDNESS");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Blindness");
				}
				else if(event.getCurrentItem().getType().equals(Material.FERMENTED_SPIDER_EYE)) {
					FoodSetup.setType(player, "CONFUSION");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Confusion");
				}
				else if(event.getCurrentItem().getType().equals(Material.IRON_CHESTPLATE)) {
					FoodSetup.setType(player, "DAMAGE_RESISTANCE");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Damage Resistance");
				}
				else if(event.getCurrentItem().getType().equals(Material.GOLD_PICKAXE)) {
					FoodSetup.setType(player, "FAST_DIGGING");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Haste");
				}
				else if(event.getCurrentItem().getType().equals(Material.BLAZE_ROD)) {
					FoodSetup.setType(player, "FIRE_RESISTANCE");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Fire Resistance");
				}
				else if(event.getCurrentItem().getType().equals(Material.BLAZE_POWDER)) {
					FoodSetup.setType(player, "HARM");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Harm");
				}
				else if(event.getCurrentItem().getType().equals(Material.WATER_LILY)) {
					FoodSetup.setType(player, "HEAL");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Heal");
				}
				else if(event.getCurrentItem().getType().equals(Material.GOLDEN_APPLE)) {
					FoodSetup.setType(player, "HEALTH_BOOST");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Health Boost");
				}
				else if(event.getCurrentItem().getType().equals(Material.ROTTEN_FLESH)) {
					FoodSetup.setType(player, "HUNGER");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Hunger");
				}
				else if(event.getCurrentItem().getType().equals(Material.IRON_SWORD)) {
					FoodSetup.setType(player, "INCREASE_DAMAGE");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Damage Boost");
				}
				else if(event.getCurrentItem().getType().equals(Material.GLASS)) {
					FoodSetup.setType(player, "INVISIBILITY");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Invisibility");
				}
				else if(event.getCurrentItem().getType().equals(Material.GOLD_BOOTS)) {
					FoodSetup.setType(player, "JUMP");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Jump Boost");
				}
				else if(event.getCurrentItem().getType().equals(Material.POTION)) {
					FoodSetup.setType(player, "NIGHT_VISION");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Night Vision");
				}
				else if(event.getCurrentItem().getType().equals(Material.RED_MUSHROOM)) {
					FoodSetup.setType(player, "POISON");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Poison");
				}
				else if(event.getCurrentItem().getType().equals(Material.GOLDEN_CARROT)) {
					FoodSetup.setType(player, "REGENERATION");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Regeneration");
				}
				else if(event.getCurrentItem().getType().equals(Material.COOKED_BEEF)) {
					FoodSetup.setType(player, "SATURATION");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Saturation");
				}
				else if(event.getCurrentItem().getType().equals(Material.CHAINMAIL_BOOTS)) {
					FoodSetup.setType(player, "SLOW");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Slow");
				}
				else if(event.getCurrentItem().getType().equals(Material.WOOD_PICKAXE)) {
					FoodSetup.setType(player, "SLOW_DIGGING");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Mining Fatigue");
				}
				else if(event.getCurrentItem().getType().equals(Material.DIAMOND_BOOTS)) {
					FoodSetup.setType(player, "SPEED");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Speed");
				}
				else if(event.getCurrentItem().getType().equals(Material.DIAMOND_HELMET)) {
					FoodSetup.setType(player, "WATER_BREATHING");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Water Breathing");
				}
				else if(event.getCurrentItem().getType().equals(Material.WOOD_SWORD)) {
					FoodSetup.setType(player, "WEAKNESS");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Weakness");
				}
				else if(event.getCurrentItem().getType().equals(Material.getMaterial(397))) {
					FoodSetup.setType(player, "WITHER");
					player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
					player.sendMessage(ChatColor.GRAY + "You've set Effect Type to " + ChatColor.RED + "Wither");
				}
				
				
				else{
					return;
				}
				player.closeInventory();
				FoodSetup.setStep(player, 6);
				player.sendMessage(ChatColor.RED + "*-*-*-*-*-*-*-*-*-*-*");
				player.sendMessage(ChatColor.GRAY + "Type duration of effect in seconds");
				player.sendMessage(ChatColor.GRAY + "(number, like " + ChatColor.RED + "180" + ChatColor.GRAY + ")");
			}
		}
	}

	public Main getPlugin() {
		return pl;
	}
	
}
