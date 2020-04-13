package org.hum.nettyproxy.compoment.interceptor.model;

import java.util.List;

import org.hum.nettyproxy.compoment.interceptor.enumtype.ActionTypeEnum;
import org.hum.nettyproxy.compoment.interceptor.enumtype.InterceptorFieldEnum;
import org.hum.nettyproxy.compoment.interceptor.enumtype.InterceptorTypeEnum;

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

	// 原始IUL字符串
	private String iul;
	// Replace/Update/Print/Add/Delete
	private ActionTypeEnum actionType;
	// 匹配(暂时只能支持单一条件匹配)
	private List<RegxMatch> match;
	// 动作
	private RegxAction action;
	
	@Data
	public static class RegxMatch {
		// request/response
		private InterceptorTypeEnum type;
		// Line/Header/Body
		private InterceptorFieldEnum field;
		// 只有当field=Header时有效：host/Content-Type...
		private String key;
		// Equals, Like
		private Object op;
		// value
		private String value;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegxAction {
		// request/response
		private InterceptorTypeEnum type;
		// Line/Header/Body
		private InterceptorFieldEnum field;
		// 只有当field=Header时有效：host/Content-Type...
		private String key;
		// value
		private String value;
	}
}
