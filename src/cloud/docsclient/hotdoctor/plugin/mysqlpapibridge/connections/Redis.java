package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections;

import java.util.UUID;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgeAPI;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.redis.RedisSubscriber;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.ServerWithoutIdentity;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.ConfigUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class Redis {
	private JedisPool pool;
	private String pathID;
	
	public Redis(String host, int port, String pathID) {
		JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setMaxTotal(128);
        jpc.setMaxIdle(128);
        jpc.setMinIdle(16);
		if(ConfigUtils.PASSWORD.exists(BridgeAPI.getPlugin(), this.getDatabasePath())) {
			pool = new JedisPool(jpc, host, port, Protocol.DEFAULT_TIMEOUT, ConfigUtils.PASSWORD.getMessage(BridgeAPI.getPlugin(), this.getDatabasePath()));
		}else {
			pool = new JedisPool(jpc, host, port);
		}
		this.pathID = pathID;
		
		
	}
	
	public void initialize() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				getConnection().subscribe(new RedisSubscriber(), "papibridge-requester", "papibridge-sender");
			}
		}).start();
	}
	
	public String getPathID() {
		return pathID;
	}
	public String getDatabasePath() {
		return "placeholders."+pathID;
	}
	public void disconnect() {
		if (pool != null) pool.destroy();
	}
	
	public Jedis getConnection() {
		Jedis jedis = pool.getResource();
		if(ConfigUtils.PASSWORD.exists(BridgeAPI.getPlugin(), this.getDatabasePath())) {
			
			jedis.auth(ConfigUtils.PASSWORD.getMessage(BridgeAPI.getPlugin(), this.getDatabasePath()));
		}
		return jedis;
	}
	
	public boolean isDisconnected() {
		return pool.isClosed();
	}
	
	
	public void request(UUID uuid, String serverTarget, String placeholder) throws ServerWithoutIdentity {
		if(ConfigUtils.SERVER_IDENTITY.getMessage(BridgeAPI.getPlugin(), this.getDatabasePath()).equals("not-defined")) throw new ServerWithoutIdentity("You have not defined an identity for the redis connection"); 
		this.getConnection().publish("papibridge-requester", uuid.toString()+";"+serverTarget+";"+placeholder+";"+ConfigUtils.SERVER_IDENTITY.getMessage(BridgeAPI.getPlugin(), this.getDatabasePath()));
	}
	
	public void send(UUID uuid, String serverTarget, String placeholder, String result) throws ServerWithoutIdentity {
		if(ConfigUtils.SERVER_IDENTITY.getMessage(BridgeAPI.getPlugin(), this.getDatabasePath()).equals("not-defined")) throw new ServerWithoutIdentity("You have not defined an identity for the redis connection."); 
		this.getConnection().publish("papibridge-sender", uuid.toString()+";"+serverTarget+";"+placeholder+";"+result);
	}

}
