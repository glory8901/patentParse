package po;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

public class XMLPoUtil<T> {
	private List<String> fieldList;
	
	
	public XMLPoUtil(List<String> fieldList) {
		super();
		this.fieldList = fieldList;
	}

	public List<String> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}

	public T putDataToPo(List<String> valueList, Class outputClass) throws Exception {
		// 全部加入完毕，生成一个新的对象
		T bean = (T) outputClass.newInstance();
		for (int i = 0; i < valueList.size(); i++) {
			BeanUtils.setProperty(bean, fieldList.get(i), valueList.get(i));
		}
		return bean;
	}

	public List<T> convertToPos(List<List<String>> xmlList) {
		List<T> poList = new ArrayList<T>();
		for (List<String> valuelist : xmlList) {
			try {
				T bean = putDataToPo(valuelist, PatentPo.class);
				poList.add(bean);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return poList;
	}
}
