package utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineManger {
	public static String getCommandLineArgs(String[] args) throws ParseException {
		// Create a Parser
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("h", "help", false, "Print this usage information");
		options.addOption("c", "configure", true, "conf xml to put in");

		// Parse the program arguments
		CommandLine commandLine = parser.parse(options, args);

		// Set the appropriate variables based on supplied options
		if (commandLine.hasOption('h')) {
			System.out.println("Help Message: \nuse java -jar xxx.jar -d conf.xml");
			System.exit(0);
		}
		if (commandLine.hasOption('c')) {
			String confXml = commandLine.getOptionValue('c');
			System.out.println("配置文件：" + confXml + "\n");
			return confXml;
		}
		return null;
	}
}
