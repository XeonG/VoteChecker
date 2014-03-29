package me.Koolio.VoteChecker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
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
	
	public String query_votingSites = "";
    public String query_totalplaytime = "";
    public String query_firstjoin = "";
    public String query_usernameTotalVotes = "";
    public String query_monthlyusername = "";
    
	HashMap<String, Integer> serverlisted = new HashMap<String, Integer>();
	
public void CheckStats(CommandSender target, String playername, String[] VotesFrom, String[] Shortname, String[] ForSites, String mySQL_address, String mySQL_database, String mySQL_table, String mySQL_username, String mySQL_password) {
		
	if(playername == null){
		playername = target.getName().toString();
	}
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;

	String url = "jdbc:mysql://" + mySQL_address + ":3306/" + mySQL_database + "?allowMultiQueries=true";
	
	String mySQL_database_logblock = "kraftzone_logblock";
	String url2 = "jdbc:mysql://" + mySQL_address + ":3306/" + mySQL_database_logblock + "?allowMultiQueries=true";
	//String targetName = target.getName().toString();
	
     query_totalplaytime = "SELECT `playerid`, `playername`, `firstlogin`, `lastlogin`, `onlinetime`, `ip` FROM `lb-players` WHERE playername='"+playername+"' ORDER BY `playername` DESC LIMIT 0 , 1";
     query_usernameTotalVotes= "SELECT COUNT(username) FROM votes WHERE `username` = '"+playername+"'";
     query_monthlyusername= "SELECT COUNT(username) FROM votes WHERE `username` = '"+playername+"' AND YEAR(timestamp) = YEAR(CURDATE()) AND MONTH(timestamp) = MONTH(CURDATE())";
     
	for (int i = 0; i < VotesFrom.length; i++) {
		serverlisted.put(VotesFrom[i], 0);
		query_votingSites += "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ playername+ "' AND fromsite = '"+VotesFrom[i]+"' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;";
		}

	//String _playername = "";
	String _firstlogin = "";
	//String _lastlogin  =  "";
	int _onlinetime = 0;
	//String _ip = "";
	String _totalvotes = "";
	String _totalmonth = "";
	try {
		con = DriverManager.getConnection(url2, mySQL_username, mySQL_password);
		
		pst = con.prepareStatement(query_totalplaytime);
		
		boolean isResult = pst.execute();
		do { 
			rs = pst.getResultSet();
			if (!rs.first() && !rs.next())
			{
				
			} else {
				rs.previous(); // go back in result order so rs.next sees the first result.
				while (rs.next()) {
					// String id = rs.getString(1);
					 //_playername = rs.getString(2);
					 //_firstlogin = rs.getString(3);
					//Fixes timestamp 2013-06-28 10:08:35.0
						Timestamp t = rs.getTimestamp(3);
					    SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
					    _firstlogin = df.format(t);
					    	//timestamp doesn't have .0 at the end now
					    
					 //_lastlogin  =  rs.getString(4);
					 _onlinetime = Integer.parseInt(rs.getString(5));
					 //_ip = rs.getString(6);
					 //console.sendMessage(ChatColor.YELLOW + " " + _firstlogin);
					 //console.sendMessage(ChatColor.YELLOW + " " + _onlinetime);
					}
				}
			isResult = pst.getMoreResults(); 
		} while (isResult); 
		
		con.close();
		
		con = DriverManager.getConnection(url, mySQL_username, mySQL_password);
		

	
		pst = con.prepareStatement(query_usernameTotalVotes);
		isResult = pst.execute();
		do { 
			rs = pst.getResultSet();
			if (!rs.first() && !rs.next())
			{
				
			} else {
				rs.previous(); // go back in result order so rs.next sees the first result.
				while (rs.next()) {
					_totalvotes = rs.getString(1);
					} 
				}
			isResult = pst.getMoreResults(); 
		} while (isResult); 
		
		pst = con.prepareStatement(query_monthlyusername);
		isResult = pst.execute();
		do { 
			rs = pst.getResultSet();
			if (!rs.first() && !rs.next())
			{
				
			} else {
				rs.previous(); // go back in result order so rs.next sees the first result.
				while (rs.next()) {
					_totalmonth = rs.getString(1);
					} 
				}
			isResult = pst.getMoreResults(); 
		} while (isResult); 

		  //$con->PHPScommand("ssay &f-&8[Player]&d ". $username ." &8-------------------");
				target.sendMessage(ChatColor.WHITE+fillRest("\u00A7f-\u00A78[Player]\u00A7d "+playername));
		        
		        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[First Joined]"+ChatColor.GREEN+" "+_firstlogin);
		        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Current Month Votes]"+ChatColor.GREEN+" "+_totalmonth);
		        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Total Votes]"+ChatColor.GREEN+" "+_totalvotes);
		        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"[Total Playtime]"+ChatColor.GREEN+" "+timeSince(_onlinetime, true));
		        target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"Last voted at the below sites...");
		        
		pst = con.prepareStatement(query_votingSites);
		isResult = pst.execute();
		do { 
			rs = pst.getResultSet();
			if (!rs.first() && !rs.next())
			{
				//console.sendMessage(ChatColor.RED + "!rs.first() && !rs.next() Failed");
			} else {
				rs.previous(); // go back in result order so rs.next sees the first result.
				while (rs.next()) {
					
					String fromSite = rs.getString(2);
					//console.sendMessage(ChatColor.RED + fromSite);
					int timeStamp = Integer.parseInt(rs.getString(5)); 
					
					serverlisted.remove(fromSite);
					serverlisted.put(fromSite, timeStamp);
					} 
				}
			isResult = pst.getMoreResults(); 
		} while (isResult); 
		
		//int i = 0;
		for (Entry<String, Integer> entry  : entriesSortedByValues(serverlisted)) {
			
				String key = entry.getKey();
			    Integer value = entry.getValue();
				// 86400 seconds = 24hrs
					   if (value >= 1 && value <= 86400) {
						   
						   target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"["+getVoteShortURL_or_Name(key, false, VotesFrom, Shortname, ForSites)+"] "+ChatColor.GREEN+timeSince(value, true)+" ago");
					    }
					   if (value >= 86400) {
				          target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"["+getVoteShortURL_or_Name(key, false, VotesFrom, Shortname, ForSites)+"] "+ChatColor.YELLOW+timeSince(value, true)+" ago");
						    }
					   //i++;
				}
		
		for(Entry<String, Integer> entry : serverlisted.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		 
		     if (value == 0) {
		          target.sendMessage(ChatColor.WHITE+"-"+ChatColor.DARK_GRAY+"["+getVoteShortURL_or_Name(key, false, VotesFrom, Shortname, ForSites)+"]"+ChatColor.RED+" Not voted here before");
			 }
		}
	
        target.sendMessage(ChatColor.WHITE+fillRest(""));
        
		
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
		
