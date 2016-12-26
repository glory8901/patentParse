package typeobj;

public class Filter {
	private String incdirName;
	private String excdirName;
	private String incfileName;
	private String excfileName;
	private String extension;
	private String outName;
	public String getIncdirName() {
		return incdirName;
	}
	public void setIncdirName(String incdirName) {
		this.incdirName = incdirName;
	}
	public String getExcdirName() {
		return excdirName;
	}
	public void setExcdirName(String excdirName) {
		this.excdirName = excdirName;
	}
	public String getIncfileName() {
		return incfileName;
	}
	public void setIncfileName(String incfileName) {
		this.incfileName = incfileName;
	}
	public String getExcfileName() {
		return excfileName;
	}
	public void setExcfileName(String excfileName) {
		this.excfileName = excfileName;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getOutName() {
		return outName;
	}
	public void setOutName(String outName) {
		this.outName = outName;
	}
	public Filter(String incdirName, String excdirName, String incfileName,
			String excfileName, String extension, String outName) {
		super();
		this.incdirName = incdirName;
		this.excdirName = excdirName;
		this.incfileName = incfileName;
		this.excfileName = excfileName;
		this.extension = extension;
		this.outName = outName;
	}
	
	
	

}
