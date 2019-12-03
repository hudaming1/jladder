package org.hum.nettyproxy.common.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.enumtype.HttpMethodEnum;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * ByteBuf&Http帮助类
 * @author huming
 */
public class ByteBufHttpHelper {

	private static final Logger logger = LoggerFactory.getLogger(ByteBufHttpHelper.class);
	private static final byte RETURN_LINE = 10;
	private static final byte[] _2_ReturnLine = (Constant.RETURN_LINE + Constant.RETURN_LINE).getBytes();
	private static String WEB_ROOT;
	private static ByteBuf _404ByteBuf;
	private static ByteBuf _500ByteBuf;
	/**
	 * 关于浏览器代理模式下的307重定向：
	 *    Chrome和Firefox的要求都比较严格，甚至连一个空格都不能多
	 */
	private static final byte[] _307 = ("HTTP/1.1 307 TemporaryRedirect" + Constant.RETURN_LINE + "Location:").getBytes();

	static {
		try {
			WEB_ROOT = NettyProxyContext.getConfig().getWebroot();
			
			if (WEB_ROOT == null || WEB_ROOT.isEmpty()) {
				WEB_ROOT = ByteBufHttpHelper.class.getClassLoader().getResource("").toURI().getPath();
				WEB_ROOT += "webapps";
			}

			_404ByteBuf = readFile(Unpooled.directBuffer(), new File(WEB_ROOT + "/404.html"));
			_500ByteBuf = readFile(Unpooled.directBuffer(), new File(WEB_ROOT + "/500.html"));
		} catch (Exception e) {
			WEB_ROOT = "";
			logger.error("init netty-simple-http-server error, can't init web-root-path", e);
		}
	}
	
	public static String getWebRoot() {
		return WEB_ROOT;
	}
	
	public static ByteBuf _404ByteBuf() {
		return _404ByteBuf;
	}
	
	public static ByteBuf _500ByteBuf() {
		return _500ByteBuf;
	}

	public static String readLine(ByteBuf byteBuf) {
		StringBuilder sbuilder = new StringBuilder();

		byte b = -1;
		while (byteBuf.isReadable() && (b = byteBuf.readByte()) != RETURN_LINE) {
			sbuilder.append((char) b);
		}

		return sbuilder.toString().trim();
	}

	public static ByteBuf readFileFromWebapps(ByteBuf byteBuf, String filePath) throws IOException {
		return readFile(byteBuf, new File(WEB_ROOT + "/" + filePath));
	}
	
	/**
	 * 将file中的内容填充到byteBuf中
	 * @param byteBuf
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ByteBuf readFile(ByteBuf byteBuf, File file) throws IOException {
		BufferedInputStream fileInputStream = null;
		try {
			fileInputStream = new BufferedInputStream(new FileInputStream(file));
			int read = -1;
			while ((read = fileInputStream.read()) != -1) {
				byteBuf.writeByte((byte) read);
			}
			return byteBuf;
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
	}

	/**
	 * 读取文件（要求是文本文件）
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile2String(File file) throws FileNotFoundException, IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			StringBuilder sbuilder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sbuilder.append(line);
			}
			return sbuilder.toString();
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}
	
	/**
	 * 来检测是否是http/https请求
	 * <pre>
		判断方式：通过读取前7个字节来确定是不是http协议
	 * </pre>
	 * @param byteBuf
	 * @return
	 */
	public static boolean isHttpProtocol(Object msg) {
		if (msg instanceof HttpRequest) {
    		return true;
    	} else if (!(msg instanceof ByteBuf)) {
    		return false;
    	}
		ByteBuf byteBuf = (ByteBuf) msg;
		// 我们只是简单通过前7个字节来判断是否是http协议（XXX 方法不算很准确，后续再优化 ）
		byte[] leakHttpMethodBytes = new byte[7];
		byteBuf.getBytes(0, leakHttpMethodBytes);
		for(byte[] methodBytes : HttpMethodEnum.getByteArray()) {
			if (ByteUtil.isEquals(leakHttpMethodBytes, methodBytes)) {
				return true;
			}
		}
    	return false;
	}
	