public void CheckVotes(CommandSender target, String playername, String[] VotesFrom, String[] Shortname, String[] ForSites, String mySQL_address, String mySQL_database, String mySQL_table, String mySQL_username, String mySQL_password) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		String url = "jdbc:mysql://" + mySQL_address + ":3306/" + mySQL_database + "?allowMultiQueries=true";
		String targetName = target.getName().toString();

for (int i = 0; i < VotesFrom.length; i++) {
	serverlisted.put(VotesFrom[i], 0);
	query_votingSites += "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = '"+VotesFrom[i]+"' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;";
	}
	try {
		con = DriverManager.getConnection(url, mySQL_username, mySQL_password);
			
			/*String query = "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'PlanetMinecraft.com' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'Minestatus' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'MinecraftServers.org' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'MCSL' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1;"
					+ "SELECT id, fromsite, username, address, UNIX_TIMESTAMP() - UNIX_TIMESTAMP(timestamp) AS seconds_ago FROM `"+ mySQL_table+ "` WHERE username = '"+ targetName+ "' AND fromsite = 'TopG.org' ORDER BY `votes`.`timestamp` DESC LIMIT 0 , 1";
			 */
		pst = con.prepareStatement(query_votingSites);
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
				         
				             target.sendMessage(ChatColor.DARK_GREEN + "Thanks for voting here: " +ChatColor.GREEN+ getVoteShortURL_or_Name(key, true, VotesFrom, Shortname, ForSites));
				             target.sendMessage(ChatColor.DARK_PURPLE + "-You last voted: " +ChatColor.DARK_GRAY+ " " + timeSince(value, false)+"ago");
						    }
						   if (value >= 86400) {
					              	target.sendMessage(ChatColor.GOLD + "Please can you vote here: "+ ChatColor.YELLOW+ getVoteShortURL_or_Name(key, true, VotesFrom, Shortname, ForSites));
							    	target.sendMessage(ChatColor.DARK_PURPLE + "-You last voted over: " +ChatColor.DARK_GRAY+ " " + timeSince(value, false)+"ago");
							    }
					}
			
			for(Entry<String, Integer> entry : serverlisted.entrySet()) {
			    String key = entry.getKey();
			    Integer value = entry.getValue();
			 
			     if (value == 0) {
			          target.sendMessage(ChatColor.RED + "-You have never voted here: " +ChatColor.DARK_PURPLE+ getVoteShortURL_or_Name(key, true, VotesFrom, Shortname, ForSites));
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

public String getVoteShortURL_or_Name(String votesite, boolean Longname, String[] VotesFrom, String[] Shortname, String[] ForSites){
	
	if(Longname){
		for (int i = 0; i < VotesFrom.length; i++) {
			//console.sendMessage(ChatColor.RED + votesite +"="+ VotesFrom[i]+ "-" +Shortname[i] + "-" +Integer.toString(i));
			 if(votesite.equals(VotesFrom[i])){
				 return ForSites[i];
				 }
			 }
		return "Site_unknown";
	}else{
		for (int i = 0; i < VotesFrom.length; i++) {
			//console.sendMessage(ChatColor.RED + votesite +"="+ VotesFrom[i]+ "-" +Shortname[i] + "-" +Integer.toString(i));
			 if(votesite.equals(VotesFrom[i])){
				 return Shortname[i];
				 }
			 }
		return "Site_unknown";
	}
	
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
	
	public String timeSince(int seconds, boolean showSeconds) {

		String Sdays = "";
		String Shours = "";
		String Sminutes = "";
		String Sseconds = "";

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
		
		if(!showSeconds){
			if (minutes > 1) {
				Sminutes = minutes + " minutes ";
			} else if (minutes == 1) {
				Sminutes = minutes + " minute ";
			} else {
				Sminutes = "";
			}
			return Sdays + Shours + Sminutes;
		} else {
		//minutes
				if (minutes > 1) {
					Sminutes = minutes + " minutes and ";
				} else if (minutes == 1) {
					Sminutes = minutes + " minute and ";
				} else {
					Sminutes = "";
				}
		//seconds
				if (seconds > 1) {
					Sseconds = seconds + " seconds";
				} else if (seconds == 1) {
					Sseconds = seconds + " second";
				}
			return Sdays + Shours + Sminutes + Sseconds;
		}
		
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