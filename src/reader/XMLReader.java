package reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import typeobj.XML;
import utils.StringUtils;
import utils.file.FileUtils;

public class XMLReader {
	public void readXml(List<File> xmlFiles, XML xml, String outName)
			throws Exception {
		String header = xml.getHeader();
		String encodingin = xml.getEncodingin();
		String encodingout = xml.getEncodingout();
		String rootName = xml.getRootnode();
		String[] arguments = xml.getNodes().split(";");
		List<String> resultList = new ArrayList<String>();

		// 将标题加入list中
		resultList.add(header);

		// 如果找不到文件，报错
		if (xmlFiles == null || xmlFiles.size() == 0) {
			System.err.println("找不到对应的xml文件:");
			writexml(resultList, outName, encodingout);
			return;
		}

		// 遍历每一个xml文件，并将结果保存到list中
		for (File eachXml : xmlFiles) {
			List<String> oneResult = readLines(eachXml.getAbsolutePath(),
					encodingin, rootName, arguments);
			resultList.addAll(oneResult);
		}
		writexml(resultList, outName, encodingout);
	}

	public static List<String> readLines(String xmlName, String encoding,
			String rootName, String... nodenameArr) throws Exception {
		List<String> resultList = new ArrayList<String>();
		String sep = ";";

		// read xml
		File file = new File(xmlName);
		Document doc = Jsoup.parse(file, encoding);
		Elements daEls = doc.select(rootName);
		for (Element xmlrow : daEls) {
			List<String> lineList = new ArrayList<String>();
			for (String nodeName : nodenameArr) {
				Elements nodeEls = xmlrow.select(nodeName);
				if (nodeEls.size() == 0) {
					lineList.add("-");
				} else if (nodeEls.size() == 1) {
					lineList.add(nodeEls.get(0).ownText());
				} else {
					String multivalue = "";
					for (Element node : nodeEls) {
						multivalue += node.ownText() + sep;
					}
					lineList.add(multivalue.replaceAll(sep + "$", "").trim());
				}
			}
			// 将行join为字符串
			resultList.add(StringUtils.join(lineList, ",", null));
		}
		return resultList;
	}

	public static void writexml(List<String> resultList, String outFileName,
			String encoding) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String text : resultList) {
			sb.append(text + "\r\n");
		}
		FileUtils.write(outFileName, sb.toString(), encoding);
	}
}
