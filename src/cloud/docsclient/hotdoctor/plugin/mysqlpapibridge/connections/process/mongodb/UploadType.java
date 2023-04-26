package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.mongodb;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.DataBridge;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events.AsyncIdentifierCompleteTaskEvent;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.events.AsyncPreprocessIdentifierCompleteTaskEvent;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.NotPossibleToUpload;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.ConfigUtils;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;
import me.clip.placeholderapi.PlaceholderAPI;

public class UploadType extends DataBridge {

	public UploadType(BridgePlugin plugin, Identifier id) {
		super(plugin, id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(OfflinePlayer p) {
		String resultado = null;
		if (PlaceholderAPI.containsPlaceholders("%" + this.getIdentifier().getIdentifierName() + "%")) {
			resultado = PlaceholderAPI.setPlaceholders(p, "%" + this.getIdentifier().getIdentifierName() + "%");
		} else {
			throw new NotPossibleToUpload(
					"PlaceholderAPI have not detected the placeholder " + this.getIdentifier().getIdentifierName());
		}
		String temp = resultado.replaceAll("\\s+", "");
		if(temp == "" || temp == " " || temp.isEmpty()) {
		    return;
		}
		AsyncPreprocessIdentifierCompleteTaskEvent event2 = new AsyncPreprocessIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
		Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event2));
		if(event2.isCancelled()) {
			return;
		}
		MongoCollection<Document> collection = this.getIdentifier().getMongoCollection();
		if (p != null) {
			if (this.getPlugin().getIdentifierManager().isUPLOAD(this.getIdentifier())) {
				Document doc = new Document("playerUUID", p.getUniqueId().toString());
				doc.append("playerName", p.getName());
				String result = resultado;
				Document document = collection.find(doc).first();
				if (document == null) {
					doc.append("value", result);
					collection.insertOne(doc);
				}else {
					Document update = new Document("playerUUID", p.getUniqueId().toString())
							.append("playerName", p.getName()).append(ConfigUtils.KEY_SAVE.getMessage(getPlugin(), getIdentifier().getPath()), result);
					collection.replaceOne(Filters.eq("_id", document.get("_id")), update);
				}
				AsyncIdentifierCompleteTaskEvent event = new AsyncIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
				Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
			}
		}
		if(this.getPlugin().getIdentifierManager().isSINGLE_UPLOAD(getIdentifier())) {
			Document doc = new Document(ConfigUtils.KEY_FOR_PLACEHOLDER.getMessage(getPlugin(), this.getIdentifier().getPath()), this.getIdentifier().getIdentifierName());
			String result = resultado;
			Document document = collection.find(doc).first();
			if (document == null) {
				doc.append("value", result);
				collection.insertOne(doc);
			}else {
				
				Document update = new Document(ConfigUtils.KEY_FOR_PLACEHOLDER.getMessage(getPlugin(), this.getIdentifier().getPath()), getIdentifier().getIdentifierName())
						.append("value", result);
				collection.replaceOne(Filters.eq("_id", document.get("_id")), update);
			}
			AsyncIdentifierCompleteTaskEvent event = new AsyncIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
			Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
		}

	}

}
