package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;

public enum ConfigUtils {
	// config values
	PREFIX(".prefix", "&7[&b&lMYSQL &e&lPAPI &9&lBRIDGE&7]"),
	NOPERM_MESSAGE(".noperm-message", "&cYou are not allowed to use this command!"),
	INVALID_COMMAND(".invalid-command", "&cError! this command does not exist in this plugin!"),
	MISSING_ARGS(".missing-args", "&cError! The command is not completed and is missing args!"),
	IDENTIFIER_DOESNOT_EXIST(".identifier-not-exist", "&cError! The identifier sent does not exists in the Configuration!"),
	PLACEHOLDER_UPDATED(".placeholder-updated", "&aSuccess! The identifier has been updated correctly."),
	UPDATING(".updating", "&6Please Wait! This may take a few seconds..."),
	// database information
	DATABASE_TYPE(".TYPE", "MySQL"),
	//mysql information
	HOST(".Host", "localhost"),
	PORT(".Port", "3306"),
	USER(".User", "root"),
	PASSWORD(".Password", "password"),
	DATABASE(".Database", "PAPIBridge"),
	SSL(".useSSL", "true"),
	ENCODING(".characterEncoding", "uft8"),
	UNICODE(".useUnicode", "true"),
	
	
	
	//mongodb information
	URI(".URL", "mongodb://user1:pwd1@host1/?authSource=db1&ssl=true"),
	MONGO_DB(".Database", "PAPIBridge"),
	
	// redis settings
	SERVER_IDENTITY(".server", "not-defined"),
	SERVER_TARGET(".targetServer", "not-defined"),
	
	// default values
	TABLE(".TABLE", "defaultTable"),
	
	MONGO_COLLECTION(".Collection", "PlaceholderCollection"),
	
	TYPE(".TYPE", "NONE"),
	TIMER(".timer", "80", ".TIMER"),
	
	// upload values
	COLUMN_SAVE(".COLUMN-TO-SAVE-INFO", "Value"),
	UPDATE_IFPLAYER(".UpdatePlaceholder-ifPlayerOnline", "false"),
	
	KEY_SAVE(".KEY-TO-SAVE-INFO", "value"),
	
	// download values
	COLUMN(".COLUMN", "Value"),
	WHERE(".WHERE", "player_uuid=\"%player_uuid%\" AND player_name=\"%player_name%\""),
	
	BOOLEAN_CLEAN_UUID(".cleanUUID", "false"),
	RESULT_NOT_FOUND(".result-not-found", "0"),
	LOADING(".loading", "&cLoading information..."),
	
	KEY(".KEY", "value"),
	
	JSON(".JSONData", "value"),
	
	//single upload
	COLUMN_FOR_PLACEHOLDER(".COLUMN-TO-SAVE-PLACEHOLDER", "Placeholder"),
	KEY_FOR_PLACEHOLDER(".KEY-TO-SAVE-PLACEHOLDER", "Placeholder"),
	UPDATE_IFPLAYERS(".UpdateOnlyPlaceholder-ifPlayersOline", "false", ".UpdateOnlyPlaceholder-ifPlayersOnline"),
	
	//single download
	
	PLACEHOLDER(".PLACEHOLDER", "Unknown"),
	UPDATE_ONLOAD(".update_onLoad", "false"),
	
	
	COLUMN_TO_FIND(".COLUMN-TO-FIND-PLACEHOLDER", "Placeholder"),
	KEY_TO_FIND(".KEY-TO-FIND-PLACEHOLDER", "Placeholder");
	
	
	
	
	
	private final String path;
	private final String defaultText;
	private String path2 = null;
	
	ConfigUtils(String path, String defaultText){
		this.path = path;
		this.defaultText = defaultText;
	}
	
	ConfigUtils(String path, String defaultText, String path2){
		this.path = path;
		this.defaultText = defaultText;
		this.path2 = path2;
	}
	
	public String getPath(String mainPath) {
		return mainPath+path;
	}
	
	public String getPath2(String mainPath) {
		return mainPath+path2;
	}
	
	public String getDefaultText() {
		return this.defaultText;
	}
	
	public boolean exists(BridgePlugin plugin, String mainPath) {
		return plugin.getConfig().contains(getPath(mainPath));
	}
	
	public boolean exists2(BridgePlugin plugin, String mainPath) {
		return plugin.getConfig().contains(getPath2(mainPath));
	}
	
	public String getMessage(BridgePlugin plugin, String mainPath) {
		if(exists(plugin, mainPath)) {
			return plugin.getConfig().getString(getPath(mainPath));
		}
		if(path2 != null) {
			if(exists2(plugin, mainPath)) {
				return plugin.getConfig().getString(getPath2(mainPath));
			}
		}
		return getDefaultText();
	}
	

}
