package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import typeobj.Filter;
import utils.file.FilesFilter;
import utils.folder.FolderRecursion;
import utils.folder.FolderUtils;
import utils.folder.MyFileVisitor;

public class FileSearcher {

	private Map<String, String> typeOutNameMap;
	private Map<String, List<String>> typeOutFilesMap;

	public FileSearcher() {
		super();
		this.typeOutNameMap = new HashMap<String, String>();// 存放结果
		this.typeOutFilesMap = new HashMap<String, List<String>>();
	}

	public Map<String, String> getTypeOutNameMap() {
		return typeOutNameMap;
	}

	public void setTypeOutNameMap(Map<String, String> typeOutNameMap) {
		this.typeOutNameMap = typeOutNameMap;
	}

	public Map<String, List<String>> getTypeOutFilesMap() {
		return typeOutFilesMap;
	}

	public void setTypeOutFilesMap(Map<String, List<String>> typeOutFilesMap) {
		this.typeOutFilesMap = typeOutFilesMap;
	}

	/**
	 * 文件搜索模块 读取配置,搜索文件
	 * 
	 * @param fileSearchArgs
	 * @param base
	 * @return
	 */
	public void search(Element fileSearchArgs, Elements base) {
		// 存放结果

		// 读取配置文件中：搜索文件的条件，并返回搜索结果
		String followpath = fileSearchArgs.select("AddPath").get(0).ownText()
				.trim();
		String country = fileSearchArgs.select("AddPath").get(0).attr("class");

		// search dir:如果是相对路径就添加，否则就直接用绝对路径
		String fullPath = "";
		if (followpath.startsWith("\\")) {
			fullPath = base.select("basedir." + country).get(0).ownText()
					+ followpath;// 获取base模块中存储的路径
		} else if ("".equals(followpath)) {
			fullPath = base.select("basedir." + country).get(0).ownText()
					.trim();
		} else {
			fullPath = followpath;
		}
		System.out.println("当前搜索：" + fullPath);

		// 遍历并筛选
		try {
			Map<String, Filter> filterMap = sepFilter(fileSearchArgs);
			walkAndFilter(fullPath, fileSearchArgs, filterMap);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 使用新的遍历规则较快
	 * 
	 * @param fullPath
	 * @param fileSearchArgs
	 * @param map
	 * @throws IOException
	 */
	public void walkAndFilter(String fullPath, Element fileSearchArgs,
			Map<String, Filter> map) throws IOException {
		// walk
		Elements extEles = fileSearchArgs.select("extend");
		List<String> extensionList = new ArrayList<String>();
		for (Element ele : extEles) {
			String ext = ele.ownText();
			extensionList.add(ext);
		}

		// 获取文件列表
		List<String> fileList = getFileList(fullPath, extensionList);
		for (String ext : extensionList) {
			Filter ft = map.get(ext);
			// 筛选文件和文件夹
			List<String> allExtfile = FilesFilter
					.filterExt(fileList, ext, null);
			// 筛选文件所在的文件夹路径
			List<String> fileCleared = FilesFilter.filter(allExtfile, true,
					ft.getIncdirName(), ft.getExcdirName());
			// 筛选文件名
			fileCleared = FilesFilter.filter(fileCleared, false,
					ft.getIncfileName(), ft.getExcfileName());
			typeOutNameMap.put(ext, ft.getOutName());
			typeOutFilesMap.put(ext, fileCleared);

		}

	}

	public Map<String, Filter> sepFilter(Element fileSearchArgs)
			throws IOException {
		Map<String, Filter> filterMap = new HashMap<String, Filter>();

		Elements filterArgs = fileSearchArgs.select("filter");
		for (Element filterArg : filterArgs) {
			// filter: 筛选文件夹的条件
			String incdirs = filterArg.select("incdir").get(0).ownText();
			String excdirs = filterArg.select("excdir").get(0).ownText();
			String incfiles = filterArg.select("incfile").get(0).ownText();
			String excfiles = filterArg.select("excfile").get(0).ownText();
			String ext = filterArg.select("extend").get(0).ownText();
			String outname = filterArg.select("outfile").get(0).ownText();

			Filter ft = new Filter(incdirs, excdirs, incfiles, excfiles, ext,
					outname);
			filterMap.put(ext, ft);
		}
		return filterMap;
	}

	/**
	 * 遍历文件夹
	 * @param basedir
	 * @param extList
	 * @return
	 * @throws IOException
	 */
	public List<String> getFileList(String basedir, List<String> extList)
			throws IOException {
		if (!new File(basedir).exists()) {
			throw new FileNotFoundException(basedir + " 不存在");
		}
		Path fileDir = Paths.get(basedir);
		//查找这两个扩展名的文件
		MyFileVisitor visitor = new MyFileVisitor(extList);
		// walk
		Files.walkFileTree(fileDir, visitor);
		// result
		List<String> fileList = visitor.getFileList();
		return fileList;
	}

	/**
	 * 目前没有用这个方法
	 * 
	 * @param fullPath
	 * @param fileSearchArgs
	 */
	public void walkAndFilter(String fullPath, Element fileSearchArgs) {
		// 遍历得到所有的文件夹
		List<String> folderList;
		try {
			folderList = FolderRecursion.getAllFoldersList(fullPath);
		} catch (FileNotFoundException e) {
			System.err.println("搜索的根路径：" + e.getMessage());
			return;
		}

		// 筛选文件夹和文件
		filterDir(folderList, fileSearchArgs);
	}

	/**
	 * 先遍历文件夹并筛选，然后遍历当前目录下的文件并筛选 目前没有用这个方法
	 * 
	 * @param folderList
	 * @param fileSearchArgs
	 */
	public void filterDir(List<String> folderList, Element fileSearchArgs) {

		// filter: 筛选文件夹的条件
		String incdirs = fileSearchArgs.select("incdir").get(0).ownText();
		String excdirs = fileSearchArgs.select("excdir").get(0).ownText();
		String incfiles = fileSearchArgs.select("incfile").get(0).ownText();
		String excfiles = fileSearchArgs.select("excfile").get(0).ownText();
		String extensions = fileSearchArgs.select("extend").get(0).ownText();
		String outfiles = fileSearchArgs.select("outfile").get(0).ownText();

		// 每种类型的文件输出一个
		String[] outfileArr = outfiles.split("\\|");// 对应上面的多个组，出来是多个文件集合
		String[] incdirArr = incdirs.split("\\|");// 多个组，出来也是多个文件集合
		String[] excdirArr = excdirs.split("\\|");// 多个组，出来也是多个文件集合
		String[] incfileArr = incfiles.split("\\|");// 多个组，出来也是多个文件集合
		String[] excfileArr = excfiles.split("\\|");// 多个组，出来也是多个文件集合
		String[] extensionArr = extensions.split("\\|");// 多个组，出来也是多个文件集合

		// 存放遍历后的文件夹筛选后的结果
		List<String> folderCleared = new ArrayList<String>();

		// 开始筛选文件夹
		for (int i = 0; i < outfileArr.length; i++) {
			String incdir = null;
			if (incdirArr.length >= 2) {
				incdir = incdirArr[i];

			}
			String excdir = null;
			if (excdirArr.length >= 2) {
				excdir = excdirArr[i];
			}
			String outname = outfileArr[i];
			// 如果包含多层筛选，则多次筛选包含的字符串
			if (incdir != null && incdir.contains("&")) {
				String[] repeatIncFilter = incdir.split("&");
				for (String oneinc : repeatIncFilter) {
					folderList = FilesFilter.filter(folderList, oneinc, excdir);
				}
				folderCleared = folderList;
			} else {
				folderCleared = FilesFilter.filter(folderList, incdir, excdir);
			}

			// 获得筛选后的文件夹中的所有文件
			List<String> allfiles = FolderUtils.getFiles(folderCleared);
			String ext = null;
			String incfs = null;
			String excfs = null;

			if (extensionArr.length >= 2) {
				ext = extensionArr[i];
			}
			if (incfileArr.length >= 2) {
				incfs = incfileArr[i];
			}
			if (excfileArr.length >= 2) {
				excfs = excfileArr[i];
			}
			// 筛选扩展名和关键词
			List<String> allExtfile = FilesFilter
					.filterExt(allfiles, ext, null);
			List<String> fileCleared = FilesFilter.filter(allExtfile, incfs,
					excfs);
			typeOutNameMap.put(ext, outname);
			typeOutFilesMap.put(ext, fileCleared);
		}
	}

}
