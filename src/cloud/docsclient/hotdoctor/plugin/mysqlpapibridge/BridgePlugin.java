package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.commands.AdminTabCompleter;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.MongoDB;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.MySQL;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.Redis;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.GettingType;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.IdentifierNameAlreadyExists;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.IdentifiersWithSameCollection;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.IdentifiersWithSameTable;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.IllegalCharacter;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.NoneType;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.fonts.DefaultFontInfo;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.listeners.Listeners;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.managers.IdentifierManager;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.papi.PlaceholderExtension;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.ConfigUtils;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.MetricsLite;
import me.clip.placeholderapi.PlaceholderAPI;

public class BridgePlugin extends JavaPlugin{
	
	// config.yml
	
	public File configyml;
	public YamlConfiguration configuration;
	public final String rutaConfig;

	private IdentifierManager identifierManager = new IdentifierManager(this);
	private String mainPath = "configuration";
	private MetricsLite metrics;
	
	private List<MySQL> savedConnections = new ArrayList<>();
	private List<MongoDB> savedMongoConnections = new ArrayList<>();
	private List<Redis> savedRedisConections = new ArrayList<>();
	
	private static BridgeAPI api;
	public static final String IDENTIFIERS=".identifiers"

	
	public static BridgeAPI getAPI() {
		return api;
	}
	
