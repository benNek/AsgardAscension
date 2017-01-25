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
		abilities = new ArrayList<>();
		Ability ability;
		
		ability = new Ability("Haste", Material.GOLD_BOOTS, 10, 100);
		ability.setItems(Arrays.asList(ItemType.SWORD, ItemType.AXE, ItemType.PICKAXE));
		ability.addDescription(ChatColor.GRAY + "Gives Haste Effect");
		ability.addDescription(ChatColor.GRAY + "Duration");
		abilities.add(ability);
		
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
	 * @param ability the ability to add to all abilities list
	 */
	public void addAbility(Ability ability) {
		abilities.add(ability);
	}
	

}
