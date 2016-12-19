package com.nekrosius.asgardascension.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.managers.TribeManager;

public class CustomEnchantsListener implements Listener {
	
	Main plugin;
	public CustomEnchantsListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			if(damager.getInventory().getItemInMainHand() == null)
				return;
			if(damager.getInventory().getItemInMainHand().getItemMeta().getLore() == null)
				return;
			Player victim = (Player) event.getEntity();
			if(!TribeManager.canAttack(damager, victim)) 
				return;
			if(damager.getInventory().getItemInMainHand().getItemMeta().getLore().contains("Wither Damage")) {
				victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
			}
			else if(damager.getInventory().getItemInMainHand().getItemMeta().getLore().contains("Fire Damage")) {
				victim.setFireTicks(100);
			}
			else if(damager.getInventory().getItemInMainHand().getItemMeta().getLore().contains("Poison Damage")) {
				victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0));
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand() == null)
			return;
		if(player.getInventory().getItemInMainHand().getItemMeta().getLore() == null)
			return;
		if(player.getInventory().getItemInMainHand().getItemMeta().getLore().contains("2x2 Explosion")) {
			BlockFace playerDir = yawToFace(player.getLocation().getYaw());
			Block block = event.getBlock();
			Block up = block.getRelative(BlockFace.UP);
			Block left = block.getRelative(BlockFace.EAST);
			Block right = block.getRelative(BlockFace.WEST);
			if(playerDir.equals(BlockFace.EAST) || playerDir.equals(BlockFace.WEST)) {
				left = block.getRelative(BlockFace.NORTH);
				right = block.getRelative(BlockFace.SOUTH);
			}
			if(Main.wg.canBuild(player, block)) {
				player.getInventory().addItem(new ItemStack(block.getType()));
				block.setType(Material.AIR);
			}
			if(Main.wg.canBuild(player, up)) {
				player.getInventory().addItem(new ItemStack(up.getType()));
				up.setType(Material.AIR);
			}
			if(Main.wg.canBuild(player, left)) {
				player.getInventory().addItem(new ItemStack(left.getType()));
				left.setType(Material.AIR);
			}
			if(Main.wg.canBuild(player, right)) {
				player.getInventory().addItem(new ItemStack(right.getType()));
				right.setType(Material.AIR);
			}
		}
	}
	
	public static BlockFace yawToFace(float yaw) {
		BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
		return axis[Math.round(yaw / 90f) & 0x3];
    }
 

}