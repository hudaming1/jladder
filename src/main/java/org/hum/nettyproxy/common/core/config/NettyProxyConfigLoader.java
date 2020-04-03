package org.hum.nettyproxy.common.core.config;

import org.hum.nettyproxy.common.enumtype.RunModeEnum;
import org.hum.nettyproxy.compoment.interceptor.IULComplier;

/**
 * NettyProxy配置加载器
 */
public abstract class NettyProxyConfigLoader {
	
	/**
	 * 加载配置
	 * @param content
	 * @return
	 */
	public NettyProxyConfig load(Object content) {
		NettyProxyConfigContent configContent = loadConfig(content);
		NettyProxyConfig config = new NettyProxyConfig();
		setRunMode(config, configContent.getRunMode());
		setWorkerCnt(config, configContent.getWorkerCnt());
		setConsolePort(config, configContent.getConsolePort());
		setPort(config, configContent.getPort());
		setOutsideProxyHost(config, configContent.getOutsideProxyHost());
		setWebroot(config, configContent.getWebroot());
		setEnableAuthority(config, configContent.getEnableAuthority());
		setInterceptorRegxList(config, configContent.getInterceptorRegxList());
		return config;
	}

	private void setInterceptorRegxList(NettyProxyConfig config, Object interceptorRegxString) {
		if (interceptorRegxString == null) {
			return ;
		}
		for (String uil : interceptorRegxString.toString().split(",")) {
			config.addInterceptRegx(IULComplier.complie(uil));
		}
	}

	private void setEnableAuthority(NettyProxyConfig config, Object enableAuthority) {
		if (enableAuthority == null) {
			return ;
		}
		try {
			config.setEnableAuthority(Boolean.parseBoolean(enableAuthority.toString()));
		} catch (Exception ce) {
			throw new IllegalArgumentException("param \"enableAuthority\" is invaild", ce);
		}
	}

	private void setWebroot(NettyProxyConfig config, Object webroot) {
		if (webroot == null) {
			return ;
		}
		config.setWebroot(webroot.toString());
	}

	private void setOutsideProxyHost(NettyProxyConfig config, Object outsideProxyHost) {
		if ((config.getRunMode() == RunModeEnum.HttpInsideServer || config.getRunMode() == RunModeEnum.SocksInsideServer) && outsideProxyHost == null) {
			throw new IllegalArgumentException("param \"outsideProxyHost\" mustn't be null when server on inside mode");
		} else if (outsideProxyHost == null) {
			return ;
		}
		String[] arr = outsideProxyHost.toString().split(":");
		if (arr.length != 2) {
			throw new IllegalArgumentException("param \"outsideProxyHost\" invaild, value=" + outsideProxyHost);
		}
		
		config.setOutsideProxyHost(arr[0]);
		config.setOutsideProxyPort(parseInt(arr[1], "param \"outsideProxyHost\" port invaild, port value=" + arr[1]));
	}

	private void setPort(NettyProxyConfig config, Object port) {
		if (port == null) {
			throw new IllegalArgumentException("param \"port\" mustn't be null");
		}
		config.setPort(parseInt(port.toString(), "param \"port\" is invaild, value=" + port));
	}

	private void setConsolePort(NettyProxyConfig config, Object consolePort) {
		if (consolePort == null) {
			return ;
		}

		config.setConsolePort(parseInt(consolePort.toString(), "param \"consolePort\" is invaild, value=" + consolePort));
	}

	protected abstract NettyProxyConfigContent loadConfig(Object content);
	
	private void setRunMode(NettyProxyConfig config, Object val) {
		if (val == null) {
			throw new IllegalArgumentException("param[\"runMode\"] mustn't be null");
		}
		RunModeEnum runMode = RunModeEnum.getEnum(parseInt(val.toString(), "\"runmode\" must be int type, actually value is " + val));
		if (runMode == null) {
			throw new IllegalArgumentException("param[\"runMode\"] value[" + runMode + "] is invaild");
		}
		config.setRunMode(runMode);
	}
	
	private void setWorkerCnt(NettyProxyConfig config, Object val) {
		if (val == null) {
			config.setWorkerCnt(Runtime.getRuntime().availableProcessors() * 4);
		} else {
			config.setWorkerCnt(parseInt(val.toString(), "\"workerCnt\" must be int type, actually value is " + val));
		}
	}
	
	private static int parseInt(String str, String message) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException ce) {
			throw new IllegalArgumentException(message, ce);
		}
	}
}
