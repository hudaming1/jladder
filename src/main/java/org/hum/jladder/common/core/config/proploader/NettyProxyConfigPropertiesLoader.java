package org.hum.jladder.common.core.config.proploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.hum.jladder.common.core.config.NettyProxyConfigContent;
import org.hum.jladder.common.core.config.NettyProxyConfigLoader;
import org.hum.jladder.common.exception.NettyProxyException;

public class NettyProxyConfigPropertiesLoader extends NettyProxyConfigLoader {

	/**
	 * 加载配置文件
	 * 
	 * @param file 配置文件完整路径
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Override
	protected NettyProxyConfigContent loadConfig(Object file) {
		if (file == null) {
			throw new IllegalArgumentException("file mustn't be null");
		} else if (!(file instanceof String)) {
			throw new IllegalArgumentException("param-type must be String.class");
		}
		Properties properties = null;
		try {
			properties = loadProperties(file.toString());
		} catch (FileNotFoundException e1) {
			throw new NettyProxyException("load config file error, file[" + file + "] not exists.", e1);
		} catch (IOException e1) {
			throw new NettyProxyException("load config file error", e1);
		}
		
		NettyProxyConfigContent content = new NettyProxyConfigContent();
		if (properties.containsKey("runMode")) {
			content.setRunMode(properties.getProperty("runMode"));
		}
		if (properties.containsKey("workerCnt")) {
			content.setWorkerCnt(properties.getProperty("workerCnt"));
		}
		if (properties.containsKey("consolePort")) {
			content.setConsolePort(properties.getProperty("consolePort"));
		}
		if (properties.containsKey("port")) {
			content.setPort(properties.getProperty("port"));
		}
		if (properties.containsKey("outsideProxyHost")) {
			content.setOutsideProxyHost(properties.getProperty("outsideProxyHost"));
		}
		if (properties.containsKey("webroot")) {
			content.setWebroot(properties.getProperty("webroot"));
		}
		if (properties.containsKey("enableAuthority")) {
			content.setEnableAuthority(properties.getProperty("enableAuthority"));
		}
		if (properties.containsKey("interceptor.regx")) {
			content.setInterceptorRegxList(properties.getProperty("interceptor.regx"));
		}
		return content;
	}

	private Properties loadProperties(String file) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(file)));
		return properties;
	}
}
