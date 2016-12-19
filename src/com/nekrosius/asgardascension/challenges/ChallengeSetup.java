package com.nekrosius.asgardascension.challenges;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ChallengeSetup {
	
	private static Map<String, Integer> setupStep = new HashMap<>();
	private static Map<String, Integer> challengeIndex = new HashMap<>();
	private static Map<String, String> 	type = new HashMap<>();
	private static Map<String, Boolean> editing = new HashMap<>();
	
	private static Map<String, Location> loc1 = new HashMap<>();
	private static Map<String, Location> loc2 = new HashMap<>();
	
	private static Map<String, String> 	mobType = new HashMap<>();
	
	private ChallengeSetup() {
		
	}
	
	public static void setStep(Player player, int step) {
		setupStep.put(player.getName(), step);
	}
	
	public static int getStep(Player player) {
		if(setupStep.get(player.getName()) == null)
			return 0;
		return setupStep.get(player.getName());
	}
	
	public static void setChallenge(Player player, int index) {
		challengeIndex.put(player.getName(), index);
	}
	
	public static int getChallenge(Player player) {
		if(challengeIndex.get(player.getName()) == null)
			return 0;
		return challengeIndex.get(player.getName());
	}
	
	public static void setType(Player player, String typ) {
		type.put(player.getName(), typ);
	}
	
	public static String getType(Player player) {
		return type.get(player.getName());
	}
	
	public static void setEditing(Player player, boolean bool) {
		editing.put(player.getName(), bool);
	}
	
	public static boolean isEditing(Player player) {
		if(editing.get(player.getName()) == null)
			return false;
		return editing.get(player.getName());
	}
	
	public static void setFirstLocation(Player player, Location loc) {
		loc1.put(player.getName(), loc);
	}
	
	public static Location getFirstLocation(Player player) {
		return loc1.get(player.getName());
	}
	
	public static void setSecondLocation(Player player, Location loc) {
		loc2.put(player.getName(), loc);
	}
	
	public static Location getSecondLocation(Player player) {
		return loc2.get(player.getName());
	}
	
	public static boolean isReady(Player player) {
		return (getFirstLocation(player) != null) && (getSecondLocation(player) != null);
	}
	
	public static void setMob(Player player, String mob) {
		mobType.put(player.getName(), mob);
		setStep(player, 8);
	}
	
	public static String getMob(Player player) {
		return mobType.get(player.getName());
	}
	
	public static void finish(Player player) {
		challengeIndex.remove(player.getName());
		setupStep.remove(player.getName());
		type.remove(player.getName());
		editing.remove(player.getName());
		loc1.remove(player.getName());
		loc2.remove(player.getName());
		mobType	.remove(player.getName());
	}

}
