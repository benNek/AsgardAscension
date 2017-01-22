package com.nekrosius.asgardascension.handlers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class FoodSetup {
	
	private static Map<String, Integer> setupStep = new HashMap<>();
	private static Map<String, Integer> foodIndex = new HashMap<>();
	private static Map<String, Boolean> editing = new HashMap<>();
	
	private static Map<String, String> effectType = new HashMap<>();
	private static Map<String, Integer> effectDuration = new HashMap<>();
	private static Map<String, Integer> effectAmplifier = new HashMap<>();
	
	private FoodSetup() {
	
	}
	
	public static void setStep(Player player, int step) {
		setupStep.put(player.getName(), step);
	}
	
	public static int getStep(Player player) {
		if(setupStep.get(player.getName()) == null)
			return 0;
		return setupStep.get(player.getName());
	}
	
	public static void setFoodIndex(Player player, int index) {
		foodIndex.put(player.getName(), index);
	}
	
	public static int getFoodIndex(Player player) {
		if(foodIndex.get(player.getName()) == null)
			return 0;
		return foodIndex.get(player.getName());
	}

	public static void setEditing(Player player, boolean bool) {
		editing.put(player.getName(), bool);
	}
	
	public static boolean isEditing(Player player) {
		if(editing.get(player.getName()) == null)
			return false;
		return editing.get(player.getName());
	}
	
	public static void setType(Player player, String type) {
		effectType.put(player.getName(), type);
	}
	
	public static String getType(Player player) {
		return effectType.get(player.getName());
	}
	
	public static void setDuration(Player player, int duration) {
		effectDuration.put(player.getName(), duration);
	}
	
	public static int getDuration(Player player) {
		if(effectDuration.get(player.getName()) == null)
			return 0;
		return effectDuration.get(player.getName());
	}
	
	public static void setAmplifier(Player player, int amplifier) {
		effectAmplifier.put(player.getName(), amplifier);
	}
	
	public static int getAmplifier(Player player) {
		if(effectAmplifier.get(player.getName()) == null)
			return 0;
		return effectAmplifier.get(player.getName());
	}
	
	public static void finish(Player player) {
		setupStep.remove(player.getName());
		foodIndex.remove(player.getName());
		editing.remove(player.getName());
		finishEffect(player);
	}
	
	public static void finishEffect(Player player) {
		effectType.remove(player.getName());
		effectDuration.remove(player.getName());
		effectAmplifier.remove(player.getName());
	}

}