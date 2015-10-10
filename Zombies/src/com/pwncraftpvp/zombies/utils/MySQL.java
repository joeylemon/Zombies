package com.pwncraftpvp.zombies.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.pwncraftpvp.zombies.core.Main;

public class MySQL {
	
	private Main main = Main.getInstance();
	
	private Connection con = null;
	private Statement statement = null;
	
	/**
	 * Connect the MySQL database
	 */
	public void connect(){
		if(main.getConfig().getString("mysql.user") != null && main.getConfig().getString("mysql.user").equalsIgnoreCase("username") == false){
			try{
				MysqlDataSource source = new MysqlDataSource();
				source.setServerName(main.getConfig().getString("mysql.host"));
				source.setDatabaseName(main.getConfig().getString("mysql.database"));
				source.setPort(3306);
				source.setUser(main.getConfig().getString("mysql.user"));
				source.setPassword(main.getConfig().getString("mysql.pass"));
				con = source.getConnection();
				statement = con.createStatement();
				this.execute("CREATE TABLE IF NOT EXISTS players (name VARCHAR(20),brains INT(10),kills INT(10),downs INT(10), revives INT(10), box INT(10), doors INT(10), perks INT(10), games INT(10), "
						+ "playtime INT(10),store VARCHAR(1000))");
				main.getLogger().info("Successfully connected to the MySQL database.");
			}catch (SQLException e){
				main.getLogger().info("The following error occured while connecting to the MySQL database:");
				main.getLogger().info(e.getMessage());
			}
		}else{
			main.getLogger().info("Please configure the database settings to connect to MySQL.");
		}
	}
	
	/**
	 * Close the MySQL connection
	 */
	public void close(){
		if(con != null){
			try{
				statement.close();
				con.close();
			}catch (SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Get the MySQL connection
	 * @return The connection
	 */
	public Connection getConnection(){
		return con;
	}
	
	/**
	 * Query the database
	 * @param sql - The SQL string
	 * @return The ResultSet from the query. Returns null if the query failed.
	 */
	public ResultSet query(String sql){
		ResultSet set = null;
		try{
			set = statement.executeQuery(sql);
			set.next();
		}catch (SQLException e){
			e.printStackTrace();
		}
		return set;
	}
	
	/**
	 * Execute an update to the database
	 * @param sql - The SQL string
	 */
	public void execute(String sql){
		try{
			statement.executeUpdate(sql);
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
}
