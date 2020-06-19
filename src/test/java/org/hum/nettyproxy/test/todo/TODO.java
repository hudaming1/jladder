package org.hum.nettyproxy.test.todo;

public class TODO {

	/**
	 * 两个p12文件内容明明相同，但大小相差1倍
	 * <pre>
	 *    解题步骤1：使用openssl命令将p12拆解出「证书」和「秘钥」分别对比文件内容
	 *    解题步骤2：最麻烦的方式，学习证书内部结构，然后通过读二进制一点一点读：粗略看了一下，应该是类型+长度+内容
	 * </pre>
	 */
}
