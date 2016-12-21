package reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import typeobj.NumberLst;
import utils.file.FileUtils;

public class LstReader {
	public static void read(List<File> allFiles, NumberLst numLst,
			String outname) throws IOException {
		// 从对象中获取各变量的数值
		String header = numLst.getHeader();
		String encodingin = numLst.getEncodingin();
		String encodingout = numLst.getEncodingout();
		int firstrow = Integer.parseInt(numLst.getFirstrow());
		String columns = numLst.getColumns();
		String sep = numLst.getSep();
		String pattern = numLst.getRe();
		String rep = numLst.getRep();

		// 存放结果
		StringBuffer sb = new StringBuffer();
		sb.append(header + "\r\n");

		// 遍历找不到相应文件, 仅输出标题行
		if (allFiles == null || allFiles.size() == 0) {
			System.err.println("找不到对应的lst文件:");
			FileUtils.write(outname, sb.toString(), encodingout);
			return;
		}

		// 如果找到文件列表，则对所有的文件读取号单
		for (File f : allFiles) {
			// 对每一个文件，读取内容并输出为号单
			String filePath = f.getAbsolutePath();

			// 读取号单并写入文件中
			sb.append(readlst(filePath, encodingin, firstrow, columns, sep,
					rep, pattern));
		}
		// write
		FileUtils.write(outname, sb.toString(), encodingout);
	}

	// 号单文件路径，文件编码，起始行，提取列，分隔符，清空字符正则表达式
	public static String readlst(String filename, String encodingin,
			int startline, String columns, String sep, String rep,
			String pattern) throws IOException {

		// 读取文件的全部内容
		List<String> text = FileUtils.readFileToList(filename, encodingin);
		StringBuffer sb = new StringBuffer();

		// 逐行来读，先拆分，再替换
		for (int i = startline - 1; i < text.size(); i++) {
			String[] lineArr = text.get(i).split(sep);
			String[] colArr = columns.split("\\*");
			List<String> outlineList = new ArrayList<String>();
			for (String index : colArr) {
				String outcell = lineArr[Integer.parseInt(index) - 1]
						.replaceAll(rep, "");// 清空字符正则表达式
				outlineList.add(outcell);
			}
			String[] newlineArr = outlineList.toArray(new String[outlineList
					.size()]);
			// 去掉空格，并将Array转变为String
			String newline = joinNoBlank(newlineArr, ",", pattern);
			sb.append(newline);
		}
		return sb.toString();
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

	public static String format(String s, String re) {
		String out = s;

		// 如果定义了正则表达式
		if (!re.equals("")) {
			// 使用正则进行匹配
			out = matchRE(s, re);
		}

		// 输出结果
		try {
			return Integer.parseInt(s) + "";
		} catch (Exception e) {
			return out;
		}
	}

	public static String matchRE(String s, String re) {
		Matcher m = Pattern.compile(re).matcher(s);
		String matched = "";
		while (m.find()) {
			matched = m.group(1);
			// System.out.println("[提取]:" + s + " ---> " + matched);
		}
		return matched;
	}
}
