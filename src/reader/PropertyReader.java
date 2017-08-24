package reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

	private static Properties props;

	public PropertyReader(String fileName) {
		/**
		 * @author yxy
		 * @date 2013-3-14 上午10:22:32
		 * @reason 在一个方法多次使用，会出现上一次的值，所以每次调用构造方法时都赋一个新的对象地址
		 */
		props = new Properties();
		loadConfigFile(fileName);

	}

	private void loadConfigFile(String fileName) {
		try {
			InputStream in = new FileInputStream(new File(fileName));
			// InputStream in = getClass().getResourceAsStream("/" + fileName);
			props.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public int size() {
		return props.size();
	}

	public Properties getProperties() {
		return props;
	}

}
