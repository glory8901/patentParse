package fiveIPOs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.XMLReader;
import utils.file.FileUtils;
import utils.file.FilesFilter;
import utils.file.FolderRecursion;
import utils.file.FolderUtils;

public class ConfigReader {
	public void loadConfig(String confxml) throws Exception {
		// 读取号单的配置，从而分别处理每一个配置
		File configFile = new File(System.getProperty("user.dir") + "/conf/"
				+ confxml);
		Document doc = null;

		try {
			doc = Jsoup.parse(configFile, "utf-8");
		} catch (IOException e) {
			System.out.println("read config xml fail.");
			e.printStackTrace();
		}
		// 选取xml中的base部分的标签
		Elements base = doc.select("baseroot");
		String destDir = doc.select("baseroot>destdir").text();

		// 选择branch的各个部分
		Elements branchs = doc.select("branch");
		for (Element branch : branchs) {
			Element fileSearchArgs = branch.select("file-search").get(0);
			Element textReadArgs = branch.select("text-read").get(0);

			Map<String, List<File>> map = getFileArgs(fileSearchArgs, base);
			if (map == null) {
				continue;
			}
			readfile(map, textReadArgs, destDir);
		}
	}

	public static Map<String, List<File>> getFileArgs(Element fileSearchArgs,
			Elements base) {
		/*
		 * 文件搜索模块 读取配置，返回{outname:List<File>}
		 */

		// 存放结果
		Map<String, List<File>> outNameAndfilesMap = new HashMap<String, List<File>>();// 存放结果

		// 读取配置文件中：搜索文件的条件，并返回搜索结果
		String followpath = fileSearchArgs.select("follow-path").get(0)
				.ownText();
		String country = fileSearchArgs.select("follow-path").get(0)
				.attr("class");
		String incdirs = fileSearchArgs.select("incdir").get(0).ownText();
		String excdirs = fileSearchArgs.select("excdir").get(0).ownText();
		String incfiles = fileSearchArgs.select("incfile").get(0).ownText();
		String excfiles = fileSearchArgs.select("excfile").get(0).ownText();
		String extend = fileSearchArgs.select("extend").get(0).ownText();
		String outfiles = fileSearchArgs.select("outfile").get(0).ownText();

		// search dir:如果是相对路径就添加，否则就直接用绝对路径
		String fullPath = "";
		if (followpath.startsWith("\\")) {
			fullPath = base.select("basedir." + country).get(0).ownText()
					+ followpath;
		} else {
			fullPath = followpath;
		}

		// filter: 筛选文件夹的条件
		String[] incdirArr = incdirs.split(";");// 多个组，出来也是多个文件集合
		String[] outfileArr = outfiles.split(";");// 对应上面的多个组，出来是多个文件集合

		// 遍历得到所有的文件夹，并筛选
		List<File> folderList;
		try {
			folderList = FolderRecursion.getAllFoldersList(fullPath);
		} catch (FileNotFoundException e) {
			System.err.println("搜索的根路径：" + e.getMessage());
			for (String outname : outfileArr) {
				outNameAndfilesMap.put(outname, null);
			}
			return outNameAndfilesMap;
		}
		// 存放遍历后的文件夹筛选后的结果
		List<File> folderCleared = new ArrayList<File>();

		// 开始筛选文件夹
		for (int i = 0; i < outfileArr.length; i++) {
			String incdir = incdirArr[i];
			String outname = outfileArr[i];
			// 如果包含多层筛选，则多次筛选包含的字符串
			if (incdir.contains("&")) {
				String[] repeatIncFilter = incdir.split("&");
				for (String oneinc : repeatIncFilter) {
					folderList = FilesFilter
							.filter(folderList, oneinc, excdirs);
				}
				folderCleared = folderList;
			} else {
				folderCleared = FilesFilter.filter(folderList, incdir, excdirs);
			}

			// 获得筛选后的文件夹中的所有文件
			List<File> allfiles = FolderUtils.getFiles(folderCleared);
			// 筛选扩展名和关键词
			List<File> allExtfile = FilesFilter.filterExt(allfiles, extend,
					null);
			List<File> fileCleared = FilesFilter.filter(allExtfile, incfiles,
					excfiles);
			outNameAndfilesMap.put(outname, fileCleared);
			// 查错
			// if(fileCleared.size() > 0){
			// System.out.println(fullPath+ "--"+ incdir
			// +"--"+outname+"--"+fileCleared.get(0));
			// }else{
			// System.out.println(fullPath+ "--"+ incdir +"--"+outname+"--" +
			// fileCleared.size());
			// }
		}
		return outNameAndfilesMap;
	}

