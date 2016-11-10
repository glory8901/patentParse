package fiveIPOs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import utils.Converter;
import utils.StringUtils;
import utils.file.FileUtils;

public class SGMReader {
	public static void readSGM(List<File> allFiles, SGM onesgm, String outname)
			throws IllegalArgumentException, IllegalAccessException,
			IOException {
		// 处理一个配置
		String parseColumns = onesgm.getLabels();
		String dateColumn = onesgm.getDateLabel();
		String encodingin = onesgm.getEncodingin();

		// 输出结果
		StringBuffer sb = new StringBuffer();
		sb.append(onesgm.getHeader() + "\r\n");

		if (allFiles == null || allFiles.size() == 0) {
			System.err.println("找不到对应的sgm文件:");
			FileUtils.write(outname, sb.toString(), onesgm.getEncodingout());
			return;
		}

		// 开始使用正则匹配其中的括号中的内容
		String reg = "\\((.*?)\\)";
		Pattern p = Pattern.compile(reg);

		for (File in : allFiles) {
			SGM contents = parse(in, encodingin, parseColumns);
			String kind = "";
			Field[] allFields = contents.getClass().getDeclaredFields();
			for (Field f : allFields) {
				String fieldname = f.getName();
				f.setAccessible(true); // 设置些属性是可以访问的
				String val = (String) f.get(contents); // 得到此属性的值
				if (val == null) {
					continue;
				}

				// //////////////////////////////////////////////////////
				// 正则匹配
				boolean flag = false;
				Matcher m = p.matcher(val);

				while (m.find()) {
					flag = true;
					if (fieldname.equals(dateColumn)) {
						// 对日期进行格式化
						sb.append(StringUtils.addmark(formatDate(m.group(1)),
								'"') + ",");
					} else {
						// 默认情况下，使用匹配后的内容
						sb.append(StringUtils.addmark(
								m.group(1).replace("D", ""), '"')
								+ ",");
					}
				}
				// 如果找不到括号，就直接置于空
				if (!flag) {
					sb.append(StringUtils.addmark("-", '"') + ",");
				}
				if (fieldname.equals("kind")) {
					kind = StringUtils.addmark(val, '"');
				}
			}
			sb.append(kind);
			sb.append("\r\n");
		}

		// 写入文件中
		try {
			FileUtils.write(outname, sb.toString(), onesgm.getEncodingout());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String formatDate(String date) {
		String[] DateArr = date.split("\\.");
		String year = DateArr[0];
		String month = String.format("%02d", Integer.parseInt(DateArr[1]));
		String day = String.format("%02d", Integer.parseInt(DateArr[2]));
		return year + month + day;
	}

	public static SGM parse(File sgmfile, String encoding, String parseColumns) {
		SGM sgm = null;
		String[] parseColumnArr = parseColumns.split("\\*");
		String[] nodes = new String[parseColumnArr.length];
		Document sgmDoc = null;
		try {
			sgmDoc = Jsoup.parse(sgmfile, encoding);
		} catch (Exception e) {
			System.out.println("sgm read fail:" + sgmfile.getName());
			e.printStackTrace();
		}
		for (int i = 0; i < parseColumnArr.length; i++) {
			try {
				nodes[i] = Converter.ToDBC(sgmDoc.select(parseColumnArr[i])
						.get(0).ownText());
			} catch (Exception e) {
				nodes[i] = "";
			}
		}

		String country = nodes[0];
		String pubdate = nodes[1];
		String kind = nodes[2];
		String regnum = nodes[3];

		// get line
		sgm = new SGM(country, pubdate, kind, regnum);
		return sgm;
	}
}
