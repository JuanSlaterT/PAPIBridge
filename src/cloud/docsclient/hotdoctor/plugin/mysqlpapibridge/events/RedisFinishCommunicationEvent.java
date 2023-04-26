package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RedisFinishCommunicationEvent extends Event {
	private final static HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}

	private final UUID uuid;
	private final String placeholder;
	private final String result;

	public RedisFinishCommunicationEvent(String uuid, String placeholder, String result) {
		this.uuid = UUID.fromString(uuid);
		this.placeholder = placeholder;
		this.result = result;
	}
	
	public UUID getInvolvedUUID() {
		return uuid;
	}
	
	public String getInvolvedIdentifierString() {
		return placeholder;
	}
	
	public String getFinalResultCommunication() {
		return result;
	}
}
