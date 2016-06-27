package fiveIPOs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.file.FileUtils;
import utils.file.FolderRecursion;

public class LstReader {
	private static String dataRootDir;
	private static String destDir;

	public void readLstAndWrite() throws IOException {
		// 读取配置
		Loadconf conf = new Loadconf();
		List<NumberLst> allNumLsts = conf.loadconfig();
		dataRootDir = conf.getDataRootDir();
		destDir = conf.getDestDir();
		System.out.println("数据位置:" + dataRootDir);

		for (NumberLst oneLst : allNumLsts) {
			read(oneLst);
		}
	}

	public static void read(NumberLst numLst) throws IOException {
		// 从对象中获取各变量的数值
		String country = numLst.getCountry();
		String basedir = dataRootDir + country;
		String[] dataTypes = numLst.getDatatype().split("\\*");
		String strFound = numLst.getStrFound();
		String header = numLst.getHeader();
		String[] outfiles = numLst.getOutfile().split("\\*");
		String encodingin = numLst.getEncodingin();
		String encodingout = numLst.getEncodingout();
		int firstrow = Integer.parseInt(numLst.getFirstrow());
		String columns = numLst.getColumns();
		String sep = numLst.getSep();
		String re = numLst.getRe();
		String rep = numLst.getRep();

		// 类型与输出文件名、数量对应的Map
		Map<String, String> outfilenames = getOutfileName(dataTypes, outfiles);
		Set<String> outfNameSet = new HashSet<String>();
		// 保存每种数据类型是否有相应数据及个数
		Map<String, Integer> typeMap = new HashMap<String, Integer>();
		for (String type : dataTypes) {
			typeMap.put(type, 0);
		}

		// 遍历源文件夹下的所有特定名称的文件
		System.out.println(Arrays.asList(dataTypes));
		List<File> fileList = FolderRecursion.getFileNameList(basedir, strFound);
		for (File f : fileList) {
			// 对每一个文件，读取内容并输出为号单
			String filePath = f.getAbsolutePath();
			System.out.println("输入：" + filePath);

			// output
			String outname = "";
			boolean mode = false;
			for (String type : dataTypes) {
				if (filePath.contains(type)) {
					typeMap.put(type, typeMap.get(type) + 1);
					outname = destDir + country + '\\' + outfilenames.get(type);
					if (outfNameSet.contains(outname)) {
						mode = true;
					}
					outfNameSet.add(outname);
					System.out.println("输出：" + outfilenames.get(type));
					break;
				}
			}

			if (!outname.equals("")) {
				// 号单文件路径，文件编码，起始行，提取列，分隔符，清空字符；输出文件路径，输出编码，输出首行，输出分隔符
				readlst(filePath, encodingin, firstrow, columns, sep, rep,
						outname, encodingout, header, ",", mode,re);
			}
		}

		// 遍历找不到相应数据的datatype,并仅输出标题行
		for (String type : typeMap.keySet()) {
			if (typeMap.get(type) == 0) {
				String outname = destDir + country + '\\'
						+ outfilenames.get(type);
				FileOutputStream out = new FileOutputStream(outname);
				out.write(header.getBytes());
				out.close();
			}
		}
	}

	public static Map<String, String> getOutfileName(String[] dataTypes,
			String[] outfiles) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < outfiles.length; i++) {
			map.put(dataTypes[i], outfiles[i]);
		}
		return map;
	}

	// 号单文件路径，文件编码，起始行，提取列，分隔符，清空字符；输出文件路径，输出编码，输出首行，输出分隔符
	public static void readlst(String filename, String encodingin,
			int startline, String columns, String sep, String rep, String outname,
			String outencoding, String header, String csvsep, boolean mode, String re) throws IOException {
		// 读取文件的全部内容
		List<String> text = FileUtils.readFileToList(filename, encodingin);
		StringBuffer sb = new StringBuffer();
		try {
			FileOutputStream out = null;
			if (mode) {
				out = new FileOutputStream(outname, true);
			} else {
				out = new FileOutputStream(outname);
				sb.append(header + "\r\n");
			}
			// 逐行来读，先拆分，再替换
			for (int i = startline - 1; i < text.size(); i++) {
				String[] lineArr = text.get(i).split(sep);
				String[] colArr = columns.split("\\*");
				List<String> outlineList = new ArrayList<String>();
				for (String index : colArr) {
					String outcell = lineArr[Integer.parseInt(index)-1]
							.replaceAll(rep, "");
					outlineList.add(outcell);
				}
				String[] newlineArr = outlineList
						.toArray(new String[outlineList.size()]);
				// 去掉空格，并将Array转变为String
				String newline = joinNoBlank(newlineArr, csvsep,re);
				sb.append(newline);
			}
			out.write(sb.toString().getBytes(outencoding));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String joinNoBlank(String[] lineArr, String sep, String re) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < lineArr.length; i++) {
			String raw = lineArr[i];
			String value = format(raw, re);
			// join
			if (i < lineArr.length - 1) {
				sb.append('"' + value + '"' + sep);
			} else {
				sb.append('"' + value + '"' + "\r\n");
			}

		}
		return sb.toString();
	}

	public static String format(String s,String re) {
		String out = s;
		
		// 如果定义了正则表达式
		if(!re.equals("")){
			// 使用正则进行匹配
			out = matchRE(s,re);
		}
		
		// 输出结果		
		try {
			return Integer.parseInt(s) + "";
		} catch (Exception e) {
			return out;
		}
	}
	
	public static String matchRE(String s,String re){
		Matcher m = Pattern.compile(re).matcher(s);
		String matched = "";
		while (m.find()) {
			matched = m.group(1);
			System.out.println("[提取]:" + s + " ---> " + matched);
		}
		return matched;
	}
}
