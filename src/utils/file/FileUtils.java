package utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUtils {
	/**
     * Reader and Writer that uses the given charset.
     *
     * @param filename  read or write filename
     * @param encoding  using charset
     * @since 1.4
     */
	
	public static List<String> readFileToList(String filename, String encoding)
			throws IOException {
		// 以指定的编码读文件
		List<String> out = new ArrayList<String>();
		// 读入流
		File f = new File(filename);
		FileInputStream in = new FileInputStream(f);
		BufferedReader br = new BufferedReader(new InputStreamReader(in,
				encoding));
		String line = null;
		while ((line = br.readLine()) != null) {
			out.add(line);
		}
		br.close();
		return out;
	}
	
	public static String readFileToString(String filename, String encoding)
			throws IOException {
		// 以指定的编码读文件
		StringBuffer out = new StringBuffer();
		// 读入流
		File f = new File(filename);
		FileInputStream in = new FileInputStream(f);
		BufferedReader br = new BufferedReader(new InputStreamReader(in,
				encoding));
		String line = null;
		while ((line = br.readLine()) != null) {
			out.append(line);
		}
		br.close();
		return out.toString();
	}

	public static void writeMultiFile(Map<String, String> fileNameContent,
			String encoding) throws IOException {
		// 以指定的编码写文件
		FileOutputStream out = null;
		for (String key : fileNameContent.keySet()) {
			File f = new File(key);
			String text = fileNameContent.get(key);
			out = new FileOutputStream(f);
			out.write(text.getBytes(encoding));
		}
		out.close();
	}
	
	public static void write(String fileName, String text, String encoding)
			throws IOException {
		// 以指定的编码写文件
		FileOutputStream out = null;
		File f = new File(fileName);
		out = new FileOutputStream(f);
		out.write(text.getBytes(encoding));
		out.close();
	}
	
	

}
