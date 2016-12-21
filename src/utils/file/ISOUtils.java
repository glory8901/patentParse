package utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ISOUtils {
	public static void openISO(String mountisodir,String isopath) {
		String cmd = String.format(mountisodir + "\\DTAgent.exe -mount dt,0,\"%s\"", isopath);
		execCMD(cmd);
	}

	public static void closeISO(String mountisodir) {
		String cmd = mountisodir + "\\DTAgent.exe -unmount_all";
		execCMD(cmd);
	}

	public static boolean execCMD(String cmd) {
		try {
			File exedir = new File("C:\\Program Files\\DAEMON Tools Lite");
			Process proc = Runtime.getRuntime().exec(cmd);
			// 若cmd命令行输出太多，容易阻塞，需要用到这个
			InputStream is = proc.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {

			}
			proc.waitFor();

			if (proc.waitFor() == 0) {
				if (proc.exitValue() == 0)
					return true;
			}
			is.close();
			reader.close();
			proc.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
