package entrance;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import reader.ConfigReader;

public class HistoryParser {
	public static void main(String[] args) {
		String confile = null;
		try {
			confile = getCommandLineArgs(args);
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

	public static String getCommandLineArgs(String[] args)
			throws ParseException {
		// Create a Parser
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("h", "help", false, "Print this usage information");
		options.addOption("d", "dir", true, "Folder to put in");
		options.addOption("t", "time", true, "Time to let program sleep");

		// Parse the program arguments
		CommandLine commandLine = parser.parse(options, args);

		// Set the appropriate variables based on supplied options
		if (commandLine.hasOption('h')) {
			System.out
					.println("Help Message: \nuse java -jar xxx.jar -d conf.xml");
			System.exit(0);
		}
		if (commandLine.hasOption('d')) {
			String confXml = commandLine.getOptionValue('d');
			System.out.println("配置文件：" + confXml + "\n");
			return confXml;
		}
		return null;
	}
}
