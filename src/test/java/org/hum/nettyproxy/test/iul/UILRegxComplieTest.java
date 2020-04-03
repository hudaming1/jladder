package org.hum.nettyproxy.test.iul;

import org.hum.nettyproxy.compoment.interceptor.IULComplier;
import org.hum.nettyproxy.compoment.interceptor.enumtype.ActionTypeEnum;
import org.hum.nettyproxy.compoment.interceptor.model.InterceptorRegx;
import org.junit.Test;

public class UILRegxComplieTest {

	@Test
	public void test1() {
		InterceptorRegx regx1 = IULComplier.complie("update header.host = 'localhost:8080' where header.host= '129.28.193.172:8080'");
		assert(regx1.getActionType() == ActionTypeEnum.Update);
		assert(regx1.getAction().getKey().equals("header.host"));
		assert(regx1.getAction().getValue().equals("localhost:8080"));
		assert(regx1.getMatch().get(0).getKey().equals("header.host"));
		assert(regx1.getMatch().get(0).getOp().equals("="));
		assert(regx1.getMatch().get(0).getValue().equals("129.28.193.172:8080"));
	}
}
