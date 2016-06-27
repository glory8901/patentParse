package utils;

import java.util.List;

public class StringUtils {

	public static String join(String[] lineArr, String sep, Character mark) {
		// same as python join
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < lineArr.length; i++) {
			if (i < lineArr.length - 1) {
				sb.append(addmark(lineArr[i], mark) + sep);
			} else {
				sb.append(addmark(lineArr[i], mark));
			}
		}
		return sb.toString();
	}
	
	public static String join(List<String> lineList, String sep, Character mark) {
		String[] lineArr = lineList.toArray(new String[lineList.size()] );
		String out = join(lineArr,sep,mark);
		return out;
	}
	
	public static String addmark(String value, Character mark){
		// such as quotation mark("")
		if(mark == null){
			return value;
		}
		return mark + value + mark;
	}
}
