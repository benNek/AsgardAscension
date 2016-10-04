package com.nekrosius.asgardascension.objects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import com.nekrosius.asgardascension.enums.TokenType;

public class GodToken {

	private String name;
	private Material icon;
	private TokenType type;
	private List<String> description;
	private int cooldown;
	private int tempPrice;
	private int permPrice;
	private int duration;
	
	public GodToken(String name) {
		this.name = name;
		setIcon(Material.APPLE);
		setType(TokenType.EXTRA);
		setDescription(Arrays.asList("God token is not customized!"));
		setCooldown(10);
		setTempPrice(1);
		setPermPrice(10);
		setDuration(-1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Material getIcon() {
		return icon;
	}

	public void setIcon(Material icon) {
		this.icon = icon;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public List<String> getDescription() {
		return description;
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}
	
	public void addDescription(String str) {
		description.add(str);
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public int getTempPrice() {
		return tempPrice;
	}

	public void setTempPrice(int tempPrice) {
		this.tempPrice = tempPrice;
	}

	public int getPermPrice() {
		return permPrice;
	}

	public void setPermPrice(int permPrice) {
		this.permPrice = permPrice;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
}
