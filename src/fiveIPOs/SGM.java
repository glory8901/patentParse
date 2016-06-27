package fiveIPOs;

public class SGM {
	private String country;
	private String pubdate;
	private String kind;
	private String regnum;
	private String datatype;
	
	private String excdir;
	private String excfile;
	private String incdir;
	private String incfile;

	private String header;
	private String outfile;
	private String encodingin;
	private String encodingout;
	private String labels;
	private String dateLabel;


	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPubdate() {
		return pubdate;
	}

	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getRegnum() {
		return regnum;
	}

	public void setRegnum(String regnum) {
		this.regnum = regnum;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getOutfile() {
		return outfile;
	}

	public void setOutfile(String outfile) {
		this.outfile = outfile;
	}

	public String getEncodingin() {
		return encodingin;
	}

	public void setEncodingin(String encodingin) {
		this.encodingin = encodingin;
	}

	public String getEncodingout() {
		return encodingout;
	}

	public void setEncodingout(String encodingout) {
		this.encodingout = encodingout;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getDateLabel() {
		return dateLabel;
	}

	public void setDateLabel(String dateLabel) {
		this.dateLabel = dateLabel;
	}

	public SGM(String country, String pubdate, String kind, String regnum) {
		super();
		this.country = country;
		this.pubdate = pubdate;
		this.kind = kind;
		this.regnum = regnum;
	}

	
	
	public String getExcdir() {
		return excdir;
	}

	public void setExcdir(String excdir) {
		this.excdir = excdir;
	}

	public String getExcfile() {
		return excfile;
	}

	public void setExcfile(String excfile) {
		this.excfile = excfile;
	}

	public String getIncdir() {
		return incdir;
	}

	public void setIncdir(String incdir) {
		this.incdir = incdir;
	}

	public String getIncfile() {
		return incfile;
	}

	public void setIncfile(String incfile) {
		this.incfile = incfile;
	}

	public SGM() {
		super();
	}

}
