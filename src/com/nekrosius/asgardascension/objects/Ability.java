package com.nekrosius.asgardascension.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.nekrosius.asgardascension.enums.ItemType;

public class Ability {
	
	private String name;
	private Material icon;
	private List<String> description;
	private List<ItemType> items;
	private int temporaryPrice;
	private int permanentPrice;
	
	public Ability(String name, Material icon, int temporaryPrice, int permanentPrice) {
		super();
		this.name = name;
		this.icon = icon;
		this.temporaryPrice = temporaryPrice;
		this.permanentPrice = permanentPrice;
		this.description = new ArrayList<>();
		this.items = new ArrayList<>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the icon
	 */
	public Material getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(Material icon) {
		this.icon = icon;
	}

	/**
	 * @return the description
	 */
	public List<String> getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(List<String> description) {
		this.description = description;
	}
	
	/**
	 * @param line the line to add to the description
	 */
	public void addDescription(String line) {
		description.add(line);
	}

	/**
	 * @return the items
	 */
	public List<ItemType> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<ItemType> items) {
		this.items = items;
	}
	
	/**
	 * @param material the material to add to the items list
	 */
	public void addItem(ItemType type) {
		items.add(type);
	}

	/**
	 * @return the temporaryPrice
	 */
	public int getTemporaryPrice() {
		return temporaryPrice;
	}

	/**
	 * @param temporaryPrice the temporaryPrice to set
	 */
	public void setTemporaryPrice(int temporaryPrice) {
		this.temporaryPrice = temporaryPrice;
	}

	/**
	 * @return the permanentPrice
	 */
	public int getPermanentPrice() {
		return permanentPrice;
	}

	/**
	 * @param permanentPrice the permanentPrice to set
	 */
	public void setPermanentPrice(int permanentPrice) {
		this.permanentPrice = permanentPrice;
	}

}