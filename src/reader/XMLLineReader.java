package reader;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import typeobj.XMLProperty;
import utils.StringUtils;

public class XMLLineReader extends XMLReader {

	public XMLLineReader(XMLProperty xmlProp) {
		super(xmlProp);
	}

	public String br2nl(String html) {
		if (html == null || html.equals("") || !html.contains("<"))
			return html;
		html = html.replaceAll("<br.*?>", "").replace("\n", "");
		Document document = Jsoup.parse(html);
		document.outputSettings(new Document.OutputSettings()
				.prettyPrint(false));// makes html() preserve linebreaks and
										// spacing
		// 如果需要br换行，在这里设置
		// document.select("br").append("\\n");
		document.select("p").prepend("\\n\\n");
		String s = document.html().replaceAll("\\\\n", "\n");
		String cleaned = Jsoup.clean(s, "", Whitelist.none(),
				new Document.OutputSettings().prettyPrint(false));
		// System.out.println(cleaned);
		String striped_text = cleaned.replace(" ", "")
				.replaceAll("[\\s]*?\n[\\s]+", "\n").trim();
		return striped_text;
	}

	/**
	 * 得到单个节点的内容
	 * 
	 * @param rootEle
	 * @param nodeName
	 * @param ifGetFirst
	 * @return
	 */
	public String getText(Element rootEle, String nodeName) {
		String sep = ";;";
		Elements nodeEls = rootEle.select(nodeName);
		if (nodeEls.size() == 0) {
			// 无值
			return "";
		} else if (nodeEls.size() == 1 || ifGetFirst) {
			// 单值
			Element ele = nodeEls.get(0);
			String html = ele.html();
			return br2nl(html);

			// // 原来的做法
			// StringBuffer sb = new StringBuffer();
			// List<Node> dns = ele.childNodes();
			// if (dns.isEmpty()) {
			// return ele.ownText().trim();
			// }
			// for (Node el : dns) {
			// if (el instanceof Element) {
			//
			// // 原来的做法
			// String tagName = el.nodeName();
			// String tagText = ((Element) el).text().trim();
			// if (tagName.equals("p")) {
			// sb.append(tagText + "\n");
			// } else {
			// sb.append(tagText);
			// }
			// } else if (el instanceof TextNode) {
			// sb.append(el.toString().trim());
			// }
			// }
			//
			// // 单值
			// return sb.toString().trim();
		} else {
			// 多值
			List<String> itemsArr = new ArrayList<String>();
			for (Element node : nodeEls) {
				if (node.text().trim().equals("")) {
					continue;
				}
				//使用上面的处理方式进行处理
				itemsArr.add(br2nl(node.html()));
			}
			return StringUtils.join(itemsArr, sep);
		}
	}
}
