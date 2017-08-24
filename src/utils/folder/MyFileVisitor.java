package utils.folder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class MyFileVisitor extends SimpleFileVisitor<Path> {
	private List<String> fileList;
	private List<String> extList;
	private String tmpdir;
	private int count;

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		// 访问文件夹之前调用
		return FileVisitResult.CONTINUE;
	}

	// @Override
	// public FileVisitResult postVisitDirectory(Path dir, IOException exec)
	// throws IOException {
	// // 访问文件夹之后调用
	// return FileVisitResult.CONTINUE;
	// }

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		// 正在访问一个文件时要干啥
		for (String oneext : extList) {
			if (file.toString().toLowerCase().endsWith("." + oneext.toLowerCase())) {
				fileList.add(file.toString());
				break;
			}
		}
		// 判断是否数量超过100W
		if (fileList.size() >= 1000000) {
			count++;
			try {
				FileUtils.writeLines(new File(tmpdir + "\\walkfile_" + count), "utf-8", fileList);
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileList.clear();
		}
		return FileVisitResult.CONTINUE;
	}

	// @Override
	// public FileVisitResult visitFileFailed(Path file, IOException exc)
	// throws IOException {
	// // 访问一个文件失败时要干啥
	// // System.out.println(exc.getMessage());
	// return FileVisitResult.CONTINUE;
	// }

	public MyFileVisitor(List<String> extList, String tempdir) {
		super();
		this.extList = extList;
		this.tmpdir = tempdir;
		this.fileList = new ArrayList<String>();
		this.count = 0;
	}

	public void writeLastList() {
		count++;
		try {
			FileUtils.writeLines(new File(tmpdir + "\\walkfile_" + count), "utf-8", fileList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileList.clear();
	}

	public List<String> getFileList() {
		return fileList;
	}
}