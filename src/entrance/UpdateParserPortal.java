package entrance;

import reader.ConfigReader;

public class UpdateParserPortal {
	public static void main(String[] args) {
		ConfigReader reader = new ConfigReader();
		try {
			reader.loadConfig("sequence_config.xml");
			// reader.loadConfig("test.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
