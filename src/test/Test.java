package test;

import java.io.IOException;
import java.util.Properties;

public class Test {
	public static void main(String[] args) {
		/**
		 * 创建Properties对象
		 */
		Properties properties = new Properties();
		try {
			/**
			 * 根据配置文件的位置进行读取数据，由于现在.properties文件在src目录下，所以直接
			 * 用Test.class.getClassLoader().getResourceAsStream("observer.properties")就可以获取到配置文件的信息
			 */
			properties.load(Test.class.getClassLoader().getResourceAsStream("observer.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * 将配置文件中的信息进行分割，返回成一个数组
		 */
		String[] observers = properties.getProperty("observers").split(",");
		for (String s : observers) {
			try {
				/**
				 * 根据数组的内容创建对象，使用Class.forName(s).newInstance()
				 */
				Class.forName(s).newInstance();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}
