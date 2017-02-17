package com.nekrosius.asgardascension.managers;

import java.util.ArrayList;
import java.util.List;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.objects.Rune;

public class RuneManager {
	
	List<Rune> runes;
	
	Main plugin;
	public RuneManager(Main plugin) {
		this.plugin = plugin;
		runes = new ArrayList<>();
		registerRunes();
	}
	
	private void registerRunes() {

	}
	
	
	

}
