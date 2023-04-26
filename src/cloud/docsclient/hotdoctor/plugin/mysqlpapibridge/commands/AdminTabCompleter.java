package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.commands;

import java.io.File;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.ConfigUtils;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;
import net.md_5.bungee.api.ChatColor;

public class AdminTabCompleter implements TabExecutor {
	private BridgePlugin plugin;

	public AdminTabCompleter(BridgePlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command comando, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		List<String> commands = new ArrayList<>();
		if (sender.hasPermission("papibridge.admin")) {
			if (args.length == 1) {
				commands.add("info");
				commands.add("run");
				commands.add("help");
				commands.add("list");
				commands.add("reload");
				StringUtil.copyPartialMatches(args[0], commands, completions);
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("run")) {
					commands = plugin.getIdentifierManager().getIdentifiersNameEnabled();
				}
				StringUtil.copyPartialMatches(args[1], commands, completions);
			} else if (args.length == 3) {
				String identifierName = args[1];
				Identifier identifier = plugin.getIdentifierManager().getIdentifierByName(identifierName);
				if (identifier != null) {
					if (plugin.getIdentifierManager().isDOWNLOAD(identifier)
							|| plugin.getIdentifierManager().isUPLOAD(identifier) || plugin.getIdentifierManager().isCOMMUNICATION(identifier)) {
						commands.add("all");
						for (Player online : Bukkit.getOnlinePlayers()) {
							commands.add(online.getName());
						}
					}
				}
				StringUtil.copyPartialMatches(args[2], commands, completions);
			}
		}
		Collections.sort(completions);
		return completions;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command comando, String label, String[] args) {
		if (!(sender instanceof Player || sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath())+" &cError: &fCommands are only avaible ingame."));
			return true;
		} else {
			CommandSender p = sender;
			if (p.hasPermission("papibridge.admin")) {
				if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help")) || (args.length == 1 && args[0].equalsIgnoreCase("1")) || (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("1"))) {
					p.sendMessage("");
					plugin.sendCenteredMessage(p, ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath()));
					plugin.sendCenteredMessage(p, "&b&ov&7&o" + plugin.getDescription().getVersion());
					p.sendMessage("");
					plugin.sendMessage("&7Permission &8» &7papibridge.admin", p);
					plugin.sendMessage("&7Aliases &8» &b/mpb, /papibridge", p);
					p.sendMessage("");
					plugin.sendMessage("&b» &a/pbridge run &c<placeholder> &b[all/player_name]", p);
					plugin.sendMessage("&b&oDescription: &7&oForces to update the selected placeholder", p);
					plugin.sendMessage("&7&oTarget player is not needed for Single Placeholders.", p);
					p.sendMessage("");
					plugin.sendMessage("&b» &a/pbridge info", p);
					plugin.sendMessage("&b&oDescription: &7&oDisplays customer information.", p);
					p.sendMessage("");
					plugin.sendMessage("&b» &a/pbridge list", p);
					plugin.sendMessage("&b&oDescription: &7&oDisplays Placeholders List Information.", p);
					p.sendMessage("");
					plugin.sendMessage("&b» &a/pbridge help &6[page]", p);
					plugin.sendMessage("&b&oDescription: &7&oShows plugin command list depending page number.", p);
					p.sendMessage("");
					plugin.sendCenteredMessage(p, "&6&oPage &b&o[1/2]");
				}else if((args[0].equalsIgnoreCase("help") && args.length == 2 && args[1].equalsIgnoreCase("2")) || (args.length == 1 && args[0].equalsIgnoreCase("2"))) {
					p.sendMessage("");
					plugin.sendCenteredMessage(p, ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath()));
					plugin.sendCenteredMessage(p, "&b&ov&7&o" + plugin.getDescription().getVersion());
					p.sendMessage("");
					plugin.sendMessage("&7Permission &8» &7papibridge.admin", p);
					plugin.sendMessage("&7Aliases &8» &b/mpb, /papibridge", p);
					p.sendMessage("");
					plugin.sendMessage("&b» &a/pbridge reload", p);
					plugin.sendMessage("&b&oDescription: &7&oUpdates configuration section. &cNot Placeholders configuration", p);
					p.sendMessage("");
					plugin.sendCenteredMessage(p, "&6&oPage &b&o[2/2]");
				}
				else if(args[0].equalsIgnoreCase("run")) {
					if(args.length >= 2) {
						Identifier id = plugin.getIdentifierManager().getIdentifierByName(args[1]);
						if (id == null){
							plugin.sendMessage(ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath())+" "+ConfigUtils.IDENTIFIER_DOESNOT_EXIST.getMessage(plugin, plugin.getMainPath()), p);
						}else {
							if(plugin.getIdentifierManager().isDOWNLOAD(id) || plugin.getIdentifierManager().isUPLOAD(id) || plugin.getIdentifierManager().isCOMMUNICATION(id)) {
								if(plugin.getIdentifierManager().isUPLOAD(id) && id.getConnection() != null) {
									String cmd = "CREATE TABLE IF NOT EXISTS "+id.getTable()+" (player_uuid varchar(36), player_name varchar(16), "+id.getColumn()+" varchar(32))";
									try {
										id.getConnection().executeUpdate(cmd);
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								if((args.length == 3) && (!args[2].equalsIgnoreCase("all"))) {
									OfflinePlayer update = Bukkit.getOfflinePlayer(args[2]);
									Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, ()->{
										id.getDatabridge().run(update);
										plugin.sendMessage(ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath())+" "+ConfigUtils.PLACEHOLDER_UPDATED.getMessage(plugin, plugin.getMainPath()), p);
									}, 1);
								}else {
									List<Player> players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
									int ticks = 1;
									plugin.sendMessage(ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath())+" "+ConfigUtils.UPDATING.getMessage(plugin, plugin.getMainPath()), p);
									String version = Bukkit.getBukkitVersion();
									for(int i=0;i<players.size();i++) {
										Player update = players.get(i);
										int n = i;
										int porcentaje = ((n+1)/players.size())*100;
										Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, ()->{
											if(p instanceof Player) {
												Player player = (Player) p;
												if(version.contains("1.8") || version.contains("1.9") || version.contains("1.10")) {
													player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a( "+porcentaje+"% / 100% )"), ChatColor.translateAlternateColorCodes('&', "&aCompleted"));
												}else {
													player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a( "+porcentaje+"% / 100% )"), ChatColor.translateAlternateColorCodes('&', "&aCompleted"), 0, 25, 0);
												}
											}else {
												plugin.sendMessage("&a("+porcentaje+"% / 100% )", update);
											}
											
											id.getDatabridge().run(update);
											if(n==players.size()-1) {
												plugin.sendMessage(ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath())+" "+ConfigUtils.PLACEHOLDER_UPDATED.getMessage(plugin, plugin.getMainPath()), p);
											}
										}, ticks);
										ticks = ticks +2;
									}
								}
							}else {
								if(plugin.getIdentifierManager().isSINGLE_DOWNLOAD(id) || plugin.getIdentifierManager().isSINGLE_UPLOAD(id)){
									if(plugin.getIdentifierManager().isSINGLE_UPLOAD(id) && id.getConnection() != null) {
										String cmd = "CREATE TABLE IF NOT EXISTS "+id.getTable()+" ("+ConfigUtils.COLUMN_FOR_PLACEHOLDER.getMessage(plugin, id.getPath())+" varchar(48), "+id.getColumn()+" varchar(26))";
										try {
											id.getConnection().executeUpdate(cmd);
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									if(plugin.getIdentifierManager().isSINGLE_UPLOAD(id) && Boolean.valueOf(ConfigUtils.UPDATE_IFPLAYERS.getMessage(id.getPlugin(), id.getPath()))){
										if(Bukkit.getOnlinePlayers().isEmpty()) {
											return true;
										}
									}
									OfflinePlayer update;
									if(Bukkit.getOnlinePlayers().isEmpty()) {
										update = Bukkit.getOfflinePlayer(UUID.fromString("444c39f6-8756-4e5d-81f0-724a3dc11a59"));
									}else {
										Random random = new Random();
										List<Player> players = new ArrayList<>();
										players.addAll(Bukkit.getOnlinePlayers());
										Player pal = players.get(random.nextInt(players.size()));
										update = Bukkit.getPlayer(pal.getUniqueId());
									}
									Bukkit.getScheduler().runTaskLater(plugin, () -> {
										Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
											id.getDatabridge().run(update);
										}, 1);
									}, 1);
									plugin.sendMessage(ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath())+" "+ConfigUtils.PLACEHOLDER_UPDATED.getMessage(plugin, plugin.getMainPath()), p);
								}
							}
						}
					}else {
						plugin.sendMessage(ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath())+" "+ConfigUtils.MISSING_ARGS.getMessage(plugin, plugin.getMainPath()), p);
					}
				}else if(args[0].equalsIgnoreCase("info")) {
					p.sendMessage("");
					plugin.sendCenteredMessage(p, ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath()));
					plugin.sendCenteredMessage(p, "&b&ov&7&o" + plugin.getDescription().getVersion());
					p.sendMessage("");
					plugin.sendMessage("&6User &b» &9"+plugin.getMetrics().user, p);
					plugin.sendMessage("&6Resource &b» &9"+plugin.getMetrics().resource, p);
					plugin.sendMessage("&6Nonce &b» &9"+plugin.getMetrics().nonce, p);
					p.sendMessage("");
					plugin.sendMessage("&6Plugin Developer &b» &9HotDoctor", p);
					p.sendMessage("");
				}else if(args[0].equalsIgnoreCase("list")){
					p.sendMessage("");
					plugin.sendCenteredMessage(p, ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath()));
					plugin.sendCenteredMessage(p, "&b&ov&7&o" + plugin.getDescription().getVersion());
					p.sendMessage("");
					List<Identifier> idf = plugin.getIdentifierManager().getIdentifiersEnabled();
					for(int i=0;i<idf.size();i++) {
						Identifier id = idf.get(i);
						if(id.getConnection() != null) {
							plugin.sendMessage("&8[&f"+i+"&8] &b» &e"+id.getIdentifierName()+" &7&o("+id.getType()+"&7&o) &fis Running a &9MySQL Connection &fevery &a"+id.getTimer()+" &fseconds.", p);
						}else if(id.getMongoConnection() != null) {
							plugin.sendMessage("&8[&f"+i+"&8] &b» &e"+id.getIdentifierName()+" &7&o("+id.getType()+"&7&o) &fis Running a &cMongoDB Connection &fevery &a"+id.getTimer()+" &fseconds.", p);
						}else if(id.getRedisConnection() != null) {
							plugin.sendMessage("&8[&f"+i+"&8] &b» &e"+id.getIdentifierName()+" &7&o("+id.getType()+"&7&o) &fis Running a &4Redis Connection &fevery &a"+id.getTimer()+" &fseconds.", p);
						}
					}
					
					
				}else if(args[0].equalsIgnoreCase("reload")){
					p.sendMessage("");
					plugin.sendCenteredMessage(p, ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath()));
					plugin.sendCenteredMessage(p, "&b&ov&7&o" + plugin.getDescription().getVersion());
					p.sendMessage("");
					plugin.sendMessage("&a&l(!) Configuration files are now updated",p);
					p.sendMessage("");
					plugin.configyml = new File(plugin.getDataFolder(), "config.yml");
					plugin.configuration = YamlConfiguration.loadConfiguration(plugin.configyml);
				}
			}else {
				plugin.sendMessage(ConfigUtils.PREFIX.getMessage(plugin, plugin.getMainPath())+" "+ConfigUtils.NOPERM_MESSAGE.getMessage(plugin, plugin.getMainPath()), p);
			}
		}
		return false;
	}

}
