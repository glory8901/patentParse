package utils;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	public List<List<String>> readExcel(String excelPath, String[] fields)
			throws Exception {

		List<String> selectColList = Arrays.asList(fields);
		List<Integer> selectIndexs = new ArrayList<Integer>();
		List<List<String>> result = new ArrayList<List<String>>();

		Workbook wb = new HSSFWorkbook(new FileInputStream(excelPath));
		Sheet sheet = wb.getSheetAt(0);

		// 加载标题行
		Row firstRow = sheet.getRow(0);
		int columnNum = firstRow.getPhysicalNumberOfCells();
		for (int i = 0; i < columnNum; i++) {
			String value = getStringCellValue(firstRow.getCell(i));
			if (selectColList.contains(value)) {
				selectIndexs.add(i);
			}
		}

		// 读取数据
		int rowNum = sheet.getLastRowNum();
		for (int i = 1; i <= rowNum; i++) {

			Row row = sheet.getRow(i);
			List<String> rowSelect = new ArrayList<String>();
			// String xml = null;
			for (Integer index : selectIndexs) {
				String cell = getStringCellValue(row.getCell(index));
				rowSelect.add(cell);
			}
			result.add(rowSelect);
			// xml = convertToXml(rowSelect, selectCol);
			// System.out.println(xml);
		}
		wb.close();
		return result;
	}

	public static String convertToXml(List<String> line, String[] labels) {
		String xml = "<rec>\r\n";
		for (int i = 0; i < line.size(); i++) {
			xml += "\t<" + labels[i] + ">" + line.get(i) + "</" + labels[i]
					+ ">\r\n";
		}
		xml += "</rec>\r\n";
		return xml;

	}

	public List<String> readXls(String excelPath, List<String> extract)
			throws Exception {
		Workbook wb = new HSSFWorkbook(new FileInputStream(excelPath));
		Sheet sheet = wb.getSheetAt(2);
		// 读取数据
		List<String> result = new ArrayList<String>();
		for (String cellposition : extract) {
			String[] line = cellposition.split(",");
			// String cellname = line[0];
			// System.out.println(cellname);
			int cellrow = Integer.parseInt(line[1]);
			int cellcol = Integer.parseInt(line[2]);
			// row and col
			Row row = sheet.getRow(cellrow - 1);
			String cell = getStringCellValue(row.getCell(cellcol - 1));
			result.add(cell);
		}
		wb.close();
		return result;
	}

	public List<String> readXlsx(String excelPath, List<String> extract)
			throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excelPath));
		XSSFSheet sheet = wb.getSheetAt(2);
		// 读取数据
		List<String> result = new ArrayList<String>();
		for (String cellposition : extract) {
			String[] line = cellposition.split(",");
			// String cellname = line[0];
			// System.out.println(cellname);
			int cellrow = Integer.parseInt(line[1]);
			int cellcol = Integer.parseInt(line[2]);
			// row and col
			XSSFRow row = sheet.getRow(cellrow - 1);
			String cell = getStringCellValue(row.getCell(cellcol - 1));
			// System.out.println(cellrow + "," + cellcol + row.getCell(cellcol
			// - 1));
			result.add(cell);
		}
		wb.close();
		return result;
	}

	/** 
	* 获取合并单元格的值 
	* @param sheet 
	* @param row 
	* @param column 
	* @return String
	*/ 
	public String getMergedRegionValue(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();

		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();

			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					Row fRow = sheet.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);

					return getStringCellValue(fCell);
				}
			}
		}
		return null;
	}
	
	protected String getStringCellValue(Cell cell) {
		String strCell = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			if (strCell.contains("*")) {
				strCell = strCell.replace("*", "×");
			}
			if (strCell == null) {
				strCell = "";
			}
			break;
		case Cell.CELL_TYPE_NUMERIC:
			DecimalFormat df = new DecimalFormat("0");
			Double number = cell.getNumericCellValue();
			strCell = df.format(number);
			// strCell = Double.toString(number);
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		case Cell.CELL_TYPE_FORMULA:
			try {
				if (strCell.indexOf("0") == 0) {
					strCell = String.format("%.5f", cell.getNumericCellValue());
					String newstr = "";
					for (int i = 0; i < strCell.length(); i++) {
						String s = strCell.substring(i, i + 1);
						newstr = newstr + s;
						if (".".equals(s)) {
							continue;
						}
						if (!"0".equals(s)
								&& "0".equals(strCell.substring(i + 1, i + 2))) {
							break;
						}
					}
					strCell = newstr;
				} else {
					strCell = Double.toString(cell.getNumericCellValue());
					if (strCell.indexOf(".0") == strCell.length() - 2) {
						strCell = strCell.substring(0, strCell.indexOf("."));
					}
				}
			} catch (IllegalStateException e) {
				strCell = cell.getStringCellValue();
			}
			break;
		default:
			strCell = "";
			break;
		}
		if ("".equals(strCell) || strCell == null) {
			return "";
		}
		if (cell == null) {
			return "";
		}
		return strCell;
	}
}
