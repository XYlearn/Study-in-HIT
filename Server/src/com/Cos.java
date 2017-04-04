package com;

import com.config.Config;
import com.qcloud.cos.sign.Credentials;
import com.qcloud.cos.sign.Sign;


/**
 * @author chengwu cos Demo代码
 */
public class Cos {
	private static String bucketName = Config.CosConfig.getBucketName();
	private static String secretId = Config.CosConfig.getSecretId();
	private static String secretKey = Config.CosConfig.getSecretKey();
	private static String region = Config.CosConfig.getRegion();
	private static long appId = Config.CosConfig.getAppId();

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
