package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.listeners;

import java.util.HashMap;

import org.bson.json.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.JsonParser;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events.AsyncIdentifierCompleteTaskEvent;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.ConfigUtils;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;

public class Listeners implements Listener{
	public Listeners(BridgePlugin plugin) {
		this.plugin = plugin;
	}
	private BridgePlugin plugin;
	
	private HashMap<Player, Integer> joinCooldown = new HashMap<>();
	public void startJoinCooldown(Player p) {
		Bukkit.getScheduler().runTaskLater(plugin, ()-> {
			if(joinCooldown.containsKey(p)) {
				joinCooldown.remove(p);
			}
		}, 10*20);
	}
	
	private HashMap<Player, Integer> quitCooldown = new HashMap<>();
	public void startQuitCooldown(Player p) {
		Bukkit.getScheduler().runTaskLater(plugin, ()-> {
			if(quitCooldown.containsKey(p)) {
				quitCooldown.remove(p);
			}
		}, 10*20);
	}
	@EventHandler
	public void end(AsyncIdentifierCompleteTaskEvent e) {
		if(plugin.getIdentifierManager().isDOWNLOAD(e.getInvolvedIdentifier()) && e.getInvolvedIdentifier().getConnection() != null) {
			if(ConfigUtils.JSON.exists(plugin, e.getInvolvedIdentifier().getPath())) {
				try {
					String text = e.getInvolvedIdentifier().getDatabridge().getRAMDatabase().getPlayerInformation(e.getInvolvedOfflinePlayer());
					com.google.gson.JsonObject json = new JsonParser().parse(text).getAsJsonObject();
					e.getInvolvedIdentifier().getDatabridge().getRAMDatabase().savePlayerInformation(e.getInvolvedOfflinePlayer(), json.get(ConfigUtils.JSON.getMessage(plugin, e.getInvolvedIdentifier().getPath())).getAsString());
				}catch(Exception a) {
					e.getInvolvedIdentifier().getDatabridge().getRAMDatabase().savePlayerInformation(e.getInvolvedOfflinePlayer(), ConfigUtils.RESULT_NOT_FOUND.getMessage(plugin, e.getInvolvedIdentifier().getPath()));
				}
			
				
			}
		}
	}
	@EventHandler
	public void join(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(!plugin.getIdentifierManager().getIdentifiersEnabled().isEmpty()) {
			if(!joinCooldown.containsKey(p)) {
				joinCooldown.put(p, 5);
				startJoinCooldown(p);
				int ticks = 1;
				for(Identifier identifier : plugin.getIdentifierManager().getIdentifiersEnabled()) {
					if(plugin.getIdentifierManager().isDOWNLOAD(identifier) || plugin.getIdentifierManager().isCOMMUNICATION(identifier)) {
						Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, ()-> {
							identifier.getDatabridge().run(p);
						}, ticks);
						ticks = ticks + 3;
						
					}
					
				}
				
			}
		}
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(!plugin.getIdentifierManager().getIdentifiersEnabled().isEmpty()) {
			if(!quitCooldown.containsKey(p)) {
				quitCooldown.put(p, 5);
				startQuitCooldown(p);
				int ticks = 1;
				for(Identifier identifier : plugin.getIdentifierManager().getIdentifiersEnabled()) {
					if(plugin.getIdentifierManager().isUPLOAD(identifier)) {
						Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, ()-> {
							identifier.getDatabridge().run(p);
						}, ticks);
						ticks = ticks + 3;
						
					}
				}
			}
		}
	}

}
