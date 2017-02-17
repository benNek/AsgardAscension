package com.nekrosius.asgardascension.enums;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
 
/**
* An enum for requesting strings from the language file.
* @author gomeow
*/
public enum Lang {
	
	// %d - number
	// %s - string
	
	HEADERS_MAIN("headers.main", "&7[&cAsgard&7]"),
	HEADERS_CHALLENGES("headers.challenges", "&7[&cChallenges&7]"),
	HEADERS_FOG("headers.fog", "&7[&cFoG&7]"),
	HEADERS_TOKENS("headers.tokens", "&7[&cTokens&7]"),
	HEADERS_RAGNORAK("headers.ragnorak", "&7[&cRagnorak&7]"),
	HEADERS_TRIBES("headers.tribes", "&7[&cTribes&7]"),
	
	COMMANDS_ONLY_PLAYER("commands.only_player", "&7Only players can use this command!"),
	COMMANDS_NO_PERMISSION("commands.no_permission", "&7You do not have permission to use this command!"),
	COMMANDS_UNKNOWN_COMMAND("commands.unknown_command", "&7Unknown command! Check your syntax!"),
	COMMANDS_PLAYER_NOT_FOUND("commands.unknown_target", "&7Player not found! Check your syntax!"),
	
	CHALLENGES_NOT_ENOUGH_MONEY("challenges.not_enough_money", "&7You don't have enough money! It costs %d"),
	CHALLENGES_START("challenges.start", "&7You've started &c%s &7challenge! (%d)"),
	CHALLENGES_COMPLETE("challenges.complete", "&7You have completed the challenge! Your Rank now is &c%s&7!"),
	CHALLENGES_TEST_COMPLETE("challenges.test_complete", "&7Test completed. If you see this message challenge works fine!"),
	CHALLENGES_LEAVE("challenges.leave", "&7You've left &c%s &7Challenge! Your money was refunded!"),
	
	PRESTIGE_SET_NOT_NUMBER("prestige.set.not_number", "&7Please type a number only (like 6)!"),
	PRESTIGE_SET_NOT_IN_RANGE("prestige.set.not_in_range", "&7Prestige should be between 0 and %d!"),
	PRESTIGE_SET_FOR_SENDER("prestige.set.for_sender", "&7You've set &c%s &7prestige to &c%d&7!"),
	PRESTIGE_SET_FOR_TARGET("prestige.set.for_target", "&7Your prestige has been set to &c%d&7!"),
	PRESTIGE_SET_LOCATION("prestige.set.location", "&7You've successfully added prestige teleport location!"),
	PRESTIGE_RANK_INSUFFICIENT("prestige.rank_insufficient", "&7Your rank is insufficient to prestige! You need to reach Rank &c%s &7before prestiging!"),
	PRESTIGE_REACHED_MAX("prestige.reached_max", "&7You've reached the last prestige level!"),
	PRESTIGE_READY("prestige.ready", "&7You're ready to ascend! Type &c/prestige confirm &7to ascend!"),
	PRESTIGE_ASCENDED("prestige.ascended", "&7You've successfully ascended! Your Prestige now is &c%d&7!"),
	PRESTIGE_TOKEN_REWARD("prestige.token_reward", "&7As a reward for hard work you got &c%d &7God Tokens!"),
	
	TOKENS_ALREADY_ACTIVE("tokens.runes.already-active", "&7You already have active rune! Wait befores it expires to use another!"),
	TOKENS_APPLY_RUNE("tokens.runes.apply", "&7You've successfully used rune &c%s!"),
	TOKENS_COOLDOWN("tokens.runes.cooldown", "&7You can use another rune in %d!"),
	TOKENS_MAGNET("tokens.abilities.magnet", "&cMagnet &7ability has successfully activated!"),
	TOKENS_POISONED("tokens.abilities.poisoned", "&7You have been poisoned by &c%s&7!"),
	TOKENS_FLAMED("tokens.abilities.flamed", "&7You have been flamed by &c%s&7!"),
	TOKENS_SHOP_NOT_ENOUGH("tokens.shop.not_enough", "&7You don't have enough GT! It costs &c%d GT"),
	TOKENS_SHOP_NOT_SUPPORTED("tokens.shop.not_supported", "&7Your current item in hand is &cnot supported&7!"),
	TOKENS_SHOP_ALREADY_APPLIED("tokens.shop.already_applied", "&7Your current item in hand already has an active ability!"),
	TOKENS_SHOP_APPLY("tokens.shop.apply", "&7You applied &c%s &7ability for your current item!"),
	TOKENS_SHOP_EXPIRED("tokens.expired", "&c%s &7has ran out!");
 
    private String path;
    private String def;
    private static YamlConfiguration LANG;
 
    /**
    * Lang enum constructor.
    * @param path The string path.
    * @param start The default string.
    */
    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }
 
    /**
    * Set the {@code YamlConfiguration} to use.
    * @param config The config to set.
    */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }
 
    @Override
    public String toString() {
        if (this == HEADERS_MAIN || this == HEADERS_RAGNORAK
        		|| this == Lang.HEADERS_TOKENS || this == Lang.HEADERS_TRIBES
        		|| this == Lang.HEADERS_FOG || this == Lang.HEADERS_CHALLENGES)
            return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def)) + " ";
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }
 
    /**
    * Get the default value of the path.
    * @return The default value of the path.
    */
    public String getDefault() {
        return this.def;
    }
 
    /**
    * Get the path to the string.
    * @return The path to the string.
    */
    public String getPath() {
        return this.path;
    }
}