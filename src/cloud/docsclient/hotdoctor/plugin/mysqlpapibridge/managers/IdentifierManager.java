package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.managers;

import java.util.ArrayList;
import java.util.List;

import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.BridgePlugin;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.MongoDB;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.MySQL;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.connections.Redis;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.GettingType;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.IdentifierNameAlreadyExists;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.IdentifiersWithSameCollection;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.IdentifiersWithSameTable;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.IllegalCharacter;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions.NoneType;
import cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.utils.Identifier;

public class IdentifierManager {
	private BridgePlugin plugin;

	public IdentifierManager(BridgePlugin plugin) {
		this.plugin = plugin;
	}

	private List<Identifier> identifiers = new ArrayList<>();

	public List<Identifier> getIdentifiersEnabled() {
		return identifiers;
	}

	public List<String> getIdentifiersNameEnabled(){
		List<String> ids = new ArrayList<>();
		for(Identifier id : this.getIdentifiersEnabled()) {
			ids.add(id.getIdentifierName());
		}
		return ids;
	}
	
	public boolean removeIdentifier(Identifier idf) {
		if(identifiers.contains(idf)) {
			idf.endTask();
			identifiers.remove(idf);
			return true;
		}
		return false;
	}
	
	public Identifier addIdentifier(String name, Redis connection, String path) throws IdentifierNameAlreadyExists, NoneType, GettingType {
		if (existsIdentifier(name)) {
			throw new IdentifierNameAlreadyExists(
					"Check your configuration file, You can not have different identifiers with equals names.");
		} else {
			Identifier id = new Identifier(plugin, name, connection, path);
			if(this.isNONE(id)) {
				throw new NoneType("Error while loading placeholders, Please contact Developer in Discord. ");
			}else {
				if(this.isValidIdentifier(id)) {
					List<Identifier> ids = this.getIdentifiersByRedis(connection);
					if(ids.isEmpty()) {
						identifiers.add(id);
						id.run();
					}else {
						for(int i=0; i<ids.size(); i++) {
							if(i==ids.size()-1) {
								identifiers.add(id);
								id.run();
							}
						}
					}
				}else {
					throw new GettingType("Error while loading placeholders, Please contact Developer in Discord.");
				}
			}
			return id;
		}
	}
	public Identifier addIdentifier(String name, MongoDB connection, String path) throws IdentifierNameAlreadyExists, NoneType, GettingType, IdentifiersWithSameCollection {
		if (existsIdentifier(name)) {
			throw new IdentifierNameAlreadyExists(
					"Check your configuration file, You can not have different identifiers with equals names.");
		} else {
			Identifier id = new Identifier(plugin, name, connection, path);
			if(this.isNONE(id)) {
				throw new NoneType("Error while loading placeholders, Please contact Developer in Discord. ");
			}else {
				if(this.isValidIdentifier(id)) {
					List<Identifier> ids = this.getIdentifiersByMongoDB(connection);
					if(ids.isEmpty()) {
						identifiers.add(id);
						id.run();
					}else {
						for(int i=0; i<ids.size(); i++) {
							Identifier idf = ids.get(i);
							if(idf.getMongoCollection().equals(id.getMongoCollection()) && areUPLOAD(id, idf)) {
								throw new IdentifiersWithSameCollection("UPLOAD Placeholders can not interact in same Collections. Check your config file again.");
							}
							if(i==ids.size()-1) {
								identifiers.add(id);
								id.run();
							}
						}
					}
				}else {
					throw new GettingType("Error while loading placeholders, Please contact Developer in Discord.");
				}
			}
			return id;
		}
	}
	public Identifier addIdentifier(String name, MySQL connection, String path) throws IdentifierNameAlreadyExists, NoneType, GettingType, IdentifiersWithSameTable, IllegalCharacter {
		if (existsIdentifier(name)) {
			throw new IdentifierNameAlreadyExists(
					"Check your configuration file, You can not have different identifiers with equals names.");
		} else {
			Identifier id = new Identifier(plugin, name, connection, path);
			if(this.isNONE(id)) {
				throw new NoneType("Error while loading placeholders, Please contact Developer in Discord.");
			}else {
				if(this.isValidIdentifier(id)) {
					if(id.getTable().contains("-") || id.getColumn().contains("-")) {
						throw new IllegalCharacter("Placeholders Tables and Columns can not contains '-' in their names.");
					}
					List<Identifier> ids = getIdentifiersByMySQL(id.getConnection());
					if(ids.isEmpty()) {
						identifiers.add(id);
						id.run();
					}else {
						for(int i=0; i<ids.size(); i++) {
							Identifier idf = ids.get(i);
							if(idf.getTable().equals(id.getTable()) && areUPLOAD(id, idf)) {
								throw new IdentifiersWithSameTable("UPLOAD Placeholders can not interact in same tables. Check your config file again.");
							}
							if(i==ids.size()-1) {
								identifiers.add(id);
								id.run();
							}
						}	
					}
				}else {
					throw new GettingType("Error while loading placeholders, Please contact Developer in Discord.");
				}
				
			}
			return id;
		}
	}
	
