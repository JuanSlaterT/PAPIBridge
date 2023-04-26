package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions;

@SuppressWarnings("serial")
public class ServerWithoutIdentity extends Exception{
	
	public ServerWithoutIdentity(String msg) {
		super(msg);
	}

}
