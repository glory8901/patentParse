package test;

import java.io.File;
import java.io.IOException;

public class NewTest {
	public static void main(String[] args) throws IOException {
		String outFileName = "D:\\Download\\temp\\kr_kpa_sgm.csv";
		String outdir = outFileName.substring(0, outFileName.lastIndexOf("\\"));
		System.out.println(outdir);
		File outDir = new File(outdir);
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
	}
}
