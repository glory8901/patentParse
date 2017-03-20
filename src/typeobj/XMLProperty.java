package typeobj;

public class XML {
	private String header;
	private String encodingin;
	private String encodingout;
	private String rootnode;
	private String textnodes;
	private String existNodes;
	private boolean ifGetFirst;
	
	
	public boolean isIfGetFirst() {
		return ifGetFirst;
	}

	public void setIfGetFirst(boolean ifGetFirst) {
		this.ifGetFirst = ifGetFirst;
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
