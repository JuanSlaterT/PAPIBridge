package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.MySQL;
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
		Identifier identifier = this.getIdentifier();
		MySQL mysql = identifier.getConnection();
		String resultado = null;
		if(PlaceholderAPI.containsPlaceholders("%"+this.getIdentifier().getIdentifierName()+"%")) {
			resultado = PlaceholderAPI.setPlaceholders(p, "%"+this.getIdentifier().getIdentifierName()+"%");
		}else {
			throw new NotPossibleToUpload("PlaceholderAPI have not detected the placeholder "+this.getIdentifier().getIdentifierName());
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
		if(p != null) {
			if(this.getPlugin().getIdentifierManager().isUPLOAD(this.getIdentifier())) {
				String command = "INSERT INTO "+identifier.getTable()+" (player_uuid, player_name, "+identifier.getColumn()+") VALUES (?, ?, ?)";
				String delCommand = "DELETE FROM "+identifier.getTable()+" WHERE player_uuid=? OR player_name=?";
				try {
					PreparedStatement del = mysql.getConnection().prepareStatement(delCommand);
					del.setString(1, p.getUniqueId().toString());
					del.setString(2, p.getName());
					del.execute();
					PreparedStatement st = mysql.getConnection().prepareStatement(command);
					st.setString(1, p.getUniqueId().toString());
					st.setString(2, p.getName());
					st.setString(3, resultado);
					st.execute();
					AsyncIdentifierCompleteTaskEvent event = new AsyncIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
					Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(this.getPlugin().getIdentifierManager().isSINGLE_UPLOAD(getIdentifier())) {
			String command = "INSERT INTO "+identifier.getTable()+" ("+ConfigUtils.COLUMN_FOR_PLACEHOLDER.getMessage(getPlugin(), identifier.getPath())+", "+identifier.getColumn()+") VALUES (?, ?)";
			String delCommand = "DELETE FROM "+identifier.getTable()+" WHERE "+ConfigUtils.COLUMN_FOR_PLACEHOLDER.getMessage(getPlugin(), identifier.getPath())+"=?";
			try {
				PreparedStatement del = mysql.getConnection().prepareStatement(delCommand);
				del.setString(1, identifier.getIdentifierName());
				del.execute();
				PreparedStatement st = mysql.getConnection().prepareStatement(command);
				st.setString(1, identifier.getIdentifierName());
				st.setString(2, resultado);
				st.execute();
				AsyncIdentifierCompleteTaskEvent event = new AsyncIdentifierCompleteTaskEvent(p, this.getIdentifier(), this.getPlugin());
				Bukkit.getScheduler().runTask(getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		

	}

}
