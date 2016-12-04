package com.nekrosius.asgardascension.listeners;

import java.util.List;

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
import com.nekrosius.asgardascension.files.GodFoodFile;
import com.nekrosius.asgardascension.handlers.FoodSetup;

import de.slikey.effectlib.effect.LoveEffect;
import de.slikey.effectlib.util.ParticleEffect;

public class GodFoodListener implements Listener {

	private Main pl;
	public GodFoodListener(Main plugin) {
		pl = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onFoodEat(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(event.getPlayer().isSneaking()) {
				if(event.getPlayer().getItemInHand() != null) {
					Player player = event.getPlayer();
					List<Integer> ids = GodFoodFile.getFoodId(player.getItemInHand().getTypeId());
					for(int id : ids) {
						byte data = (byte)GodFoodFile.getData(id);
						if(player.getItemInHand().getData().getData() != data) continue;
						if(id != 0){
							if(player.getItemInHand().getAmount() > GodFoodFile.getAmount(id)){
								ItemStack item = player.getItemInHand();
								item.setAmount(item.getAmount() - GodFoodFile.getAmount(id));
								player.setItemInHand(item);
							}else if(player.getItemInHand().getAmount() == GodFoodFile.getAmount(id)){
								player.setItemInHand(new ItemStack(Material.AIR));
							}else{
								return;
							}
							event.setCancelled(true);
							int maxDuration = -1;
							for(PotionEffect pe : GodFoodFile.getPotionEffects(id)){
								player.addPotionEffect(pe);
								if(pe.getDuration() > maxDuration) maxDuration = pe.getDuration();
							}
							final LoveEffect he = new LoveEffect(Main.em);
							he.particle = ParticleEffect.CLOUD;
							he.setEntity(player);
							he.infinite();
							he.start();
							new BukkitRunnable() {
								public void run() {
									he.cancel();
								}
							}.runTaskLater(pl, maxDuration);
							player.sendMessage(FoodSetup.mh + "You used " 
									+ ChatColor.RED + GodFoodFile.getName(id) + ChatColor.GRAY + "!");
						}
					}
				}
			}
		}
	}
	
	public Main getPlugin() {
		return pl;
	}
	
}
