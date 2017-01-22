package com.nekrosius.asgardascension.enums;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
 
/**
* An enum for requesting strings from the language file.
* @author gomeow
*/
public enum Lang {
	
	HEADERS_MAIN("headers.main", "&7[&cAsgard&7]"),
	HEADERS_CHALLENGES("headers.challenges", "&7[&cChallenges&7]"),
	HEADERS_FOG("headers.fog", "&7[&cFoG&7]"),
	HEADERS_TOKENS("headers.tokens", "&7[&cTokens&7]"),
	HEADERS_RAGNORAK("headers.ragnorak", "&7[&cRagnorak&7]"),
	HEADERS_TRIBES("headers.tribes", "&7[&cTribes&7]"),
	
	CHALLENGES_NOT_ENOUGH_MONEY("challenges.not_enough_money", "&7You don't have enough money! It costs %p"),
	CHALLENGES_START("challenges.start", "&7You've started &c%t &7challenge! (%p)"),
	CHALLENGES_COMPLETE("challenges.complete", "&7You have completed the challenge! Your Rank now is &c%t&7!"),
	CHALLENGES_TEST_COMPLETE("challenges.test_complete", "&7Test completed. If you see this message challenge works fine!"),
	CHALLENGES_LEAVE("challenges.leave", "&7You've left &c%t &7Challenge! Your money was refunded!");
 
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