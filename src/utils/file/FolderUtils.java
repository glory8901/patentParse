package utils.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/*
 * 单层文件夹：查看文件，获得文件集合，对比两个文件夹内容
 */
public class FolderUtils {

	public static List<File> getFiles(String path) {
		// 获取该层目录下的所有文件
		List<File> files = new ArrayList<File>();
		File basefile = new File(path);
		File[] subFileArr = basefile.listFiles();
		// System.out.println("该目录下对象个数：" + subFileArr.length);

		for (File file : subFileArr) {
			if (file.isFile()) {
				files.add(file);
			}
		}
		return files;
	}

	public static List<File> getFolders(String path) {
		// 获取该层目录下的所有文件夹
		List<File> folders = new ArrayList<File>();
		File basefile = new File(path);
		File[] subFileArr = basefile.listFiles();
		// System.out.println("该目录下对象个数：" + subFileArr.length);

		for (File file : subFileArr) {
			if (file.isDirectory()) {
				folders.add(file);
			}
		}
		return folders;
	}

	public static List<File> getFiles(List<File> folderList) {
		// 获取一系列文件夹中根目录下的文件
		List<File> outFileList = new ArrayList<File>();
		for (File folder : folderList) {
			String folderName = folder.getAbsolutePath();
			outFileList.addAll(getFiles(folderName));
		}
		return outFileList;
	}

	public static Set<FileProperties> getProperty(String path) {
		// 获取文件夹内所有文件的属性
		Set<FileProperties> foldSet = new HashSet<FileProperties>();
		File basedir = new File(path);
		File[] subfiles = basedir.listFiles();

		for (File f : subfiles) {
			// using FileProperties(obj) to represent (name,time,size)
			FileProperties fs = new FileProperties(f.getName(),
					f.lastModified(), f.length());
			foldSet.add(fs);
		}
		return foldSet;
	}

	public static void compareFolder() {
		// 对比两个文件夹内的文件是否相同(name,time,size)
		File configFile = new File(System.getProperty("user.dir")
				+ "/src/config.xml");
		Document doc = null;
		try {
			doc = Jsoup.parse(configFile, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Elements folders = doc.select("folder");
		Elements paths = folders.select("path");
		String pathEl1 = paths.get(0).text();
		String pathEl2 = paths.get(1).text();

		Set<FileProperties> controlfoldSet = FolderUtils.getProperty(pathEl1);

		File f = new File(pathEl2);
		File[] subfiles = f.listFiles();
		System.out.println("两个文件夹中不相同的文件有：");
		for (File file : subfiles) {
			FileProperties fs = new FileProperties(file.getName(),
					file.lastModified(), file.length());
			if (!controlfoldSet.contains(fs)) {
				System.out.println(file.getName() + "\t" + file.length() / 1024
						+ "KB");
			}
		}

	}
}
