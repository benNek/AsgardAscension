package com.nekrosius.asgardascension.listeners;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.handlers.GodTokens;
import com.nekrosius.asgardascension.managers.TribeManager;
import com.nekrosius.asgardascension.utils.Convert;

import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.ParticleEffect;

public class GodTokenListener implements Listener {
	
	private Main pl;
	public GodTokenListener(Main plugin) {
		pl = plugin;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getPlayer().getInventory().getItemInMainHand() == null)
			return;
		if(!(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GOLD_SWORD)))
			return;
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			Player player = event.getPlayer();
			boolean remove = false;
			if(!Main.isPVPEnabled(player)) {
				return;
			}
			if("Fireball".equalsIgnoreCase(GodTokens.getSkill(player.getName()))) {
				Fireball fb = player.launchProjectile(Fireball.class);
				fb.setMetadata("god_token", new FixedMetadataValue(pl, player.getName()));
				GodTokens.addCooldown(player.getName(), 10);
				remove = true;
			}
			else if("Lightning".equals(GodTokens.getSkill(player.getName()))) {
				@SuppressWarnings("deprecation")
				Block block = player.getTargetBlock((HashSet<Byte>) null, 15);
				block.getWorld().strikeLightning(block.getLocation());
				for(Entity e : block.getWorld().getEntities()){
					if(e instanceof Player) {
						Player ee = (Player) e;
						if(TribeManager.canAttack(player, ee)) {
							if(ee.getLocation().distance(block.getLocation()) < 1){
								if(ee.getHealth() > 16)
									ee.setHealth(ee.getHealth() - 16);
								else ee.setHealth(0);
							}
							else if(ee.getLocation().distance(block.getLocation()) <= 3){
								if(ee.getHealth() > 10) {
									ee.setHealth(ee.getHealth() - 10);
								}else{
									ee.setHealth(0);
								}
							}
						}
					}
				}
				remove = true;
			}
			else if("Freeze".equals(GodTokens.getSkill(player.getName())) && getTargetPlayer(player) != null) {
				LineEffect eff = new LineEffect(Main.em);
				Player target = getTargetPlayer(player);
				eff.setEntity(player);
				eff.setTargetEntity(target);
				eff.particle = ParticleEffect.SNOW_SHOVEL;
				eff.start();
				player.sendMessage(GodTokens.MESSAGE_HEADER + "You've frozen " + ChatColor.RED + target.getName());
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 6));
				target.sendMessage(GodTokens.MESSAGE_HEADER + "You were frozen by God Token of " + ChatColor.RED + player.getName());
				remove = true;
			}
			if(remove) {
				player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				GodTokens.finish(player.getName());
			}
		}
	}
	
	@EventHandler
	public void onUseNetherStar(PlayerInteractEvent event) {
		if(event.getPlayer().getInventory().getItemInMainHand() == null)
			return;
		if(!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)))
			return;
		if(!(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.NETHER_STAR)))
			return;
		if(!event.getPlayer().getInventory().getItemInMainHand().hasItemMeta())
			return;
		if(!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "God Token"))
			return;
		
		Player player = event.getPlayer();
		int amount = player.getInventory().getItemInMainHand().getAmount();
		ItemStack item = player.getInventory().getItemInMainHand();
		if(amount > 1) {
			item.setAmount(amount - 1);
		}
		else {
			item.setType(Material.AIR);
		}
		player.getInventory().setItemInMainHand(item);
		pl.getPlayerManager().addTokens(player, 1);
		player.sendMessage(GodTokens.MESSAGE_HEADER + "You've successfully deposited 1 GT!");
	}
	
	@EventHandler
	public void onAttack(EntityDamageEvent event) {
		if(event.isCancelled())
			return;
		if(event.getEntity() instanceof Player){
			if("Dodge".equals(GodTokens.getSkill(((Player)event.getEntity()).getName()))){
				if(getRandom(1, 100) <= 30) {
					((Player)event.getEntity()).sendMessage(GodTokens.MESSAGE_HEADER + "You've dodged an attack!");
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if(event.isCancelled())
			return;
		if(event.getDamager() instanceof Fireball && event.getEntity() instanceof Player) {
			if(event.getDamager().hasMetadata("god_token")) {
				Player damager = Bukkit.getPlayer(event.getDamager().getMetadata("god_token").get(0).asString());
				Player victim = (Player) event.getEntity();
				if(damager == victim) {
					event.setCancelled(true);
					return;
				}
				double damage = 6 + 2 * pl.getPlayerManager().getPrestige(damager);
				event.setDamage(0D);
				damager.sendMessage(damage + "");
				if(victim.getHealth() > damage) {
					victim.setHealth(victim.getHealth() - damage);
				} else {
					victim.setHealth(1);
					event.setDamage(200D);
				}
			}
		}
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player damager = (Player) event.getDamager();
			if(GodTokens.getSkill(damager.getName()).equalsIgnoreCase("wither")){
				if(GodTokens.canUse(damager.getName())){
					((Player)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, pl.getPlayerManager().getPrestige((Player)event.getEntity())));
					((Player)event.getEntity()).sendMessage(GodTokens.MESSAGE_HEADER + "You got Wither effect from " + ChatColor.RED + damager.getName());
					damager.sendMessage(GodTokens.MESSAGE_HEADER + "You've withered " + ChatColor.RED + ((Player)event.getEntity()).getName());
					GodTokens.addCooldown(damager.getName(), 20);
				}
			}
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if(event.getEntityType().equals(EntityType.FIREBALL)){
			Fireball fb = (Fireball) event.getEntity();
			if(fb.hasMetadata("god_token")){
				event.setCancelled(true);
				for(Entity e : event.getEntity().getNearbyEntities(5D, 5D, 5D)){
					if(e instanceof LivingEntity){
						((LivingEntity) e).damage(0, event.getEntity());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(GodTokens.getSkill(player.getName()) == null)
			return;
		if(!"Explosive".equals(GodTokens.getSkill(player.getName())))
			return;
		BlockFace playerDir = Convert.yawToFace(player.getLocation().getYaw());
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
	
	static Player getTargetPlayer(Player player) {
        return getTarget(player, player.getWorld().getPlayers());
    }
 
    static <T extends Entity> T getTarget(Entity entity, Iterable<T> entities) {
        T target = null;
        double threshold = 1;
        for (T other:entities) {
        	if(entity.getLocation().distance(other.getLocation()) <= 15){
	            Vector n = other.getLocation().toVector().subtract(entity.getLocation().toVector());
	            if (entity.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold && n.normalize().dot(entity.getLocation().getDirection().normalize()) >= 0) {
	                if (target == null || target.getLocation().distanceSquared(entity.getLocation()) > other.getLocation().distanceSquared(entity.getLocation()))
	                    target = other;
	            }
        	}
        }
		return target;
	}
    
    public int getRandom(int min, int max){
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
	
	public Main getPlugin() {
		return pl;
	}

}