	public static void readfile(Map<String, List<File>> map, Element readArgs,
			String destDir) throws Exception {
		String filetype = readArgs.select("filetype").text();
		if (filetype.toLowerCase().equals("xml")) {
			System.out.println("开始读取xml");
			readXML(map, readArgs, destDir);
			System.out.println("结束读取xml\n");
		} else if (filetype.toLowerCase().equals("sgm")) {
			System.out.println("开始读取sgm");
			readSGM(map, readArgs, destDir);
			System.out.println("结束读取sgm\n");
		} else if (filetype.toLowerCase().equals("lst")) {
			System.out.println("开始读取lst");
			readTXT(map, readArgs, destDir);
			System.out.println("结束读取lst\n");
		} else if (filetype.toLowerCase().equals("path")) {
			System.out.println("开始读取path");
			readPATH(map, readArgs, destDir);
			System.out.println("结束读取path\n");
		}
	}

	public static void readPATH(Map<String, List<File>> map, Element readArgs,
			String destDir) throws Exception {
		String header = readArgs.select("header").get(0).ownText();
		String encodingout = readArgs.select("encoding-out").get(0).ownText();

		StringBuilder sb = new StringBuilder();
		sb.append(header + "\r\n");
		for (String key : map.keySet()) {
			String outname = destDir + key;
			List<File> inFiles = map.get(key);
			// 若未找到文件
			if (inFiles == null || inFiles.size() == 0) {
				System.err.println("未查找到符合条件的文件");
				System.out.println("输出：" + key);
				FileUtils.write(outname, sb.toString(), encodingout);
				return;
			}

			// 得到文件名，不含扩展名
			for (File f : inFiles) {
				String fileName = f.getName().substring(0,
						f.getName().lastIndexOf("."));
				sb.append(fileName + "\r\n");
			}
			System.out.println("输出：" + key);
			FileUtils.write(outname, sb.toString(), encodingout);
		}
	}

	public static XML readXML(Map<String, List<File>> map, Element readArgs,
			String destDir) throws Exception {
		XML xml = new XML();
		String header = readArgs.select("header").get(0).ownText();
		String encodingin = readArgs.select("encoding-in").get(0).ownText();
		String encodingout = readArgs.select("encoding-out").get(0).ownText();
		String rootnode = readArgs.select("rootnode").get(0).ownText();
		String nodes = readArgs.select("nodes").get(0).ownText();

		// read
		xml.setHeader(header);
		xml.setEncodingin(encodingin);
		xml.setEncodingout(encodingout);
		xml.setRootnode(rootnode);
		xml.setNodes(nodes);

		XMLReader xmlReader = new XMLReader();
		for (String key : map.keySet()) {
			String outname = destDir + key;
			List<File> inFiles = map.get(key);
			xmlReader.readXml(inFiles, xml, outname);
			System.out.println("输出：" + key);
		}
		return xml;
	}

	public static NumberLst readTXT(Map<String, List<File>> map,
			Element readArgs, String destDir) throws IOException {
		String header = readArgs.select("header").get(0).ownText();
		String charsetIn = readArgs.select("encoding-in").get(0).ownText();
		String charsetOut = readArgs.select("encoding-out").get(0).ownText();
		String firstrow = readArgs.select("firstrow").get(0).ownText();
		String columns = readArgs.select("columns").get(0).ownText();
		String sep = readArgs.select("sep").get(0).ownText();
		String match = readArgs.select("match").get(0).ownText();
		String rep = readArgs.select("rep").get(0).ownText();

		NumberLst numLst = new NumberLst();

		numLst.setHeader(header);
		numLst.setEncodingin(charsetIn);
		numLst.setEncodingout(charsetOut);
		numLst.setFirstrow(firstrow);
		numLst.setColumns(columns);
		numLst.setSep(sep);
		numLst.setRe(match);
		numLst.setRep(rep);
		// read lst
		for (String key : map.keySet()) {
			String outname = destDir + key;
			List<File> inFiles = map.get(key);
			LstReader.read(inFiles, numLst, outname);
			System.out.println("输出：" + key);
		}
		return numLst;
	}

	public static SGM readSGM(Map<String, List<File>> map, Element readArgs,
			String destDir) throws Exception {
		SGM sgm = new SGM();
		String header = readArgs.select("header").get(0).ownText();
		String encodingin = readArgs.select("encoding-in").get(0).ownText();
		String encodingout = readArgs.select("encoding-out").get(0).ownText();
		String labels = readArgs.select("labels").get(0).ownText();
		String dateLabel = readArgs.select("date-label").get(0).ownText();

		// set to object
		sgm.setHeader(header);
		sgm.setEncodingin(encodingin);
		sgm.setEncodingout(encodingout);
		sgm.setLabels(labels);
		sgm.setDateLabel(dateLabel);

		for (String key : map.keySet()) {
			String outname = destDir + key;
			List<File> inFiles = map.get(key);
			SGMReader.readSGM(inFiles, sgm, outname);
			System.out.println("输出：" + key);
		}
		return sgm;
	}
}
