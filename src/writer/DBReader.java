package writer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import po.PatentPo;
import po.ResultSetMapper;


public class DBReader<T> {
	private Connection conn;
	private String tableName;
	private String readSql;

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getReadSql() {
		return readSql;
	}

	public void setReadSql(String readSql) {
		this.readSql = readSql;
	}

	public String getDefaultSql(int limit) {
		readSql = String.format("select * from %s where rownum <= %d", tableName, limit);
		return readSql;
	}

	public List<T> readAll() throws SQLException {
		PreparedStatement ps = null;
		ps = conn.prepareStatement(readSql);
		ResultSet rs = null;
		List<T> poList = null;
		try {
			rs = ps.executeQuery();
			ResultSetMapper<T> rsm = new ResultSetMapper<T>();
			poList = rsm.mapRersultSetToObject(rs, PatentPo.class);
			return poList;
		} catch (Exception e) {
			System.out.println("==============ERROR: DB=================");
			e.printStackTrace();
		} finally {
			DBUtil.closeAll(rs, ps, null);
		}
		return null;
	}

}
