package po;

/**
 * 这里的字段名称与数据库中是保持一致的
 * 本来是想根据配置中的名称自动生成字段名称，但是目前没法做到。要保持与配置名称和数据库名称一致。
 * 
 * @author glory
 *
 */
public class PatentPo {
	private String pub_number;
	private String pub_country;
	private String kind;
	private String pub_date;
	private String app_country;
	private String app_number;
	private String app_date;
	private String ipc;
	private String title;
	private String abs;
	private String applicant_name;
	private String applicant_address;
	private String inventor;
	private String lang;
	private String description;
	private String technical_field;
	private String background_art;
	private String disclosure;
	private String description_of_drawings;
	private String mode_for_invention;
	private String claims;
	private String path;

	public String getPub_number() {
		return pub_number;
	}

	public void setPub_number(String pub_number) {
		this.pub_number = pub_number;
	}

	public String getPub_country() {
		return pub_country;
	}

	public void setPub_country(String pub_country) {
		this.pub_country = pub_country;
	}

	
	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getPub_date() {
		return pub_date;
	}

	public void setPub_date(String pub_date) {
		this.pub_date = pub_date;
	}

	public String getApp_country() {
		return app_country;
	}

	public void setApp_country(String app_country) {
		this.app_country = app_country;
	}

	public String getApp_number() {
		return app_number;
	}

	public void setApp_number(String app_number) {
		this.app_number = app_number;
	}

	public String getApp_date() {
		return app_date;
	}

	public void setApp_date(String app_date) {
		this.app_date = app_date;
	}

	public String getIpc() {
		return ipc;
	}

	public void setIpc(String ipc) {
		this.ipc = ipc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAbs() {
		return abs;
	}

	public void setAbs(String abs) {
		this.abs = abs;
	}

	public String getApplicant_name() {
		return applicant_name;
	}

	public void setApplicant_name(String applicant_name) {
		this.applicant_name = applicant_name;
	}

	public String getApplicant_address() {
		return applicant_address;
	}

	public void setApplicant_address(String applicant_address) {
		this.applicant_address = applicant_address;
	}

	public String getInventor() {
		return inventor;
	}

	public void setInventor(String inventor) {
		this.inventor = inventor;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClaims() {
		return claims;
	}

	public void setClaims(String claims) {
		this.claims = claims;
	}

	public String getTechnical_field() {
		return technical_field;
	}

	public void setTechnical_field(String technical_field) {
		this.technical_field = technical_field;
	}

	public String getBackground_art() {
		return background_art;
	}

	public void setBackground_art(String background_art) {
		this.background_art = background_art;
	}

	public String getDisclosure() {
		return disclosure;
	}

	public void setDisclosure(String disclosure) {
		this.disclosure = disclosure;
	}

	public String getDescription_of_drawings() {
		return description_of_drawings;
	}

	public void setDescription_of_drawings(String description_of_drawings) {
		this.description_of_drawings = description_of_drawings;
	}

	public String getMode_for_invention() {
		return mode_for_invention;
	}

	public void setMode_for_invention(String mode_for_invention) {
		this.mode_for_invention = mode_for_invention;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
