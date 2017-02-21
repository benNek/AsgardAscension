package com.nekrosius.asgardascension.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.ItemType;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.objects.Ability;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class AbilityManager {
	
	private List<Ability> abilities;
		
	Main plugin;
	
	public AbilityManager(Main plugin) {
		this.plugin = plugin;
		registerAbilities();
	}
	
	/**
	 * Starts timer To add potion effect abilities
	 * @param player The player
	 */
	public void startTimer(Player player) {
		new BukkitRunnable() {
			public void run() {
				handleTimedEffects(player, player.getInventory().getItemInMainHand());
				handleTimedEffects(player, player.getInventory().getHelmet());
				handleTimedEffects(player, player.getInventory().getChestplate());
				handleTimedEffects(player, player.getInventory().getLeggings());
				handleTimedEffects(player, player.getInventory().getBoots());
			}
		}.runTaskTimer(plugin, 0L, 40L);
	}
	
	/**
	 * Adding potion effects to ability items
	 * @param player The player
	 * @param item The item with ability
	 */
	private void handleTimedEffects(Player player, ItemStack item) {
		if(item == null)
			return;
		
		if(!plugin.getAbilityManager().hasAbility(item))
			return;
		
		Ability ability = plugin.getAbilityManager().getAbility(item);
		
		PotionEffectType type;
		switch(ability.getName()) {
			case "Haste":
				type = PotionEffectType.FAST_DIGGING;
				break;
			case "High Jump":
				type = PotionEffectType.JUMP;
				break;
			case "Speed":
				type = PotionEffectType.SPEED;
				break;
			default:
				return;
		}
		if(player.hasPotionEffect(type)) {
			player.removePotionEffect(type);
		}
		player.addPotionEffect(new PotionEffect(type, 60, 1));
	}
	
	/**
	 * If player has any active temporary abilities,
	 * god tokens will be refunded and ability removed
	 * @param player The player
	 * @param refund whether to refund god tokens to player
	 */
	public void compensateTokens(Player player, boolean refund) {
		for(int i = 0; i < 32; i++) {
			ItemStack item = player.getInventory().getItem(i);
			if(item == null || !plugin.getAbilityManager().hasAbility(item) || !plugin.getAbilityManager().isTemporaryItem(item))
				continue;
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setItem(i, plugin.getAbilityManager().removeItemLore(item, ability));
			
			if(refund) {
				plugin.getPlayerManager().addTokens(player, ability.getTemporaryPrice());
			}
		}
		
		ItemStack item = player.getInventory().getHelmet();
		if(item != null && plugin.getAbilityManager().hasAbility(item) && plugin.getAbilityManager().isTemporaryItem(item)) {
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setHelmet(plugin.getAbilityManager().removeItemLore(item, ability));
			
			if(refund) {
				plugin.getPlayerManager().addTokens(player, ability.getTemporaryPrice());
			}
		}
		
		item = player.getInventory().getChestplate();
		if(item != null && plugin.getAbilityManager().hasAbility(item) && plugin.getAbilityManager().isTemporaryItem(item)) {
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setHelmet(plugin.getAbilityManager().removeItemLore(item, ability));
			
			if(refund) {
				plugin.getPlayerManager().addTokens(player, ability.getTemporaryPrice());
			}
		}
		
		item = player.getInventory().getLeggings();
		if(item != null && plugin.getAbilityManager().hasAbility(item) && plugin.getAbilityManager().isTemporaryItem(item)) {
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setHelmet(plugin.getAbilityManager().removeItemLore(item, ability));
			
			if(refund) {
				plugin.getPlayerManager().addTokens(player, ability.getTemporaryPrice());
			}
		}
		
		item = player.getInventory().getBoots();
		if(item != null && plugin.getAbilityManager().hasAbility(item) && plugin.getAbilityManager().isTemporaryItem(item)) {
			Ability ability = plugin.getAbilityManager().getAbility(item);
			player.getInventory().setHelmet(plugin.getAbilityManager().removeItemLore(item, ability));
			
			if(refund) {
				plugin.getPlayerManager().addTokens(player, ability.getTemporaryPrice());
			}
		}
	}
	
	/**
	 * Registers all the abilities
	 */
	private void registerAbilities() {
		abilities = new ArrayList<>();
		Ability ability;
		
		// Haste
		ability = new Ability("Haste", Material.GOLD_BOOTS, 10, 100);
		ability.setItems(Arrays.asList(ItemType.SWORD, ItemType.AXE, ItemType.PICKAXE));
		ability.addDescription(ChatColor.GRAY + "Gives Haste Effect");
		ability.addDescription(ChatColor.GRAY + "Haste Level: " + ChatColor.RED + "I");
		abilities.add(ability);
		
		// Poison
		ability = new Ability("Poison", Material.BEETROOT, 15, 150);
		ability.setItems(Arrays.asList(ItemType.SWORD, ItemType.AXE));
		ability.addDescription(ChatColor.GRAY + "Poisons target enemy");
		ability.addDescription(ChatColor.GRAY + "Poison Level: " + ChatColor.RED + "I");
		ability.addDescription(ChatColor.GRAY + "Poison Duration: " + ChatColor.RED + "3 seconds");
		abilities.add(ability);
		
		// AoE (4 blocks)
		ability = new Ability("AoE", Material.GOLD_PICKAXE, 20, 750);
		ability.setItems(Arrays.asList(ItemType.PICKAXE));
		ability.addDescription(ChatColor.GRAY + "Destroys 4 blocks instead of 1");
		abilities.add(ability);
		
		// Magnet
		ability = new Ability("Magnet", Material.DETECTOR_RAIL, 25, 250);
		ability.setItems(Arrays.asList(ItemType.PICKAXE));
		ability.addDescription(ChatColor.GRAY + "Small chance to get crate key");
		ability.addDescription(ChatColor.GRAY + "Chance: " + ChatColor.RED + "0.01%");
		abilities.add(ability);
		
		// Flame
		ability = new Ability("Flame", Material.BLAZE_POWDER, 15, 750);
		ability.setItems(Arrays.asList(ItemType.SWORD, ItemType.AXE));
		ability.addDescription(ChatColor.GRAY + "Sets target on fire");
		ability.addDescription(ChatColor.GRAY + "Fire Duration: " + ChatColor.RED + "3 seconds");
		abilities.add(ability);
		
		// High Jump
		ability = new Ability("High Jump", Material.ELYTRA, 20, 200);
		ability.setItems(Arrays.asList(ItemType.BOOTS, ItemType.LEGGINGS, ItemType.CHESTPLATE, ItemType.HELMET));
		ability.addDescription(ChatColor.GRAY + "Jump 2 blocks high");
		ability.addDescription(ChatColor.GRAY + "Jump Level: " + ChatColor.RED + "II");
		abilities.add(ability);
		
		// Speed
		ability = new Ability("Speed", Material.FEATHER, 15, 150);
		ability.setItems(Arrays.asList(ItemType.BOOTS, ItemType.LEGGINGS, ItemType.CHESTPLATE, ItemType.HELMET));
		ability.addDescription(ChatColor.GRAY + "Gives speed boost");
		ability.addDescription(ChatColor.GRAY + "Speed Level: " + ChatColor.RED + "I");
		abilities.add(ability);
		
		// Lucky Repair
		ability = new Ability("Lucky Repair", Material.ANVIL, 25, 250);
		ability.setItems(Arrays.asList(ItemType.SWORD, ItemType.AXE, ItemType.PICKAXE));
		ability.addDescription(ChatColor.GRAY + "Small chance to repair item");
		ability.addDescription(ChatColor.GRAY + "Chance: " + ChatColor.RED + "0.01%");
		abilities.add(ability);
	}
	
	public void applyAbility(Player player, ItemStack item, Ability ability, boolean temporary) {
		// Adding lore
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ?
				meta.getLore() : new ArrayList<>();
		
		lore.add(ChatColor.GRAY + "Ability: " + ChatColor.RED + ability.getName());
		if(temporary)
			lore.add(ChatColor.GRAY + "Temporary");
		meta.setLore(lore);
		item.setItemMeta(meta);
		player.getInventory().setItemInMainHand(item);
		
		if(!temporary)
			return;
		
		// Adding timer for temporary abilities
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(!isActive(player, item)) {
					this.cancel();
				}
				
				updateTemporaryItem(player, item, ability);
				
				player.sendMessage(Lang.HEADERS_TOKENS.toString() 
						+ Lang.TOKENS_SHOP_EXPIRED.toString()
							.replaceAll("%s", ability.getName()));
			}
			
		}.runTaskLater(plugin, 100 * 60 * 20);
	}
	
	public ItemStack removeItemLore(ItemStack item, Ability ability) {
		ItemStack newItem = item.clone();
		ItemMeta newMeta = newItem.getItemMeta();
		List<String> newLore = newMeta.getLore();
		newLore.remove(ChatColor.GRAY + "Ability: " + ChatColor.RED + ability.getName());
		newLore.remove(ChatColor.GRAY + "Temporary");
		newMeta.setLore(newLore);
		newItem.setItemMeta(newMeta);
		return newItem;
	}
	
	public void updateTemporaryItem(Player player, ItemStack item, Ability ability) {
		
		// Creating item with removed lore
		ItemStack newItem = removeItemLore(item, ability);
		
		player.sendMessage("hiiii");
		// Updating old one (inventory)
		player.sendMessage(player.getInventory().getItem(0) + "");
		for(int i = 0; i < 32; i++) {
			player.sendMessage(i + ".");
			ItemStack tempItem = player.getInventory().getItem(i);
			if(tempItem == null)
				continue;
			
			player.sendMessage(tempItem.getType().toString());
			if(tempItem.equals(item)) {
				player.sendMessage(i + "");
				player.getInventory().setItem(i, newItem);
				break;
			}
		}
		
		// Updating old one (armor)
		if(player.getInventory().getChestplate() != null && player.getInventory().getChestplate().equals(item)) {
			player.getInventory().setChestplate(newItem);
		}
		else if(player.getInventory().getHelmet() != null && player.getInventory().getHelmet().equals(item)) {
			player.getInventory().setHelmet(newItem);
		}
		else if(player.getInventory().getLeggings() != null && player.getInventory().getLeggings().equals(item)) {
			player.getInventory().setLeggings(newItem);
		}
		else if(player.getInventory().getBoots() != null && player.getInventory().getBoots().equals(item)) {
			player.getInventory().setBoots(newItem);
		}
	}
	
	public Ability getAbility(ItemStack item) {
		String name = "";
		for(String lore : item.getItemMeta().getLore()) {
			if(lore.contains("Ability: ")) {
				name = lore.substring(13);
			}
		}
		if(name.isEmpty())
			return null;
		
		for(Ability ability : getAbilities()) {
			if(ability.getName().equalsIgnoreCase(name))
				return ability;
		}
		return null;
	}
	
	/**
	 * @param item player's item
	 * @return is player's item has any abilities already
	 */
	public boolean hasAbility(ItemStack item) {
		if(!item.hasItemMeta() || !item.getItemMeta().hasLore())
			return false;
		
		for(String lore : item.getItemMeta().getLore()) {
			if(lore.contains("Ability: ")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param item player's item
	 * @param supported list of supported items
	 * @return is player's item is supported by ability
	 */
	public boolean isSupported(ItemStack item, List<ItemType> supported)
	{
		if(ItemStackGenerator.isAxe(item)) {
			return supported.contains(ItemType.AXE);
		}
		else if(ItemStackGenerator.isPickaxe(item)) {
			return supported.contains(ItemType.PICKAXE);
		}
		else if(ItemStackGenerator.isSword(item)) {
			return supported.contains(ItemType.SWORD);
		}
		else if(ItemStackGenerator.isHelmet(item)) {
			return supported.contains(ItemType.HELMET);
		}
		else if(ItemStackGenerator.isChestplate(item)) {
			return supported.contains(ItemType.CHESTPLATE);
		}
		else if(ItemStackGenerator.isLeggings(item)) {
			return supported.contains(ItemType.LEGGINGS);
		}
		else if(ItemStackGenerator.isBoots(item)) {
			return supported.contains(ItemType.BOOTS);
		}
		return false;
	}
	
	/**
	 * @param item Item to check
	 * @return whetever item has temporary or not ability
	 */
	public boolean isTemporaryItem(ItemStack item) {
		if(!item.hasItemMeta() || !item.getItemMeta().hasLore())
			return false;
		
		for(String lore : item.getItemMeta().getLore()) {
			if(lore.contains("Temporary")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the abilities
	 */
	public List<Ability> getAbilities() {
		return abilities;
	}

	/**
	 * @param abilities the abilities to set
	 */
	public void setAbilities(List<Ability> abilities) {
		this.abilities = abilities;
	}
	
	/**
	 * @param name search for ability with this name
	 * @return found ability
	 */
	public Ability getAbility(String name) {
		for(Ability ability : getAbilities()) {
			if(ability.getName().equals(name))
				return ability;
		}
		return null;
	}
	
	/**
	 * @param ability the ability to add to all abilities list
	 */
	public void addAbility(Ability ability) {
		abilities.add(ability);
	}
	
	public boolean isActive(Player player, ItemStack item) {
		return player.isOnline() && player.getInventory().contains(item);
	}
	
	
}
