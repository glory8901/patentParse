package reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import typeobj.XML;
import utils.StringUtils;

public class XMLReader {
	public void readXml(List<String> xmlFiles, XML xml, String outName)
			throws Exception {
		String header = xml.getHeader();
		String encodingin = xml.getEncodingin();
		String encodingout = xml.getEncodingout();
		String rootName = xml.getRootnode();
		String textNodes = xml.getTextnodes();
		String existNodes = xml.getExistNodes();
		boolean ifgetFirst = xml.isIfGetFirst();
		List<String> lineList = new ArrayList<String>();

		// 对原来的输出进行处理
		File outFile = new File(outName);
		if (outFile.exists()) {
			int index = outName.lastIndexOf(".");
			for (int i = 1; i < 100; i++) {
				String outfileName = outName.substring(0, index) + "_" + i
						+ outName.substring(index);
				if (!new File(outfileName).exists()) {
					outName = outfileName;
					break;
				}
			}
		}

		// 输出文件名
		System.out.println("输出：" + outName);

		// 将标题加入list中
		lineList.add(header.replace(",", "\t"));

		// 如果找不到文件，报错
		if (xmlFiles == null || xmlFiles.size() == 0) {
			System.err.println("找不到对应的xml文件:");
			writexml(lineList, outName, encodingout);
			return;
		}

		// 遍历每一个xml文件，并将结果保存到list中

		for (String eachXml : xmlFiles) {
			// 读取每一个xml，并将结果存入linelist中
			List<String> textValue = getNodeText(eachXml, encodingin, rootName,
					textNodes, existNodes, ifgetFirst);
			lineList.addAll(textValue);
			int size = lineList.size();
			if (size >= 1000) {
				// 每1000行写一次
				writexml(lineList, outName, encodingout);
				lineList.clear();
			}
		}
		writexml(lineList, outName, encodingout);
	}

	public static List<String> getNodeText(String xmlFile, String encoding,
			String rootName, String textnodes, String existnodes,
			boolean ifGetFirst) {
		// 用于存放结果
		List<String> allValues = new ArrayList<String>();
		// 多值的分割符
		String sep = ";";
		String[] textNodesArr = textnodes.split(";");
		// 读取xml文件
		Document doc = null;
		try {
			doc = Jsoup.parse(new File(xmlFile), encoding);
		} catch (Exception e) {
			System.err.println("Parse Fail:" + xmlFile);
		}

		Elements daEls;
		if (rootName == null || "".equals(rootName)) {
			daEls = doc.select("body");
		} else {
			daEls = doc.select(rootName);
		}
		// 每一行xml
		for (Element xmlrow : daEls) {
			List<String> lineList = new ArrayList<String>();
			for (String nodeName : textNodesArr) {
				Elements nodeEls = xmlrow.select(nodeName);
				if (nodeEls.size() == 0) {
					// 无值
					lineList.add("--");
				} else if (nodeEls.size() == 1 || ifGetFirst) {
					// 单值
					lineList.add(nodeEls.get(0).ownText());
				} else {
					// 多值
					List<String> itemArr = new ArrayList<String>();
					for (Element node : nodeEls) {
						itemArr.add(node.ownText());
					}
					lineList.add(StringUtils.join(itemArr, sep));
				}
			}

			// 加入有值无值的判断
			List<String> existValue = existvalue(xmlrow, existnodes);
			if (existValue != null) {
				lineList.addAll(existValue);
			}
			lineList.add(xmlFile);
			// 将行join为字符串
			allValues.add(StringUtils.join(lineList, "\t", '"'));
		}
		return allValues;
	}

	/**
	 * 
	 * @param doc
	 * @param parseColumns
	 * @return
	 */
	public static List<String> existvalue(Element doc, String parseColumns) {
		if (parseColumns == null || "".equals(parseColumns)) {
			return null;
		}
		String[] parseColumnArr = parseColumns.split(";");
		List<String> result = new ArrayList<String>();

		// 每一个字段进行处理
		for (int i = 0; i < parseColumnArr.length; i++) {
			Elements colpElements = doc.select(parseColumnArr[i]);
			// 只要有一个 p 就为true
			boolean flag = false;
			// 如果p标签不存在
			if (colpElements.size() == 0) {
				flag = false;
			} else {
				// each p
				for (Element p : colpElements) {
					// 获取包含text及子标签的所有内容
					String nodeText = p.text();
					if (nodeText != null && !"".equals(nodeText.trim())) {
						flag = true;
						break;
					}
				}
				// 对这个字段的处理
				if (flag == true) {
					result.add("1");
				} else {
					result.add("0");
				}
			}
		}
		// 最后返回结果
		return result;
	}

	public static void writexml(List<String> resultList, String outFileName,
			String encoding) throws IOException {
		// 以指定的编码写文件
		String outdir = outFileName.substring(0, outFileName.lastIndexOf("\\"));
		File outDir = new File(outdir);
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		FileOutputStream out = new FileOutputStream(outFileName, true);
		for (String text : resultList) {
			out.write((text + "\r\n").getBytes(encoding));
			out.flush();
		}
		out.close();
	}
}
