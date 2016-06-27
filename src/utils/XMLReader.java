package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.file.FileUtils;

public class XMLReader {

	public static void main(String[] args) {
		try {
			XMLReader reader = new XMLReader();
			String filename = "C:\\Users\\Lenovo\\Desktop\\FR_FRAMDST86_2016-11_0001.xml";
			List<String> resultList = reader.readLines(filename,
					"DesignApplication", "PublicationDate",
					"RegistrationNumber", "DesignCurrentStatusCode",
					"ViewDetails>View>ViewNumber");
			reader.writexml(resultList,"C:\\Users\\Lenovo\\Desktop\\xml_amd.csv");
			
			filename = "C:\\Users\\Lenovo\\Desktop\\FR_FRNEWST86_2016-11_0001.xml";
			resultList = reader.readLines(filename,
					"DesignApplication", "PublicationDate",
					"RegistrationNumber", "DesignCurrentStatusCode",
					"ViewDetails>View>ViewNumber");
			reader.writexml(resultList,"C:\\Users\\Lenovo\\Desktop\\xml_new.csv");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readXmlAndWrite(List<File> xmlFiles, String outName,
			String rootName, String... arguments)
			throws Exception {
		XMLReader reader = new XMLReader();
		List<String> resultList = new ArrayList<String>();
		
		// 将标题加入list中
		List<String> headers = new ArrayList<String>();
		for (String nodeName : arguments) {
			headers.add(nodeName.substring(nodeName.lastIndexOf(">") == -1 ? 0
					: nodeName.lastIndexOf(">") + 1));
		}
		resultList.add(StringUtils.join(headers, ",", null));
		
		// 遍历每一个xml文件，并将结果保存到list中
		for (File eachXml : xmlFiles) {
			List<String> oneResult = reader.readLines(eachXml.getAbsolutePath(), rootName,
					arguments);
			resultList.addAll(oneResult);
		}
		reader.writexml(resultList, outName);
	}
	
	public List<String> readLines(String xmlName, String rootName,
			String... nodenameArr) throws Exception {

		List<String> resultList = new ArrayList<String>();
		String sep = ";";

		// read xml
		File file = new File(xmlName);
		Document doc = Jsoup.parse(file, "utf-8");
		Elements daEls = doc.select(rootName);
		for (Element xmlrow : daEls) {
			List<String> lineList = new ArrayList<String>();
			for (String nodeName : nodenameArr) {
				Elements nodeEls = xmlrow.select(nodeName);
				if (nodeEls.size() == 0) {
					lineList.add("-");
				} else if (nodeEls.size() <= 1) {
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

	public void writexml(List<String> resultList, String outFileName)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String text : resultList) {
			sb.append(text + "\r\n");
		}
		FileUtils.write(outFileName, sb.toString(), "gbk");
	}
}
