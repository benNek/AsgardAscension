package com.nekrosius.asgardascension.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.ItemType;
import com.nekrosius.asgardascension.objects.Ability;

import net.md_5.bungee.api.ChatColor;

public class AbilityManager {
	
	private List<Ability> abilities;
	Main plugin;
	
	public AbilityManager(Main plugin) {
		this.plugin = plugin;
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
	
	public boolean canUse(ItemStack item, List<ItemType> supported)
	{
		
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
	
}
