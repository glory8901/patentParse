package reader;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import po.PatentPo;
import po.XMLPoUtil;
import typeobj.FilterFeature;
import typeobj.NumListProperty;
import typeobj.XMLProperty;
import utils.log.LogUtils;
import writer.DBUtil;
import writer.DBWriter;

public class ConfigReader {
	private String baseDir;
	private String destDir;
	private String tmpDir;

	public void loadConfig(String confxml) throws Exception {
		// 读取号单的配置，从而分别处理每一个配置
		File configFile = new File(System.getProperty("user.dir") + "/conf/" + confxml);
		LogUtils.checkLogDir();
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
		baseDir = base.select("basedir").get(0).ownText();
		destDir = base.select("destdir").get(0).ownText();
		tmpDir = base.select("tempdir").get(0).ownText();

		// 选择branch的各个部分,每个branch代表一些搜索条件和文件
		Elements branchs = doc.select("branch");
		for (Element branch : branchs) {
			Element fileSearchArgs = branch.select("FileSearcher").get(0);// 每个branch只能有一个
			Elements typeReaderArgs = branch.select("TextReader");// 可能有多个
			Elements writerArgs = branch.select("Writer");

			// 搜索文件夹
			FileSearcher searcher = new FileSearcher(fileSearchArgs);
			searcher.search(fileSearchArgs, base);
			System.out.println("搜索完毕\n");

			// 分批次筛选文件夹，并读xml
			Map<String, FilterFeature> filterMap = searcher.getFilterMap();
			String tmpdir = tmpDir + fileSearchArgs.select("AddPath").get(0).ownText();
			File[] tmpDir = new File(tmpdir).listFiles();
			for (File tmpfile : tmpDir) {
				String tmpFileName = tmpfile.getName();
				// 如果临时文件不以walkfile开头，或者是文件夹就跳过
				if (!tmpFileName.startsWith("walkfile") || tmpfile.isDirectory()) {
					continue;
				}
				System.out.println("开始筛选文件夹...");
				List<String> fileList = FileUtils.readLines(tmpfile, "utf-8");
				searcher.filterFileList(fileList, filterMap);
				Map<String, List<String>> fileMap = searcher.getTypeOutFilesMap();
				Map<String, String> outnameMap = searcher.getTypeOutNameMap();
				if (fileMap == null || outnameMap == null) {
					continue;
				}
				System.out.println("结束筛选\n");
				// 根据文件类型的不同分别采用不同的方法读取
				readByType(fileMap, outnameMap, typeReaderArgs, writerArgs.first(), destDir);
			}
		}
	}

