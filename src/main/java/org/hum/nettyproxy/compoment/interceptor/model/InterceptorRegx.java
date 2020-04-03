package org.hum.nettyproxy.compoment.interceptor.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * $actionType $actionValue where $matchKey $matchOp $matchValue
 * update request.header.host = 'localhost:8080' where request.header.host= '129.28.193.172:8080'
 * update response.header.Content-Type = 'application/json' where request.header.host = '129.28.193.172:8080';
 * print [header/body/line/*] where header.host= '129.28.193.172:8080'
 * 
 */
@Data
public class InterceptorRegx  {

	// 匹配(暂时只能支持单一条件匹配)
	private List<RegxMatch> match;
	// 动作
	private RegxAction action;
	
	@Data
	public static class RegxMatch {
		// Line/Header/Body
		private Object key;
		// Equals, Like
		private Object op;
		// value
		private String value;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegxAction {
		// Replace/Update/Print/Add/Delete
		private Object actionType;
		// value
		private String value;
	}
}
