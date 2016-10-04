package com.nekrosius.asgardascension.inventories;

import java.util.Arrays;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.TokenType;
import com.nekrosius.asgardascension.files.GodFoodFile;
import com.nekrosius.asgardascension.handlers.GodTokens;
import com.nekrosius.asgardascension.objects.GodToken;
import com.nekrosius.asgardascension.utils.Convert;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class MainInventory {
	
	private static Main plugin = (Main)Bukkit.getPluginManager().getPlugin("AsgardAscension");
	
	public static void setupRankUpMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Rank-Up");
		int challenge = plugin.getPlayerManager().getRank(player) + 1;
		inv.setItem(2, ItemStackGenerator.createItem(Material.DIAMOND_HELMET, 0, 0, 
				ChatColor.LIGHT_PURPLE + "I trust my strength!",
				Arrays.asList(	ChatColor.RED 	+ "Earn rankup by completing the challenge",
								ChatColor.RED 	+ "for lesser amount of money!"), true));
		inv.setItem(4, ItemStackGenerator.createItem(Material.BOOK, 0, 0, 
				ChatColor.LIGHT_PURPLE + "RankUp Information",
				Arrays.asList(	ChatColor.RED + "Next rank: " + challenge,
								ChatColor.RED + "Challenge type: " + WordUtils.capitalize(plugin.getChallengesFile().getType(challenge)),
								ChatColor.RED + "Price: " + Convert.toPrice(plugin.getChallengesFile().getPrice(challenge) * (plugin.getPlayerManager().getPrestige(player) + 1)))));
		inv.setItem(6, ItemStackGenerator.createItem(Material.GOLD_INGOT, 0, 0, 
				ChatColor.LIGHT_PURPLE + "I can buy anything!",
				Arrays.asList(	ChatColor.RED 	+ "Skip the challenge by",
								ChatColor.RED + "paying 2 times more!")));
		player.openInventory(inv);
	}
	
	public static void setupTokensDurationMenu(Player player, GodToken token) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "BUY " + token.getName());
		inv.setItem(2, ItemStackGenerator.createItem(Material.PAPER, 0, 0, ChatColor.RED + "Temporary (" + ChatColor.GOLD + token.getTempPrice() + " tokens"  + ChatColor.RED + ")", null));
		inv.setItem(4, ItemStackGenerator.createItem(token.getIcon(), 0, 0, ChatColor.RED + token.getName(), token.getDescription(), true));
		inv.setItem(6, ItemStackGenerator.createItem(Material.BOOK, 0, 0, ChatColor.RED + "Permanent (" + ChatColor.GOLD + token.getPermPrice() + " tokens"  + ChatColor.RED + ")", null));
		inv.setItem(8, ItemStackGenerator.createItem(Material.REDSTONE_BLOCK, 0, 0, ChatColor.RED + "Go back!", null));
		player.openInventory(inv);
	}
	
	public static void setupTokensShopMenu(Player player, TokenType type) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "God Tokens");
		for(GodToken token : GodTokens.tokens) {
			if(token.getType().equals(type)) {
				inv.addItem(ItemStackGenerator.createItem(token.getIcon(),
						0, 0, ChatColor.LIGHT_PURPLE + token.getName(),
						token.getDescription(), true));
			}
		}
		inv.setItem(8, ItemStackGenerator.createItem(Material.REDSTONE_BLOCK, 0, 0, ChatColor.RED + "Go back!", null));
		player.openInventory(inv);
	}
	
	public static void setupTokensMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "God Tokens Type");
		inv.setItem(0, ItemStackGenerator.createItem(Material.BOOK, 0, 0,
				ChatColor.GRAY + "God Tokens: " + ChatColor.RED + plugin.getPlayerManager().getTokens(player), null));
		inv.setItem(8, ItemStackGenerator.createItem(Material.BOOK, 0, 0,
				ChatColor.GRAY + "God Tokens: " + ChatColor.RED + plugin.getPlayerManager().getTokens(player), null));
		
		if(canBuyPlot(player)) {
			inv.setItem(2, ItemStackGenerator.createItem(Material.STONE_SPADE, 0, 0, ChatColor.LIGHT_PURPLE + "Additional plot access", 
					Arrays.asList(	ChatColor.GRAY + "Price: " + ChatColor.RED + "25 god tokens"), true));
		}

		//inv.setItem(2, ItemStackGenerator.createItem(Material.CHEST, 0, 0, ChatColor.LIGHT_PURPLE + "Donator rank", 
		//		Arrays.asList(ChatColor.GREEN + "Price: " + ChatColor.RED + "8 god tokens")));
		inv.setItem(6, ItemStackGenerator.createItem(Material.CHEST, 0, 0, ChatColor.LIGHT_PURPLE + "Crate", 
				Arrays.asList(ChatColor.GREEN + "Price: " + ChatColor.RED + "8 god tokens")));
		Material values[] = {Material.GOLD_SWORD, Material.GOLD_PICKAXE, Material.DOUBLE_PLANT};
		int i = 0;
		for(TokenType type : TokenType.values()) {
			inv.setItem(i + 3, ItemStackGenerator.createItem(values[i], 0, 0,
					ChatColor.LIGHT_PURPLE + type.name(), null, true));
			i++;
		}
		player.openInventory(inv);
	}
	
	public static void setupInventory(Player player){
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Asgard Ascension");
		inv.setItem(0, ItemStackGenerator.createItem(Material.NETHER_STAR, 0, 0, 
				ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Rank-ups and Challenges", null));
		inv.setItem(1, ItemStackGenerator.createItem(Material.DIAMOND, 0, 0,
				ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Ragnorak", null));
		inv.setItem(2, ItemStackGenerator.createItem(Material.GOLDEN_CARROT, 0, 0,
				ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Food of the Gods", null));
		inv.setItem(8, ItemStackGenerator.createItem(Material.REDSTONE_BLOCK, 0, 0,
				ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Reload Configs", null));
		player.openInventory(inv);
	}
	
	public static void setupRagnorakInventory(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Ragnorak");
		inv.setItem(2, ItemStackGenerator.createItem(Material.LEVER, 0, 0,
				ChatColor.LIGHT_PURPLE + "Start Ragnorak", null));
		inv.setItem(4, ItemStackGenerator.createItem(Material.DIAMOND_SWORD, 0, 0,
				ChatColor.LIGHT_PURPLE + "Add your current item to Ragnorak", null, true));
		inv.setItem(6, ItemStackGenerator.createItem(Material.COAL, 0, 0,
				ChatColor.LIGHT_PURPLE + "Add your location to Ragnorak", null));
		player.openInventory(inv);
	}
	
	public static void setupChallengesInventory(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Rank-ups and Challenges");
		inv.setItem(2, ItemStackGenerator.createItem(Material.SLIME_BALL, 0, 0,
				ChatColor.RED + "" + ChatColor.BOLD + "Add Challenge", null));
		inv.setItem(4, ItemStackGenerator.createItem(Material.PAPER, 0, 0,
				ChatColor.RED + "" + ChatColor.BOLD + "Statistics", 
				Arrays.asList(ChatColor.GRAY + "Amount of Challenges: " + ChatColor.RED + plugin.getChallengesFile().getChallengesAmount())));
		inv.setItem(6, ItemStackGenerator.createItem(Material.BOOK_AND_QUILL, 0, 0,
				ChatColor.RED + "" + ChatColor.BOLD + "Edit Challenge", null));
		player.openInventory(inv);
	}
	
	public static void setupGodFoodInventory(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Food of the Gods");
		inv.setItem(2, ItemStackGenerator.createItem(Material.SLIME_BALL, 0, 0,
				ChatColor.RED + "" + ChatColor.BOLD + "Add Food of the Gods", null));
		inv.setItem(4, ItemStackGenerator.createItem(Material.PAPER, 0, 0,
				ChatColor.RED + "" + ChatColor.BOLD + "Statistics", 
				Arrays.asList(ChatColor.GRAY + "Amount of God Foods: " + ChatColor.RED + GodFoodFile.getEffectAmount())));
		inv.setItem(6, ItemStackGenerator.createItem(Material.BOOK_AND_QUILL, 0, 0,
				ChatColor.RED + "" + ChatColor.BOLD + "Edit Food of the Gods", null));
		player.openInventory(inv);
	}
	
	public static void setupFoodEditListMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, getInventorySize(GodFoodFile.getEffectAmount()), ChatColor.BOLD + "Edit Food of the Gods");
		for(int i = 1; i <= GodFoodFile.getEffectAmount(); i++) {
			inv.addItem(ItemStackGenerator.createItem(
					GodFoodFile.getFoodType(i), GodFoodFile.getAmount(i), GodFoodFile.getData(i), ChatColor.LIGHT_PURPLE + GodFoodFile.getName(i), null));
		}
		player.openInventory(inv);
	}
	
	public static void setupFoodEditMenu(Player player, int food) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "" + food + " Edit Menu");
		inv.setItem(0, ItemStackGenerator.createItem(Material.PAPER, 0, 0, ChatColor.LIGHT_PURPLE + "Change name", null));
		inv.setItem(1, ItemStackGenerator.createItem(Material.WORKBENCH, 0, 0, ChatColor.LIGHT_PURPLE + "Change item", null));
		inv.setItem(2, ItemStackGenerator.createItem(Material.ARROW, 16, 0, ChatColor.LIGHT_PURPLE + "Change amount", null));
		inv.setItem(3, ItemStackGenerator.createItem(Material.STICK, 0, 0, ChatColor.LIGHT_PURPLE + "Change data value", null));
		inv.setItem(4, ItemStackGenerator.createItem(Material.WATER_LILY, 0, 0, ChatColor.LIGHT_PURPLE + "Add Effect", null));
		inv.setItem(5, ItemStackGenerator.createItem(Material.BLAZE_POWDER, 0, 0, ChatColor.LIGHT_PURPLE + "Remove all Effects", null));
		inv.setItem(8, ItemStackGenerator.createItem(Material.IRON_BARDING, 0, 0, ChatColor.LIGHT_PURPLE + "Test Food of the Gods", null));
		player.openInventory(inv);
	}
	
	@SuppressWarnings("deprecation")
	public static void setupFoodEffectTypeInventory(Player player) {
		Inventory inv = Bukkit.createInventory(player, 27, ChatColor.BOLD + "Food Effect Type");
		inv.setItem(0, ItemStackGenerator.createItem(Material.GOLD_BLOCK, 0, 0, ChatColor.GRAY + "Absorption", 
				Arrays.asList(ChatColor.RED + "Increases the maximum health of an entity with health that cannot be regenerated.")));
		inv.setItem(1, ItemStackGenerator.createItem(Material.SPIDER_EYE, 0, 0, ChatColor.GRAY + "Blindness", 
				Arrays.asList(ChatColor.RED + "Blinds an entity.")));
		inv.setItem(2, ItemStackGenerator.createItem(Material.FERMENTED_SPIDER_EYE, 0, 0, ChatColor.GRAY + "Confusion", 
				Arrays.asList(ChatColor.RED + "Warps vision on the client.")));
		inv.setItem(3, ItemStackGenerator.createItem(Material.IRON_CHESTPLATE, 0, 0, ChatColor.GRAY + "Damage Resistance", 
				Arrays.asList(ChatColor.RED + "Decreases damage dealt to an entity."), true));
		inv.setItem(4, ItemStackGenerator.createItem(Material.GOLD_PICKAXE, 0, 0, ChatColor.GRAY + "Haste", 
				Arrays.asList(ChatColor.RED + "Increases dig speed."), true));
		inv.setItem(5, ItemStackGenerator.createItem(Material.BLAZE_ROD, 0, 0, ChatColor.GRAY + "Fire Resistance", 
				Arrays.asList(ChatColor.RED + "Stops fire damage.")));
		inv.setItem(6, ItemStackGenerator.createItem(Material.BLAZE_POWDER, 0, 0, ChatColor.GRAY + "Harm", 
				Arrays.asList(ChatColor.RED + "Hurts an entity.")));
		inv.setItem(7, ItemStackGenerator.createItem(Material.WATER_LILY, 0, 0, ChatColor.GRAY + "Heal", 
				Arrays.asList(ChatColor.RED + "Heals an entity.")));
		inv.setItem(8, ItemStackGenerator.createItem(Material.GOLDEN_APPLE, 0, 0, ChatColor.GRAY + "Health Boost", 
				Arrays.asList(ChatColor.RED + "Increases the maximum health of an entity.")));
		inv.setItem(9, ItemStackGenerator.createItem(Material.ROTTEN_FLESH, 0, 0, ChatColor.GRAY + "Hunger", 
				Arrays.asList(ChatColor.RED + "Increases hunger.")));
		inv.setItem(10, ItemStackGenerator.createItem(Material.IRON_SWORD, 0, 0, ChatColor.GRAY + "Damage Boost", 
				Arrays.asList(ChatColor.RED + "Increases damage dealt."), true));
		inv.setItem(11, ItemStackGenerator.createItem(Material.GLASS, 0, 0, ChatColor.GRAY + "Invisibility", 
				Arrays.asList(ChatColor.RED + "Grants invisibility.")));
		inv.setItem(12, ItemStackGenerator.createItem(Material.GOLD_BOOTS, 0, 0, ChatColor.GRAY + "Jump Boost", 
				Arrays.asList(ChatColor.RED + "Increases jump height."), true));
		inv.setItem(13, ItemStackGenerator.createItem(Material.POTION, 0, 0, ChatColor.GRAY + "Night Vision", 
				Arrays.asList(ChatColor.RED + "Allows an entity to see in the dark.")));
		inv.setItem(14, ItemStackGenerator.createItem(Material.RED_MUSHROOM, 0, 0, ChatColor.GRAY + "Poison", 
				Arrays.asList(ChatColor.RED + "Deals damage to an entity over time.")));
		inv.setItem(15, ItemStackGenerator.createItem(Material.GOLDEN_CARROT, 0, 0, ChatColor.GRAY + "Regeneration", 
				Arrays.asList(ChatColor.RED + "Regenerates health.")));
		inv.setItem(16, ItemStackGenerator.createItem(Material.COOKED_BEEF, 0, 0, ChatColor.GRAY + "Saturation", 
				Arrays.asList(ChatColor.RED + "Increases the food level of an entity each tick.")));
		inv.setItem(17, ItemStackGenerator.createItem(Material.CHAINMAIL_BOOTS, 0, 0, ChatColor.GRAY + "Slow", 
				Arrays.asList(ChatColor.RED + "Decreases movement speed."), true));
		inv.setItem(18, ItemStackGenerator.createItem(Material.WOOD_PICKAXE, 0, 0, ChatColor.GRAY + "Mining Fatigue", 
				Arrays.asList(ChatColor.RED + "Decreases dig speed."), true));
		inv.setItem(19, ItemStackGenerator.createItem(Material.DIAMOND_BOOTS, 0, 0, ChatColor.GRAY + "Speed", 
				Arrays.asList(ChatColor.RED + "Increases movement speed."), true));
		inv.setItem(20, ItemStackGenerator.createItem(Material.DIAMOND_HELMET, 0, 0, ChatColor.GRAY + "Water Breathing", 
				Arrays.asList(ChatColor.RED + "Allows breathing underwater."), true));
		inv.setItem(21, ItemStackGenerator.createItem(Material.WOOD_SWORD, 0, 0, ChatColor.GRAY + "Weakness", 
				Arrays.asList(ChatColor.RED + "Decreases damage dealt by an entity."), true));
		inv.setItem(22, ItemStackGenerator.createItem(Material.getMaterial(397), 0, 1, ChatColor.GRAY + "Wither",
				Arrays.asList(ChatColor.RED + "Deals damage to an entity over time."), true));
		
		player.openInventory(inv);
	}
	
	public static void setupChallengesChoseMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "Challenge type");
		inv.setItem(2, ItemStackGenerator.createItem(Material.GOLD_BOOTS, 0, 0, ChatColor.GRAY + "" + ChatColor.BOLD + "Parkour", null, true));
		inv.setItem(4, ItemStackGenerator.createItem(Material.TORCH, 0, 0, ChatColor.GRAY + "" + ChatColor.BOLD + "Maze", null, true));
		inv.setItem(6, ItemStackGenerator.createItem(Material.GOLD_SWORD, 0, 0, ChatColor.GRAY + "" + ChatColor.BOLD + "Fight", null, true));
		player.openInventory(inv);
	}
	
	public static void setupEditMenu(Player player) {
		Inventory inv = Bukkit.createInventory(player, getInventorySize(plugin.getChallengesFile().getChallengesAmount()), ChatColor.BOLD + "Edit Challenge");
		for(int i = 1; i <= plugin.getChallengesFile().getChallengesAmount(); i++){
			Material mat;
			if(plugin.getChallengesFile().getType(i).equalsIgnoreCase("parkour")){
				mat = Material.GOLD_BOOTS;
			}
			else if(plugin.getChallengesFile().getType(i).equalsIgnoreCase("maze")){
				mat = Material.TORCH;
			}else{
				mat = Material.GOLD_SWORD;
			}
			inv.addItem(ItemStackGenerator.createItem(mat, 0, 0,
					ChatColor.GRAY + "" + i + Convert.getOrdinalFor(i) + " Challenge", null, true));
		}
		player.openInventory(inv);
	}
	
	public static void setupChallengeEditMenu(Player player, int challenge) {
		Inventory inv;
		if(plugin.getChallengesFile().getType(challenge).equalsIgnoreCase("fight")){
			inv = Bukkit.createInventory(player, 18, ChatColor.BOLD + "" + challenge + " Challenge Edit Menu");
		}else{
			inv = Bukkit.createInventory(player, 9, ChatColor.BOLD + "" + challenge + " Challenge Edit Menu");
		}
		inv.setItem(0, ItemStackGenerator.createItem(Material.GOLD_INGOT, 0, 0,
				ChatColor.GRAY + "Change Price", null));
		inv.setItem(1, ItemStackGenerator.createItem(Material.COAL, 0, 0,
				ChatColor.GRAY + "Change Spawnpoint", null));
		inv.setItem(2, ItemStackGenerator.createItem(Material.NOTE_BLOCK, 0, 0,
				ChatColor.GRAY + "Change Noteblock Location", null));
		inv.setItem(3, ItemStackGenerator.createItem(Material.GOLD_HELMET, 0, 0,
				ChatColor.GRAY + "Change Victory Spawnpoint", null, true));
		inv.setItem(4, ItemStackGenerator.createItem(Material.WATER_LILY, 0, 0,
				ChatColor.GRAY + "Add commands", null));
		inv.setItem(5, ItemStackGenerator.createItem(Material.BLAZE_POWDER, 0, 0,
				ChatColor.GRAY + "Remove all commands", null));
		if(plugin.getChallengesFile().getType(challenge).equalsIgnoreCase("fight")){
			inv.setItem(9, ItemStackGenerator.createItem(Material.STICK, 0, 0,
					ChatColor.GRAY + "Set mobs location", null));
			inv.setItem(10, ItemStackGenerator.createItem(Material.MOB_SPAWNER, 0, 0,
					ChatColor.GRAY + "Add Mob Spawn", null));
			inv.setItem(11, ItemStackGenerator.createItem(Material.BLAZE_ROD, 0, 0,
					ChatColor.GRAY + "Remove all Mob Spawns", null));
		}
		inv.setItem(8, ItemStackGenerator.createItem(Material.IRON_BARDING, 0, 0,
				ChatColor.GRAY + "Test this Challenge", null));
		player.openInventory(inv);
	}
	
	public static void setupMobSpawnInventory(Player player) {
		Inventory inv = Bukkit.createInventory(player, 18, ChatColor.BOLD + "Select Mob Type");
		inv.setItem(0, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 50, ChatColor.DARK_GREEN + "Creeper", null));
		inv.setItem(1, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 51, ChatColor.GRAY + "Skeleton", null));
		inv.setItem(2, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 52, ChatColor.DARK_GRAY + "Spider", null));
		inv.setItem(3, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 54, ChatColor.DARK_AQUA + "Zombie", null));
		inv.setItem(4, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 55, ChatColor.GREEN + "Slime", null));
		inv.setItem(5, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 56, ChatColor.RED + "Ghast", null));
		inv.setItem(6, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 57, ChatColor.RED + "Pigman", null));
		inv.setItem(7, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 58, ChatColor.DARK_GRAY + "Enderman", null));
		inv.setItem(8, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 59, ChatColor.DARK_RED + "Cave Spider", null));
		inv.setItem(9, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 60, ChatColor.DARK_GRAY + "Silverfish", null));
		inv.setItem(10, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 61, ChatColor.YELLOW + "Blaze", null));
		inv.setItem(11, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 62, ChatColor.GRAY + "Magma Cube", null));
		inv.setItem(12, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 65, ChatColor.DARK_GRAY + "Bat", null));
		inv.setItem(13, ItemStackGenerator.createItem(Material.MONSTER_EGG, 0, 66, ChatColor.DARK_PURPLE + "Witch", null));
		player.openInventory(inv);
	}
	
	public static boolean canBuyPlot(Player player) {
		return !player.hasPermission("plots.plot.2");
	}
	
	public static int getInventorySize(int items) {
		for(int i = 1; i <= 6; i++){
			if(i * 9 > items) {
				return i * 9;
			}
		}
		return 9;
	}
	
}
