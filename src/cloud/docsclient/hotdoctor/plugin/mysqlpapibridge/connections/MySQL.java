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
	public static final String PLACEHOLDER="placeholders.";
	
	
	public MySQL(String host, String dataBase, String user, String password, Integer port, String ssl, String pathID) {
		String legacy = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
		String updated = "com.mysql.cj.jdbc.MysqlDataSource";
		String toUse;
		toUse= isPresent(legacy)?legacy:updated;
		hikari = new HikariDataSource();
		hikari.setDataSourceClassName(toUse);
		hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", dataBase);
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", password);
        hikari.addDataSourceProperty("useSSL", ssl);
        hikari.addDataSourceProperty("autoReconnect", true);
        hikari.setMaximumPoolSize(30);
        hikari.setConnectionTimeout(300000);
        hikari.setMaxLifetime(1800000);
		if(ConfigUtils.ENCODING.exists(BridgeAPI.getPlugin(), MySQL.PLACEHOLDER+pathID)) {
			hikari.addDataSourceProperty("characterEncoding", ConfigUtils.ENCODING.getMessage(BridgeAPI.getPlugin(), MySQL.PLACEHOLDER+pathID));
		}
		if(ConfigUtils.UNICODE.exists(BridgeAPI.getPlugin(), MySQL.PLACEHOLDER+pathID)) {
			hikari.addDataSourceProperty("useUnicode", ConfigUtils.UNICODE.getMessage(BridgeAPI.getPlugin(), MySQL.PLACEHOLDER+pathID));
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
		return st.executeQuery(string);
	}
	public void executeUpdate(String string) throws SQLException {
		Statement st = connection().createStatement();
		st.executeUpdate(string);
	}
	
	public boolean isConnected() throws SQLException {
		boolean ret = true;
		if(conn.isClosed()) {
			ret =false;
			return ret;
		}
		return ret;
	}
	
	
	public void Disconnect() throws SQLException {
		hikari.close();
	}
	


	
}
