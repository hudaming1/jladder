package org.hum.nettyproxy.test.https_client.tls.domain;

import org.hum.nettyproxy.test.https_client.tls.enumtype.TLS_ContentTypeEnum;

import lombok.Data;

/**
 * <pre>
 * 内容类型: 
 * 	 1.封装的高层协议(8位)
 * 		1) 握手协议(handshake): 22 
 * 		2) 警告协议(alert): 21 
 * 		3) 改变密码格式协议(change_cipher_spec): 20 
 * 		4) 应用数据协议(application_data): 23 
 * 	 2. 主要版本(8位):
 * 		使用的SSL主要版本，目前的SSL版本是SSL v3，所以这个字段的值只有3这个值 
 * 	 3. 次要版本(8位): 使用的SSL次要版本。对于SSLv3.0，值为0。 
 * 	 4. 数据包长度(16位): 
 * 		1) 明文数据包：这个字段表示的是"明文数据"以"字节"为单位的长度 
 * 		2) 压缩数据包：这个字段表示的是"压缩数据"以"字节"为单位的长度 
 * 		3) 加密数据包：这个字段表示的是"加密数据"以"字节"为单位的长度 
 * 	 5. 记录数据 这个区块封装了上层协议的数据
 * 		1) 明文数据包：opaque fragment[SSLPlaintext.length]; 
 * 		2) 压缩数据包：opaque fragment[SSLCompressed.length]; 
 * 		3) 加密数据包 
 * 			3.1) 流式(stream)加密:GenericStreamCipher 
 * 				3.1.1) opaque content[SSLCompressed.length]; 
 * 				3.1.2) opaque MAC[CipherSpec.hash_size]; 
 * 			3.2) 分组(block)加密: GenericBlockCipher 
 * 				3.2.1) opaque content[SSLCompressed.length]; 
 * 				3.2.2) opaque MAC[CipherSpec.hash_size]; 
 * 				3.2.3) uint8 padding[GenericBlockCipher.padding_length]; 
 * 				3.2.4) uint8 padding_length; 
 *   6. MAC(0、16、20位)
 * </pre>
 */
@Data
public class TLSRecordProtocol {

	/**
	 * Record类型
	 * {@link TLS_ContentTypeEnum}
	 */
	private Byte contentType;
	/**
	 * 主版本号
	 */
	private Byte majorVersion = 3;
	/**
	 * 次要版本
	 */
	private Byte minorVersion = 0;
	/**
	 * 数据包长度
	 */
	private Short length;
	/**
	 * 消息内容
	 */
	private byte[] body;
}
