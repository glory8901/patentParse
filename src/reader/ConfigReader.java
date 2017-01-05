package reader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import typeobj.Filter;
import typeobj.NumberLst;
import typeobj.SGM;
import typeobj.XML;

public class ConfigReader {

	public void loadConfig(String confxml) throws Exception {
		// 读取号单的配置，从而分别处理每一个配置
		File configFile = new File(System.getProperty("user.dir") + "/conf/"
				+ confxml);
		Document doc = null;

		try {
			doc = Jsoup.parse(configFile, "utf-8");
		} catch (IOException e) {
			System.err.println("读取xml配置文件失败.");
			System.exit(0);
			e.printStackTrace();
		}

		// 选取xml中的base部分的标签
		Elements base = doc.select("baseroot");
		String destDir = base.select("destdir").text();
		String tmpdir = base.select("tempdir").text();

		// 选择branch的各个部分,每个branch代表一些搜索条件和文件
		Elements branchs = doc.select("branch");
		for (Element branch : branchs) {
			Element fileSearchArgs = branch.select("FileSearcher").get(0);// 只能有一个
			Elements typeReaderArgs = branch.select("TextReader");
			// 搜索文件夹
			FileSearcher searcher = new FileSearcher();
			searcher.search(fileSearchArgs, base);
			System.out.println("搜索完毕\n");
			Map<String, Filter> filterMap = searcher.getFilterMap();
			// 分批次筛选文件夹，并读xml
			File[] tmpDir = new File(tmpdir).listFiles();
			for (File tmpfile : tmpDir) {
				String tmpFileName = tmpfile.getName();
				// 如果不以walkfile开头
				if (!tmpFileName.startsWith("walkfile")) {
					continue;
				}
				System.out.println("开始筛选文件夹...");
				List<String> fileList = FileUtils.readLines(tmpfile, "utf-8");
				searcher.filterFileList(fileList, filterMap);
				Map<String, List<String>> fileMap = searcher
						.getTypeOutFilesMap();
				Map<String, String> outnameMap = searcher.getTypeOutNameMap();
				if (fileMap == null || outnameMap == null) {
					continue;
				}
				System.out.println("结束筛选\n");
				// 根据文件类型的不同分别采用不同的方法读取
				readByType(fileMap, outnameMap, typeReaderArgs, destDir);
			}
		}
	}

	public static void readByType(Map<String, List<String>> filemap,
			Map<String, String> outnameMap, Elements typeReaderArgs,
			String destBaseDir) throws Exception {
		if (!destBaseDir.endsWith("\\")) {
			destBaseDir = destBaseDir + "\\";
		}
		for (Element typeArg : typeReaderArgs) {
			// xml
			String filetype = typeArg.select("filetype").text();
			// xml outpath
			String outfilepath = destBaseDir + outnameMap.get(filetype);
			// xml read args
			List<String> readPaths = filemap.get(filetype);
			readfile(filetype, readPaths, typeArg, outfilepath);
		}
	}

	public static void readfile(String filetype, List<String> readPaths,
			Element typeArgs, String outPath) throws Exception {
		if (filetype.toLowerCase().equals("xml")) {
			System.out.println("开始读取xml");
			readXML(readPaths, typeArgs, outPath);
			System.out.println("结束读取xml\n");
		} else if (filetype.toLowerCase().equals("sgm")) {
			System.out.println("开始读取sgm");
			// 修改了原来的解析程序此处用的是sgm
			readXML(readPaths, typeArgs, outPath);
			System.out.println("结束读取sgm\n");
		} else if (filetype.toLowerCase().equals("nrm")) {
			System.out.println("开始读取nrm");
			readXML(readPaths, typeArgs, outPath);
			System.out.println("结束读取nrm\n");
		} else if (filetype.toLowerCase().equals("lst")) {
			System.out.println("开始读取lst");
			readTXT(readPaths, typeArgs, outPath);
			System.out.println("结束读取lst\n");
		} else if (filetype.toLowerCase().equals("path")) {
			System.out.println("开始读取path");
			readPATH(readPaths, typeArgs, outPath);
			System.out.println("结束读取path\n");
		}
	}

	public static void readPATH(List<String> readPaths, Element readArgs,
			String outPath) throws Exception {
		// 读取文件路径
		String header = readArgs.select("header").get(0).ownText();
		String encodingout = readArgs.select("encoding-out").get(0).ownText();

		StringBuilder sb = new StringBuilder();
		sb.append(header + "\r\n");
		// 若未找到文件
		if (readPaths == null || readPaths.size() == 0) {
			System.err.println("未查找到符合条件的文件");
			System.out.println("输出：" + outPath);
			FileUtils.write(new File(outPath), sb.toString(), encodingout);
			return;
		}

		// 得到文件名，不含扩展名
		for (String fileName : readPaths) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			sb.append(fileName + "\r\n");
		}
		System.out.println("输出：" + outPath);
		FileUtils.write(new File(outPath), sb.toString(), encodingout);
	}

	public static XML readXML(List<String> readPaths, Element readArgs,
			String outPath) throws Exception {

		// read
		Elements headerEles = readArgs.select("header");
		Elements encInEles = readArgs.select("encoding-in");
		Elements encOutEles = readArgs.select("encoding-out");
		Elements rootnodeEles = readArgs.select("rootnode");
		Elements textnodesEles = readArgs.select("textnodes");
		Elements existnodesEles = readArgs.select("existnodes");
		Elements getFirstEles = readArgs.select("getfirst");

		XML xml = new XML();
		String header = null;
		String encodingin = null;
		String encodingout = null;
		String rootnode = null;
		String textnodes = null;
		String existnodes = null;
		String getFirst = null;

		// getvalue
		if (headerEles.size() > 0) {
			header = headerEles.get(0).ownText();
		}
		if (encInEles.size() > 0) {
			encodingin = encInEles.get(0).ownText();
		}
		if (encOutEles.size() > 0) {
			encodingout = encOutEles.get(0).ownText();
		}
		if (rootnodeEles.size() > 0) {
			rootnode = rootnodeEles.get(0).ownText();
		}
		if (textnodesEles.size() > 0) {
			textnodes = textnodesEles.get(0).ownText();
		}
		if (existnodesEles.size() > 0) {
			existnodes = existnodesEles.get(0).ownText();
		}
		if (getFirstEles.size() > 0) {
			getFirst = getFirstEles.get(0).ownText();
		}

		// change
		boolean flag = false;
		if ("1".equals(getFirst)) {
			flag = true;
		}

		// read
		xml.setHeader(header);
		xml.setEncodingin(encodingin);
		xml.setEncodingout(encodingout);
		xml.setRootnode(rootnode);
		xml.setTextnodes(textnodes);
		xml.setExistNodes(existnodes);
		xml.setIfGetFirst(flag);

		XMLReader xmlReader = new XMLReader();
		xmlReader.readXml(readPaths, xml, outPath);

		return xml;
	}

	public static NumberLst readTXT(List<String> readPaths, Element readArgs,
			String outPath) throws IOException {
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
		LstReader.read(readPaths, numLst, outPath);
		System.out.println("输出：" + outPath);
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
