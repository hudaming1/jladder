package org.hum.nettyproxy.test.https_client.tls;

import java.util.HashMap;
import java.util.Map;

import org.hum.nettyproxy.test.https_client.tls.domain.TLSRecordProtocol;
import org.hum.nettyproxy.test.https_client.tls.enumtype.TLS_ContentTypeEnum;

public class Parser {
    
    private static Map<Character, Integer> HEX_MAP = new HashMap<>();
    static {
    	HEX_MAP.put('0', 0);
    	HEX_MAP.put('1', 1);
    	HEX_MAP.put('2', 2);
    	HEX_MAP.put('3', 3);
    	HEX_MAP.put('4', 4);
    	HEX_MAP.put('5', 5);
    	HEX_MAP.put('6', 6);
    	HEX_MAP.put('7', 7);
    	HEX_MAP.put('8', 8);
    	HEX_MAP.put('9', 9);
    	HEX_MAP.put('a', 10);
    	HEX_MAP.put('b', 11);
    	HEX_MAP.put('c', 12);
    	HEX_MAP.put('d', 13);
    	HEX_MAP.put('e', 14);
    	HEX_MAP.put('f', 15);
    }


	public static TLSRecordProtocol toTLSRecordProtocol(String s) {
		TLSRecordProtocol protocol = new TLSRecordProtocol();
		protocol.setContentType(TLS_ContentTypeEnum.getEnum(hex2Number(s.substring(0, 2))).getCode());
		protocol.setMajorVersion((byte) hex2Number(s.substring(2, 4)));
		protocol.setMinorVersion((byte) hex2Number(s.substring(4, 6)));
		protocol.setLength((short) hex2Number(s.substring(6, 10)));
		for (int i = 0; i < s.length(); i += 2) {
			int num = HEX_MAP.get(s.charAt(i)) * 16 + HEX_MAP.get(s.charAt(i + 1)) ;
		}
		return protocol;
	}
	
	private static int hex2Number(String hex) {
		int num = 0;
		int pow = 1;
		for (int i = hex.length() - 1; i >= 0 ; i --) {
			num += HEX_MAP.get(hex.charAt(i)) * pow;
			pow *= 16;
		}
		return num;
	}
	
	public static void main(String[] args) {
		String s = "1603010264010002600303d72becbade4d2681a30391d4604978f8578eb6060a1817a49640f1e2e2d44e8720c2292743202588b940e61081368edee434519441adc81cd259f7efc13f766a6300225a5a130113021303c02bc02fc02cc030cca9cca8c013c014009c009d002f0035000a010001f52a2a0000000000160014000011647373302e62647374617469632e636f6d00170000ff01000100000a000a0008aaaa001d00170018000b00020100002300000010000e000c02683208687474702f312e31000500050100000000000d00140012040308040401050308050501080606010201001200000033002b0029aaaa000100001d0020646fd5d5ec56e5d920a54ab813261130dc5f3901f4510de3fd27ccc43a0a2d2b002d00020101002b000b0a7a7a0304030303020301001b00030200025a5a0001000029012b00f600f02edfda67ad65f1301bd318a3cf1fc6d7c7c1bf69d6364313e25f04ea4ad1a8145354423222fbacda1fd120de41f9710d1eacf4575bac72863fa1d436a369328daae9b559cb32f893909e130bc2e59c834f9a1e03a0693b46fc51e82ee517449d77ed8f5e7e78ebc29fb08fc496de1947a5744ad10012112784839fc789482bfb7e71ec57c0ad39391d90cd8b58fff92877780edb03e66a3b85083ccdcc4c32745bd793bf48eeae9eb0a3b6a7daeb0c30ddb3aeaae55722d87756521b023249e32e8bd97d97294c4cbc7df5edb37082309d2784ccb48e43c02ac9d13d12744ccfec9b2b22911c8a864e9892999cdaf6ed9b73472d003130a5432433717226c4fb685b5e9a58a13f6c460a81b57599c4ab4737a16ee98d69c54040cb35b76f29103c26429fcb44d4";
		System.out.println(s.substring(6,10));
		System.out.println(hex2Number(s.substring(6,10)));
	}
}
