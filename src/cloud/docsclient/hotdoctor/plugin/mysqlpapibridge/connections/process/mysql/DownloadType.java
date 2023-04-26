package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.mysql;

import java.sql.ResultSet;

import java.sql.SQLException;

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

public class DownloadType extends DataBridge {

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
				String WHERE;
				try {
					WHERE = ConfigUtils.WHERE.getMessage(getPlugin(), identifier.getPath());
					if (PlaceholderAPI.containsPlaceholders(WHERE)) {
						if (Boolean.valueOf(ConfigUtils.BOOLEAN_CLEAN_UUID.getMessage(getPlugin(), identifier.getPath()))) {
							WHERE = WHERE.replace("%player_uuid%", p.getUniqueId().toString().replace("-", ""));
						}
						WHERE = PlaceholderAPI.setPlaceholders(p, WHERE);
					}else {
						throw new NotPossibleToDownload("PlaceholderAPI failed to catch placeholders in WHERE Data of the configuration "+identifier.getIdentifierName());
					}
				}catch(LinkageError e) {
					throw new NotPossibleToDownload("There is some plugin interacting with main placeholderapi class, are you using Plugman or Translation Plugins? "+identifier.getIdentifierName());
				}
				
				
				try {
					ResultSet rs = identifier.getConnection().executeQuery(
							"SELECT " + identifier.getColumn() + " FROM " + identifier.getTable() + " WHERE " + WHERE);
					if (rs.next()) {
						String data = rs.getString(identifier.getColumn());
						this.getRAMDatabase().savePlayerInformation(p, data);
					} else {
						String data = ConfigUtils.RESULT_NOT_FOUND.getMessage(getPlugin(), identifier.getPath());
						this.getRAMDatabase().savePlayerInformation(p, data);
					}
					AsyncIdentifierCompleteTaskEvent event = new AsyncIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
					Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(this.getPlugin().getIdentifierManager().isSINGLE_DOWNLOAD(this.getIdentifier())) {
			try {
				ResultSet rs = identifier.getConnection().executeQuery("SELECT "+identifier.getColumn()+" FROM "+identifier.getTable()+" WHERE "+ConfigUtils.COLUMN_TO_FIND.getMessage(getPlugin(), identifier.getPath())+"='"+ConfigUtils.PLACEHOLDER.getMessage(getPlugin(), identifier.getPath())+"'");
				if (rs.next()) {
					String data = rs.getString(identifier.getColumn());
					this.getRAMDatabase().savePlayerInformation(p, data);
				} else {
					String data = ConfigUtils.RESULT_NOT_FOUND.getMessage(getPlugin(), identifier.getPath());
					this.getRAMDatabase().savePlayerInformation(p, data);
				}
				AsyncIdentifierCompleteTaskEvent event = new AsyncIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
				Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

	}

}
