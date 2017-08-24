package utils.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.output.FileWriterWithEncoding;

public class LogUtils {

	public static void checkLogDir() {
		File log = new File("log");
		if (!log.exists()) {
			log.mkdirs();
		} else if (log.isFile()) {
			log.delete();
			log.mkdirs();
		}
	}

	public static void addErrlog(String msg) {
		addlog(msg, "log/parse-error.log", true);
	}

	public static void addlog(String msg, String logpath, boolean append) {
		File file = new File(logpath);
		try {
			FileWriterWithEncoding writer = new FileWriterWithEncoding(file, "utf-8", append);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = df.format(new Date());
			writer.write(date + "\t" + msg + "\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
