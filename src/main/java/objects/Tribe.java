package main.java.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.java.files.TribeFile;
import main.java.managers.TribeManager;

public class Tribe {
	
	private String name;
	private String type;
	private int level;
	private double balance;
	private String leader;
	private String description;
	private List<String> members;
	private List<String> allies;
	private List<String> enemies;
	private List<String> invites;
	private List<String> allyRequests;
	private List<String> neutralRequests;
	private Inventory inv;
	private boolean opened = false;
	
	public Tribe(String name) {
		setName(name);
		setBalance(0);
		setLevel(1);
		members = new ArrayList<String>();
		invites = new ArrayList<String>();
		setAllies(new ArrayList<String>());
		setEnemies(new ArrayList<String>());
		setAllyRequests(new ArrayList<String>());
		setNeutralRequests(new ArrayList<String>());
		setDescription("");
		inv = Bukkit.createInventory(null, 54, ChatColor.BOLD + "Communal Chest");
	}
	
	public void delete() {
		if(getMembers().size() > 0){
			for(String m : getMembers()){
				TribeManager.removePlayerTribe(m);
			}
		}
		if(getAllies().size() > 0){
			for(String st : getAllies()){
				TribeManager.getTribe(st).removeAlly(getName());
			}
		}
		if(getEnemies().size() > 0){
			for(String st : getAllies()){
				TribeManager.getTribe(st).removeEnemy(getName());
			}
		}
		TribeFile.createConfig(name);
		TribeFile.delete(name);
		TribeManager.removeTribe(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getLeaderUUID() {
		return leader;
	}
	
	public String getLeader() {
		try {
			return Bukkit.getOfflinePlayer(UUID.fromString(getLeaderUUID())).getName();
		}
		catch (IllegalArgumentException e) {
			return getLeaderUUID();
		}
	}

	public void setLeader(String player) {
		this.leader = player;
	}
	
	public List<String> getMembers() {
		return members;
	}
	
	public void setMembers(List<String> members) {
		this.members = members;
		for(String member : members) {
			String name;
			try{
				name = Bukkit.getOfflinePlayer(UUID.fromString(member)).getName();
			}
			catch (IllegalArgumentException e) {
				name = member;
			}
			TribeManager.setPlayerTribe(name, this);
		}
	}
	
	public void addMember(String player) {
		members.add(player);
		TribeManager.setPlayerTribe(player, this);
	}
	
	public void removeMember(String player) {
		members.remove(player);
		TribeManager.removePlayerTribe(player);
	}
	
	public void invitePlayer(String player) {
		invites.add(player);
	}
	
	public boolean isInvited(String player) {
		return invites.contains(player);
	}
	
	public void removeInvite(String player) {
		invites.remove(player);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<String> getAllies() {
		return allies;
	}
	
	public void setAllies(List<String> allies) {
		this.allies = allies;
	}
	
	public void addAlly(String ally) {
		allies.add(ally);
	}
	
	public void removeAlly(String ally) {
		allies.remove(ally);
	}
	
	public boolean isAlly(Tribe tribe) {
		return getAllies().contains(tribe.getName());
	}
	
	public List<String> getEnemies() {
		return enemies;
	}
	
	public void setEnemies(List<String> enemies) {
		this.enemies = enemies;
	}
	
	public void addEnemy(String enemy) {
		enemies.add(enemy);
	}
	
	public void removeEnemy(String enemy) {
		enemies.remove(enemy);
	}
	
	public boolean isEnemy(Tribe tribe) {
		return getEnemies().contains(tribe.getName());
	}
	
	public List<String> getAllyRequests() {
		return allyRequests;
	}
	
	public void setAllyRequests(List<String> requests) {
		allyRequests = requests;
	}
	
	public void addAllyRequest(String tribe) {
		allyRequests.add(tribe);
	}
	
	public boolean isAllyRequested(String tribe) {
		return allyRequests.contains(tribe);
	}
	
	public void removeAllyRequest(String tribe) {
		allyRequests.remove(tribe);
	}
	
	public List<String> getNeutralRequests() {
		return neutralRequests;
	}
	
	public void setNeutralRequests(List<String> requests) {
		neutralRequests = requests;
	}
	
	public void addNeutralRequest(String tribe) {
		neutralRequests.add(tribe);
	}
	
	public void removeNeutralRequest(String tribe) {
		neutralRequests.remove(tribe);
	}
	
	public boolean isNeutralRequested(String tribe) {
		return neutralRequests.contains(tribe);
	}
	
	public ItemStack[] getContent() {
		return inv.getContents();
	}
	
	public ItemStack[] loadContent() {
		TribeFile.createConfig(getName());
		if(TribeFile.getChest() != null) {
			List<?> list = TribeFile.getChest();
			ItemStack[] turinys = new ItemStack[list.size()];
				for (int i = 0; i < list.size(); i++) {
					Object o = list.get(i);
					if (o instanceof ItemStack) {
						turinys[i] = (ItemStack) o;
					} else {
						turinys[i] = new ItemStack(Material.AIR);
					}
				}
			return turinys;
		}
		return null;
	}

	public void openInventory(Player player) {
		if(!opened){
			if(loadContent() == null){
				opened = true;
			}else{
				inv.setContents(loadContent());
				opened = true;
			}
		}
		player.openInventory(inv);
	}
	
}
