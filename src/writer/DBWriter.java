package writer;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import utils.log.LogUtils;

public class DBWriter<T> {
	private List<String> fieldList;
	private Connection conn;
	private String tableName;
	private String psSql;
	private T xmlPo;
	private int successCount;
	private int failCount;

	public String getPsSql() {
		psSql = genInsSql(tableName, true);
		return psSql;
	}

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

	public T getXmlPo() {
		return xmlPo;
	}

	public void setXmlPo(T xmlPo) {
		this.xmlPo = xmlPo;
	}

	public List<String> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}
	
	

	public String getProperty(String field) {
		String value = "";
		try {
			value = BeanUtils.getSimpleProperty(xmlPo, field);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 生成sql语句，可选择是否使用PS
	 * 
	 * @param tableName
	 * @param usePs
	 * @return
	 */
	public String genInsSql(String tableName, boolean usePs) {
		String fields = "";
		String values = "";

		for (int i = 0; i < fieldList.size(); i++) {
			String field = fieldList.get(i);
			String value = "?";

			if (!usePs) {
				value = getProperty(field);
			}
			if (i < fieldList.size() - 1) {
				fields += (field + ",");
				values += (value + ",");
			} else {
				fields += field;
				values += value;
			}
		}
		String sql = String.format("insert into %s(%s) values(%s)", tableName, fields, values);
		return sql;
	}

	public void insert() throws SQLException {
		PreparedStatement ps = null;
		ps = conn.prepareStatement(psSql);
		for (int i = 0; i < fieldList.size(); i++) {
			String field = fieldList.get(i);
			String value = getProperty(field);
			ps.setString(i + 1, value);
		}
		try {
			ps.executeUpdate();
			successCount++;
		} catch (Exception e) {
			failCount++;
			String path = getProperty(fieldList.get(fieldList.size() - 1));
			System.out.println("ERROR:" + path);
			LogUtils.addErrlog(path);
			e.printStackTrace();
		}finally{
			DBUtil.closeAll(null, ps, null);
		}
	}

}
