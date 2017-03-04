package com;

import com.qcloud.cos.sign.Credentials;
import com.qcloud.cos.sign.Sign;


/**
 * @author chengwu cos Demo代码
 */
public class Cos {
	private static String bucketName = "sih";
	private static String secretId = "AKIDxlLgmf2mzwjHbguAYhcQ0LSKxLUSGl9I";
	private static String secretKey = "3sSmqeFahYIoAJFyWqxqX0FH8nFTTHrD";
	private static String region = "tj";
	private static long appId = 1253199804;

	private Credentials cred;
	enum TYPE {
		PICTURE,
		FILE
	}

	String getDownloadSign(String filename, TYPE type) {
		try {
			String root="/";
			switch (type) {
				case FILE:
					root = "/pic/";
					break;
				case PICTURE:
					root = "/file/";
					break;
			}
			return Sign.getDownLoadSign(bucketName, root+filename, cred, 1000);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	String getUploadSign(String filename, TYPE type) {
		try {
			String root = "";
			switch (type) {
				case FILE:
					root = "/";
					break;
				case PICTURE:
					root = "/";
					break;
			}
			return Sign.getOneEffectiveSign(bucketName, root+filename, cred);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public Cos() {
		cred = new Credentials(appId, secretId, secretKey);
	}

}
