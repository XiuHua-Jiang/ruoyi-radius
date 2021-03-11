package com.ruoyi.radius.toughradius.utils.wx;

import java.util.UUID;

public class UUIDUtils {

	public static String createUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
}
