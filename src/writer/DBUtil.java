package writer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import reader.PropertyReader;

public class DBUtil {
	private static Connection conn;
	private static String driver;
	private static String url;
	private static String username;
	private static String password;

	static {
		PropertyReader reader = new PropertyReader(System.getProperty("user.dir") + "/src/db.properties");
		// 连接Oracle数据库，输入用户名和密码
		driver = reader.getProperty("jdbcDriver");
		url = reader.getProperty("jdbcUrl");
		username = reader.getProperty("jdbcUser");
		password = reader.getProperty("jdbcPasswd");

	}

	public static Connection getConn() {
		try {
			// 加载Oracle驱动
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void closeAll(ResultSet rs, PreparedStatement ps, Connection conn) {
		try {
			if (rs != null) { // 关闭记录集
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) { // 关闭声明
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) { // 关闭连接对象
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
