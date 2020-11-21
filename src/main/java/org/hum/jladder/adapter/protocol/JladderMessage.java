package org.hum.jladder.adapter.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JladderMessage {

	private String host;
	private int port;
	private byte[] body;
}
