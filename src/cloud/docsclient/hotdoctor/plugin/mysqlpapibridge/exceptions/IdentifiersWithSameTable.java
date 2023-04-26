package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions;

@SuppressWarnings("serial")
public class IdentifiersWithSameTable extends Exception{
	public IdentifiersWithSameTable(String msg) {
		super(msg);
	}

}
