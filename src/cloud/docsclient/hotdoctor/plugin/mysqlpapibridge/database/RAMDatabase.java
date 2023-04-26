package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.database;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.ConfigUtils;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;

public class RAMDatabase {
	private Identifier identifier;
	private BridgePlugin plugin;
	private HashMap<OfflinePlayer, String> information = new HashMap<>();
	private String info;
	
	public RAMDatabase(BridgePlugin plugin, Identifier identifier) {
		this.plugin = plugin;
		this.identifier = identifier;
		info = ConfigUtils.LOADING.getMessage(getPlugin(), this.getIdentifier().getPath());
	}
	
	public String getPlayerInformation(OfflinePlayer p) {
		if(plugin.getIdentifierManager().isDOWNLOAD(getIdentifier()) || plugin.getIdentifierManager().isCOMMUNICATION(getIdentifier())) {
			if(information.containsKey(p)){
				return information.get(p);
			}
			return ConfigUtils.LOADING.getMessage(getPlugin(), this.getIdentifier().getPath());
		}else if(plugin.getIdentifierManager().isSINGLE_DOWNLOAD(getIdentifier())) {
			return info;
		}
		return null;
	}
	
	public void savePlayerInformation(OfflinePlayer p, String save) {
		if(plugin.getIdentifierManager().isDOWNLOAD(getIdentifier())  || plugin.getIdentifierManager().isCOMMUNICATION(getIdentifier())) {
			if(information.containsKey(p)) {
				information.remove(p);
			}
			information.put(p, save);
		}else if(plugin.getIdentifierManager().isSINGLE_DOWNLOAD(getIdentifier())) {
			info = save;
		}
		
	}

	public BridgePlugin getPlugin() {
		return plugin;
	}


	public Identifier getIdentifier() {
		return identifier;
	}


}
