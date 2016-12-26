package utils.file;

import java.io.UnsupportedEncodingException;

public class InferEncodeErr {
	/**
	 * java判断获取到的中文字符串是否乱码
	 * 用getBytes(encoding)：返回字符串的一个byte数组 当b[0]为 63时，应该是转码错误
	 *  A、不乱码的汉字字符串：
	 * 1、encoding用GB2312时，每byte是负数； 2、encoding用ISO8859_1时，b[i]全是63。
	 *  B、乱码的汉字字符串：
	 * 1、encoding用ISO8859_1时，每byte也是负数； 2、encoding用GB2312时，b[i]大部分是63。
	 *  C、英文字符串
	 * 1、encoding用ISO8859_1和GB2312时，每byte都大于0； 
	 * 
	 * 总结：给定一个字符串，用getBytes("iso8859_1")
	 * 1、如果b[i]有63，不用转码； A-2 
	 * 2、如果b[i]全大于0，那么为英文字符串，不用转码； B-1
	 * 3、如果b[i]有小于0的，那么已经乱码，要转码。 C-1
	 */

	public String toGb2312(String str) {
		if (str == null)
			return null;
		String retStr = str;
		byte b[];
		try {
			b = str.getBytes("ISO8859_1");
			for (int i = 0; i < b.length; i++) {
				byte b1 = b[i];
				if (b1 == 63)
					break; // 1
				else if (b1 > 0)
					continue;// 2
				else if (b1 < 0) { // 不可能为0，0为字符串结束符
					// 小于0乱码
					retStr = new String(b, "GB2312");
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
		}
		return retStr;
	}
}
