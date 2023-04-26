package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.redis;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.DataBridge;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events.AsyncIdentifierCompleteTaskEvent;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events.AsyncPreprocessIdentifierCompleteTaskEvent;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events.RedisFinishCommunicationEvent;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.NotPossibleToCommunicate;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.ServerWithoutIdentity;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;

public class CommunicationType extends DataBridge implements Listener{

	public CommunicationType(BridgePlugin plugin, Identifier id) {
		super(plugin, id);
		Bukkit.getPluginManager().registerEvents(this, plugin);
		// TODO Auto-generated constructor stub
	}
	
	
	@EventHandler
	public void event(RedisFinishCommunicationEvent e)  {
		if(this.getIdentifier().isEnded() == false && e.getInvolvedIdentifierString().equals(this.getIdentifier().getIdentifierName())) {
			String idf = "%"+this.getIdentifier().getIdentifierName()+"%";
			try {
				if(idf.equals(e.getFinalResultCommunication())) {
					throw new NotPossibleToCommunicate("Plugin was not able to get a custom information in the server "+this.getIdentifier().getTargetServer()+" for Player "+Bukkit.getOfflinePlayer(e.getInvolvedUUID()).getName());
				}
			}catch (Exception ex1) {
				ex1.printStackTrace();
				return;
			}
			this.getRAMDatabase().savePlayerInformation(Bukkit.getOfflinePlayer(e.getInvolvedUUID()), e.getFinalResultCommunication());
			AsyncIdentifierCompleteTaskEvent event = new AsyncIdentifierCompleteTaskEvent(Bukkit.getOfflinePlayer(e.getInvolvedUUID()), this.getIdentifier(), this.getPlugin());
			Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
			
			
		}
	}

	@Override
	public void run(OfflinePlayer p) {
		UUID uuid = p.getUniqueId();
		AsyncPreprocessIdentifierCompleteTaskEvent event = new AsyncPreprocessIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
		Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
		if(event.isCancelled()) {
			return;
		}
		if(p != null) {
			if(this.getPlugin().getIdentifierManager().isCOMMUNICATION(getIdentifier()) && this.getIdentifier().getRedisConnection() != null) {
				try {
					this.getIdentifier().getRedisConnection().request(uuid, this.getIdentifier().getTargetServer(), this.getIdentifier().getIdentifierName());
				} catch (ServerWithoutIdentity e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
