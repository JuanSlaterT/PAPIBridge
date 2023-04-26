package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;

public class AsyncIdentifierCompleteTaskEvent extends Event{
	private static final HandlerList HANDLERS = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return HANDLERS;
	}
	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}

	private OfflinePlayer player;
	private Identifier identifier;
	private BridgePlugin plugin;
	
	public AsyncIdentifierCompleteTaskEvent(OfflinePlayer player, Identifier identifier, BridgePlugin plugin) {
		this.player = player;
		this.identifier = identifier;
		this.plugin = plugin;
	}
	
	public OfflinePlayer getInvolvedOfflinePlayer() {
		if(plugin.getIdentifierManager().isDOWNLOAD(identifier) || plugin.getIdentifierManager().isUPLOAD(identifier)) {
			return player;
		}else {
			return null;
		}
		
	}
	
	
	public Identifier getInvolvedIdentifier() {
		return identifier;
	}
	

}
