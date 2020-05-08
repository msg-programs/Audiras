package streamlogic;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class StreamMeta {

	public String name = "";
	public String genre = "";
	public String bitrate = "";
	public String url = "";
	public String format = "";
	public int metaInt = -1;

	public String error = null;

	public StreamMeta(String url) {

		this.url = url;

		Map<String, List<String>> res = null;

		try {
			URLConnection conn = new URL(url).openConnection();
			conn.setRequestProperty("Icy-MetaData", "1");
			conn.setRequestProperty("Connection", "close");

			res = conn.getHeaderFields();

			conn.getInputStream().close(); // hm

		} catch (IOException e) {
			System.err.println(url + " doesn't seem to be valid or timed out!");
			error = "Connection error!";
		}
		
		// god dammit
		try {
		if (res.get("Content-Type") == null) {
			format = res.get("content-type").get(0);
		} else {
			format = res.get("Content-Type").get(0);
		}

		if (!format.equals("audio/mpeg")) {
			error = "Format not (yet) supported!";
		}

		name = res.get("icy-name").get(0);
		genre = res.get("icy-genre").get(0);
		bitrate = res.get("icy-br").get(0);
		try {
			metaInt = Integer.parseInt(res.get("icy-metaint").get(0));
		} catch (NumberFormatException e) {
			error = "No metaint was send!";
		}
//		System.out.println("=== METADATA ===");
//		System.out.println("name: " + name);
//		System.out.println("format: " + format);
//		System.out.println("genre: " + genre);
//		System.out.println("bitrate: " + bitrate);
//		System.out.println("metaint: " + metaInt);
		} catch (Exception e) {
			System.err.println("Error while parsing metadata!");
			error = "Invalid metadata!";
		}
	}

}
