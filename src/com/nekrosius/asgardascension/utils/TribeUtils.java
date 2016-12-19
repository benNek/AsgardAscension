package com.nekrosius.asgardascension.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TribeUtils {
	
	public TribeUtils() {
		// It's empty because why not?
	}
	
	public Location getCenterLocation(Location loc1, Location loc2){
		Location loc = new Location(loc1.getWorld(), 0, 0, 0);
		double x = (loc1.getX() + loc2.getX()) / 2;
		double z = (loc1.getZ() + loc2.getZ()) / 2;
		loc.setX(x);
		loc.setY(loc1.getY());
		loc.setZ(z);
		return loc;
	}
	
	public List<Player> getNearbyPlayers(Location loc, double radius){
		List<Player> players = new ArrayList<>();
		for(Entity e : loc.getWorld().getEntities()){
			if(e instanceof Player && loc.distance(e.getLocation()) <= radius){
				players.add((Player)e);
			}
		}
		return players;
	}
	
	public double getSmashDamageByTribeLevel(int level) {
		switch(level) {
			case 1:
				return 3;
			case 2:
				return 4;
			case 3:
				return 6;
			case 4:
				return 7;
			default:
				return 0;
		}
	}
	
	public double getFireballDamageByTribeLevel(int level, boolean direct) {
		if(direct){
			return 10;
		}
		else{
			if(level == 1 || level == 2)
				return 4;
			else
				return 6;
		}
	}

}
