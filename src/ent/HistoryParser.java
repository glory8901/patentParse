package ent;

import reader.ConfigReader;

public class HistoryParser {
	public static void main(String[] args) {
		ConfigReader reader = new ConfigReader();
		try {
			reader.loadConfig("kr_st96.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
