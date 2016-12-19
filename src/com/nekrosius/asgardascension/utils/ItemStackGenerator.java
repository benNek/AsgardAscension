package com.nekrosius.asgardascension.utils;

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
	
	private ItemStackGenerator() {

	}
	
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
		if(lore != null && !lore.isEmpty()){
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createItem(Material material, int amount, int data, String name, List<String> lore, boolean removeAtt){
		short dat = (short) data;
		ItemStack item;
		if(amount == 0 && dat == 0) {
			item = new ItemStack(material);
		}
		else if(amount == 0 && dat != 0) {
			item = new ItemStack(material, 1, dat);
		}
		else if(amount != 0 && dat == 0) {
			item = new ItemStack(material, amount);
		}
		else {
			item = new ItemStack(material, amount, dat);
		}
		ItemMeta meta = item.getItemMeta();
		if(name != null) {
			meta.setDisplayName(name);
		}
		if(lore != null && !lore.isEmpty()) {
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		if(removeAtt)
			item = removeAttributes(item);
		return item;
	}
	
	public static ItemStack createItem(ItemStack item, String name, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		if(name != null){
			meta.setDisplayName(name);
		}
		if(lore != null && !lore.isEmpty()){
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public static boolean isHelmet(ItemStack item) {
		switch(item.getType()) {
			case LEATHER_HELMET:
			case IRON_HELMET:
			case CHAINMAIL_HELMET:
			case GOLD_HELMET:
			case DIAMOND_HELMET:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isChestplate(ItemStack item) {
		switch(item.getType()) {
			case LEATHER_CHESTPLATE:
			case IRON_CHESTPLATE:
			case CHAINMAIL_CHESTPLATE:
			case GOLD_CHESTPLATE:
			case DIAMOND_CHESTPLATE:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isLeggings(ItemStack item) {
		switch(item.getType()) {
			case LEATHER_LEGGINGS:
			case IRON_LEGGINGS:
			case CHAINMAIL_LEGGINGS:
			case GOLD_LEGGINGS:
			case DIAMOND_LEGGINGS:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isBoots(ItemStack item) {
		switch(item.getType()) {
			case LEATHER_BOOTS:
			case IRON_BOOTS:
			case CHAINMAIL_BOOTS:
			case GOLD_BOOTS:
			case DIAMOND_BOOTS:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isTool(ItemStack item) {
		boolean isTool = isAxe(item) || isPickaxe(item) || isSpade(item) || isHoe(item);
		return isTool || isSword(item);
	}
	
	public static boolean isAxe(ItemStack item) {
		switch(item.getType()) {
			case WOOD_AXE:
			case STONE_AXE:
			case IRON_AXE:
			case GOLD_AXE:
			case DIAMOND_AXE:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isPickaxe(ItemStack item) {
		switch(item.getType()) {
			case WOOD_PICKAXE:
			case STONE_PICKAXE:
			case IRON_PICKAXE:
			case GOLD_PICKAXE:
			case DIAMOND_PICKAXE:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isSpade(ItemStack item) {
		switch(item.getType()) {
			case WOOD_SPADE:
			case STONE_SPADE:
			case IRON_SPADE:
			case GOLD_SPADE:
			case DIAMOND_SPADE:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isHoe(ItemStack item) {
		switch(item.getType()) {
			case WOOD_HOE:
			case STONE_HOE:
			case IRON_HOE:
			case GOLD_HOE:
			case DIAMOND_HOE:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isSword(ItemStack item) {
		switch(item.getType()) {
			case WOOD_SWORD:
			case STONE_SWORD:
			case IRON_SWORD:
			case GOLD_SWORD:
			case DIAMOND_SWORD:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isRepairable(ItemStack item) {
		if(item.getType().equals(Material.AIR))
			return false;
		
		boolean isArmourPart = isHelmet(item) || isChestplate(item) || isLeggings(item) || isBoots(item);
		return isTool(item) || isArmourPart;
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
