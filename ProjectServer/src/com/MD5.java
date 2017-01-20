package com;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xy16 on 17-1-18.
 */
public class MD5 {
	static private MessageDigest md = null;
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static String getMd5(String str) {
		byte[] bs = md.digest(str.getBytes());
		StringBuilder sb = new StringBuilder(40);
		for(byte x:bs) {
			if((x & 0xff)>>4 == 0) {
				sb.append("0").append(Integer.toHexString(x & 0xff));
			} else {
				sb.append(Integer.toHexString(x & 0xff));
			}
		}
		return sb.toString();
	}
}
