package utils.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FilesFilter {

	public static List<String> filterExt(List<String> fileList,
			String containsExt, String excludeExt) {
		// 筛选文件的扩展名
		List<String> out = new ArrayList<String>();
		for (String fileName : fileList) {
			if (new File(fileName).isFile()) {
				// 是否保留与是否删除
				boolean shouldRetain = containsExtension(fileName, containsExt);
				boolean shouldExclude = excludeExtension(fileName, excludeExt);
				if (shouldRetain && !shouldExclude) {
					out.add(fileName);
				}
			}
		}
		return out;
	}

	public static boolean contains(String fileName, String containsName) {
		// 判断是否为包含
		// if (containsName == null || "".equals(containsName)) {
		// return true;
		// }
		// 如果不为空串
		String[] containArr = StringUtils.split(containsName, ";");
		for (String keyword : containArr) {
			if (fileName.toLowerCase().contains(keyword.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean exclude(String fileName, String excludeName) {
		// 判断是否排除
		// if (excludeName == null || "".equals(excludeName)) {
		// return false;
		// }
		// 如果不为空串
		String[] excludeArr = StringUtils.split(excludeName, ";");
		for (String exd : excludeArr) {
			if (fileName.toLowerCase().contains(exd.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsExtension(String fileName, String Extensions) {
		// 判断是否为包含
		if (Extensions == null || "".equals(Extensions)) {
			return true;
		}
		// 如果不为空串
		String[] containArr = Extensions.split(";");
		for (String ext : containArr) {
			if (fileName.toLowerCase().endsWith(ext.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean excludeExtension(String fileName, String Extensions) {
		// 判断是否排除
		if (Extensions == null || "".equals(Extensions)) {
			return false;
		}
		// 如果不为空串
		String[] excludeArr = Extensions.split(";");
		for (String ext : excludeArr) {
			if (fileName.toLowerCase().endsWith(ext.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