	public static ByteBuf create307Response(ByteBuf directBuffer, String relocation) {
		directBuffer.writeBytes(_307);
		directBuffer.writeBytes(relocation.getBytes());
		directBuffer.writeBytes(Constant.RETURN_LINE.getBytes()); // end of header
		directBuffer.writeBytes(Constant.RETURN_LINE.getBytes()); // end of http-response
		return directBuffer;
	}

    /**
     * ByteBuf -> HttpRequest
     * @param byteBuf
     * @return
     */
    public static HttpRequest decode(ByteBuf byteBuf) {
    	if (byteBuf == null) {
    		return null;
    	}
    	try {
	    	HttpRequest request = new HttpRequest();
	    	// read request-line
	    	request.setLine(readLine(byteBuf));
	    	
	    	// parse to method
	    	request.setMethod(request.getLine().split(" ")[0]);
	    	
	    	// read request-header
	    	String line = null;
	    	while (!(line = readLine(byteBuf)).equals("")) {
	    		int splitIndex = line.indexOf(":");
	    		
	    		if (splitIndex <= 0) {
	    			continue;
	    		}
	    		
	    		String key = line.substring(0, splitIndex).trim();
	    		String value = line.substring(splitIndex + 1, line.length()).trim();
	    		request.getHeaders().put(key, value);
	    		
	    		// parse to host and port
	    		if (Constant.HTTP_HOST_HEADER.equalsIgnoreCase(key)) {
	    			if (value.contains(":")) {
	    				String[] arr = value.split(":");
		    			request.setHost(arr[0]);
		    			request.setPort(Integer.parseInt(arr[1]));
	    			} else {
						request.setHost(value);
						request.setPort(Constant.HTTPS_METHOD.equalsIgnoreCase(request.getMethod()) ? Constant.DEFAULT_HTTPS_PORT : Constant.DEFAULT_HTTP_PORT);
	    			}
	    		}
	    	}
	    	
	    	// TODO POST请求使用Content-Length作为标准
	    	
	    	// read request-body
	    	StringBuilder body = new StringBuilder();
	    	while (!(line = readLine(byteBuf)).equals("")) {
	    		body.append(line);
	    	}
	    	request.setBody(body.toString());
	    	
	    	// reference ByteBuf
	    	request.setByteBuf(byteBuf);
	    	
	    	// fixbug: 有些服务器要求比较严格，目前看多几换行比少换行更能有效的访问到正确的URL，如果删除这段\r\n\r\n代码，再访问这个URL就会没有响应：http://pos.baidu.com/auto_dup?psi=2825367ec24f0f2315cbfc2e69f5a2c0&di=0&dri=0&dis=0&dai=0&ps=0&enu=encoding&dcb=___baidu_union_callback_&dtm=AUTO_JSONP&dvi=0.0&dci=-1&dpt=none&tsr=0&tpr=1560287949982&ti=%E8%BF%993%E4%B8%AA%E6%9C%89%E5%85%B3%E6%8A%A4%E8%82%A4%E7%9A%84%E5%B0%8F%E7%AA%8D%E9%97%A8%EF%BC%8C%E7%94%A8%E8%BF%87%E4%BC%9A%E7%AB%8B%E9%A9%AC%E6%8F%90%E5%8D%87%E9%A2%9C%E5%80%BC%E5%93%A6%EF%BC%8C%E4%BD%A0%E7%9F%A5%E9%81%93%E4%BA%86%E5%90%97&ari=2&dbv=0&drs=3&pcs=1680x439&pss=1680x3139&cfv=0&cpl=0&chi=1&cce=true&cec=UTF-8&tlm=1560259149&rw=439&ltu=http%3A%2F%2Fbaijiahao.baidu.com%2Fs%3Fid%3D1636038282713233707&ltr=http%3A%2F%2Fnews.baidu.com%2F&ecd=1&uc=1680x961&pis=-1x-1&sr=1680x1050&tcn=1560287950&dc=4
	    	byteBuf.writeBytes(_2_ReturnLine);
	    	// reset bytebuf read_size, ensure readable
	    	return request;
    	} finally {
    		byteBuf.resetReaderIndex();
    	}
    }
}