	public void readByType(Map<String, List<String>> filemap, Map<String, String> outnameMap, Elements typeReaderArgs,
			Element writerArgs, String destBaseDir) throws Exception {
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
			readfile(filetype, readPaths, typeArg, writerArgs, outfilepath);
		}
	}

	public void readfile(String filetype, List<String> readPaths, Element typeArgs, Element writerArgs, String outPath)
			throws Exception {
		String reader = typeArgs.select("reader").get(0).ownText().toLowerCase();
		if (reader.toLowerCase().startsWith("xml")) {
			System.out.println("开始读取xml: " + filetype);
			readXML(readPaths, typeArgs, writerArgs, outPath);
			System.out.println("结束读取xml\n");
		} else if (reader.equals("txt")) {
			System.out.println("开始读取TXT: " + filetype);
			readTXT(readPaths, typeArgs, writerArgs, outPath);
			System.out.println("结束读取TXT\n");
		} else if (reader.equals("path")) {
			System.out.println("开始读取path: " + filetype);
			readPATH(readPaths, typeArgs, writerArgs, outPath);
			System.out.println("结束读取path\n");
		} else {
			System.out.println("暂无该种类型文件的读取方法");
		}
	}

	/**
	 * 读取路径文件
	 * 
	 * @param readPaths
	 *            所有的xml路径
	 * @param readArgs
	 *            xml读取的参数
	 * @param outPath
	 *            读取xml输出路径
	 * @throws Exception
	 */
	public void readPATH(List<String> readPaths, Element readArgs, Element writeArgs, String outPath) throws Exception {
		// 读取文件路径
		String header = readArgs.select("header").get(0).ownText();
		String encodingout = readArgs.select("encoding-out").get(0).ownText();

		StringBuilder sb = new StringBuilder();
		sb.append(header.replace(";", "\t") + "\r\n");

		// 对原来的输出进行处理
		File outFile = new File(outPath);
		if (outFile.exists()) {
			int index = outPath.lastIndexOf(".");
			for (int i = 1; i < 100; i++) {
				String outfileName = outPath.substring(0, index) + "_" + i + outPath.substring(index);
				if (!new File(outfileName).exists()) {
					outPath = outfileName;
					break;
				}
			}
		}

		// 若未找到文件
		if (readPaths == null || readPaths.size() == 0) {
			System.err.println("未查找到符合条件的文件");
			System.out.println("输出：" + outPath);
			FileUtils.write(new File(outPath), sb.toString(), encodingout);
			return;
		}

		// 输出文件名
		System.out.println("输出：" + outPath);
		// 得到文件名，不含扩展名
		for (String filePath : readPaths) {
			String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.lastIndexOf("."));
			sb.append(fileName + "\t" + filePath + "\r\n");
		}
		FileUtils.write(new File(outPath), sb.toString(), encodingout);
	}

	/**
	 * 读取xml文件
	 * 
	 * @param readPaths
	 *            所有的xml路径
	 * @param readArgs
	 *            xml读取的参数
	 * @param outPath
	 *            读取xml输出路径
	 * @param xmlType
	 *            0:普通xml读取方法，1：特殊xml读取方法
	 * @return
	 * @throws Exception
	 */
	public XMLProperty readXML(List<String> readPaths, Element readArgs, Element writeArgs, String outPath)
			throws Exception {

		// read
		Elements readerEles = readArgs.select("reader");
		Elements headerEles = readArgs.select("header");
		Elements encInEles = readArgs.select("encoding-in");
		Elements encOutEles = readArgs.select("encoding-out");
		Elements rootnodeEles = readArgs.select("rootnode");
		Elements textnodesEles = readArgs.select("textnodes");
		Elements existnodesEles = readArgs.select("existnodes");
		Elements getFirstEles = readArgs.select("getfirst");

		XMLProperty xmlProp = new XMLProperty();
		String reader = readerEles.get(0).ownText().toLowerCase();
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
		// header变为list
		List<String> fieldList = Arrays.asList(header.trim().split(";"));
		// change
		boolean flag = false;
		if ("1".equals(getFirst)) {
			flag = true;
		}

		// 放在xml的这个对象中
		xmlProp.setFields(header);
		xmlProp.setEncodingin(encodingin);
		xmlProp.setEncodingout(encodingout);
		xmlProp.setRootnode(rootnode);
		xmlProp.setTextnodes(textnodes);
		xmlProp.setExistNodes(existnodes);
		xmlProp.setIfGetFirst(flag);
		xmlProp.setPathReplace(baseDir);

		// write
		String tableName = writeArgs.select("tablename").first().ownText().trim();
		boolean useDB = true;
		if (tableName == null || tableName.equals("")) {
			useDB = false;
		}
		// 判断xml类型，选择方法
		XMLReader xmlReader = null;
		switch (reader) {
		case "xml":
			// 使用普通xml读取方法
			xmlReader = new XMLReader(xmlProp);
			break;
		case "xmllinereader":
			// 使用特殊的xml读取方法
			xmlReader = new XMLLineReader(xmlProp);
			break;
		default:
			System.err.println("未找到合适的解析器，当前解析器为" + reader);
			break;
		}
		if (!useDB) {
			// 不使用数据库
			xmlReader.readXMLToFile(readPaths, outPath);
		} else {
			// 读写数据库的类
			DBWriter<PatentPo> db = new DBWriter<PatentPo>();
			// 获取数据库连接
			Connection conn = DBUtil.getConn();
			db.setConn(conn);
			db.setTableName(tableName);
			db.setFieldList(fieldList);
			String sql = db.getPsSql();
			System.out.println(sql);
			
			//开始入库
			xmlReader.readXML2db(readPaths,db);
			
			// 关闭数据库连接
			DBUtil.closeAll(null, null, conn);
		}
		return xmlProp;
	}

	/**
	 * 读取文本文件，按行读取
	 * 
	 * @param readPaths
	 *            所有的xml路径
	 * @param readArgs
	 *            xml读取的参数
	 * @param outPath
	 *            读取xml输出路径
	 * @return
	 * @throws IOException
	 */
	public NumListProperty readTXT(List<String> readPaths, Element readArgs, Element writeArgs, String outPath)
			throws IOException {
		String header = readArgs.select("header").get(0).ownText();
		String charsetIn = readArgs.select("encoding-in").get(0).ownText();
		String charsetOut = readArgs.select("encoding-out").get(0).ownText();
		String firstrow = readArgs.select("firstrow").get(0).ownText();
		String columns = readArgs.select("columns").get(0).ownText();
		String sep = readArgs.select("sep").get(0).ownText();
		String match = readArgs.select("match").get(0).ownText();
		String rep = readArgs.select("rep").get(0).ownText();

		NumListProperty numLst = new NumListProperty();

		numLst.setHeader(header);
		numLst.setEncodingin(charsetIn);
		numLst.setEncodingout(charsetOut);
		numLst.setFirstrow(firstrow);
		numLst.setColumns(columns);
		numLst.setSep(sep);
		numLst.setRe(match);
		numLst.setRep(rep);

		// 读取号单
		LstReader.read(readPaths, numLst, outPath);
		System.out.println("输出：" + outPath);
		return numLst;
	}
}
