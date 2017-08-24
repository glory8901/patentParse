package entrance;

import org.apache.commons.cli.ParseException;

import reader.ConfigReader;
import utils.CommandLineManger;

public class MyParser {
	public static void main(String[] args) {
		String confile = null;
		try {
			confile = CommandLineManger.getCommandLineArgs(args);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		// 开始读取配置
		ConfigReader reader = new ConfigReader();
		try {
			if (confile != null) {
				reader.loadConfig(confile);
			} else {
				System.err.println("找不到配置文件");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
