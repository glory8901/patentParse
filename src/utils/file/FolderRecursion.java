package utils.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * 描述：遍历文件夹
 * Method:递归。
 * 功能：包括创建多层文件夹目录，遍历文件及文件夹，遍历得到所有文件的集合。
 */
public class FolderRecursion {

	public static void copydirs(String basePath, String targetPath) {
		// 复制文件夹，不复制内容
		mkdirs(basePath, basePath, targetPath);
	}

	public static void mkdirs(String basePath, String baseroot, String destroot) {
		// 仅创建文件夹，不复制内容(not use)
		String targetPath = basePath.replace(baseroot, destroot);
		File targetFile = new File(targetPath);

		if (!targetFile.exists()) {
			targetFile.mkdir();
		}
		File[] subFiles = new File(basePath).listFiles();
		for (File subFile : subFiles) {
			if (subFile.isDirectory()) {
				mkdirs(subFile.getAbsolutePath(), baseroot, destroot);
			}
		}
	}

	public static void traverse(String path) {
		// 查看该文件夹下层的所有文件和文件夹
		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				System.out.println("文件夹为空");
				return;
			} else {
				for (File file2 : files) {
					if (file2.isHidden()) {
						System.out.println("隐藏文件夹:" + file2.getName());
					}
					if (file2.isDirectory()) {
						System.out.println("文件夹:" + file2.getAbsolutePath());
						traverse(file2.getAbsolutePath());
					} else {
						System.out.println("文件:" + file2.getAbsolutePath());
					}
				}
			}
		} else {
			System.out.println("文件夹不存在!");
		}
	}

	public static List<File> getAllFoldersList(String folderPath) throws FileNotFoundException {
		// 遍历文件夹，获得子文件夹的列表
		File basePath = new File(folderPath);
		// 如果搜索的文件夹不存在，则直接返回
		if(!basePath.exists()){
			throw new FileNotFoundException(folderPath + " 不存在");
		}
		List<File> folderList = new ArrayList<File>();
		// add root path
		folderList.add(basePath);
		// walk
		walkForFolders(folderPath, folderList);
		return folderList;
	}

	public static void walkForFolders(String path, List<File> folderList) {
		// 遍历得到文件夹中的所有子文件夹
		File root = new File(path);
		if (root.exists()) {
			File[] rootFiles = root.listFiles();
			if (rootFiles.length == 0) {
				return;
			} else {
				for (File file : rootFiles) {
					if (file.isDirectory()) {
						folderList.add(file);
						walkForFolders(file.getAbsolutePath(),folderList);
					}
				}
			}
		}
	}
	
	public static List<File> getAllFilesList(String folderPath) {
		// 遍历文件夹，获得所有文件的列表
		File basePath = new File(folderPath);
		List<File> fileList = new ArrayList<File>();
		// add root path
		fileList.add(basePath);
		// walk
		walkForFiles(folderPath, fileList);
		return fileList;
	}
	
	public static void walkForFiles(String path, List<File> fileList) {
		// 遍历得到文件夹中所有的文件
		File root = new File(path);
		if (root.exists()) {
			File[] rootFiles = root.listFiles();
			if (rootFiles.length == 0) {
				return;
			} else {
				for (File file : rootFiles) {
					// 只添加文件
					if (file.isFile()) {
						fileList.add(file);
						walkForFolders(file.getAbsolutePath(),fileList);
					}
				}
			}
		}
	}

	public static List<File> getExtendFileList(String strPath,
			String extendNames) {
		// 得到所有以指定扩展名结尾的文件，其中extendNames以*分隔
		List<File> fileList = new ArrayList<File>();
		String[] extendnameArr = extendNames.split("\\*");
		getFileList(strPath, extendnameArr, fileList);
		return fileList;
	}

	public static void getFileList(String strPath, String[] extendnameArr,
			List<File> fileList) {
		// 获取该路径下的指定扩展名的所有文件
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (files[i].isDirectory()) {
					getFileList(files[i].getAbsolutePath(), extendnameArr,
							fileList);
				} else {
					for (String extname : extendnameArr) {
						if (fileName.toLowerCase().endsWith(
								extname.toLowerCase())) {
							fileList.add(files[i]);
							break;
						}
					}
				}
			}
		}
	}

	public static List<File> getFileNameList(String strPath, String strFileName) {
		// 查找特定名字的文件
		List<File> fileList = new ArrayList<File>();
		getFileList(strPath, strFileName.toLowerCase(), fileList);
		return fileList;
	}

	public static void getFileList(String strPath, String strFileName,
			List<File> fileList) {
		// 获取该路径下的文件名中包含"strFileName"的所有文件
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				String strFilePath = files[i].getAbsolutePath();
				if (files[i].isDirectory()) {
					getFileList(strFilePath, strFileName, fileList);
				} else if (fileName.toLowerCase().contains(strFileName)) {
					// System.out.println("符合条件的文件---" + strFilePath);
					fileList.add(files[i]);
				} else {
					continue;
				}
			}

		}
	}

	public static double getDirSize(File file) {
		// 获取文件夹的容量
		// 判断文件是否存在
		if (file.exists()) {
			// 如果是目录则递归计算其内容的总大小
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				double size = 0;
				for (File f : children)
					size += getDirSize(f);
				return size;
			} else {// 如果是文件则直接返回其大小,以“兆”为单位
				double size = (double) file.length() / 1024 / 1024;
				return size;
			}
		} else {
			System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
			return 0.0;
		}
	}
}
