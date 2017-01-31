package com.nekrosius.asgardascension.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.ItemType;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.objects.Ability;
import com.nekrosius.asgardascension.utils.ItemStackGenerator;

public class AbilityManager {
	
	private List<Ability> abilities;
	
	// |--------|------|-----------|
	// | Player | Item | Time left |
	// |--------|------|-----------|
	private Table<Player, ItemStack, Integer> items;
	
	Main plugin;
	
	public AbilityManager(Main plugin) {
		this.plugin = plugin;
		this.items = HashBasedTable.create();
		registerAbilities();
	}
	
	/**
	 * Registers all the abilities
	 */
	private void registerAbilities() {
		String duration = ChatColor.GRAY + "Duration: " + ChatColor.RED + "15 minutes";
		abilities = new ArrayList<>();
		Ability ability;
		
		// Haste
		ability = new Ability("Haste", Material.GOLD_BOOTS, 10, 100);
		ability.setItems(Arrays.asList(ItemType.SWORD, ItemType.AXE, ItemType.PICKAXE));
		ability.addDescription(ChatColor.GRAY + "Gives Haste Effect");
		ability.addDescription(ChatColor.GRAY + "Haste Level: " + ChatColor.RED + "I");
		ability.addDescription(duration);
		abilities.add(ability);
		
		// Poison
		ability = new Ability("Poison", Material.BEETROOT, 15, 150);
		ability.setItems(Arrays.asList(ItemType.SWORD, ItemType.AXE));
		ability.addDescription(ChatColor.GRAY + "Poisons target enemy");
		ability.addDescription(ChatColor.GRAY + "Poison Level: " + ChatColor.RED + "I");
		ability.addDescription(ChatColor.GRAY + "Poison Duration: " + ChatColor.RED + "3 seconds");
		ability.addDescription(duration);
		abilities.add(ability);
		
		// AoE (4 blocks)
		ability = new Ability("AoE", Material.GOLD_PICKAXE, 20, 200);
		ability.setItems(Arrays.asList(ItemType.PICKAXE));
		ability.addDescription(ChatColor.GRAY + "Destroys 4 blocks instead of 1");
		ability.addDescription(duration);
		abilities.add(ability);
		
		// Magnet
		ability = new Ability("Magnet", Material.DETECTOR_RAIL, 25, 250);
		ability.setItems(Arrays.asList(ItemType.PICKAXE));
		ability.addDescription(ChatColor.GRAY + "Small chance to get crate key");
		ability.addDescription(ChatColor.GRAY + "Chance: " + ChatColor.RED + "0.01%");
		ability.addDescription(duration);
		abilities.add(ability);
		
		// Flame
		ability = new Ability("Flame", Material.BLAZE_POWDER, 15, 150);
		ability.setItems(Arrays.asList(ItemType.SWORD, ItemType.AXE));
		ability.addDescription(ChatColor.GRAY + "Sets target on fire");
		ability.addDescription(ChatColor.GRAY + "Fire Duration: " + ChatColor.RED + "3 seconds");
		ability.addDescription(duration);
		abilities.add(ability);
		
		// High Jump
		ability = new Ability("High Jump", Material.ELYTRA, 20, 200);
		ability.setItems(Arrays.asList(ItemType.BOOTS, ItemType.LEGGINGS, ItemType.CHESTPLATE, ItemType.HELMET));
		ability.addDescription(ChatColor.GRAY + "Jump 2 blocks high");
		ability.addDescription(ChatColor.GRAY + "Jump Level: " + ChatColor.RED + "II");
		ability.addDescription(duration);
		abilities.add(ability);
		
		// Speed
		ability = new Ability("Speed", Material.FEATHER, 15, 150);
		ability.setItems(Arrays.asList(ItemType.BOOTS, ItemType.LEGGINGS, ItemType.CHESTPLATE, ItemType.HELMET));
		ability.addDescription(ChatColor.GRAY + "Gives speed boost");
		ability.addDescription(ChatColor.GRAY + "Speed Level: " + ChatColor.RED + "I");
		ability.addDescription(duration);
		abilities.add(ability);
		
		// Lucky Repair
		ability = new Ability("Lucky Repair", Material.ANVIL, 25, 250);
		ability.setItems(Arrays.asList(ItemType.SWORD, ItemType.AXE, ItemType.PICKAXE));
		ability.addDescription(ChatColor.GRAY + "Small chance to repair item");
		ability.addDescription(ChatColor.GRAY + "Chance: " + ChatColor.RED + "0.01%");
		ability.addDescription(duration);
		abilities.add(ability);
	}
	
	public void applyAbility(Player player, ItemStack item, Ability ability, boolean temporary) {
		// Adding lore
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ?
				meta.getLore() : new ArrayList<>();
				
		lore.add(ChatColor.GRAY + "Ability: " + ChatColor.RED + ability.getName());
		meta.setLore(lore);
		item.setItemMeta(meta);
		player.getInventory().setItemInMainHand(item);
		
		addItem(player, item);
		
		if(!temporary)
			return;
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(!isActive(player, item)) {
					player.sendMessage("cancelled");
					this.cancel();
				}
				
				/*
				ItemMeta newMeta = item.getItemMeta();
				List<String> newLore = newMeta.getLore();
				newLore.remove(ChatColor.GRAY + "Ability: " + ChatColor.RED + ability.getName());
				newMeta.setLore(newLore);
				item.setItemMeta(newMeta);
				player.getInventory().setItemInMainHand(item);
				*/
				player.sendMessage(Lang.HEADERS_TOKENS.toString() 
						+ Lang.TOKENS_SHOP_EXPIRED.toString()
							.replaceAll("%t", ability.getName()));
			}
			
		}.runTaskLater(plugin, 100); //* 60 * 20);
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
	
	/**
	 * @param player the item holder
	 * @param item the player's item
	 */
	public void addItem(Player player, ItemStack item) {
		items.put(player, item, 15);
	}
	
	/**
	 * @param player the item holder
	 * @param item the player's item
	 */
	public void subtractMinute(Player player, ItemStack item) {
		items.put(player, item, items.get(player, item) - 1);
	}
	
	public boolean isActive(Player player, ItemStack item) {
		return player.getInventory().contains(item);
	}
	
	
}
