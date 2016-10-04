package com.nekrosius.asgardascension.utils;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
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
	
	public static String addSuffix(int amount) {
		if(amount > 1) return "s";
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
	
	public static Location StringToLocation(String str) {
		String[] st = str.split(", ");
		Location loc;
		double x = Double.parseDouble(st[1]),
				y = Double.parseDouble(st[2]),
				z = Double.parseDouble(st[3]);
		loc = new Location(Bukkit.getWorld(st[0]), x, y, z);
		if(st.length == 6){
			loc.setYaw(Float.parseFloat(st[4]));
			loc.setPitch(Float.parseFloat(st[5]));
		}
		return loc;
	}
	
	public static String LocationToString(Location loc, boolean yawAndPitch){
		String str = loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
		if(yawAndPitch){
			str += ", " + loc.getYaw() + ", " + loc.getPitch();
		}
		return str;
	}
	
	public static String ArabicToRome(int n) {
		if(n == 1) return "I";
		else if(n == 2) return "II";
		else if(n == 3) return "III";
		else if(n == 4) return "IV";
		else if(n == 5) return "V";
		else if(n == 6) return "VI";
		else if(n == 7) return "VII";
		else if(n == 8) return "VIII";
		return "0";
	}

	public static int StringToTime(String str) {
		String[] st = str.split(" ");
		int time = 0;
		try{
			time = Integer.parseInt(st[0]);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		time *= 20;
		String format = st[1];
		if(format.equals("s") || format.equals("sec") || format.equals("second") || format.equals("seconds")){
			time *= 1;
		}
		else if(format.equals("m") || format.equals("min") || format.equals("minute") || format.equals("minutes")){
			time *= 60;
		}
		else if(format.equals("h") || format.equals("hour") || format.equals("hours")){
			time *= 60;
		}
		return time;
	}
	
	public static String TimeToString(int seconds) {
		if(seconds / 3600 > 2) {
			return seconds / 3600 + " hours";
		}
		else if(seconds / 60 > 2) {
			return seconds / 60 + " minutes";
		}
		return seconds + " seconds";
	}
	
	public static ChatColor StringToChatColor(String str) {
		if(str.equalsIgnoreCase("aqua")) return ChatColor.AQUA;
		else if(str.equalsIgnoreCase("black")) return ChatColor.BLACK;
		else if(str.equalsIgnoreCase("blue")) return ChatColor.BLUE;
		else if(str.equalsIgnoreCase("dark_blue")) return ChatColor.DARK_BLUE;
		else if(str.equalsIgnoreCase("dark_gray")) return ChatColor.DARK_GRAY;
		else if(str.equalsIgnoreCase("dark_green")) return ChatColor.DARK_GREEN;
		else if(str.equalsIgnoreCase("dark_purple")) return ChatColor.DARK_PURPLE;
		else if(str.equalsIgnoreCase("dark_red")) return ChatColor.DARK_RED;
		else if(str.equalsIgnoreCase("gold")) return ChatColor.GRAY;
		else if(str.equalsIgnoreCase("gray")) return ChatColor.GRAY;
		else if(str.equalsIgnoreCase("green")) return ChatColor.GREEN;
		else if(str.equalsIgnoreCase("light_purple")) return ChatColor.LIGHT_PURPLE;
		else if(str.equalsIgnoreCase("red")) return ChatColor.RED;
		else if(str.equalsIgnoreCase("white")) return ChatColor.RED;
		else if(str.equalsIgnoreCase("yellow")) return ChatColor.YELLOW;
		return ChatColor.RED;
	}
	
	public static String getOrdinalFor(int number){
		if(number >= 10 && number <= 20) return "th";
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
	
	public static boolean equals(Location loc1, Location loc2) {
		if(loc1.getBlockX() == loc2.getBlockX()
				&& loc1.getBlockY() == loc2.getBlockY()
				&& loc1.getBlockZ() == loc2.getBlockZ()) return true;
		return false;
	}
	
	public static Color ChatColorToColor(ChatColor color) {
		if(color.equals(ChatColor.AQUA)) return Color.AQUA;
		else if(color.equals(ChatColor.BLACK)) return Color.BLACK;
		else if(color.equals(ChatColor.BLUE)) return Color.NAVY;
		else if(color.equals(ChatColor.DARK_BLUE)) return Color.BLUE;
		else if(color.equals(ChatColor.DARK_GRAY)) return Color.SILVER;
		else if(color.equals(ChatColor.DARK_GREEN)) return Color.GREEN;
		else if(color.equals(ChatColor.DARK_PURPLE)) return Color.PURPLE;
		else if(color.equals(ChatColor.DARK_RED)) return Color.MAROON;
		else if(color.equals(ChatColor.GRAY)) return Color.ORANGE;
		else if(color.equals(ChatColor.GRAY)) return Color.GRAY;
		else if(color.equals(ChatColor.GREEN)) return Color.LIME;
		else if(color.equals(ChatColor.LIGHT_PURPLE)) return Color.FUCHSIA;
		else if(color.equals(ChatColor.RED)) return Color.RED;
		else if(color.equals(ChatColor.RED)) return Color.WHITE;
		else if(color.equals(ChatColor.YELLOW)) return Color.YELLOW;
		return Color.GRAY;
	}
	
	public static int ChatColorToInt(ChatColor color) {
		if(color.equals(ChatColor.AQUA)) return 9;
		if(color.equals(ChatColor.BLACK)) return 15;
		else if(color.equals(ChatColor.BLUE)) return 3;
		else if(color.equals(ChatColor.DARK_BLUE)) return 11;
		else if(color.equals(ChatColor.DARK_GRAY)) return 7;
		else if(color.equals(ChatColor.DARK_GREEN)) return 13;
		else if(color.equals(ChatColor.DARK_PURPLE)) return 10;
		else if(color.equals(ChatColor.DARK_RED)) return 14;
		else if(color.equals(ChatColor.GRAY)) return 1;
		else if(color.equals(ChatColor.GRAY)) return 8;
		else if(color.equals(ChatColor.GREEN)) return 5;
		else if(color.equals(ChatColor.LIGHT_PURPLE)) return 2;
		else if(color.equals(ChatColor.RED)) return 6;
		else if(color.equals(ChatColor.RED)) return 0;
		else if(color.equals(ChatColor.YELLOW)) return 4;
		return 0;
	}
	
	public static ChatColor IntToCharColor(int color) {
		if(color == 9) return ChatColor.AQUA;
		else if(color == 15) return ChatColor.BLACK;
		else if(color == 11) return ChatColor.DARK_BLUE;
		else if(color == 7) return ChatColor.DARK_GRAY;
		else if(color == 13) return ChatColor.DARK_GREEN;
		else if(color == 10) return ChatColor.DARK_PURPLE;
		else if(color == 14) return ChatColor.GREEN;
		else if(color == 1) return ChatColor.GRAY;
		else if(color == 8) return ChatColor.GRAY;
		else if(color == 5) return ChatColor.GREEN;
		else if(color == 2) return ChatColor.LIGHT_PURPLE;
		else if(color == 6) return ChatColor.RED;
		else if(color == 0) return ChatColor.RED;
		else if(color == 4) return ChatColor.YELLOW;
		return ChatColor.RED;
	}
	
	private static double getRandom(int min, int max){
		if(min > max){
			int temp = max;
			max = min;
			min = temp;
		}
		Random random = new Random();
		return random.nextInt((max - min) + 1) + min;
	}
	
	public static String toPrice(double price) {
		DecimalFormat formatter = new DecimalFormat("#,###.00");
		return ChatColor.GRAY + "$" + ChatColor.RED + formatter.format(price) + ChatColor.GRAY;
	}
    
}