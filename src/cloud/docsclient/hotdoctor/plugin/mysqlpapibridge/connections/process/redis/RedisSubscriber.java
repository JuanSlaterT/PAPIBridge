package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.redis;

import java.util.UUID;


import org.bukkit.Bukkit;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgeAPI;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events.RedisFinishCommunicationEvent;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.NotPossibleToCommunicate;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.ServerWithoutIdentity;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.ConfigUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import redis.clients.jedis.JedisPubSub;

public class RedisSubscriber extends JedisPubSub {
	
	@Override
	public void onMessage(String channel, String message) {
		switch (channel) {
		case "papibridge-requester":
			String[] split = message.split(";");
			String serverTarget = split[1];
			BridgeAPI.getPlugin().getActiveRedisConnections().forEach(redisD ->{
				if(serverTarget.equals(getCurrentServer(redisD.getDatabasePath()))) {
					String uuid = split[0];
					String placeholder = split[2];
					String serverSender = split[3];
					String result = PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), "%"+placeholder+"%");
					if(result.isEmpty() || result.equals("")) {
						Bukkit.getScheduler().runTask(BridgeAPI.getPlugin(), ()->{
							try {
								throw new NotPossibleToCommunicate("There was an error while catching information of placeholder "+placeholder+" from the server configuration "+serverSender+" because placeholder information is null or empty.");	
							}catch(Exception e) {
								e.printStackTrace();
								return;
							}
							
						});
						
					}
					try {
						redisD.send(UUID.fromString(uuid), serverSender, placeholder, result);
					} catch (ServerWithoutIdentity e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
				
			break;
		case "papibridge-sender":
			String[] split1 = message.split(";");
			String serverTarget1 = split1[1];
			BridgeAPI.getPlugin().getActiveRedisConnections().forEach(redisD ->{
				if(serverTarget1.equals(getCurrentServer(redisD.getDatabasePath()))) {
					String uuid = split1[0];
					String placeholder = split1[2];
					String result = split1[3];
					
					Bukkit.getScheduler().runTask(BridgeAPI.getPlugin(), ()-> {
						RedisFinishCommunicationEvent event = new RedisFinishCommunicationEvent(uuid, placeholder, result);
						Bukkit.getPluginManager().callEvent(event);
					});
				}
			});
			break;
		}
	}
	private String getCurrentServer(String databasePath) {
		return ConfigUtils.SERVER_IDENTITY.getMessage(BridgeAPI.getPlugin(), databasePath);
		
	}
}
