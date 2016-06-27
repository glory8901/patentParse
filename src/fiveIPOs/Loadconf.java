package fiveIPOs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Loadconf {
	private String dataRootDir;
	private String destDir;

	public String getDataRootDir() {
		return dataRootDir;
	}

	public void setDataRootDir(String dataRootDir) {
		this.dataRootDir = dataRootDir;
	}

	public String getDestDir() {
		return destDir;
	}

	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}

	public List<NumberLst> loadconfig() {
		// 读取号单的配置，从而分别处理每一个配置
		List<NumberLst> configList = new ArrayList<NumberLst>();

		File configFile = new File(System.getProperty("user.dir")
				+ "/conf/config.xml");
		Document doc = null;

		try {
			doc = Jsoup.parse(configFile, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 选取xml中的部分标签
		Elements numlst = doc.select("numlist");
		dataRootDir = doc.select("basedir").text();
		destDir = doc.select("destdir").text();

		for (Element element : numlst) {
			NumberLst noLst = new NumberLst();
			String country = element.select("country").text();
			String datatype = element.select("dataType").text();
			String strFound = element.select("strFound").text();
			String header = element.select("header").text();
			String outfile = element.select("outfile").text();
			String encodingin = element.select("encodingin").text();
			String encodingout = element.select("encodingout").text();
			String firstrow = element.select("firstrow").text();
			String columns = element.select("columns").text();
			String sep = element.select("sep").text();
			String re = element.select("match").text();
			String rep = element.select("rep").text();

			noLst.setCountry(country);
			noLst.setDatatype(datatype);
			noLst.setStrFound(strFound);
			noLst.setHeader(header);
			noLst.setOutfile(outfile);
			noLst.setEncodingin(encodingin);
			noLst.setEncodingout(encodingout);
			noLst.setFirstrow(firstrow);
			noLst.setColumns(columns);
			noLst.setSep(sep);
			noLst.setRe(re);
			noLst.setRep(rep);
			configList.add(noLst);
		}
		return configList;
	}

	public List<SGM> loadSGMconfig() {
		// 读取号单的配置，从而分别处理每一个配置
		List<SGM> configList = new ArrayList<SGM>();

		File configFile = new File(System.getProperty("user.dir")
				+ "/conf/config.xml");
		Document doc = null;

		try {
			doc = Jsoup.parse(configFile, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 选取xml中的部分标签
		Elements sgmlst = doc.select("sgm");
		dataRootDir = doc.select("basedir").text();
		destDir = doc.select("destdir").text();

		for (Element element : sgmlst) {
			SGM sgm = new SGM();
			String country = element.select("country").text();
			String datatype = element.select("dataType").text();
			String excfile = element.select("excfile").text();
			String excdir = element.select("excdir").text();
			String incfile = element.select("incfile").text();
			String incdir = element.select("incdir").text();
			String header = element.select("header").text();
			String outfile = element.select("outfile").text();
			String encodingin = element.select("encodingin").text();
			String encodingout = element.select("encodingout").text();
			String labels = element.select("labels").text();
			String dateLabel = element.select("date-label").text();

			//set to object
			sgm.setCountry(country);
			sgm.setDatatype(datatype);
			sgm.setExcfile(excfile);
			sgm.setExcdir(excdir);
			sgm.setIncdir(incdir);
			sgm.setIncfile(incfile);
			sgm.setHeader(header);
			sgm.setOutfile(outfile);
			sgm.setEncodingin(encodingin);
			sgm.setEncodingout(encodingout);
			sgm.setLabels(labels);
			sgm.setDateLabel(dateLabel);
			// add to list
			configList.add(sgm);
		}
		return configList;
	}
}
