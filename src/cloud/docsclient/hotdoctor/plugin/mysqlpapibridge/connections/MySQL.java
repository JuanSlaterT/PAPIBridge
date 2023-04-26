package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections;


import com.zaxxer.hikari.HikariDataSource;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgeAPI;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.ConfigUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MySQL {

	private HikariDataSource hikari;
	private Connection conn;
	private String pathID;
	
	
	public MySQL(String Host, String Database, String User, String Password, Integer Port, String SSL, String pathID) {
		String legacy = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
		String updated = "com.mysql.cj.jdbc.MysqlDataSource";
		String toUse;
		if(isPresent(legacy)) {
			toUse = legacy;
		}else {
			toUse = updated;
		}
		hikari = new HikariDataSource();
		hikari.setDataSourceClassName(toUse);
		hikari.addDataSourceProperty("serverName", Host);
        hikari.addDataSourceProperty("port", Port);
        hikari.addDataSourceProperty("databaseName", Database);
        hikari.addDataSourceProperty("user", User);
        hikari.addDataSourceProperty("password", Password);
        hikari.addDataSourceProperty("useSSL", SSL);
        hikari.addDataSourceProperty("autoReconnect", true);
        hikari.setMaximumPoolSize(30);
        hikari.setConnectionTimeout(300000);
        hikari.setMaxLifetime(1800000);
		if(ConfigUtils.ENCODING.exists(BridgeAPI.getPlugin(), "placeholders."+pathID)) {
			hikari.addDataSourceProperty("characterEncoding", ConfigUtils.ENCODING.getMessage(BridgeAPI.getPlugin(), "placeholders."+pathID));
		}
		if(ConfigUtils.UNICODE.exists(BridgeAPI.getPlugin(), "placeholders."+pathID)) {
			hikari.addDataSourceProperty("useUnicode", ConfigUtils.UNICODE.getMessage(BridgeAPI.getPlugin(), "placeholders."+pathID));
		}
        this.pathID = pathID;
	}
	
	public boolean isPresent(String className) {
	    try {
	         Class.forName(className);
	    } catch (ClassNotFoundException e) {
	         return false;
	     }
	    return true;
	}
	
	public String getPathID() {
		return pathID;
	}
	
	public Connection Connect() throws SQLException {
		conn = hikari.getConnection();
		return conn;
	}
	
	public Connection getConnection() {
		return connection();
	}
	
	public Connection connection() {
		try {
			if(conn.isClosed()) {
				conn = hikari.getConnection();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
		
	}
	
	public ResultSet executeQuery(String string) throws SQLException {
		Statement st = connection().createStatement();
		ResultSet rs = st.executeQuery(string);
		return rs;
	}
	public void executeUpdate(String string) throws SQLException {
		Statement st = connection().createStatement();
		st.executeUpdate(string);
	}
	
	public boolean isConnected() throws SQLException {
		if(conn.isClosed()) {
			return false;
		}else {
			return true;
		}
	}
	
	
	public void Disconnect() throws SQLException {
		hikari.close();
	}
	


	
}
