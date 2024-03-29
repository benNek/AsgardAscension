package com.nekrosius.asgardascension.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class Rune {
	
	private String name;
	private Material icon;
	private List<String> description;
	private int price;
	private int duration;
	
	public Rune(String name, Material icon, int price, int duration) {
		super();
		this.name = name;
		this.icon = icon;
		this.price = price;
		this.duration = duration;
		this.description = new ArrayList<>();
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}