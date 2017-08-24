package test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import po.PatentPo;
import writer.DBReader;
import writer.DBUtil;

public class PrepareData {
	/**
	 * 准备原始的专利数据，是通过Annotation注释来实现数据库映射到java对象的。 使用了ResultSetMapper
	 * 
	 * @param tableName
	 * @param limit
	 * @return
	 */
	public List<PatentPo> testPrepareData(String tableName, int limit) {
		// 获取连接
		Connection conn = DBUtil.getConn();
		DBReader<PatentPo> dbReader = new DBReader<PatentPo>();
		dbReader.setConn(conn);
		dbReader.setTableName(tableName);
		String sql = dbReader.getDefaultSql(limit);// 读取1万条数据
		System.out.println(sql);
		// 开始读取数据，没有使用orm
		List<PatentPo> poList = null;
		try {
			poList = dbReader.readAll();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeAll(null, null, conn);
		}
		return poList;
	}
}
