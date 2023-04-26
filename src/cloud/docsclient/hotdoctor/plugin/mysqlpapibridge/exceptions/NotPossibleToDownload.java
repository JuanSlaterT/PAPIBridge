package cloud.docsclient.hotdoctor.plugin.mysqlpapibridge.exceptions;

@SuppressWarnings("serial")
public class NotPossibleToDownload extends RuntimeException{
	
	public NotPossibleToDownload(String msg) {
		super(msg);
	}

}
