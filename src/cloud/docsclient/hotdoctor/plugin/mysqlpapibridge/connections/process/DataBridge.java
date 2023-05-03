package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process;

import org.bukkit.OfflinePlayer;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.database.RAMDatabase;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;

public abstract class DataBridge {

	private BridgePlugin plugin;
	private Identifier id;
	private RAMDatabase database;

	protected DataBridge(BridgePlugin plugin, Identifier id) {
		this.setPlugin(plugin);
		this.setId(id);
		if (plugin.getIdentifierManager().isDOWNLOAD(id) || plugin.getIdentifierManager().isSINGLE_DOWNLOAD(id) || plugin.getIdentifierManager().isCOMMUNICATION(id)) {
			database = new RAMDatabase(plugin, id);
		}
	}

	public abstract void run(OfflinePlayer p);

	public RAMDatabase getRAMDatabase() {
		return database;
	}

	public BridgePlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(BridgePlugin plugin) {
		this.plugin = plugin;
	}

	public Identifier getIdentifier() {
		return id;
	}

	public void setId(Identifier id) {
		this.id = id;
	}

}
