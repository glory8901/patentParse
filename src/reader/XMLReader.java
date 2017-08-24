package reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import po.PatentPo;
import po.XMLPoUtil;
import typeobj.XMLProperty;
import utils.StringUtils;
import writer.DBUtil;
import writer.DBWriter;

public class XMLReader {
	protected String header;
	protected String pathPrefix;
	protected String encodingin;
	protected String encodingout;
	protected String rootName;
	protected String textNodes;
	protected String existNodes;
	protected boolean ifGetFirst;

	public XMLReader(XMLProperty xmlProp) {
		super();
		header = xmlProp.getFields();
		encodingin = xmlProp.getEncodingin();
		encodingout = xmlProp.getEncodingout();
		rootName = xmlProp.getRootnode();
		textNodes = xmlProp.getTextnodes();
		existNodes = xmlProp.getExistNodes();
		ifGetFirst = xmlProp.isIfGetFirst();
		pathPrefix = xmlProp.getPathReplace();
	}

	public String getOutputFileName(String outName) {
		// 获取文件名
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
		return outName;
	}

	public void readXML2db(List<String> xmlFiles, DBWriter db) {
		List<List<String>> xmlList = new ArrayList<List<String>>();
		List<String> fieldList = Arrays.asList(header.trim().split(";"));
		XMLPoUtil<PatentPo> xmlPo = new XMLPoUtil<PatentPo>(fieldList);
		// 多值的分割符
		String[] textNodesArr = textNodes.split(";");
		int count = 0;
		int totalCount = xmlFiles.size();
		// 遍历每一个xml文件，并将结果保存到list中
		for (String eachXml : xmlFiles) {
			count++;
			// 读取每一个xml，并将结果存入linelist中
			List<List<String>> list2d = getNodesText(eachXml, textNodesArr);
			// 如果为null
			if (list2d != null) {
				xmlList.addAll(list2d);
			}
			if (count % 2000 == 0) {
				System.out.println(count + "/" + totalCount);
				insertToDB(db, xmlList, xmlPo);
			}
		}
		// 最后将剩余部分也入库
		insertToDB(db, xmlList, xmlPo);
	}

