package po;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.apache.commons.beanutils.BeanUtils;

/**
 * http://blog.csdn.net/suifeng3051/article/details/39025053
 * 如何把ResultSet转换成Java对象
 * 
 * @author lenovo
 *
 * @param <T>
 */
public class ResultSetMapper<T> {
	@SuppressWarnings("unchecked")
	public List<T> mapRersultSetToObject(ResultSet rs, Class outputClass) throws IOException {
		List<T> outputList = null;
		try {
			// make sure resultset is not null
			if (rs != null) {
				// check if outputClass has 'Entity' annotation
				if (outputClass.isAnnotationPresent(Entity.class)) {
					// get the resultset metadata
					ResultSetMetaData rsmd = rs.getMetaData();
					// get all the attributes of outputClass
					Field[] fields = outputClass.getDeclaredFields();
					while (rs.next()) {
						T bean = (T) outputClass.newInstance();
						for (int _iterator = 0; _iterator < rsmd.getColumnCount(); _iterator++) {
							// getting the SQL column name
							String columnName = rsmd.getColumnName(_iterator + 1);
							// reading the value of the SQL column
							Object columnValue = rs.getObject(_iterator + 1);
							if (columnValue instanceof Clob) {
								String clobStr = "";
								Reader reader = ((Clob) columnValue).getCharacterStream();
								BufferedReader br = new BufferedReader(reader);
								String line = null;
								while ((line = br.readLine()) != null) {
									clobStr += (line + "\n");
								}
								br.close();
								columnValue = clobStr;
							}
							// iterating over outputClass attributes to check if
							// any attribute has 'Column' annotation with
							// matching 'name' value
							for (Field field : fields) {
								if (field.isAnnotationPresent(Column.class)) {
									Column column = field.getAnnotation(Column.class);
									if (column.name().equalsIgnoreCase(columnName) && columnValue != null) {
										BeanUtils.setProperty(bean, field.getName(), columnValue);
										break;
									}
								}
							}
						}
						if (outputList == null) {
							outputList = new ArrayList<T>();
						}
						outputList.add(bean);
					}

				} else {
					// throw some error
				}
			} else {
				return null;
			}
		} catch (

		IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return outputList;
	}
}
