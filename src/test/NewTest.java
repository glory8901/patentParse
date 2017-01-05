package test;

import java.io.File;
import java.io.IOException;

public class NewTest {
	public static void main(String[] args) throws IOException {
		String outName = "D:\\Download\\temp\\kr_legal_design_xml.csv";
		// int index = outName.lastIndexOf(".");
		// String newOutfileName = outName.substring(0, index) + "_" + 1
		// + outName.substring(index);
		File outFile = new File(outName);
		if (outFile.exists()) {
			int index = outName.lastIndexOf(".");
			for (int i = 1; i < 10; i++) {
				String newOutfileName = outName.substring(0, index) + "_" + i
						+ outName.substring(index);
				if (!new File(newOutfileName).exists()) {
					outFile = new File(newOutfileName);
					System.out.println(newOutfileName);
					break;
				}
			}
		}
	}
}