	public void insertToDB(DBWriter db, List<List<String>> xmlList,
			XMLPoUtil<PatentPo> xmlPo) {
		List<PatentPo> poList = xmlPo.convertToPos(xmlList);
		xmlList.clear();// 清空xml解析结果

		for (PatentPo po : poList) {
			db.setXmlPo(po);
			try {
				db.insert();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// 清空polist
		poList.clear();
	}

	public void readXMLToFile(List<String> xmlFiles, String outName)
			throws Exception {
		List<String> lineList = new ArrayList<String>();
		// ////////////////////////////////////////////////
		// 多值的分割符，不使用正则表达式进行切割
		String[] textNodesArr = org.apache.commons.lang3.StringUtils.split(
				textNodes, ";");
		// 获取输出文件名
		outName = getOutputFileName(outName);
		// ////////////////////////////////////////////////

		// 将标题加入list中
		lineList.add(header.replace(";", "\t"));

		// 如果找不到文件，报错
		if (xmlFiles == null || xmlFiles.size() == 0) {
			System.err.println("找不到对应的xml文件:");
			writexml(lineList, outName, encodingout);
			return;
		}

		// 遍历每一个xml文件，并将结果保存到list中
		int count = 0;
		int size = 0;
		for (String eachXml : xmlFiles) {
			count++;
			// 读取每一个xml，并将结果存入linelist中
			List<List<String>> list2d = getNodesText(eachXml, textNodesArr);
			List<String> textValue = compress2dList(list2d);
			// 如果为null
			if (textValue != null) {
				lineList.addAll(textValue);
			}
			size = lineList.size();
			if (size >= 1000) {
				// 每1000行写一次
				System.out.println((count + 1) + "/" + xmlFiles.size());
				writexml(lineList, outName, encodingout);
				lineList.clear();
			}
		}
		writexml(lineList, outName, encodingout);
	}

	/**
	 * 获取节点的属性
	 * 
	 * @param rootEle
	 * @param nodeName
	 * @param attr
	 * @param ifGetFirst
	 * @return
	 */
	public String getAttr(Element rootEle, String nodeName, String attr) {
		String sep = ";";
		Elements nodeEls = rootEle.select(nodeName);
		if (nodeEls.size() == 0) {
			// 无值
			return "--";
		} else if (nodeEls.size() == 1 || ifGetFirst) {
			// 单值
			return nodeEls.get(0).attr(attr);
		} else {
			// 多值
			List<String> itemArr = new ArrayList<String>();
			for (Element node : nodeEls) {
				itemArr.add(node.attr(attr));
			}
			return StringUtils.join(itemArr, sep);
		}
	}

	/**
	 * 得到单个节点的内容
	 * 
	 * @param rootEle
	 * @param nodeName
	 * @param ifGetFirst
	 * @return
	 */
	public String getText(Element rootEle, String nodeName) {
		String sep = ";";
		Elements nodeEls = rootEle.select(nodeName);
		if (nodeEls.size() == 0) {
			// 无值
			return "--";
		} else if (nodeEls.size() == 1 || ifGetFirst) {
			// 单值
			return nodeEls.get(0).text();
		} else {
			// 多值
			List<String> itemArr = new ArrayList<String>();
			for (Element node : nodeEls) {
				itemArr.add(node.text());
			}
			return StringUtils.join(itemArr, sep);
		}
	}

	/**
	 * 压缩二维的list
	 * 
	 * @param list2d
	 * @return
	 */
	public List<String> compress2dList(List<List<String>> list2d) {
		if (list2d == null) {
			return null;
		}
		List<String> compressedList = new ArrayList<String>();
		for (List<String> list : list2d) {
			String cell = StringUtils.join(list, "\t", '"');
			compressedList.add(cell);
		}
		return compressedList;
	}

	/**
	 * 获取节点的值
	 * 
	 * @param xmlPath
	 * @return
	 */
	public List<List<String>> getNodesText(String xmlPath, String[] textNodesArr) {
		// 用于存放结果
		List<List<String>> xmlList = new ArrayList<List<String>>();
		// 读取xml文件
		Document doc = null;
		try {
			doc = Jsoup.parse(new File(xmlPath), encodingin);
		} catch (Exception e) {
			System.err.println("解析失败:" + xmlPath);
		}

		Elements daEls;
		if (rootName == null || "".equals(rootName)) {
			daEls = doc.select("body");
		} else {
			daEls = doc.select(rootName);
		}
		// 每一行xml
		for (Element lineInXML : daEls) {
			List<String> lineList = new ArrayList<String>();
			for (String node : textNodesArr) {
				node = node.trim();
				String context = null;
				if (node.contains("@")) {
					// 拆分为nodename 和 attr
					String[] nodeAttr = org.apache.commons.lang3.StringUtils
							.split(node, "@");
					context = getAttr(lineInXML, nodeAttr[0], nodeAttr[1]);
				} else {
					context = getText(lineInXML, node);
				}
				lineList.add(context);
			}

			// 加入有值无值的判断
			List<String> existValue = existvalue(lineInXML, existNodes);
			if (existValue != null) {
				lineList.addAll(existValue);
			}
			// 加入该xml的路径
			lineList.add(xmlPath.replace(
					pathPrefix.substring(0, pathPrefix.lastIndexOf("\\")), ""));
			// 将行list加入
			xmlList.add(lineList);
		}
		return xmlList;
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

		// 每一个是否存在的字段进行处理
		for (int i = 0; i < parseColumnArr.length; i++) {
			Elements existEles = doc.select(parseColumnArr[i].trim());
			// 只要有一个标签内有内容就为true
			boolean flag = false;
			// 如果标签不存在
			if (existEles.size() == 0) {
				flag = false;
			} else {
				// each child
				for (Element child : existEles) {
					// 获取包含text及子标签的所有内容
					String nodeText = child.text();
					if (nodeText != null && !"".equals(nodeText.trim())) {
						flag = true;
						break;
					}
				}
			}
			// 对这个字段的处理
			if (flag == true) {
				result.add("1");
			} else {
				result.add("0");
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
