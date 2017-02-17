package com.nekrosius.asgardascension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.intellectualcrafters.plot.api.PlotAPI;
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
import com.nekrosius.asgardascension.commands.WarpsExecutor;
import com.nekrosius.asgardascension.enums.Lang;
import com.nekrosius.asgardascension.files.ChallengesFile;
import com.nekrosius.asgardascension.files.ConfigFile;
import com.nekrosius.asgardascension.files.GodFoodFile;
import com.nekrosius.asgardascension.files.MessagesFile;
import com.nekrosius.asgardascension.files.PlayerFile;
import com.nekrosius.asgardascension.files.RagnorakFile;
import com.nekrosius.asgardascension.files.TribeFile;
import com.nekrosius.asgardascension.files.WarpsFile;
import com.nekrosius.asgardascension.handlers.Ragnorak;
import com.nekrosius.asgardascension.managers.AbilityManager;
import com.nekrosius.asgardascension.managers.ListenerManager;
import com.nekrosius.asgardascension.managers.PlayerManager;
import com.nekrosius.asgardascension.managers.RuneManager;
import com.nekrosius.asgardascension.managers.TribeManager;
import com.nekrosius.asgardascension.objects.Tribe;
import com.nekrosius.asgardascension.utils.Logger;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

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
	
	// Managers
	
	private ListenerManager listenerManager;
	private TribeManager tribeManager;
	private PlayerManager playerManager;
	private AbilityManager abilityManager;
	private RuneManager runeManager;
	private EffectManager effectManager;
	
	// Files
	
	private PlayerFile playerFile;
	private ChallengesFile challengesFile;
	
	// Libraries
	
	private static WorldGuardPlugin worldGuard;
	private static PlotAPI plots;
	private Economy economy;
	private Chat chat;
	
	// Other
	
	private YamlConfiguration LANG;
	private File LANG_FILE;
	private Logger logger;
	private Ragnorak ragnorak;
	private Challenge challenges;
	
	public void onEnable(){
		setupChat();
		logger = new Logger(this);
		setupFiles();
		setupManagers();
		setupClasses();
		setupCommands();
		if (!setupEconomy()) {
			getLogs().log("Vault or Economy plugin hasn't been found! Plugin is shutting down!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		enablePlaceholders();
		
		// Loading players' data
		for(Player p : Bukkit.getOnlinePlayers()) {
			getPlayerManager().loadData(p);
			getAbilityManager().startTimer(p);
		}
		
		
		getLogs().log("AA initialized succesfully!");
	}
	
	public void onDisable() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			// Quitting challenges for those who are doing it
			if(getChallenges().getChallenge(p) != 0) {
				getChallenges().quitChallenge(p);
			}
			// Compensating temporary tokens
			getAbilityManager().compensateTokens(p, true);
			
			// Saving player's data to a personal file
			getPlayerManager().saveData(p);
			
			
			// Showing invisible players
			if(getRuneManager().hasActiveRune(p)) {
				getRuneManager().finish(p, false);
			}
		}
				
		// Saving tribes data
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
		
		// Finishing the ragnorak
		if(getRagnorak().eventStarted) {
			getRagnorak().finishEvent();
		}
		
		// Unregistering libraries and hooks
		PlaceholderHandler.unregisterPlaceholderHook(this);
	}
	
	private void setupManagers() {
		EffectLib lib = EffectLib.instance();
		listenerManager = new ListenerManager(this);
		tribeManager = new TribeManager(this);
		playerManager = new PlayerManager(this);
		abilityManager = new AbilityManager(this);
		runeManager = new RuneManager(this);
		effectManager = new EffectManager(lib);
		worldGuard = registerWorldGuard();
		plots = new PlotAPI();
	}
	
	public void loadLang() {
		File lang = new File(getDataFolder(), "lang.yml");
		if (!lang.exists()) {
			try {
				getDataFolder().mkdir();
				lang.createNewFile();
				InputStream defConfigStream = this.getResource("lang.yml");
				if (defConfigStream != null) {
					@SuppressWarnings("deprecation")
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
					defConfig.save(lang);
					Lang.setFile(defConfig);
					//return defConfig;
				}
			} catch(IOException e) {
				e.printStackTrace(); // So they notice
				logger.log("Couldn't create language file.");
				logger.log("This is a fatal error. Now disabling");
				this.setEnabled(false);
			}
		}
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		for(Lang item : Lang.values()) {
			if (conf.getString(item.getPath()) == null) {
				conf.set(item.getPath(), item.getDefault());
			}
		}
		Lang.setFile(conf);
		LANG = conf;
		LANG_FILE = lang;
		try {
			conf.save(getLangFile());
		} catch(IOException e) {
			logger.log("Failed to save lang.yml.");
			logger.log("Report this stacktrace.");
			e.printStackTrace();
		}
	}
	
	/**
	* Gets the lang.yml config.
	* @return The lang.yml config.
	*/
	public YamlConfiguration getLang() {
		return LANG;
	}
	 
	/**
	* Get the lang.yml file.
	* @return The lang.yml file.
	*/
	public File getLangFile() {
		return LANG_FILE;
	}
	
	private void setupClasses() {
		challenges = new Challenge(this);
		ragnorak = new Ragnorak(this);
	}
	
	public void setupFiles() {
		playerFile = new PlayerFile(this);
		challengesFile = new ChallengesFile(this);
		loadLang();
		new ConfigFile(this);
		new GodFoodFile(this);
		new RagnorakFile(this);
		new WarpsFile(this);
		// TODO remove this
		new MessagesFile(this);
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
		getCommand("tokenwarp").setExecutor(new WarpsExecutor(this));
	}
	
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}
	
	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return chat != null;
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
				}
			);
			PlaceholderAPI.registerPlaceholder(this, "rankup_progress",
				new PlaceholderReplacer() {
					@Override
					public String onPlaceholderReplace(
							PlaceholderReplaceEvent event) {
						if(economy == null)
							return ChatColor.GRAY + "0%";
						Player player = event.getPlayer();
						double percentage = ((double)economy.getBalance(player) /
								(getChallengesFile().getPrice(getPlayerManager().getRank(player) + 1) * (getPlayerManager().getPrestige(player) + 1))) * 100;
						if(percentage > 100)
							percentage = 100;
						return ChatColor.GRAY + "" + String.format("%.1f", percentage) + "%";
					}
				}
			);
		}
		else {
			getLogs().log("MVdWPlaceholderAPI is missing! Custom placeholders won't work!");
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("DeluxeChat")) {
			PlaceholderHandler.registerPlaceholderHook(this, new DeluxePlaceholderHook() {
				@Override
				public String onPlaceholderRequest(Player p, String identifier) {
					if ("prestige".equals(identifier)) {
						return  "&6P" + getPlayerManager().getPrestige(p);
					}
					else if("tokens".equals(identifier)) {
						return "&6P" + getPlayerManager().getTokens(p);
					}
					return null;
				}
			});
		}
		else getLogs().log("DeluxeChat is missing! Chat placeholders won't work!");
	}
	
	public static WorldGuardPlugin registerWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}
		return (WorldGuardPlugin) plugin;
	}
	
	public Logger getLogs() {
		return logger;
	}
	
	public Challenge getChallenges() {
		return challenges;
	}
	
	public Ragnorak getRagnorak() {
		return ragnorak;
	}
	
	// Managers
	
	public ListenerManager getListenerManager() {
		return listenerManager;
	}
	
	public TribeManager getTribeManager() {
		return tribeManager;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public AbilityManager getAbilityManager() {
		return abilityManager;
	}
	
	public RuneManager getRuneManager() {
		return runeManager;
	}
	
	public EffectManager getEffectManager() {
		return effectManager;
	}
	
	// Files
	
	public PlayerFile getPlayerFile() {
		return playerFile;
	}
	
	public ChallengesFile getChallengesFile() {
		return challengesFile;
	}
	
	// Libraries
	
	public WorldGuardPlugin getWorldGuard() {
		return worldGuard;
	}
	
	public PlotAPI getPlotsAPI() {
		return plots;
	}
	
	public Economy getEconomy() {
		return economy;
	}
	
}
