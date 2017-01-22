package com.nekrosius.asgardascension.utils;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import com.nekrosius.asgardascension.Main;

/*
 * Util by BENAS "BENRUSH" NEKROÐIUS
 * Converts
 */

public class Convert {
	
	private static Main plugin = (Main)Bukkit.getPluginManager().getPlugin("AsgardAscension");
	
	private Convert() {
		
	}
	
	public static String addSuffix(int amount) {
		if(amount > 1)
			return "s";
		return "";
	}
	
	public static void spawnEntity(String player, int challenge, String str, int amount) {
		EntityType entity = EntityType.valueOf(str);
		Location loc1 = plugin.getChallengesFile().getFirstLocation(challenge);
		Location loc2 = plugin.getChallengesFile().getSecondLocation(challenge);
		Location spawnLoc = loc1;
		for(int i = 0; i < amount; i++) {
			spawnLoc.setX(getRandom(loc1.getBlockX(), loc2.getBlockX()));
			spawnLoc.setY(getRandom(loc1.getBlockY(), loc2.getBlockY()));
			spawnLoc.setZ(getRandom(loc1.getBlockZ(), loc2.getBlockZ()));
			Entity e = loc1.getWorld().spawnEntity(spawnLoc, entity);
			e.setMetadata("challenge", new FixedMetadataValue(plugin, String.valueOf(challenge) + ", " + player));
		}
	}
	
	public static BlockFace yawToFace(float yaw) {
		BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
		return axis[Math.round(yaw / 90f) & 0x3];
    }
	
	public static Location stringToLocation(String str) {
		String[] st = str.split(", ");
		Location loc;
		double x = Double.parseDouble(st[1]);
		double y = Double.parseDouble(st[2]);
		double z = Double.parseDouble(st[3]);
		loc = new Location(Bukkit.getWorld(st[0]), x, y, z);
		if(st.length == 6){
			loc.setYaw(Float.parseFloat(st[4]));
			loc.setPitch(Float.parseFloat(st[5]));
		}
		return loc;
	}
	
	public static String locationToString(Location loc, boolean yawAndPitch){
		String str = loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
		if(yawAndPitch){
			str += ", " + loc.getYaw() + ", " + loc.getPitch();
		}
		return str;
	}
	
	public static String arabicToRome(int n) {
		if(n == 1)
			return "I";
		else if(n == 2)
			return "II";
		else if(n == 3)
			return "III";
		else if(n == 4)
			return "IV";
		else if(n == 5)
			return "V";
		else if(n == 6)
			return "VI";
		else if(n == 7)
			return "VII";
		else if(n == 8)
			return "VIII";
		return "0";
	}

	public static int stringToTime(String str) {
		String[] st = str.split(" ");
		int time = 0;
		try{
			time = Integer.parseInt(st[0]);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		time *= 20;
		String format = st[1];
		
		switch(format) {
			case "m":
			case "min":
			case "minute":
			case "minutes":
				time *= 60;
				break;
			case "h":
			case "hour":
			case "hours":
				time *= 3600;
				break;
			default:
				break;
		}
		
		return time;
	}
	
	public static String timeToString(int seconds) {
		if(seconds / 3600 > 2) {
			return seconds / 3600 + " hours";
		}
		else if(seconds / 60 > 2) {
			return seconds / 60 + " minutes";
		}
		return seconds + " seconds";
	}
	
	public static ChatColor stringToChatColor(String str) {
		switch(str.toLowerCase()) {
			case "aqua":
				return ChatColor.AQUA;
			case "black":
				return ChatColor.BLACK;
			case "blue":
				return ChatColor.BLUE;
			case "dark_blue":
				return ChatColor.DARK_BLUE;
			case "dark_gray":
				return ChatColor.DARK_GRAY;
			case "dark_green":
				return ChatColor.DARK_GREEN;
			case "dark_purple":
				return ChatColor.DARK_PURPLE;
			case "dark_red":
				return ChatColor.DARK_RED;
			case "gold":
				return ChatColor.GOLD;
			case "gray":
				return ChatColor.GRAY;
			case "green":
				return ChatColor.GREEN;
			case "light_puple":
				return ChatColor.LIGHT_PURPLE;
			case "white":
				return ChatColor.WHITE;
			case "yellow":
				return ChatColor.YELLOW;
			default:
				return ChatColor.RED;
		}
	}
	
	public static String getOrdinalFor(int number){
		if(number >= 10 && number <= 20)
			return "th";
		int remainder = number % 10;
		if(remainder == 1){
			return "st";
		}else if(remainder == 2){
			return "nd";
		}else if(remainder == 3){
			return "rd";
		}else{
			return "th";
		}
	}
	
	public static boolean areLocationsEqual(Location loc1, Location loc2) {
		if(loc1.getBlockX() == loc2.getBlockX()
				&& loc1.getBlockY() == loc2.getBlockY()
				&& loc1.getBlockZ() == loc2.getBlockZ()) return true;
		return false;
	}
	
	public static Color chatColorToColor(ChatColor color) {
		switch(color) {
			case AQUA:
				return Color.AQUA;
			case BLACK:
				return Color.BLACK;
			case BLUE:
				return Color.NAVY;
			case DARK_BLUE:
				return Color.BLUE;
			case DARK_GRAY:
				return Color.SILVER;
			case DARK_GREEN:
				return Color.GREEN;
			case DARK_PURPLE:
				return Color.PURPLE;
			case DARK_RED:
				return Color.MAROON;
			case GOLD:
				return Color.ORANGE;
			case GRAY:
				return Color.GRAY;
			case GREEN:
				return Color.LIME;
			case LIGHT_PURPLE:
				return Color.FUCHSIA;
			case YELLOW:
				return Color.YELLOW;
			case WHITE:
				return Color.WHITE;
			default:
				return Color.RED;
		}
	}
	
	static double getRandom(int min, int max){
		if(min > max){
			int temp = max;
			max = min;
			min = temp;
		}
		Random random = new Random();
		return random.nextInt((max - min) + 1) + (double)min;
	}
	
	public static String toPrice(double price) {
		DecimalFormat formatter = new DecimalFormat("#,###.00");
		System.out.println(formatter.format(price));
		return ChatColor.GRAY + "\\$" + ChatColor.RED + formatter.format(price) + ChatColor.GRAY;
	}
    
}