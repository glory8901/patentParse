package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class parseData {
	private String strSource;// 数据源

	public parseData(String s) {
		this.strSource = s;
	}

	public String getInfo(String name, String[] split)// 名称，值，分隔符
	{
		String str = name + split[0] + "(.+?)" + split[1];
		// System.out.println(str);
		Pattern pattern = Pattern.compile(str);// 匹配的模式
		Matcher matcher = pattern.matcher(this.strSource);
		String value = "";
		boolean isFind = false;
		if (matcher.find()) {
			value = matcher.group(1);
		} else// 可能是最后一个字符
		{
			pattern = Pattern.compile(name + split[0] + "(.+?)" + "$");// $
																		// 表示为限定结尾
			matcher = pattern.matcher(this.strSource);
			if (matcher.find()) {
				value = matcher.group(1);
			}

		}
		return value;
	}

}