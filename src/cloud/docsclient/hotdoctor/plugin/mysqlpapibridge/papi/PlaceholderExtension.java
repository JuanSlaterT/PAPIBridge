package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.papi;

import org.bukkit.OfflinePlayer;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;

public class PlaceholderExtension extends PlaceholderExpansion{

	private BridgePlugin plugin;
	
	@Override
	public String getAuthor() {
		return "HotDoctor";
	}
	
	public void setMainPlugin(BridgePlugin plugin) {
		this.plugin = plugin;
	}
	@Override
	public String getIdentifier() {
		return "MySQLBridge";
	}

	@Override
	public String getVersion() {
		return "2.0.1";
	}
	
    @Override
    public String onRequest(OfflinePlayer player, String params) {
    	if(plugin.getIdentifierManager().existsIdentifier(params)) {
    		Identifier identifier = plugin.getIdentifierManager().getIdentifierByName(params);
    		if(!player.isOnline()) {
    			identifier.addOfflinePlayer(player);
    		}
    		if(plugin.getIdentifierManager().isDOWNLOAD(identifier) || plugin.getIdentifierManager().isSINGLE_DOWNLOAD(identifier) || plugin.getIdentifierManager().isCOMMUNICATION(identifier)) {
    			return ChatColor.translateAlternateColorCodes('&', identifier.getDatabridge().getRAMDatabase().getPlayerInformation(player));
    		}
    	}
    	
    	return null;
    	
    }
	
}