	public void onEnable() {
		configyml = new File(this.getDataFolder(), "config.yml");
		registerConfiguration();
		configuration = YamlConfiguration.loadConfiguration(configyml);
		Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
		metrics = new MetricsLite(this, 8387);
		PlaceholderExtension papi = new PlaceholderExtension();
		api = new BridgeAPI(this);
		papi.register();
		papi.setMainPlugin(this);
		for(String id : this.getConfig().getConfigurationSection("placeholders").getKeys(false)) {
			Identifier idf = null;
			String dataPath = "placeholders."+id;
			if(ConfigUtils.DATABASE_TYPE.getMessage(this, dataPath).equalsIgnoreCase("MySQL")) {
				String host = ConfigUtils.HOST.getMessage(this, dataPath);
				String password = ConfigUtils.PASSWORD.getMessage(this, dataPath);
				String database = ConfigUtils.DATABASE.getMessage(this, dataPath);
				String port = ConfigUtils.PORT.getMessage(this, dataPath);
				String user = ConfigUtils.USER.getMessage(this, dataPath);
				String ssl = ConfigUtils.SSL.getMessage(this, dataPath);
				MySQL mysql = new MySQL(host, database, user, password, Integer.valueOf(port), ssl, id);
				try {
					mysql.Connect();
				} catch (SQLException e) {
					e.printStackTrace();
					continue;
				}
				this.savedConnections.add(mysql);
				if(this.getConfig().getConfigurationSection(dataPath+BridgePlugin.IDENTIFIERS).getKeys(false).isEmpty()) {
					return;
				}
				for(String identifier : this.getConfig().getConfigurationSection(dataPath+BridgePlugin.IDENTIFIERS).getKeys(false)) {
					try {
						idf  = this.getIdentifierManager().addIdentifier(identifier, mysql, dataPath+BridgePlugin.IDENTIFIERS+identifier);
						if(this.getIdentifierManager().getIdentifiersEnabled().contains(idf) && this.getIdentifierManager().isValidIdentifier(idf)) {
							if(this.getIdentifierManager().isUPLOAD(idf)) {
								String cmd = "CREATE TABLE IF NOT EXISTS "+idf.getTable()+" (player_uuid varchar(36), player_name varchar(16), "+idf.getColumn()+" varchar(32))";
								mysql.executeUpdate(cmd);
							}else if(this.getIdentifierManager().isSINGLE_UPLOAD(idf)) {
								String cmd = "CREATE TABLE IF NOT EXISTS "+idf.getTable()+" ("+ConfigUtils.COLUMN_FOR_PLACEHOLDER.getMessage(this, idf.getPath())+" varchar(48), "+idf.getColumn()+" varchar(26))";
								mysql.executeUpdate(cmd);
							}
							
						}
					}catch(IdentifierNameAlreadyExists | IdentifiersWithSameTable | IllegalCharacter e) {
						
						e.printStackTrace();
					}catch (NoneType | GettingType e) {
						e.printStackTrace();
						Bukkit.getPluginManager().disablePlugin(this);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}else if(ConfigUtils.DATABASE_TYPE.getMessage(this, dataPath).equalsIgnoreCase("MongoDB")) {
				String uri = ConfigUtils.URI.getMessage(this, dataPath);
				String database = ConfigUtils.MONGO_DB.getMessage(this, dataPath);
				MongoDB connection = new MongoDB(uri, database, id);
				this.savedMongoConnections.add(connection);
				if(!this.getConfig().getConfigurationSection(dataPath+".identifiers").getKeys(false).isEmpty()) {
					for(String identifier : this.getConfig().getConfigurationSection(dataPath+".identifiers").getKeys(false)) {
						try {
							this.getIdentifierManager().addIdentifier(identifier, connection, dataPath+".identifiers."+identifier);
						}catch(IdentifierNameAlreadyExists | IdentifiersWithSameCollection e) {
							
							e.printStackTrace();
						}catch (NoneType | GettingType e) {
							e.printStackTrace();
							Bukkit.getPluginManager().disablePlugin(this);
						}
						
					}
				}
			}else if(ConfigUtils.DATABASE_TYPE.getMessage(this, dataPath).equalsIgnoreCase("Redis")) {
				String host = ConfigUtils.HOST.getMessage(this, dataPath);
				String port = ConfigUtils.PORT.getMessage(this, dataPath);
				Redis redis = new Redis(host, Integer.valueOf(port), id);
				this.savedRedisConections.add(redis);
				if(!this.getConfig().getConfigurationSection(dataPath+".identifiers").getKeys(false).isEmpty()) {
					for(String identifier : this.getConfig().getConfigurationSection(dataPath+".identifiers").getKeys(false)) {
						try {
							this.getIdentifierManager().addIdentifier(identifier, redis, dataPath+".identifiers."+identifier);
						}catch(IdentifierNameAlreadyExists e1) {
							
							e1.printStackTrace();
						}catch (NoneType | GettingType e) {
							e.printStackTrace();
							Bukkit.getPluginManager().disablePlugin(this);
						}
					}
				}
				redis.initialize();
				
				
			}
		}
		Bukkit.getPluginCommand("MySQLPapiBridge").setExecutor(new AdminTabCompleter(this));
	}
	
	public List<MySQL> getActiveMySQLConnections(){
		return this.savedConnections;
	}
	
	public List<MongoDB> getActiveMongoDBConnections(){
		return this.savedMongoConnections;
	}
	
	public List<Redis> getActiveRedisConnections(){
		return this.savedRedisConections;
	}
	
 	public void removeActiveConnection(MySQL conn) {
		if(this.savedConnections.contains(conn)) {
			this.savedConnections.remove(conn);
		}
	}
	
	public void removeActiveConnection(MongoDB conn) {
		if(this.savedMongoConnections.contains(conn)) {
			this.savedMongoConnections.remove(conn);
		}
	}
	
	public void removeActiveConnection(Redis redis) {
		if(this.savedRedisConections.contains(redis)) {
			this.savedRedisConections.remove(redis);
		}
	}
	
	public void addActiveConnection(MySQL conn) {
		if(!this.savedConnections.contains(conn)) {
			this.savedConnections.add(conn);
		}
	}
	
	public void addActiveConnection(MongoDB conn) {
		if(!this.savedMongoConnections.contains(conn)) {
			this.savedMongoConnections.add(conn);
		}
	}
	
	public void addActiveConnection(Redis conn) {
		if(!this.savedRedisConections.contains(conn)) {
			this.savedRedisConections.add(conn);
		}
	}
	public void onDisable() {
		if(!this.getIdentifierManager().getIdentifiersEnabled().isEmpty()) {
			for(Identifier identifier : this.getIdentifierManager().getIdentifiersEnabled()) {
				if(identifier.getConnection() != null) {
					try {
						if(identifier.getConnection().isConnected()) {
							identifier.getConnection().Disconnect();
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					if(identifier.getMongoConnection() != null) {
						identifier.getMongoConnection().Disconnect();
					}
				
				}
				identifier.endTask();
			}
		}
		
	}
	
	
	public IdentifierManager getIdentifierManager() {
		return identifierManager;
	}
	
	
	
	private void registerConfiguration() {
		rutaConfig = configyml.getAbsolutePath();
		if(!(configyml.exists())) {
			setConfigurationDefaults();
			
		}
	}
	private void setConfigurationDefaults() {
		this.saveResource("config.yml", true);
	}
	
	@Override
	public FileConfiguration getConfig() {
		return this.getConfiguration();
	}
	
	public YamlConfiguration getConfiguration() {
		return configuration;
	}
	
	public File getConfigurationFile() {
		return configyml;
	}
	
	public String getMainPath() {
		return mainPath;
	}
	
	public void sendMessage(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public void sendMessage(String message, CommandSender p) {
		message = amazingText(message,p);
		p.sendMessage(message);
	}
	
	public String amazingText(String message, CommandSender player) {
		if(player instanceof Player) {
			message = PlaceholderAPI.setPlaceholders((Player) player, message);
		}
		if(Bukkit.getBukkitVersion().contains("1.16") || Bukkit.getBukkitVersion().contains("1.17") || Bukkit.getBukkitVersion().contains("1.18")) {
			Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
		}
		message = ChatColor.translateAlternateColorCodes('&', message);
		
		
		return message;
	}
	
    public void sendCenteredMessage(CommandSender player, String message) {
        if (message == null || message.equals("")) {
            player.sendMessage("");
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = this.amazingText(message, player);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        char[] charArray;
        for (int length = (charArray = message.toCharArray()).length, i = 0; i < length; ++i) {
            final char c = charArray[i];
            if ('�'.equals(c)) {
                previousCode = true;
            }
            else if (previousCode) {
                previousCode = false;
                isBold = ('l'.equals(c) || 'L'.equals(c));
            }
            else {
                final DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += (isBold ? dFI.getBoldLength() : dFI.getLength());
                ++messagePxSize;
            }
        }
        final int halvedMessageSize = messagePxSize / 2;
        final int toCompensate = 154 - halvedMessageSize;
        final int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        final StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(String.valueOf(sb.toString()) + message);
    }
    
    public MetricsLite getMetrics() {
    	return metrics;
    }


}
