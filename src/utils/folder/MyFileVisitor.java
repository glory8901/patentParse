package utils.folder;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class MyFileVisitor extends SimpleFileVisitor<Path> {
	private List<String> fileList = new ArrayList<String>();
	private List<String> extList;

	// @Override
	// public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes
	// attrs)
	// throws IOException {
	// // 访问文件夹之前调用
	// dirCount++;
	// return FileVisitResult.CONTINUE;
	// }

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
			if (file.toString().toLowerCase().endsWith(oneext.toLowerCase())) {
				fileList.add(file.toString());
				break;
			}
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

	public MyFileVisitor(List<String> extList) {
		super();
		this.extList = extList;
	}

	public List<String> getFileList() {
		return fileList;
	}
}