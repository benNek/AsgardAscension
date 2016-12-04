package main.java.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

/*
 * Util by BENAS "BENRUSH" NEKROÐIUS
 * Converts
 */

public class ItemStackGenerator {
	
	public static ItemStack createItem(Material material, int amount, int data, String name, List<String> lore){
		short dat = (short) data;
		ItemStack item;
		if(amount == 0 && dat == 0){
			item = new ItemStack(material);
		}else if(amount == 0 && dat != 0){
			item = new ItemStack(material, 1, dat);
		}else if(amount != 0 && dat == 0){
			item = new ItemStack(material, amount);
		}else{
			item = new ItemStack(material, amount, dat);
		}
		ItemMeta meta = item.getItemMeta();
		if(name != null){
			meta.setDisplayName(name);
		}
		if(lore != null){
			if(!lore.isEmpty()){
				meta.setLore(lore);
			}
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createItem(Material material, int amount, int data, String name, List<String> lore, boolean removeAtt){
		short dat = (short) data;
		ItemStack item;
		if(amount == 0 && dat == 0){
			item = new ItemStack(material);
		}else if(amount == 0 && dat != 0){
			item = new ItemStack(material, 1, dat);
		}else if(amount != 0 && dat == 0){
			item = new ItemStack(material, amount);
		}else{
			item = new ItemStack(material, amount, dat);
		}
		ItemMeta meta = item.getItemMeta();
		if(name != null){
			meta.setDisplayName(name);
		}
		if(lore != null){
			if(!lore.isEmpty()){
				meta.setLore(lore);
			}
		}
		item.setItemMeta(meta);
		if(removeAtt) item = removeAttributes(item);
		return item;
	}
	
	public static boolean isHelmet(ItemStack item) {
		if(item.getType().equals(Material.LEATHER_HELMET)) return true;
		else if(item.getType().equals(Material.IRON_HELMET)) return true;
		else if(item.getType().equals(Material.CHAINMAIL_HELMET)) return true;
		else if(item.getType().equals(Material.GOLD_HELMET)) return true;
		else if(item.getType().equals(Material.DIAMOND_HELMET)) return true;
		return false;
	}
	
	public static boolean isChestplate(ItemStack item) {
		if(item.getType().equals(Material.LEATHER_CHESTPLATE)) return true;
		else if(item.getType().equals(Material.IRON_CHESTPLATE)) return true;
		else if(item.getType().equals(Material.CHAINMAIL_CHESTPLATE)) return true;
		else if(item.getType().equals(Material.GOLD_CHESTPLATE)) return true;
		else if(item.getType().equals(Material.DIAMOND_CHESTPLATE)) return true;
		return false;
	}
	
	public static boolean isLeggings(ItemStack item) {
		if(item.getType().equals(Material.LEATHER_LEGGINGS)) return true;
		else if(item.getType().equals(Material.IRON_LEGGINGS)) return true;
		else if(item.getType().equals(Material.CHAINMAIL_LEGGINGS)) return true;
		else if(item.getType().equals(Material.GOLD_LEGGINGS)) return true;
		else if(item.getType().equals(Material.DIAMOND_LEGGINGS)) return true;
		return false;
	}
	
	public static boolean isBoots(ItemStack item) {
		if(item.getType().equals(Material.LEATHER_BOOTS)) return true;
		else if(item.getType().equals(Material.IRON_BOOTS)) return true;
		else if(item.getType().equals(Material.CHAINMAIL_BOOTS)) return true;
		else if(item.getType().equals(Material.GOLD_BOOTS)) return true;
		else if(item.getType().equals(Material.DIAMOND_BOOTS)) return true;
		return false;
	}
	
	public static boolean isTool(ItemStack item) {
		// AXE
		if(item.getType().equals(Material.WOOD_AXE)) return true;
		if(item.getType().equals(Material.STONE_AXE)) return true;
		if(item.getType().equals(Material.IRON_AXE)) return true;
		if(item.getType().equals(Material.GOLD_AXE)) return true;
		if(item.getType().equals(Material.DIAMOND_AXE)) return true;
		
		// PICKAXE
		if(item.getType().equals(Material.WOOD_PICKAXE)) return true;
		if(item.getType().equals(Material.STONE_PICKAXE)) return true;
		if(item.getType().equals(Material.IRON_PICKAXE)) return true;
		if(item.getType().equals(Material.GOLD_PICKAXE)) return true;
		if(item.getType().equals(Material.DIAMOND_PICKAXE)) return true;
		
		// SPADE
		if(item.getType().equals(Material.WOOD_SPADE)) return true;
		if(item.getType().equals(Material.STONE_SPADE)) return true;
		if(item.getType().equals(Material.IRON_SPADE)) return true;
		if(item.getType().equals(Material.GOLD_SPADE)) return true;
		if(item.getType().equals(Material.DIAMOND_SPADE)) return true;
		
		// HOE
		if(item.getType().equals(Material.WOOD_HOE)) return true;
		if(item.getType().equals(Material.STONE_HOE)) return true;
		if(item.getType().equals(Material.IRON_HOE)) return true;
		if(item.getType().equals(Material.GOLD_HOE)) return true;
		if(item.getType().equals(Material.DIAMOND_HOE)) return true;
		
		return false;
	}
	
	private static ItemStack removeAttributes(ItemStack item) {
	    if (!MinecraftReflection.isCraftItemStack(item)) {
	        item = MinecraftReflection.getBukkitItemStack(item);
	    }
	    try {
	    	NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(item);
	    	compound.put(NbtFactory.ofList("AttributeModifiers"));
	    } catch (IllegalArgumentException e) {
	    	return item;
	    }
	    return item;
	}
	
}
