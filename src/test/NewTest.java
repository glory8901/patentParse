package test;

import java.io.File;
import java.io.IOException;

public class NewTest {
	public static void main(String[] args) throws IOException {
		String tempdir = "D:\\Download\\temp\\kr_legal";
		File tempFile = new File(tempdir);
		if (!tempFile.exists()) {
			tempFile.mkdirs();
		}
	}
}
