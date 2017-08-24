package typeobj;

/**
 * XML文件读取的一些特征
 * 
 * @author lenovo
 *
 */

public class XMLProperty {
	private String fields;
	private String encodingin;
	private String encodingout;
	private String rootnode;
	private String textnodes;
	private String existNodes;
	private boolean ifGetFirst;
	private String pathReplace;

	public String getPathReplace() {
		return pathReplace;
	}

	public void setPathReplace(String pathReplace) {
		this.pathReplace = pathReplace;
	}

	public boolean isIfGetFirst() {
		return ifGetFirst;
	}

	public void setIfGetFirst(boolean ifGetFirst) {
		this.ifGetFirst = ifGetFirst;
	}


	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
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

	public String getRootnode() {
		return rootnode;
	}

	public void setRootnode(String rootnode) {
		this.rootnode = rootnode;
	}

	public String getTextnodes() {
		return textnodes;
	}

	public void setTextnodes(String textnodes) {
		this.textnodes = textnodes;
	}

	public String getExistNodes() {
		return existNodes;
	}

	public void setExistNodes(String existNodes) {
		this.existNodes = existNodes;
	}

}
