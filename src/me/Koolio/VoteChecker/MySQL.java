package me.Koolio.VoteChecker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class MySQL {
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	
	public String query = "";
	HashMap<String, Integer> serverlisted = new HashMap<String, Integer>();
	
public void CheckStats(CommandSender target, String playername, String[] VotesFrom, String mySQL_address, String mySQL_database, String mySQL_table, String mySQL_username, String mySQL_password) {
		
	if(playername == null){
		playername = target.getName().toString();
	}
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;

	String url = "jdbc:mysql://" + mySQL_address + ":3306/" + mySQL_database + "?allowMultiQueries=true";
	String targetName = target.getName().toString();

	for (int i = 0; i < VotesFrom.length; i++) {
		serverlisted.put(VotesFrom[i], 0);
		query += "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = '"+VotesFrom[i]+"' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;";
		}

	try {
		con = DriverManager.getConnection(url, mySQL_username, mySQL_password);
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
		
        //$con->PHPScommand("ssay &f-&8[Player]&d ". $username ." &8-------------------");
		target.sendMessage(ChatColor.WHITE+fillRest("\u00A7f-\u00A78[Player]\u00A7d "+playername));
        /*
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[First Joined]"+ChatColor.GREEN+" $joininfo['firstlogin']");
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Current Month Votes]"+ChatColor.GREEN+" "+totalmonth);
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Total Votes]"+ChatColor.GREEN+" "+totalvotes);
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Total Playtime]"+ChatColor.GREEN+" "+secondsToTime($info['onlinetime']));
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"Last voted at the below sites...");
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[PlanetMC]"+ChatColor.GREEN+" "+planetmc);
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[TopG]"+ChatColor.GREEN+" "+topG);
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Minestatus]"+ChatColor.GREEN+" "+minestatus);
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[MCServers]"+ChatColor.GREEN+" "+mcserver);
        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[McList]"+ChatColor.GREE+" "+mclist);
        //$con->PHPScommand("ssay &8----------------------------------------------------");
        target.sendMessage(ChatColor.WHITE+fillRest(""));
	*/
	} catch (SQLException ex) {
		//Send the console something!
		console.sendMessage(ChatColor.RED + " " + ex.toString());
	
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
			
			console.sendMessage(ChatColor.YELLOW + " " + ex.toString());
		}
	}
}
		
public void CheckVotes(CommandSender target, String playername, String[] VotesFrom, String mySQL_address, String mySQL_database, String mySQL_table, String mySQL_username, String mySQL_password) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		String url = "jdbc:mysql://" + mySQL_address + ":3306/" + mySQL_database + "?allowMultiQueries=true";
		String targetName = target.getName().toString();

for (int i = 0; i < VotesFrom.length; i++) {
	serverlisted.put(VotesFrom[i], 0);
	query += "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = '"+VotesFrom[i]+"' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;";
	}
	try {
		con = DriverManager.getConnection(url, mySQL_username, mySQL_password);
			
			/*String query = "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'PlanetMinecraft.com' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'Minestatus' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'MinecraftServers.org' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'MCSL' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'TopG.org' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1";
			 */
		pst = con.prepareStatement(query);
			boolean isResult = pst.execute();
			do { 
				rs = pst.getResultSet();
				if (!rs.first() && !rs.next())
				{
					//console.sendMessage(ChatColor.RED + "!rs.first() && !rs.next() Failed");
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
			//Send the console something!
			console.sendMessage(ChatColor.RED + " " + ex.toString());
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
				
				console.sendMessage(ChatColor.YELLOW + " " + ex.toString());
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
	
	public String fillRest(String text){
		int textlength = text.length();
		  text += "\u00A78"; //&8
		  //int textadded = 0;
		      for(int x = textlength; x <= 50; x += 1){
		            text += "-";
		           // textadded += 1;
		        }
		//return $text.$textlength." ".$textadded;
		return text;
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