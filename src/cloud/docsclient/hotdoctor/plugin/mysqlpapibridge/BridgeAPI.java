package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.managers.IdentifierManager;

public class BridgeAPI {
	
	private static IdentifierManager manager;
	private static BridgePlugin plugin;
	public BridgeAPI(BridgePlugin plugin) {
		BridgeAPI.plugin = plugin;
		manager = plugin.getIdentifierManager();
		
	}
	
	public static BridgePlugin getPlugin() {
		return plugin;
	}
	static IdentifierManager getIdentifierManager() {
		return manager;
	}

}
