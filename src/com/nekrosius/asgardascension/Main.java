package com.nekrosius.asgardascension;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.nekrosius.asgardascension.challenges.Challenge;
import com.nekrosius.asgardascension.commands.AsgardAscensionCommand;
import com.nekrosius.asgardascension.commands.ChallengesExecutor;
import com.nekrosius.asgardascension.commands.FOGCommand;
import com.nekrosius.asgardascension.commands.PrestigeCommand;
import com.nekrosius.asgardascension.commands.RagnorakCommand;
import com.nekrosius.asgardascension.commands.RankCommand;
import com.nekrosius.asgardascension.commands.RankUpCommand;
import com.nekrosius.asgardascension.commands.TokenCommand;
import com.nekrosius.asgardascension.commands.TribeCommand;
import com.nekrosius.asgardascension.files.ChallengesFile;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.files.GodFoodFile;
import com.nekrosius.asgardascension.files.PlayerFile;
import com.nekrosius.asgardascension.files.RagnorakFile;
import com.nekrosius.asgardascension.files.TribeFile;
import com.nekrosius.asgardascension.handlers.GodTokens;
import com.nekrosius.asgardascension.managers.ListenerManager;
import com.nekrosius.asgardascension.managers.PlayerManager;
import com.nekrosius.asgardascension.managers.TribeManager;
import com.nekrosius.asgardascension.objects.Tribe;
import com.nekrosius.asgardascension.utils.Logger;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import me.clip.deluxechat.placeholders.DeluxePlaceholderHook;
import me.clip.deluxechat.placeholders.PlaceholderHandler;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin{
	
	public static String mh = ChatColor.GRAY + "[" + ChatColor.RED + "Asgard" + ChatColor.GRAY + "] ";
	public static WorldGuardPlugin wg;
	
	private ListenerManager lm;
	private TribeManager tm;
	private PlayerManager pm;
	
	public static EffectManager em;
	
	public static Economy econ = null;
	public static Chat chat = null;
	
	private Logger logger;
	private PlayerFile pf;
	private ChallengesFile cf;
	
	private Challenge challenges;
	
	public void onEnable(){
        setupChat();
		logger = new Logger(this);
        setupManagers();
        setupClasses();
        setupFiles();
		setupCommands();
        if (!setupEconomy()) {
        	getLogs().log("Vault or Economy plugin hasn't been found! Plugin is shutting down!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        enablePlaceholders();
		GodTokens.setupTokens();
		for(Player p : Bukkit.getOnlinePlayers()) {
			getPlayerManager().loadData(p);
		}
		
		getLogs().log("AA initialized succesfully!");
	}
	
	private void setupManagers() {
		EffectLib lib = EffectLib.instance();
		lm = new ListenerManager(this);
		tm = new TribeManager(this);
		pm = new PlayerManager(this);
		em = new EffectManager(lib);
		wg = getWorldGuard();
	}
	
	private void setupClasses() {
		challenges = new Challenge(this);
	}
	
	public void setupFiles() {
		pf = new PlayerFile(this);
		cf = new ChallengesFile(this);
		
		new ConfigFile(this);
		new GodFoodFile(this);
		new RagnorakFile(this);
	}
	
	private void setupCommands() {
		getCommand("asgardascension").setExecutor(new AsgardAscensionCommand(this));
		getCommand("rankup").setExecutor(new RankUpCommand(this));
		getCommand("prestige").setExecutor(new PrestigeCommand(this));
		getCommand("tribe").setExecutor(new TribeCommand(this));
		getCommand("token").setExecutor(new TokenCommand(this));
		getCommand("ragnorak").setExecutor(new RagnorakCommand(this));
		getCommand("rank").setExecutor(new RankCommand(this));
		getCommand("fog").setExecutor(new FOGCommand(this));
		getCommand("challenge").setExecutor(new ChallengesExecutor(this));
	}
	
	public void onDisable() {
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(getChallenges().getChallenge(p) != 0) {
				getChallenges().quitChallenge(p);
			}
			getPlayerManager().saveData(p);
		}
		for(Tribe t : TribeManager.getTribes()) {
			TribeFile.createConfig(t.getName());
			TribeFile.setChest(t.getContent());
			TribeFile.setAllies(t.getAllies());
			TribeFile.setBalance(t.getBalance());
			TribeFile.setDescription(t.getDescription());
			TribeFile.setEnemies(t.getEnemies());
			TribeFile.setLeader(t.getLeader());
			TribeFile.setLevel(t.getLevel());
			TribeFile.setMembers(t.getMembers());
			TribeFile.setName(t.getName());
			TribeFile.setType(t.getType());
		}
		PlaceholderHandler.unregisterPlaceholderHook(this);
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }
	
	private void enablePlaceholders() {
		// Check if the MVdWPlaceholderAPI plugin is present
		if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
			// The plugin is enabled
			PlaceholderAPI.registerPlaceholder(this, "tokens",
					new PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(
								PlaceholderReplaceEvent event) {
							Player player = event.getPlayer();
							return ChatColor.GRAY + "" + getPlayerManager().getTokens(player);
						}

			});
			PlaceholderAPI.registerPlaceholder(this, "rankup_progress",
					new PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(
								PlaceholderReplaceEvent event) {
							Player player = event.getPlayer();
							double percentage = ((double)econ.getBalance(player) /
									(getChallengesFile().getPrice(getPlayerManager().getRank(player) + 1) * (getPlayerManager().getPrestige(player) + 1))) * 100;
							if(percentage > 100) percentage = 100;
							return ChatColor.GRAY + "" + String.format("%.1f", percentage) + "%";
						}

			});
		}
		else getLogs().log("MVdWPlaceholderAPI is missing! Custom placeholders won't work!");
		if (Bukkit.getPluginManager().isPluginEnabled("DeluxeChat")) {
			PlaceholderHandler.registerPlaceholderHook(this, new DeluxePlaceholderHook() {
				@Override
				public String onPlaceholderRequest(Player p, String identifier) {
					if (identifier.equals("prestige")) {
						return  "&6P" + getPlayerManager().getPrestige(p);
					}
					else if(identifier.equals("tokens")) {
						return "&6P" + getPlayerManager().getTokens(p);
					}
					return null;
				}
			});
		}
		else getLogs().log("DeluxeChat is missing! Chat placeholders won't work!");
	}
	
	public static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}
		return (WorldGuardPlugin) plugin;
	}
	
	public ListenerManager getListenerManager() {
		return lm;
	}
	
	public TribeManager getTribeManager() {
		return tm;
	}
	
	public PlayerManager getPlayerManager() {
		return pm;
	}
	
	public PlayerFile getPlayerFile() {
		return pf;
	}
	
	public Logger getLogs() {
		return logger;
	}
	
	public Challenge getChallenges() {
		return challenges;
	}
	
	public ChallengesFile getChallengesFile() {
		return cf;
	}
	
	public static boolean isPVPEnabled(Player player) {
		if(Main.wg.getRegionManager(player.getWorld()) == null) return true;
		RegionManager regionManager = Main.wg.getRegionManager(player.getWorld());
		ApplicableRegionSet arset = regionManager.getApplicableRegions(player.getLocation());
		ProtectedRegion region = regionManager.getRegion("__global__");
		int priority = -10000;
		for(ProtectedRegion r : arset.getRegions()) { 
			if(r.getPriority() > priority) {
				region = r;
				priority = r.getPriority();
			}
		}
		if(region == null) {
			if(regionManager.getRegion("__global__") == null) return false;
			return regionManager.getRegion("__global__").getFlag(DefaultFlag.PVP).toString().equals("ALLOW");
		}
		if(region.getFlag(DefaultFlag.PVP) == null) return true;
		return region.getFlag(DefaultFlag.PVP).toString().equalsIgnoreCase("ALLOW");
	}
	
}
