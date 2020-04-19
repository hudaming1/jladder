/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.hum.nettyproxy.test.officaldemo.https_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * An HTTP server that sends back the content of the received HTTP request in a
 * pretty plaintext form.
 * 
 * Windows generator cert by java-keytool
 * 1. 以jks格式生成服务器端包含Public key和Private Key的keystore文件，keypass与storepass务必要一样，因为在tomcat server.xml中只配置一个password.
 * 	"C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -genkey -alias server -keystore serverKeystore.jks -keypass 123456 -storepass 123456 -keyalg RSA  -keysize 512 -validity 365 -v -dname "CN = W03GCA01A,O = ABC BANK,DC = Server Https,DC = ABC,OU = Firefly Technology And Operation"
 * 2.从keystore中导出别名为server的服务端证书.
 * 	"C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -export -alias server -keystore serverKeystore.jks -storepass 123456 -file server.cer
 * 3.将server.cer导入客户端的信任证书库clientTruststore.jks。
 * 	"C:\Program Files\Java\jdk1.8.0_131\bin\keytool" -import -alias trustServer -file server.cer -keystore clientTruststore.jks -storepass 123456
 * 参考：
 * 	https://firefly.iteye.com/blog/667196
 * 	Tomcat安装证书：https://www.jianshu.com/p/a493a6380c23
 * 
 * 
 */
public final class HttpHelloWorldServer {

	static final boolean SSL = true;
	static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "52007" : "8080"));
	
	public static void main(String[] args) throws Exception {
		// Configure SSL.
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}

		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new HttpHelloWorldServerInitializer(sslCtx));

			Channel ch = b.bind(PORT).sync().channel();

			System.err.println("Open your web browser and navigate to " + (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');

			ch.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
