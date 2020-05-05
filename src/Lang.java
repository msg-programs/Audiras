import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class Lang {
	
	
	private static final File LANG = new File(System.getenv("APPDATA") + "/Audiras/" + Settings.getLang() + ".txt");
	private static HashMap<String, String> trans = new HashMap<>();
//	private static HashMap<String, String> backup = new HashMap<>();
	
	public static void init() {
		String line=null;
		try {
			BufferedReader bfr = new BufferedReader(new FileReader(LANG));

			
			while ((line = bfr.readLine()) != null) {
				trans.put(line.split("=")[0], line.split("=")[1]);
//				System.out.println("backup.put(\"" +line.split("=")[0]+"\", \""+line.split("=")[1]+"\");" );
			}

			bfr.close();
		} catch (Exception e) {
			
			System.err.println(line);
//			e.printStackTrace();
			
		}
		
	}
	
	public static String get(String key) {
		String r = trans.get(key);
		if (!trans.containsKey(key))System.err.println("Can't find " + key + " in translation!");
		return r;
	}
}
