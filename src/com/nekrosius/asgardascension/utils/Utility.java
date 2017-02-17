package com.nekrosius.asgardascension.utils;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.intellectualcrafters.plot.object.Plot;
import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.managers.TribeManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Utility {
	
	private static Main plugin = (Main)Bukkit.getPluginManager().getPlugin("AsgardAscension");
	private static Random random = new Random();
	
	public static int getRandom(int min, int max){
		return random.nextInt((max - min) + 1) + min;
	}
	
	public static boolean isPVPEnabled(Player player) {
		return isPVPEnabled(player.getLocation());
	}
	
	public static boolean isPVPEnabled(Location location) {
		String global = "__global__";
		if(plugin.getWorldGuard().getRegionManager(location.getWorld()) == null)	
			return true;
		RegionManager regionManager = plugin.getWorldGuard().getRegionManager(location.getWorld());
		ApplicableRegionSet arset = regionManager.getApplicableRegions(location);
		ProtectedRegion region = regionManager.getRegion(global);
		int priority = -10000;
		for(ProtectedRegion r : arset.getRegions()) { 
			if(r.getPriority() > priority) {
				region = r;
				priority = r.getPriority();
			}
		}
		if(region == null) {
			if(regionManager.getRegion(global) == null)
				return false;
			return "ALLOW".equals(regionManager.getRegion(global).getFlag(DefaultFlag.PVP).toString());
		}
		if(region.getFlag(DefaultFlag.PVP) == null)
			return true;
		return "ALLOW".equalsIgnoreCase(region.getFlag(DefaultFlag.PVP).toString());
	}
	
	public static boolean canBuild(Player player, Block block) {
		return plugin.getWorldGuard().canBuild(player, block) && canBreak(player, block.getLocation())
				&& !block.getType().equals(Material.BEDROCK);
	}
	
	public static boolean canBreak(Player player, Location location) {
		Plot plot = plugin.getPlotsAPI().getPlot(location);
		if(plot == null) {
			return true;
		}
		return plot.isAdded(player.getUniqueId());
	}
	
	public static boolean canAttack(Player one, Player two) {
		if(!isPVPEnabled(one) || !isPVPEnabled(two)) {
			return false;
		}
		if(!TribeManager.hasTribe(one.getName()) || !TribeManager.hasTribe(two.getName())) {
			return true;
		}
		if(TribeManager.getPlayerTribe(one.getName()).getName().equals(TribeManager.getPlayerTribe(two.getName()).getName())){
			return false;
		}
		if(TribeManager.isAllies(TribeManager.getPlayerTribe(one.getName()), TribeManager.getPlayerTribe(two.getName()))) {
			return false;
		}
		return true;
	}
	
	public static Player getTargetPlayer(Player player, int distance) {
		return getTarget(player, player.getWorld().getPlayers(), distance);
	}
 
	public static <T extends Entity> T getTarget(Entity entity, Iterable<T> entities, int distance) {
		T target = null;
		double threshold = 1;
		for (T other:entities) {
			if(entity.getLocation().distance(other.getLocation()) <= distance){
				Vector n = other.getLocation().toVector().subtract(entity.getLocation().toVector());
				if (entity.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold && n.normalize().dot(entity.getLocation().getDirection().normalize()) >= 0) {
					if (target == null || target.getLocation().distanceSquared(entity.getLocation()) > other.getLocation().distanceSquared(entity.getLocation()))
						target = other;
				}
			}
		}
		return target;
	}

}
