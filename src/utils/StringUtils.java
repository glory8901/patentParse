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
		String[] lineArr = lineList.toArray(new String[lineList.size()]);
		String out = join(lineArr, sep, mark);
		return out;
	}

	public static String addmark(String value, Character mark) {
		// such as quotation mark("")
		if (mark == null) {
			return value;
		}
		return mark + value + mark;
	}

	public static String join(String[] itemArr, String sep) {
		// same as python join
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < itemArr.length; i++) {
			if (i < itemArr.length - 1) {
				sb.append(itemArr[i] + sep);
			} else {
				sb.append(itemArr[i]);
			}
		}
		return sb.toString();
	}

	public static String join(List<String> itemArr, String sep) {
		// same as python join
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < itemArr.size(); i++) {
			if (i < itemArr.size() - 1) {
				sb.append(itemArr.get(i) + sep);
			} else {
				sb.append(itemArr.get(i));
			}
		}
		return sb.toString().trim();
	}
}
