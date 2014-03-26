package me.Koolio.VoteReminder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.ChatColor;

import org.bukkit.command.CommandSender;

public class MySQL {

	public void CheckVotes(CommandSender target, String server, String database, String table, String username, String password) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		String url = "jdbc:mysql://" + server + ":3306/" + database + "?allowMultiQueries=true";
		String playerName = target.getName().toString();

HashMap<String, Integer> serverlisted = new HashMap<String, Integer>();
serverlisted.put("PlanetMinecraft.com", 0);
serverlisted.put("Minestatus", 0);
serverlisted.put("MinecraftServers.org", 0);
serverlisted.put("TopG.org", 0);

		try {

			con = DriverManager.getConnection(url, username, password);
			
			String query = "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ table+ "` WHERE username = '"+ playerName+ "' AND fromsite = 'PlanetMinecraft.com' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ table+ "` WHERE username = '"+ playerName+ "' AND fromsite = 'Minestatus' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ table+ "` WHERE username = '"+ playerName+ "' AND fromsite = 'MinecraftServers.org' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ table+ "` WHERE username = '"+ playerName+ "' AND fromsite = 'MCSL' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ table+ "` WHERE username = '"+ playerName+ "' AND fromsite = 'TopG.org' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1";


			pst = con.prepareStatement(query);

			boolean isResult = pst.execute();

			
			do { 
				rs = pst.getResultSet();


				if (!rs.first() && !rs.next())
				{
					
				} else {
					
					rs.previous(); // go back in result order so rs.next sees the first result.

					while (rs.next()) {
						
						String fromSite = rs.getString(2);
						
						int timeStamp = Integer.parseInt(rs.getString(5)); 
						
						serverlisted.remove(fromSite);
						serverlisted.put(fromSite, timeStamp);
						} 
					}
				isResult = pst.getMoreResults(); 
			} while (isResult); 
			
			target.sendMessage(ChatColor.DARK_GRAY + "-------------"+ChatColor.GRAY +"[/votecheck help]"+ChatColor.DARK_GRAY +"-------------");
			
			for (Entry<String, Integer> entry  : entriesSortedByValues(serverlisted)) {
					String key = entry.getKey();
				    Integer value = entry.getValue();
					// 86400 seconds = 24hrs
						   if (value >= 1 && value <= 86400) {
				         
				             target.sendMessage(ChatColor.DARK_GREEN + "Thanks for voting here: " +ChatColor.GREEN+ getVotesite(key));
				             target.sendMessage(ChatColor.DARK_PURPLE + "-You last voted: " +ChatColor.DARK_GRAY+ " " + timeSince(value));
						    }
						   if (value >= 86400) {
					              	target.sendMessage(ChatColor.GOLD + "Please can you vote here: "+ ChatColor.YELLOW+ getVotesite(key));
							    	target.sendMessage(ChatColor.DARK_PURPLE + "-You last voted over: " +ChatColor.DARK_GRAY+ " " + timeSince(value));
							    }
					}
			
			for(Entry<String, Integer> entry : serverlisted.entrySet()) {
			    String key = entry.getKey();
			    Integer value = entry.getValue();
			 
			     if (value == 0) {
			          target.sendMessage(ChatColor.RED + "-You have never voted here: " +ChatColor.DARK_PURPLE+ getVotesite(key));
				 }
			  
			}
		
		} catch (SQLException ex) {
		
			Main.printlnfx(ChatColor.RED + " " + ex.toString());

		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				Main.printlnfx(ChatColor.YELLOW + " " + ex.toString());
			}
		}
	}

	public String getVotesite(String votesite){
		
		switch (votesite){
		
		case "PlanetMinecraft.com":
			votesite = "http://kraftzone.net/planetmc";
			break;
		case "Minestatus":
			votesite = "http://kraftzone.net/minestatus";
			break;
		case "MinecraftServers.org":
			votesite = "http://kraftzone.net/mcservers";
			break;
		case "TopG.org":
			votesite = "http://kraftzone.net/topg";
			break;
		case "MCSL":
			votesite = "http://kraftzone.net/msl";
			break;
		}
		return votesite;
	}
	
	
	
	public String timeSince(int seconds) {

		String Sdays = "";
		String Shours = "";
		String Sminutes = "";
		//String Sseconds = "";

		int days = (int) Math.floor(seconds / 86400);
		seconds %= 86400;
		int hours = (int) Math.floor(seconds / 3600);
		seconds %= 3600;
		int minutes = (int) Math.floor(seconds / 60);
		seconds %= 60;
		seconds *= 1;

		if (days > 360) {
			return "Not voted here yet.";
		}

		if (days > 1) {
			Sdays = days + " days, ";
		} else if (days == 1) {
			Sdays = days + " day, ";
		} else {
			Sdays = "";
		}
		if (hours > 1) {
			Shours = hours + " hours, ";
		} else if (hours == 1) {
			Shours = hours + " hour, ";
		} else {
			Shours = "";
		}
		//minutes
		if (minutes > 1) {
			Sminutes = minutes + " minutes ";
		} else if (minutes == 1) {
			Sminutes = minutes + " minute ";
		} else {
			Sminutes = "";
		}
		
		//minutes
				/*if (minutes > 1) {
					Sminutes = minutes + " minutes and ";
				} else if (minutes == 1) {
					Sminutes = minutes + " minute and ";
				} else {
					Sminutes = "";
				}*/
		//seconds
		/*if (seconds > 1) {
			Sseconds = seconds + " seconds";
		} else if (seconds == 1) {
			Sseconds = seconds + " second";
		}*/

		//return Sdays + Shours + Sminutes + Sseconds + " ago";
		return Sdays + Shours + Sminutes + "ago";
	}
	
	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

}