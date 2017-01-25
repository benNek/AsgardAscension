package com.nekrosius.asgardascension.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.nekrosius.asgardascension.Main;
import com.nekrosius.asgardascension.handlers.FoodSetup;

public class GodFoodFile {

	static File file;
	public static FileConfiguration config;

	private Main plugin;
	public GodFoodFile(Main plugin) {
		this.plugin = plugin;
		createConfig();
	}
	
	public void createConfig() {
		(new File("plugins" + File.separator + "AsgardAscension" + File.separator
				+ "")).mkdirs();
		file = new File("plugins" + File.separator + "AsgardAscension",
				"food.yml");
		config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()){
			config.options().header("Potion Effect Types: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html");
			config.addDefault("1.name", "Nidhogg’s Heart");
			config.addDefault("1.item", 372);
			config.addDefault("1.amount", 1);
			List<String> effects = new ArrayList<String>();
			effects.add("SPEED, 120, 2");
			config.addDefault("1.effects", effects);
			config.options().copyDefaults(true);
		}
		saveConfig();
	}
	
	public static void saveConfig()
	{
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getEffectAmount() {
		return config.getKeys(false).size();
	}
	
	public static void remove(int food) {
		config.set(String.valueOf(food), null);
		saveConfig();
	}
	
	public static void removeEffects(int food) {
		List<String> eff = new ArrayList<String>();
		config.set(String.valueOf(food) + ".effects", eff);
		saveConfig();
	}
	
	public static void setName(int food, String name) {
		config.set(String.valueOf(food) + ".name", name);
		saveConfig();
	}
	
	public static void setItemId(int food, int id) {
		config.set(String.valueOf(food) + ".item", id);
		saveConfig();
	}
	
	public static void setAmount(int food, int amount) {
		config.set(String.valueOf(food) + ".amount", amount);
		saveConfig();
	}
	
	public static void setData(int food, int data) {
		config.set(String.valueOf(food) + ".data", data);
		saveConfig();
	}
	
	public static void addEffect(Player player, int food) {
		List<String> effects = config.getStringList(String.valueOf(food) + ".effects");
		effects.add(FoodSetup.getType(player) + ", " + FoodSetup.getDuration(player) + ", " + FoodSetup.getAmplifier(player));
		FoodSetup.finishEffect(player);
		config.set(String.valueOf(food) + ".effects", effects);
		saveConfig();
	}
	
	@SuppressWarnings("deprecation")
	public static Material getFoodType(int food) {
		return Material.getMaterial(config.getInt(String.valueOf(food) + ".item"));
	}
	
	public static List<Integer> getFoodId(int material) {
		List<Integer> ids = new ArrayList<Integer>();
		for(String str : config.getKeys(false)) {
			if(config.getInt(String.valueOf(str) + ".item") == material){
				ids.add(Integer.parseInt(str));
			}
		}
		return ids;
	}
	
	public static int getAmount(int food) {
		return config.getInt(String.valueOf(food) + ".amount");
	}
	
	public static List<String> GetEffects(int food) {
		return config.getStringList(String.valueOf(food) + ".effects");
	}
	
	public static List<PotionEffect> getPotionEffects(int food) {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		String[] e;
		PotionEffectType eft;
		int duration;
		int amplifier;
		for(String eff : config.getStringList(String.valueOf(food) + ".effects")){
			e = eff.split(", ");
			eft = PotionEffectType.getByName(e[0]);
			duration = Integer.parseInt(e[1]) * 20;
			amplifier = Integer.parseInt(e[2]) - 1;
			effects.add(new PotionEffect(eft, duration, amplifier));
		}
		return effects;
	}
	
	public static String getName(int food) {
		return config.getString(String.valueOf(food) + ".name");
	}
	
	public static int getData(int food) {
		return config.getInt(String.valueOf(food) + ".data");
	}
	
	public Main getPlugin() {
		return plugin;
	}
}
