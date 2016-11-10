package utils.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesFilter {

	public static List<File> filter(List<File> fileList, String containsName,
			String excludeName) {
		// 筛选文件或文件夹
		List<File> out = new ArrayList<File>();
		for (File file : fileList) {
			String fileName = file.getAbsolutePath();
			// 是否保留与是否删除
			boolean shouldRetain = contains(fileName, containsName);// 或的关系，包含其中一个即可
			boolean shouldExclude = exclude(fileName, excludeName);// 与的关系，出现在列表中的都要排除
			if (shouldRetain && !shouldExclude) {
				out.add(file);
			}
		}
		return out;
	}

	public static List<String> filter(List<String> fileList, String containsName) {
		// 筛选文件或文件夹
		List<String> out = new ArrayList<String>();
		for (String fileName : fileList) {
			// 是否保留与是否删除
			boolean shouldRetain = contains(fileName, containsName);// 或的关系，包含其中一个即可
			if (shouldRetain) {
				out.add(fileName);
			}
		}
		return out;
	}

	public static List<File> filterExt(List<File> fileList, String containsExt,
			String excludeExt) {
		// 筛选文件的扩展名
		List<File> out = new ArrayList<File>();
		for (File file : fileList) {
			if (file.isFile()) {
				String fileName = file.getName();
				// 是否保留与是否删除
				boolean shouldRetain = containsExtension(fileName, containsExt);
				boolean shouldExclude = excludeExtension(fileName, excludeExt);
				if (shouldRetain && !shouldExclude) {
					out.add(file);
				}
			}
		}
		return out;
	}

	public static boolean contains(String fileName, String containsName) {
		// 判断是否为包含
		if (containsName == null || "".equals(containsName)) {
			return true;
		}
		// 如果不为空串
		String[] containArr = containsName.split("\\*");
		for (String keyword : containArr) {
			if (fileName.toLowerCase().contains(keyword.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean exclude(String fileName, String excludeName) {
		// 判断是否排除
		if (excludeName == null || "".equals(excludeName)) {
			return false;
		}
		// 如果不为空串
		String[] excludeArr = excludeName.split("\\*");
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
		String[] containArr = Extensions.split("\\*");
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
		String[] excludeArr = Extensions.split("\\*");
		for (String ext : excludeArr) {
			if (fileName.toLowerCase().endsWith(ext.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
