package utils;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ConfigReader {

	public static String loadconfig(String label) {
		//fastcopy
		File configFile = new File(System.getProperty("user.dir")
				+ "/config/config.xml");
		Document doc = null;

		try {
			doc = Jsoup.parse(configFile, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Elements folders = doc.select("fastcopy");
		String content = folders.select(label).text();
		return content;
	}

	public static String tocommand(String mode, String source, String destdir) {
		String fcmode = ConfigReader.loadconfig(mode);
		String cmd = fcmode + " \"" + source + "\"" + " /to=\"" + destdir
				+ "\"\r\n";
		return cmd;
	}
}
