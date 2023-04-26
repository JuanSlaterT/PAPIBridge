package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.mongodb;

import java.util.ArrayList;

import java.util.List;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.DataBridge;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events.AsyncIdentifierCompleteTaskEvent;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events.AsyncPreprocessIdentifierCompleteTaskEvent;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.NotPossibleToDownload;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.ConfigUtils;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;
import me.clip.placeholderapi.PlaceholderAPI;

public class DownloadType extends DataBridge{

	public DownloadType(BridgePlugin plugin, Identifier id) {
		super(plugin, id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(OfflinePlayer p) {
		Identifier identifier = this.getIdentifier();
		AsyncPreprocessIdentifierCompleteTaskEvent event2 = new AsyncPreprocessIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
		Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event2));
		if(event2.isCancelled()) {
			return;
		}
		if(p != null) {
			if(this.getPlugin().getIdentifierManager().isDOWNLOAD(this.getIdentifier())){
				List<String> WHERE = this.getPlugin().getConfig().getStringList(ConfigUtils.WHERE.getPath(identifier.getPath()));
				List<String> newWHERE = new ArrayList<>();
				boolean hasPlaceholders = false;
				if(WHERE.isEmpty()) {
					throw new NotPossibleToDownload("PlaceholderAPI failed to catch placeholders in WHERE Data of the configuration "+identifier.getIdentifierName());
				}
				for(int i=0;i<WHERE.size();i++) {
					String s = WHERE.get(i);
					if(PlaceholderAPI.containsPlaceholders(s)) {
						hasPlaceholders = true;
						s = PlaceholderAPI.setPlaceholders(p, s);
						newWHERE.add(s);
					}
					if(i==WHERE.size()-1) {
						if(!hasPlaceholders) {
							throw new NotPossibleToDownload("PlaceholderAPI failed to catch placeholders in WHERE Data of the configuration "+identifier.getIdentifierName());
						}
					}
				}
				String key = WHERE.get(0).split(":")[0];
				String value = WHERE.get(0).split(":")[1];
				Document doc = new Document(key, value);
				for(int i=0;i<newWHERE.size();i++) {
					String s = newWHERE.get(i);
					String[] split = s.split(":");
					doc.append(split[0], split[1]);
				}
				Document result = this.getIdentifier().getMongoCollection().find(doc).first();
				if(result == null) {
					getRAMDatabase().savePlayerInformation(p, ConfigUtils.RESULT_NOT_FOUND.getMessage(getPlugin(), identifier.getPath()));
				}else {
					String valueByKey = result.getString(ConfigUtils.KEY.getMessage(getPlugin(), identifier.getPath()));
					getRAMDatabase().savePlayerInformation(p, valueByKey);
				}
				AsyncIdentifierCompleteTaskEvent event = new AsyncIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
				Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
				
			}
		}
		if(this.getPlugin().getIdentifierManager().isSINGLE_DOWNLOAD(this.getIdentifier())) {
			Document doc = new Document(ConfigUtils.KEY_TO_FIND.getMessage(getPlugin(), identifier.getPath()), ConfigUtils.PLACEHOLDER.getMessage(getPlugin(), identifier.getPath()));
			Document result = getIdentifier().getMongoCollection().find(doc).first();
			if(result == null) {
				getRAMDatabase().savePlayerInformation(p, ConfigUtils.RESULT_NOT_FOUND.getMessage(getPlugin(), identifier.getPath()));
			}else {
				String valueByKey = result.getString(ConfigUtils.KEY.getMessage(getPlugin(), identifier.getPath()));
				getRAMDatabase().savePlayerInformation(p, valueByKey);
			}
			AsyncIdentifierCompleteTaskEvent event = new AsyncIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
			Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
		}
		
	}

}
