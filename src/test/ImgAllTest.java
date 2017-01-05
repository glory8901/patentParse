package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import utils.file.ISOUtils;
import utils.folder.MyFileVisitor;

public class ImgAllTest {
	private static String basedir;
	private static String mountisodir;
	private static String disk;

	public static void main(String[] args) {
		try {
			ImgAllTest nparser = new ImgAllTest();
			Elements typeReaderArgs = nparser.loadConfig("test.xml");
			List<String> allISOList = nparser.getAllISO();
			nparser.readISO(allISOList, typeReaderArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getAllISO() {
		List<String> allFilesList = null;
		try {
			allFilesList = getFileList(basedir, ".iso");

			System.out.println("ISO:" + allFilesList.size());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return allFilesList;
	}

	public void readISO(List<String> allFilesList, Elements typeReaderArgs) {
		// 遍历每一个ISO文件
		for (String isoFilePath : allFilesList) {
			try {
				// close iso
				ISOUtils.closeISO(mountisodir);
				// open iso
				ISOUtils.openISO(mountisodir, isoFilePath);
				// read xmls
				System.out.println(String
						.format("opening ISO:%s.", isoFilePath));
				openISOReadFile(isoFilePath, disk, ".NRM*.XML", typeReaderArgs);
			} catch (IOException e) {
				System.err.println(isoFilePath + ":" + e.getMessage());
				continue;
			}
		}
	}

	public Elements loadConfig(String confxml) throws Exception {
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
		Elements base = doc.select("basedir");
		basedir = base.get(0).ownText();
		disk = doc.select("disk").get(0).ownText();
		mountisodir = doc.select("ISO").get(0).ownText();

		// 选择branch的各个部分
		Element branch = doc.select("branch").get(0);
		Elements typeReaderArgs = branch.select("text-read");
		return typeReaderArgs;
	}

	public static void openISOReadFile(String isopath, String ISODiskLabel,
			String lookingForExtsFiles, Elements typeReaderArgs)
			throws IOException {
		String isoName = new File(isopath).getName();
		// get all xmls
		List<String> fileList = getFileList(ISODiskLabel, lookingForExtsFiles);
		System.out.println(String.format("XML:%d", fileList.size()));
		// get all xml content
		List<String> xmlDocList = new ArrayList<String>();
		// // add header
		// String header =
		// typeReaderArgs.get(0).select("header").get(0).ownText();
		// xmlDocList.add(header);
		// System.out.println(header);
		// read xmls
		for (String file : fileList) {
			String line = null;
			if (file.toLowerCase().endsWith(".xml")) {
				line = readTypeFile(file, "xml", typeReaderArgs);
			} else if (file.toLowerCase().endsWith(".nrm")) {
				line = readTypeFile(file, "nrm", typeReaderArgs);
			}
			if (line != null) {
				xmlDocList.add(line + "," + isoName);
			}
		}
		// outpath
		FileUtils.writeLines(new File("out/ru_2005-2016.csv"), "utf-8",
				xmlDocList, true);
	}

	public static List<String> getFileList(String basedir, String ext)
			throws IOException {
		if (!new File(basedir).exists()) {
			throw new FileNotFoundException(basedir + " 不存在");
		}
		Path fileDir = Paths.get(basedir);
		List<String> extensionList = new ArrayList<String>();
		extensionList.add(ext);

		MyFileVisitor visitor = new MyFileVisitor(extensionList,"");
		// walk
		Files.walkFileTree(fileDir, visitor);
		// result
		List<String> fileList = visitor.getFileList();
		return fileList;
	}

	public static String readTypeFile(String nrmpath, String filetype,
			Elements typeReaderArgs) {
		File nrmfile = new File(nrmpath);

		for (Element typeArg : typeReaderArgs) {
			String ftype = typeArg.select("filetype").get(0).ownText();
			String encoding = typeArg.select("encoding-in").get(0).ownText();
			String parseColumns = typeArg.select("textnodes").get(0).ownText();
			String existColumns = typeArg.select("existnodes").get(0).ownText();
			if (ftype.equals(filetype)) {
				String line = parse(nrmfile, encoding, parseColumns,
						existColumns);
				if (line == null) {
					return null;
				}
				return line;
			}
		}
		return null;
	}

	public static String parse(File xmlfile, String encoding,
			String parseColumns, String existColumns) {
		List<String> outline = new ArrayList<String>();
		Document doc = null;
		try {
			doc = Jsoup.parse(xmlfile, encoding);
		} catch (Exception e) {
			System.err.println("Parse Fail:" + xmlfile.getAbsolutePath());
			return null;
			// e.printStackTrace();
		}
		outline.addAll(textvalue(doc, parseColumns));
		List<String> exist = existvalue(doc, existColumns);
		if (exist == null) {
			return null;
		}
		outline.addAll(exist);
		outline.add(xmlfile.getAbsolutePath());
		// System.out.println(join(outline, ","));
		return join(outline, ",");
	}

	public static String join(List<String> list, String sep) {
		// python join
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				sb.append('"' + list.get(i) + '"' + sep);
			} else {
				sb.append('"' + list.get(i) + '"');
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param doc
	 * @param parseColumns
	 * @return
	 */
	public static List<String> existvalue(Document doc, String parseColumns) {
		String[] parseColumnArr = parseColumns.split("\\*");
		List<String> result = new ArrayList<String>();
		// 判断是否添加该行
		boolean ifaddTheXML = false;
		// each column
		for (int i = 0; i < parseColumnArr.length; i++) {
			Elements colpElements = doc.select(parseColumnArr[i]);
			boolean flag = true;
			// all p
			if (colpElements.size() == 0) {
				flag = false;
			} else {
				for (Element p : colpElements) {
					// each p,all p should have img
					List<Node> textAndnodes = p.childNodes();// 获取包含text及子标签的所有内容
					if (textAndnodes.size() == 0) {
						flag = false;
						break;
					}
					// 测试是否第一个内容是标签，且标签是img
					Node fNode = null;
					for (int j = 0; j < textAndnodes.size(); j++) {
						fNode = textAndnodes.get(j);
						if (fNode instanceof TextNode) {
							String text = fNode.toString();
							if (text == null || "".equals(text.trim())) {
								// 找到第一个节点
								continue;
							} else {
								break;
							}
						} else if (fNode instanceof Element) {
							break;
							// System.out.println("Element");
						} else {
							System.out.println("other");
							break;
						}
					}
					// 判断含标签的文档
					if (fNode instanceof Element) {
						// 类型转换
						Element img = (Element) fNode;
						String tagImg = img.tagName();
						if (!"img".equals(tagImg)) {
							flag = false;
							break;
						}
					} else {
						flag = false;
						break;
					}
				}
			}
			if (flag == true) {
				result.add("1");
				ifaddTheXML = true;
			} else {
				result.add("0");
			}
		}
		// if has 1,then add
		if (ifaddTheXML) {
			return result;
		} else {
			return null;
		}
	}

	public static List<String> textvalue(Document doc, String parseColumns) {
		String[] parseColumnArr = parseColumns.split("\\*");
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < parseColumnArr.length; i++) {
			try {
				Elements nodeelements = doc.select(parseColumnArr[i]);
				if (nodeelements.size() == 0) {
					result.add("-");
				} else {
					String nodeText = doc.select(parseColumnArr[i]).get(0)
							.ownText();
					result.add(nodeText);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
