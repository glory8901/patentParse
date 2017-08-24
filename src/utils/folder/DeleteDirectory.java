package utils.folder;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class DeleteDirectory implements FileVisitor {

	boolean deleteFileByFile(Path file) throws IOException {
		return Files.deleteIfExists(file);
	}

	@Override
	public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
//		if (exc == null) {
//			System.out.println("Visited: " + (Path) dir);
//			boolean success = deleteFileByFile((Path) dir);
//
//			if (success) {
//				System.out.println("Deleted: " + (Path) dir);
//			} else {
//				System.out.println("Not deleted: " + (Path) dir);
//			}
//		} else {
//			throw exc;
//		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
		boolean success = deleteFileByFile((Path) file);

		if (success) {
			System.out.println("删除 临时文件: " + (Path) file);
		} else {
			System.out.println("删除临时文件失败！: " + (Path) file);
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
		// report an error if necessary

		return FileVisitResult.CONTINUE;
	}
}
