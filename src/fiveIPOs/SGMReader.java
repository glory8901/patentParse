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
import utils.file.FilesFilter;
import utils.file.FolderRecursion;
import utils.file.FolderUtils;

public class SGMReader {
	
	public void readSGMAndWrite() throws Exception {
		// 读取配置
		Loadconf conf = new Loadconf();
		List<SGM> allSgms = conf.loadSGMconfig();
		String dataRootDir = conf.getDataRootDir();
		String destDir = conf.getDestDir();
		System.out.println("load JP configure complete!");
		// begin to read
		readSGM(allSgms, dataRootDir, destDir);

	}

	public static void readSGM(List<SGM> allSgms, String dataRootDir,
			String destDir) throws IllegalArgumentException,
			IllegalAccessException {
		// 开始逐个处理每一个配置
		for (SGM onesgm : allSgms) {
			// 输入与输出
			String path = dataRootDir + onesgm.getCountry() + "\\"
					+ onesgm.getDatatype();
			String outname = destDir + onesgm.getCountry() + "\\"
					+ onesgm.getOutfile();
			System.out.println(path + "--->" + outname);

			String datecolumn = onesgm.getDateLabel();// 专门处理日期这一列
			// file handle
			String excdirs = onesgm.getExcdir();
			String excfiles = onesgm.getExcfile();
			String incfiles = onesgm.getIncfile();
			String incdirs = onesgm.getIncdir();
			String parseColumns = onesgm.getLabels();
			
			// 文件夹操作：获取并筛选得到的所有sgm文件
			List<File> files = openIO(path, excdirs, excfiles, incdirs,
					".sgm");

			/////////////////////////////////////////////////////////////////
			// 开始使用正则匹配其中的括号中的内容
			String reg = "\\((.*?)\\)";
			Pattern p = Pattern.compile(reg);
			StringBuffer sb = new StringBuffer();
			sb.append(onesgm.getHeader() + "\r\n");

			for (File in : files) {
				SGM contents = parse(in, onesgm.getEncodingin(), parseColumns);
				String kind = "";
				Field[] allFields = contents.getClass().getDeclaredFields();
				for (Field f : allFields) {
					String fieldname = f.getName();
					f.setAccessible(true); // 设置些属性是可以访问的
					String val = (String) f.get(contents); // 得到此属性的值
					if (val == null) {
						continue;
					}

					/////////////////////////////////////////////////////////////////
					// 正则匹配
					boolean flag = false;
					Matcher m = p.matcher(val);

					while (m.find()) {
						flag = true;
						if (fieldname.equals(datecolumn)) {
							// 对日期进行格式化
							sb.append(StringUtils.addmark(formatDate(m.group(1)),
									'"') + ",");
						} else {
							// 默认情况下，使用匹配后的内容
							sb.append(StringUtils.addmark(m.group(1).replace("D", ""), '"') + ",");
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
				FileUtils
						.write(outname, sb.toString(), onesgm.getEncodingout());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<File> openIO(String path, String excdirs,
			String excfiles, String incdirs, String incfiles) {
		// 文件夹获取与筛选,注意是单次筛选
		List<File> allFolders = FolderRecursion.getAllFoldersList(path);
		// filter
		List<File> outfolderList = FilesFilter.filter(allFolders, incdirs,
				excdirs);
		// 文件获取与筛选
		List<File> fileList = FolderUtils.getFiles(outfolderList);
		// filter
		List<File> files = FilesFilter.filter(fileList, incfiles, excfiles);
		return files;
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
		try {
			Document sgmDoc = Jsoup.parse(sgmfile, encoding);
			String country = sgmDoc.select(parseColumnArr[0]).text();
			String pubdate = sgmDoc.select(parseColumnArr[1]).text();
			String kind = sgmDoc.select(parseColumnArr[2]).text();
			String regnum = sgmDoc.select(parseColumnArr[3]).text();
			// line
			sgm = new SGM(Converter.ToDBC(country), Converter.ToDBC(pubdate),
					Converter.ToDBC(kind), Converter.ToDBC(regnum));
		} catch (Exception e) {
			System.out.println("sgm file error:" + sgmfile.getName());
			e.printStackTrace();
		}
		return sgm;
	}
	
	public static void replaceStrs(){
		
	}

}
