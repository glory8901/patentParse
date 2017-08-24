package typeobj;

/**
 * SGM文件读取的一些特征
 * @author lenovo
 *
 */

public class SGMProperty {
	private String country;
	private String pubdate;
	private String kind;
	private String regnum;

	private String header;
	private String encodingin;
	private String encodingout;
	private String labels;
	private String dateLabel;

	public SGMProperty(String country, String pubdate, String kind, String regnum) {
		super();
		this.country = country;
		this.pubdate = pubdate;
		this.kind = kind;
		this.regnum = regnum;
	}

	public SGMProperty() {
		super();
	}

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

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
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

}