	public List<Identifier> getIdentifiersByMongoDB(MongoDB connection){
		List<Identifier> identifiers = new ArrayList<>();
		for(Identifier identifier : this.getIdentifiersEnabled()) {
			if(identifier.getMongoConnection() != null) {
				if(identifier.getMongoConnection().equals(connection)) {
					identifiers.add(identifier);
				}
			}
		}
		return identifiers;
	}
	
	public List<Identifier> getIdentifiersByRedis(Redis connection){
		List<Identifier> identifiers = new ArrayList<>();
		for(Identifier identifier : this.getIdentifiersEnabled()) {
			if(identifier.getRedisConnection() != null) {
				if(identifier.getRedisConnection().equals(connection)) {
					identifiers.add(identifier);
				}
			}
		}
		return identifiers;
	}
	
	public List<Identifier> getIdentifiersByMySQL(MySQL connection){
		List<Identifier> identifiers = new ArrayList<>();
		for(Identifier identifier : this.getIdentifiersEnabled()) {
			if(identifier.getConnection() != null) {
				if(identifier.getConnection().equals(connection)) {
					identifiers.add(identifier);
				}
			}
		}
		return identifiers;
	}
	
	public List<Identifier> getIdentifiersByMySQLPathID(String pathID) {
		List<Identifier> identifiers = new ArrayList<>();
		for(Identifier identifier : this.getIdentifiersEnabled()) {
			if(identifier.getConnection() != null) {
				if(identifier.getConnection().getPathID().equals(pathID)) {
					identifiers.add(identifier);
				}
			}
		}
		return identifiers;
	}
	
	public List<Identifier> getIdentifiersByRedisPathID(String pathID) {
		List<Identifier> identifiers = new ArrayList<>();
		for(Identifier identifier : this.getIdentifiersEnabled()) {
			if(identifier.getRedisConnection() != null) {
				if(identifier.getConnection().getPathID().equals(pathID)) {
					identifiers.add(identifier);
				}
			}
		}
		return identifiers;
	}
	
	public List<Identifier> getIdentifiersByMongoPathID(String pathID) {
		List<Identifier> identifiers = new ArrayList<>();
		for(Identifier identifier : this.getIdentifiersEnabled()) {
			if(identifier.getMongoConnection() != null) {
				if(identifier.getMongoConnection().getPathID().equals(pathID)) {
					identifiers.add(identifier);
				}
			}
		}
		return identifiers;
	}
	
	
	public boolean areUPLOAD(Identifier i1, Identifier i2) {
		if(i1.getType().equals("UPLOAD") && i2.getType().equals("UPLOAD")) {
			return true;
		}
		return false;
	}
	public Identifier getIdentifierByName(String name) {
		if (existsIdentifier(name)) {
			for (Identifier identifier : getIdentifiersEnabled()) {
				if (identifier.getIdentifierName().equals(name)) {
					return identifier;
				}
			}
		}
		return null;
	}

	public boolean existsIdentifier(String name) {
		for (Identifier identifier : getIdentifiersEnabled()) {
			if (identifier.getIdentifierName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSINGLE_UPLOAD(Identifier identifier) {
		if (identifier.getType().equals("SINGLE_UPLOAD")) {
			return true;
		}
		return false;
	}
	
	public boolean isCOMMUNICATION(Identifier identifier) {
		if (identifier.getType().equals("COMMUNICATION")) {
			return true;
		}
		return false;
	}

	public boolean isUPLOAD(Identifier identifier) {
		if (identifier.getType().equals("UPLOAD")) {
			return true;
		}
		return false;
	}

	public boolean isSINGLE_DOWNLOAD(Identifier identifier) {
		if (identifier.getType().equals("SINGLE_DOWNLOAD")) {
			return true;
		}
		return false;
	}

	public boolean isDOWNLOAD(Identifier identifier) {
		if (identifier.getType().equals("DOWNLOAD")) {
			return true;
		}
		return false;
	}

	public boolean isNONE(Identifier identifier) {
		if (identifier.getType().equals("NONE")) {
			return true;
		}
		return false;
	}
	
	public boolean isValidIdentifier(Identifier identifier) {
		Identifier i = identifier;
		if(this.isSINGLE_DOWNLOAD(i) || this.isSINGLE_UPLOAD(i) || this.isDOWNLOAD(i) || this.isUPLOAD(i) || this.isCOMMUNICATION(i)) {
			return true;
		}
		return false;
	}

}
