package com.nekrosius.asgardascension.utils;

import org.bukkit.entity.Player;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Cooldowns {

	static Table<String, String, Long> allCooldowns = HashBasedTable.create();
	
	private Cooldowns() {

	}
	
	public static long getCooldown(Player player, String key) {
		return calculateRemainder(allCooldowns.get(player.getName(), key));
	}
	
	public static long setCooldown(Player player, String key, long delay) {
		return calculateRemainder(
				allCooldowns.put(player.getName(), key, System.currentTimeMillis() + delay));
	}
	 
	public static boolean tryCooldown(Player player, String key, long delay) {
		if (getCooldown(player, key) <= 0) {
			setCooldown(player, key, delay);
			return true;
		}
		return false;
	}
	 
	static long calculateRemainder(Long expireTime) {
		return expireTime != null ? expireTime - System.currentTimeMillis() : Long.MIN_VALUE;
	}
	
}