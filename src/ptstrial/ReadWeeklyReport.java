package ptstrial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reader.ExcelReader;
import utils.CSVUtils;

public class ReadWeeklyReport {
	public List<String> loadconfig() {
		File con = new File("resources/config.csv");
		List<String> config = CSVUtils.importCsv(con);
		return config;
	}

	public List<String> loadColumnName() {
		File col = new File("resources/columns.csv");
		List<String> colName = CSVUtils.importCsv(col);
		return colName;
	}

	public List<String> readExcel(String excelPath, List<String> extract) {
		ExcelReader ereader = new ExcelReader();
		try {
			List<String> etext = null;
			if (excelPath.endsWith("xlsx")) {
				etext = ereader.readXlsx(excelPath, extract);
			} else {
				etext = ereader.readXls(excelPath, extract);
			}
			return etext;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, File[]> walkfolder(String folder) {
		File basedir = new File(folder);
		Map<String, File[]> map = new HashMap<String, File[]>();
		File[] months = basedir.listFiles();
		for (File m : months) {
			String month = m.getName();
			File[] excels = m.listFiles();
			map.put(month, excels);
		}
		return map;
	}

	public void getEachExcel() {
		ReadWeeklyReport r = new ReadWeeklyReport();
		List<String> config = r.loadconfig();
		String path = "D:\\数据管理\\交换数据解析核对\\专利试验系统\\试验系统年度报表";
		Map<String, File[]> fileMap = r.walkfolder(path);
		try {
			FileWriter fw = new FileWriter("out/eachexcel.csv");
			for (String month : fileMap.keySet()) {
				System.out.println(month);
				File[] excelPaths = fileMap.get(month);
				for (File excel : excelPaths) {
					fw.write(month + ",");
					System.out.println(excel.getName());
					String excelPath = excel.getAbsolutePath();
					List<String> etext = r.readExcel(excelPath, config);
					for (String s : etext) {
						fw.write(s + ",");
					}
					fw.write("\r\n");
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			FileOutputStream fw = new FileOutputStream("out/report.csv");
			StringBuffer sb = new StringBuffer();
			ReadWeeklyReport r = new ReadWeeklyReport();
			List<String> colName = r.loadColumnName();
			sb.append("月份,");
			for (String line : colName) {
				String[] lineArr = line.split(",");
				sb.append(lineArr[0] + ",");
			}
			sb.append("\r\n");
			File eachexcel = new File("out/eachexcel.csv");
			List<String> text = CSVUtils.importCsv(eachexcel);
			Map<String, Report> map = new HashMap<String, Report>();
			Report report = null;
			for (String line : text) {
				String[] lineArr = line.split(",");
				String key = lineArr[0];
				report = new Report();
				for (int i = 1; i < lineArr.length; i++) {
					try {
						Method method = Report.class.getMethod("setNum" + i,
								new Class[] { Integer.class });
						method.invoke(report, Integer.parseInt(lineArr[i]));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (map.containsKey(key)) {
					Report reportTemp = map.get(key);
					reportTemp.add(report);
				} else {
					map.put(key, report);
				}
			}

			for (String key : map.keySet()) {
				sb.append(key + ",");
				sb.append(map.get(key).toString());
			}
			fw.write(sb.toString().getBytes("gb2312"));
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Report {
	private Integer num1;
	private Integer num2;
	private Integer num3;
	private Integer num4;
	private Integer num5;
	private Integer num6;
	private Integer num7;
	private Integer num8;
	private Integer num9;
	private Integer num10;
	private Integer num11;
	private Integer num12;
	private Integer num13;
	private Integer num14;
	private Integer num15;
	private Integer num16;

	public Integer getNum1() {
		return num1;
	}

	@Override
	public String toString() {
		Field[] attrs = Report.class.getDeclaredFields();
		for (Field field : attrs) {
			field.setAccessible(true); // 设置些属性是可以访问的
			try {
				System.out.print(field.get(this)+",");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println();
		String res = num1 + "," + num2 + "," + num3 + "," + num4 + "," + num5
				+ "," + num6 + "," + num7 + "," + num8 + "," + num9 + ","
				+ num10 + "," + num11 + "," + num12 + "," + num13 + "," + num14
				+ "," + num15 + "," + num16 + "\r\n";
		return res;
	}

	public void add(Report report) {
		num1 += report.getNum1();
		num2 += report.getNum2();
		num3 += report.getNum3();
		num4 += report.getNum4();
		num5 += report.getNum5();
		num6 += report.getNum6();
		num7 += report.getNum7();
		num8 += report.getNum8();
		num9 += report.getNum9();
		num10 += report.getNum10();
		num11 += report.getNum11();
		num12 += report.getNum12();
		num13 += report.getNum13();
		num14 += report.getNum14();
		num15 += report.getNum15();
		num16 += report.getNum16();
	}

	public Integer getNum16() {
		return num16;
	}

	public void setNum16(Integer num16) {
		this.num16 = num16;
	}

	public void setNum1(Integer num1) {
		this.num1 = num1;
	}

	public Integer getNum2() {
		return num2;
	}

	public void setNum2(Integer num2) {
		this.num2 = num2;
	}

	public Integer getNum3() {
		return num3;
	}

	public void setNum3(Integer num3) {
		this.num3 = num3;
	}

	public Integer getNum4() {
		return num4;
	}

	public void setNum4(Integer num4) {
		this.num4 = num4;
	}

	public Integer getNum5() {
		return num5;
	}

	public void setNum5(Integer num5) {
		this.num5 = num5;
	}

	public Integer getNum6() {
		return num6;
	}

	public void setNum6(Integer num6) {
		this.num6 = num6;
	}

	public Integer getNum7() {
		return num7;
	}

	public void setNum7(Integer num7) {
		this.num7 = num7;
	}

	public Integer getNum8() {
		return num8;
	}

	public void setNum8(Integer num8) {
		this.num8 = num8;
	}

	public Integer getNum9() {
		return num9;
	}

	public void setNum9(Integer num9) {
		this.num9 = num9;
	}

	public Integer getNum10() {
		return num10;
	}

	public void setNum10(Integer num10) {
		this.num10 = num10;
	}

	public Integer getNum11() {
		return num11;
	}

	public void setNum11(Integer num11) {
		this.num11 = num11;
	}

	public Integer getNum12() {
		return num12;
	}

	public void setNum12(Integer num12) {
		this.num12 = num12;
	}

	public Integer getNum13() {
		return num13;
	}

	public void setNum13(Integer num13) {
		this.num13 = num13;
	}

	public Integer getNum14() {
		return num14;
	}

	public void setNum14(Integer num14) {
		this.num14 = num14;
	}

	public Integer getNum15() {
		return num15;
	}

	public void setNum15(Integer num15) {
		this.num15 = num15;
	}

}
