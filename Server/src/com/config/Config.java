package com.config;

/**
 * Created by xy16 on 17-4-3.
 */
public class Config {

	public static class ServerConfig {
		private static int port = 8972;

		public static int getPort() {return port;}
	}

	public static class DataBaseConfig {
		private static String driver = "org.mariadb.jdbc.Driver";
		private static String url = "jdbc:mariadb://localhost:3306/sih";
		private static String user = "Admin";//"root";//
		private static String password = "SiH@6666";//"aa199877";//

		public static String getDriver() {return driver;}
		public static String getUrl() {return url;}
		public static String getUser() {return user;}
		public static String getPassword() {return password;}
	}

	public static class CosConfig {
		private static String bucketName = "sih";
		private static String secretId = "AKIDxlLgmf2mzwjHbguAYhcQ0LSKxLUSGl9I";
		private static String secretKey = "3sSmqeFahYIoAJFyWqxqX0FH8nFTTHrD";
		private static String region = "tj";
		private static long appId = 1253199804;

		public static String getBucketName() {return bucketName;}
		public static String getSecretId() {return secretId;}
		public static String getSecretKey() {return secretKey;}
		public static String getRegion() {return region;}
		public static long getAppId() {return appId;}
	}
}
