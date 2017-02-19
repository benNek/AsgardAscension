package com.nekrosius.asgardascension.listeners.tokens;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.objects.Rune;
import com.nekrosius.asgardascension.utils.Convert;
import com.nekrosius.asgardascension.utils.Cooldowns;

public class RuneListener implements Listener {
	
	Main plugin;
	public RuneListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onRuneUse(PlayerInteractEvent event) {
		if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			return;
		}
		
		if(event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) {
			return;
		}
		
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if(item == null || item.getItemMeta() == null || !item.getType().equals(Material.ENCHANTED_BOOK)) {
			return;
		}
		
		Player player = event.getPlayer();
		Rune rune = plugin.getRuneManager().getRune(item.getItemMeta().getDisplayName().substring(2));
		if(rune == null) {
			return;
		}
		
		event.setCancelled(true);
		
		if(plugin.getRuneManager().hasActiveRune(player)) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString() + Lang.TOKENS_RUNE_ALREADY_ACTIVE.toString());
			return;
		}
		
		if(Cooldowns.getCooldown(player, "rune") > 0) {
			player.sendMessage(Lang.HEADERS_TOKENS.toString() + Lang.TOKENS_RUNE_COOLDOWN.toString()
				.replaceAll("%d", Convert.timeToString((int) (Cooldowns.getCooldown(player, "rune") / 1000))));
			return;
		}
		
		plugin.getRuneManager().start(player, rune);
	}
	
	@EventHandler
	public void onTakeRune(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if(plugin.getRuneManager().getEffect(player) != null && plugin.getRuneManager().getActiveRune(player) == null) {
			plugin.getRuneManager().removeEffect(event.getPlayer());
		}
		
		ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if(item == null || item.getItemMeta() == null || !item.getType().equals(Material.ENCHANTED_BOOK)) {
			return;
		}
		
		Rune rune = plugin.getRuneManager().getRune(item.getItemMeta().getDisplayName().substring(2));
		if(rune == null) {
			return;
		}
		
		plugin.getRuneManager().startHoldingEffect(player, rune);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if(plugin.getRuneManager().hasActiveRune(event.getPlayer())) {
			plugin.getRuneManager().finish(event.getPlayer(), false);
		}
		
		// Removing invisibility of hidden players
		for(UUID uuid : plugin.getRuneManager().getInvisiblePlayers()) {
			event.getPlayer().showPlayer(Bukkit.getPlayer(uuid));
		}
	}

}
