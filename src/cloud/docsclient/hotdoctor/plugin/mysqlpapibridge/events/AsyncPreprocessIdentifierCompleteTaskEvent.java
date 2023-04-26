package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;

public class AsyncPreprocessIdentifierCompleteTaskEvent extends Event implements Cancellable{
	private final static HandlerList HANDLERS = new HandlerList();
	
	private OfflinePlayer player;
	private Identifier identifier;
	private BridgePlugin plugin;
	private boolean cancelled = false;
	
	
	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}

	
	public AsyncPreprocessIdentifierCompleteTaskEvent(OfflinePlayer player, Identifier identifier, BridgePlugin plugin) {
		this.player = player;
		this.identifier = identifier;
		this.plugin = plugin;
		cancelled = false;
	}
	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
		
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return HANDLERS;
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
