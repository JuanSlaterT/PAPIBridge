package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections;


import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


public class MongoDB {

	private MongoClient connection;
	private MongoDatabase db;

	public MongoDB(String url, String database, String pathID) {
		connection = MongoClients.create(new ConnectionString(url));
		db = connection.getDatabase(database);
		this.pathID = pathID;
	}

	public void Disconnect() {
		connection.close();
	}

	public MongoDatabase getConnectionDatabase() {
		return this.db;
	}

	public MongoClient getConnection() {
		return connection;
	}
	
	public String getPathID() {
		return pathID;
	}
	private String pathID;
	

}
