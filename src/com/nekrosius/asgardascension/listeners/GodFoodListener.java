package com.nekrosius.asgardascension.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.files.GodFoodFile;

import de.slikey.effectlib.effect.LoveEffect;
import de.slikey.effectlib.util.ParticleEffect;

public class GodFoodListener implements Listener {
	
	HashMap<UUID, Boolean> hasEffect;

	Main plugin;
	public GodFoodListener(Main plugin) {
		this.plugin = plugin;
		hasEffect = new HashMap<>();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onFoodEat(PlayerInteractEvent event) {
		if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			return;
		}
		if(!event.getPlayer().isSneaking() || event.getPlayer().getInventory().getItemInMainHand() == null) {
			return;
		}
		
		Player player = event.getPlayer();
		List<Integer> ids = GodFoodFile.getFoodId(player.getInventory().getItemInMainHand().getTypeId());
		for(int id : ids) {
			byte data = (byte)GodFoodFile.getData(id);
			
			if(player.getInventory().getItemInMainHand().getData().getData() != data || id == 0)
				continue;
			
			if(player.getInventory().getItemInMainHand().getAmount() > GodFoodFile.getAmount(id)) {
				ItemStack item = player.getInventory().getItemInMainHand();
				item.setAmount(item.getAmount() - GodFoodFile.getAmount(id));
				player.setItemInHand(item);
			} else if(player.getInventory().getItemInMainHand().getAmount() == GodFoodFile.getAmount(id)) {
				player.setItemInHand(new ItemStack(Material.AIR));
			} else {
				return;
			}
			event.setCancelled(true);
			
			addPotionEffects(player, id);
			player.sendMessage(Lang.HEADERS_FOG.toString() + "You used " 
					+ ChatColor.RED + GodFoodFile.getName(id) + ChatColor.GRAY + "!");
		}
	}
	
	private void addPotionEffects(Player player, int id) {
		int maxDuration = -1;
		
		// Adding all potion effects to player
		for(PotionEffect pe : GodFoodFile.getPotionEffects(id)) {
			player.addPotionEffect(pe);
			if(pe.getDuration() > maxDuration)
				maxDuration = pe.getDuration();
		}
		
		if(hasEffect.get(player.getUniqueId()) != null) {
			return;
		}
		// Showing visual effect to indicate that player is using FoG
		hasEffect.put(player.getUniqueId(), true);
		final LoveEffect he = new LoveEffect(plugin.getEffectManager());
		he.particle = ParticleEffect.CLOUD;
		he.setEntity(player);
		he.infinite();
		he.start();
		
		// Canceling the visual effect once the potion effects are over
		new BukkitRunnable() {
			public void run() {
				he.cancel();
				hasEffect.remove(player.getUniqueId());
			}
		}.runTaskLater(plugin, maxDuration);
	}
	
}
