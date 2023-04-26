package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.mongodb.client.MongoCollection;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.MongoDB;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.MySQL;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.Redis;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.DataBridge;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.mysql.DownloadType;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.mysql.UploadType;

public class Identifier {

	private String identifier;
	private String type;
	private MySQL connection;
	private String column;
	private String table;
	private String path;
	private BridgePlugin plugin;
	private DataBridge databridge;
	private int timer = 0;
	private List<OfflinePlayer> disconnectedPlayers = new ArrayList<>();
	private MongoDB mongoConnection;
	private MongoCollection<Document> mongoCollection;
	private Redis redisConnection;
	private String server;
	private String target;

	public Redis getRedisConnection() {
		return redisConnection;
	}

	public String getServer() {
		return server;
	}

	public String getTargetServer() {
		return target;
	}

	// general
	public String getType() {
		return type;

	}

	public String getIdentifierName() {
		return identifier;
	}

	public String getPath() {
		return path;
	}

	// mysql

	public String getTable() {
		return table;
	}

	public MySQL getConnection() {
		return connection;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	// mongo
	public MongoDB getMongoConnection() {
		return mongoConnection;
	}

	public MongoCollection<Document> getMongoCollection() {
		return mongoCollection;
	}

	@SuppressWarnings("deprecation")
	public Identifier(BridgePlugin plugin, String identifier, MongoDB connection, String path) {
		super();
		this.identifier = identifier;
		this.mongoConnection = connection;
		this.path = path;
		this.plugin = plugin;
		this.type = ConfigUtils.TYPE.getMessage(plugin, path);
		this.timer = Integer.valueOf(ConfigUtils.TIMER.getMessage(getPlugin(), path));

		if (timer < 15) {
			timer = 40;
		}
		this.mongoCollection = connection.getConnectionDatabase()
				.getCollection(ConfigUtils.MONGO_COLLECTION.getMessage(getPlugin(), path));
		if (plugin.getIdentifierManager().isUPLOAD(this) || plugin.getIdentifierManager().isSINGLE_UPLOAD(this)) {
			this.databridge = new cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.mongodb.UploadType(
					plugin, this);
		} else if (plugin.getIdentifierManager().isDOWNLOAD(this)
				|| plugin.getIdentifierManager().isSINGLE_DOWNLOAD(this)) {
			this.databridge = new cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.mongodb.DownloadType(
					plugin, this);
		}
		if (plugin.getIdentifierManager().isSINGLE_DOWNLOAD(this)
				&& Boolean.valueOf(ConfigUtils.UPDATE_ONLOAD.getMessage(getPlugin(), path))) {
			this.databridge.run(Bukkit.getPlayer(Bukkit.getOfflinePlayer("HotDoctor").getUniqueId()));
		}

	}

	public Identifier(BridgePlugin plugin, String identifier, Redis connection, String path) {
		this.identifier = identifier;
		this.redisConnection = connection;
		this.path = path;
		this.plugin = plugin;
		this.type = ConfigUtils.TYPE.getMessage(plugin, path);
		this.timer = Integer.valueOf(ConfigUtils.TIMER.getMessage(getPlugin(), path));
		this.target = ConfigUtils.SERVER_TARGET.getMessage(getPlugin(), path);
		if (timer < 15) {
			timer = 40;
		}
		this.server = ConfigUtils.SERVER_IDENTITY.getMessage(getPlugin(), redisConnection.getDatabasePath());

		if (plugin.getIdentifierManager().isCOMMUNICATION(this)) {
			this.databridge = new cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.process.redis.CommunicationType(
					getPlugin(), this);
		}
	}

	@SuppressWarnings("deprecation")
	public Identifier(BridgePlugin plugin, String identifier, MySQL connection, String path) {
		super();
		this.identifier = identifier;
		this.connection = connection;
		this.path = path;
		this.plugin = plugin;
		this.type = ConfigUtils.TYPE.getMessage(plugin, path);
		this.table = ConfigUtils.TABLE.getMessage(getPlugin(), path);
		this.timer = Integer.valueOf(ConfigUtils.TIMER.getMessage(getPlugin(), path));
		if (timer < 15) {
			timer = 40;
		}
		if (plugin.getIdentifierManager().isUPLOAD(this) || plugin.getIdentifierManager().isSINGLE_UPLOAD(this)) {
			column = ConfigUtils.COLUMN_SAVE.getMessage(plugin, path);
			this.databridge = new UploadType(plugin, this);
		} else if (plugin.getIdentifierManager().isDOWNLOAD(this)
				|| plugin.getIdentifierManager().isSINGLE_DOWNLOAD(this)) {
			column = ConfigUtils.COLUMN.getMessage(plugin, path);
			this.databridge = new DownloadType(plugin, this);

		}
		if (plugin.getIdentifierManager().isSINGLE_DOWNLOAD(this)
				&& Boolean.valueOf(ConfigUtils.UPDATE_ONLOAD.getMessage(getPlugin(), path))) {
			this.databridge.run(Bukkit.getPlayer(Bukkit.getOfflinePlayer("HotDoctor").getUniqueId()));
		}
	}

	private int currentInt;
	private int currentInt2;
	private BukkitTask ID;

	public void run() {
		if (plugin.getIdentifierManager().isDOWNLOAD(this) || plugin.getIdentifierManager().isUPLOAD(this) || plugin.getIdentifierManager().isCOMMUNICATION(this)) {
			ID = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
				int ticks = 1;
				List<Player> players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
				for (int i = 0; i < players.size(); i++) {
					currentInt = i;
					Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
						this.getDatabridge().run(players.get(currentInt));
					}, ticks);
					ticks++;
				}
				List<OfflinePlayer> players2 = disconnectedPlayers;
				
				for (int i = 0; i < players2.size(); i++) {
					currentInt2 = i;
					if (!players2.get(i).isOnline()) {
						if (plugin.getIdentifierManager().isUPLOAD(this)
								&& Boolean.valueOf(ConfigUtils.UPDATE_IFPLAYER.getMessage(getPlugin(), this.getPath()))) {
							if (Bukkit.getOnlinePlayers().isEmpty()) {
								continue;
							}
						}
						Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
							this.getDatabridge().run(players2.get(currentInt2));
						}, ticks);
						ticks++;

					} else {
						removeOfflinePlayer(players2.get(i));
					}
				}

			}, this.getTimer() * 20, this.getTimer() * 20);
		} else {
			ID = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
				if (plugin.getIdentifierManager().isSINGLE_UPLOAD(this)
						&& Boolean.valueOf(ConfigUtils.UPDATE_IFPLAYERS.getMessage(getPlugin(), this.getPath()))) {
					if (Bukkit.getOnlinePlayers().isEmpty()) {
						return;
					}
				}
				Player update;
				if (Bukkit.getOnlinePlayers().isEmpty()) {
					update = Bukkit.getPlayer(UUID.fromString("444c39f6-8756-4e5d-81f0-724a3dc11a59"));
				} else {
					Random random = new Random();
					List<Player> players = new ArrayList<>();
					players.addAll(Bukkit.getOnlinePlayers());
					Player p = players.get(random.nextInt(players.size()));
					update = Bukkit.getPlayer(p.getUniqueId());
				}
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
					this.getDatabridge().run(update);
				}, 1);
			}, this.getTimer() * 20, this.getTimer() * 20);
		}
	}

	public void endTask() {
		if (ID != null) {
			ID.cancel();
		}
		end = true;
	}
	
	private boolean end = false;
	
	public boolean isEnded() {
		return end;
	}
	
	public void removeOfflinePlayer(OfflinePlayer player) {
		if (disconnectedPlayers.contains(player)) {
			disconnectedPlayers.remove(player);
		}
	}

	public void addOfflinePlayer(OfflinePlayer player) {
		if (!disconnectedPlayers.contains(player)) {
			disconnectedPlayers.add(player);
		}
	}

	public BridgePlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(BridgePlugin plugin) {
		this.plugin = plugin;
	}

	public DataBridge getDatabridge() {
		return databridge;
	}

	public int getTimer() {
		return timer;
	}

}
