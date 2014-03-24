package me.Koolio.VoteReminder;

//import java.util.HashMap;
//import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
//import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	// private HashMap<OfflinePlayer, Boolean> voteCheck = new
	// HashMap<OfflinePlayer, Boolean>();
	MyConfigManager manager;
	MyConfig PlayerSettings;

	public String server = "";
	public String database = "";
	public String table = "";
	public String username = "";
	public String password = "";
	public Integer update_interval_minutes = 60;
	public int id = 0;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.sendMessage(ChatColor.GREEN + "Hi");
	}

	public static void printlnfx(String message) {
		Server server = Bukkit.getServer();
		ConsoleCommandSender console = server.getConsoleSender();
		console.sendMessage(message);
	}

	public void onEnable() {

		// getConfig().options().copyDefaults(true);
		// saveConfig();
		saveDefaultConfig(); // saves commends from config.yml
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		server = getConfig().getString("mysql.server");
		database = getConfig().getString("mysql.database");
		table = getConfig().getString("mysql.table");
		username = getConfig().getString("mysql.username");
		password = getConfig().getString("mysql.password");

		update_interval_minutes = getConfig().getInt("update_interval_minutes");

		manager = new MyConfigManager(this);
		PlayerSettings = manager.getNewConfig("players.yml", new String[]{"This is the player settings file"});

		id = Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(this, new Runnable() {
					public void run() {
						sendToAllOnline();
					}
				}, 60 * 20 /* ~1mins */,20 * 60 * update_interval_minutes);

		/* List<String> s = getConfig().getStringList("voteCheck"); for (String str : s){ String[] words = str.split(":"); } */
	}

	public void onDisable() {
		/* List<String> s = getConfig().getStringList("voteCheck"); for (OfflinePlayer p : voteCheck.keySet()){ //s.add(p.getName() = ":" + "true"); } */
		Bukkit.getServer().getScheduler().cancelTask(id);
	}

	MySQL my = new MySQL();

	public boolean onCommand(CommandSender sender, Command cmd, String commandlevel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("votecheck")) {
			if (args.length == 0) {
				// self check
				// sender.sendMessage(ChatColor.GREEN+"Use '/votecheck help' to see sub-commands");
				my.CheckVotes(sender, server, database, table, username,password);
				return true;
			}
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.GRAY + "----------" + ChatColor.GOLD + "VoteChecker by Koolio" + ChatColor.GRAY + "----------");
					sender.sendMessage(ChatColor.GREEN + "/votecheck" + ChatColor.GRAY + " - Shows you when you last voted");
					sender.sendMessage(ChatColor.GREEN + "/votecheck help" + ChatColor.GRAY + " - You are reading it");
					sender.sendMessage(ChatColor.GREEN + "/votecheck disable" + ChatColor.GRAY + " - Disable scheduled notices");
					sender.sendMessage(ChatColor.GREEN + "/votecheck enable" + ChatColor.GRAY + " - Enable scheduled notices");
					if (sender.hasPermission("votecheck.help")) {
					sender.sendMessage(ChatColor.GREEN + "/votecheck interval" + ChatColor.GRAY + " - Check scheduled interval");
					sender.sendMessage(ChatColor.GREEN + "/votecheck interval set" + ChatColor.GRAY + " - Admin command");
					sender.sendMessage(ChatColor.GREEN + "/votecheck all" + ChatColor.GRAY + " - Admin command");
					sender.sendMessage(ChatColor.GREEN + "/votecheck stop" + ChatColor.GRAY + " - Admin command");
					sender.sendMessage(ChatColor.GREEN + "/votecheck reload" + ChatColor.GRAY + " - Admin command");
					}
					sender.sendMessage(ChatColor.GRAY + "-----------------------------------------");
					return true;
				}
				if (args[0].equalsIgnoreCase("all")) {
					if (sender.hasPermission("votecheck.all")) {
						sender.sendMessage(ChatColor.GREEN + sender.getName().toString() + " everyones votes are being checked!");
						sendToAllOnline();
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + sender.getName().toString() + " you need perm 'votecheck.all' to do this!");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("disable")) {
					sender.sendMessage(ChatColor.GREEN + "Auto votecheck disabled, " + sender.getName().toString());
					PlayerSettings.set("VoteCheckDisabled." + sender.getName(),true);
					PlayerSettings.saveConfig();
					return true;
				}
				if (args[0].equalsIgnoreCase("enable")) {
					sender.sendMessage(ChatColor.GREEN + "Auto votecheck enabled, " + sender.getName().toString());
					PlayerSettings.removeKey("VoteCheckDisabled." + sender.getName());
					PlayerSettings.saveConfig();
					return true;
				}
				if (args[0].equalsIgnoreCase("stop")) {
					if (sender.hasPermission("votecheck.stop")) {
						sender.sendMessage(ChatColor.GREEN + sender.getName().toString() + " Scheduled votecheck stopped, use '/votecheck reload' to restart!");
						Bukkit.getServer().getScheduler().cancelTask(id);
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + sender.getName().toString() + " you need perm 'votecheck.stop' to do this!");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("interval") && args.length == 1) {
					sender.sendMessage(ChatColor.YELLOW + "Votecheck interval currently set at: " + update_interval_minutes + " minutes");
					return true;
				}
				if (args[0].equalsIgnoreCase("interval") && args.length == 2 && isInteger(args[1])) {
					
					if (sender.hasPermission("votecheck.setinterval")) {
							if (isInteger(args[1])){
								getConfig().set("update_interval_minutes", Integer.parseInt(args[1]));
								saveConfig();
								reloadSettings(sender);
								sender.sendMessage(ChatColor.GREEN + "Set votecheck interval: "+args[1]);
								return true;
								} else {
								sender.sendMessage(ChatColor.RED + args[2] + " is not valid input for /votecheck interval set <interval in minutes (int)>");
								return true;
								}
						} else {
						sender.sendMessage(ChatColor.RED + sender.getName().toString() + " you need perm 'votecheck.setinterval' to do this!");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("votecheck.reload")) {
						reloadSettings(sender);
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + sender.getName().toString() + " you need perm 'votecheck.reload' to do this!");
						return true;
					}
				}
				sender.sendMessage(ChatColor.RED + sender.getName().toString() + " unknown /votecheck sub-command: " + args[0]);
			}
		}
		return true;
	}

	public void sendToAllOnline() {
		PlayerSettings.reloadConfig();
		boolean playertf = false;

		Player[] onlinePlayers = this.getServer().getOnlinePlayers();
		for (Player player : onlinePlayers) {

			playertf = PlayerSettings.getBoolean("VoteCheckDisabled." + player.getName());
			// Main.printlnfx(ChatColor.RED + " " +
			// player.getName()+" "+String.valueOf(playertf));

			if (playertf == false) {
				my.CheckVotes(player, server, database, table, username, password);
			} else {
				//String playerName = player.getName();
				player.sendMessage(ChatColor.YELLOW +"You have scheduled votecheck disabled: /votecheck enable");
			}
		}
		// String playerName = player.getName();
		// Player target = Bukkit.getServer().getPlayer(playerName);
		// player.sendMessage(ChatColor.GREEN+"Done! "+playerName);

	}

	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}

	public void reloadSettings(CommandSender sender) {

		Bukkit.getServer().getScheduler().cancelTask(id);
		server = getConfig().getString("mysql.server");
		database = getConfig().getString("mysql.database");
		table = getConfig().getString("mysql.table");
		username = getConfig().getString("mysql.username");
		password = getConfig().getString("mysql.password");
		update_interval_minutes = getConfig().getInt("update_interval_minutes");

		id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
					public void run() {
						sendToAllOnline();
					}
				}, 60 * 20 /* ~1mins */, 20 * 60 * update_interval_minutes);
		sender.sendMessage(ChatColor.GREEN + sender.getName().toString() + " votechecker settings reloaded!");
	}

}
