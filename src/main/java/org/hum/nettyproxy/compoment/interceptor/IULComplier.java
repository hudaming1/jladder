package org.hum.nettyproxy.compoment.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hum.nettyproxy.compoment.interceptor.enumtype.ActionTypeEnum;
import org.hum.nettyproxy.compoment.interceptor.model.InterceptorRegx;
import org.hum.nettyproxy.compoment.interceptor.model.InterceptorRegx.RegxAction;
import org.hum.nettyproxy.compoment.interceptor.model.InterceptorRegx.RegxMatch;

/**
 * IUL = Intercept Update Language
 * 
 */
public class IULComplier {

	/**
	 * update header.host = 'localhost:8080' where header.host= '129.28.193.172:8080'
	 * $actionType $actionValue where $matchKey $matchOp $matchValue
	 * @param iul
	 * @return
	 */
	public static InterceptorRegx complie(String iul) {
		InterceptorRegx regx = new InterceptorRegx();

		StringBuffer sbuilder = new StringBuffer(iul);
		RegxAction action = new RegxAction();
		
		/** split to actionType **/
		Pattern pattern = Pattern.compile("(update|replace|print|add|delete)\\s+");
		Matcher matcher = pattern.matcher(sbuilder.toString());
		if (!matcher.find()) {
			throw new IllegalArgumentException("IUL invaild");
		}
		regx.setActionType(ActionTypeEnum.getEnum(matcher.group()));
		sbuilder.delete(matcher.start(), matcher.end());
		
		/** split to actionValue1 **/
		pattern = Pattern.compile("^((header|line|body)\\.[a-zA-Z0-9-]*\\s*(?==))");
		matcher = pattern.matcher(sbuilder.toString());
		if (!matcher.find()) {
			throw new IllegalArgumentException("IUL invaild");
		}
		action.setKey(matcher.group().trim());
		sbuilder.delete(matcher.start(), matcher.end() + 1); // 上一步用零宽断言匹配"="，因此这里+1代表多删除了"="
		
		/** split to actionValue2 **/
		pattern = Pattern.compile("^'((?!').)*'{1}");
		matcher = pattern.matcher(sbuilder.toString().trim());
		if (!matcher.find()) {
			throw new IllegalArgumentException("IUL invaild");
		}
		String valueContainQuot = matcher.group().trim();
		action.setValue(valueContainQuot.substring(1, valueContainQuot.length() - 1));
		sbuilder.delete(matcher.start(), matcher.end() + 1); // 上一步用零宽断言匹配"单引号"，因此这里+1代表多删除了"单引号"
		
		/** skip where **/
		pattern = Pattern.compile("^where\\s+");
		matcher = pattern.matcher(sbuilder.toString().trim());
		if (!matcher.find()) {
			throw new IllegalArgumentException("IUL invaild");
		}
		sbuilder.delete(matcher.start(), matcher.end()); 

		List<RegxMatch> matchList = new ArrayList<>();
		RegxMatch match = new RegxMatch();
		
		/** split to $matchKey **/
		pattern = Pattern.compile("^((header|line|body)\\.[a-zA-Z0-9-]*\\s*(?=(=|<|>|(like))))");
		matcher = pattern.matcher(sbuilder.toString().trim());
		if (!matcher.find()) {
			throw new IllegalArgumentException("IUL invaild");
		}
		match.setKey(matcher.group().trim());
		sbuilder.delete(matcher.start(), matcher.end() + 1); 

		/** split to $matchOp **/
		pattern = Pattern.compile("(=|<|>|(like))");
		matcher = pattern.matcher(sbuilder.toString().trim());
		if (!matcher.find()) {
			throw new IllegalArgumentException("IUL invaild");
		}
		match.setOp(matcher.group().trim());
		sbuilder.delete(matcher.start(), matcher.end() + 1); 

		/** split to $matchValue **/
		pattern = Pattern.compile("^'((?!').)*'{1}");
		matcher = pattern.matcher(sbuilder.toString().trim());
		if (!matcher.find()) {
			throw new IllegalArgumentException("IUL invaild, iul=" + iul);
		}
		valueContainQuot = matcher.group().trim();
		match.setValue(valueContainQuot.substring(1, valueContainQuot.length() - 1));
		matchList.add(match);
		
		regx.setAction(action);
		regx.setMatch(matchList);
		
		return regx;
	}
}
