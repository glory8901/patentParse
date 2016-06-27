package utils.file;

/*
 * 文件属性：文件名、修改时间、大小
 * @author fxh
 * @email：none@sina.com
 * @version 0.1
 */
public class FileProperties {

	private String filename;
	private String time;
	private String size;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FileProperties)) {
			return false;
		}
		if (((FileProperties) obj).getFilename().equals(filename)
				&& ((FileProperties) obj).getTime().equals(time)
				&& ((FileProperties) obj).getSize().equals(size)) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return filename.hashCode() * 2 + time.hashCode() * 3 + size.hashCode()
				* 5;
	}

	public FileProperties(String filename, long time, long size) {
		this.filename = filename;
		this.time = time + "";
		this.size = size + "";
	}
}
