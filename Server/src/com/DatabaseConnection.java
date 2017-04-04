package com;

import com.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by xy16 on 17-1-12.
 * 负责数据库连接
 */
public class DatabaseConnection {
	//mariadb
	private String driver = Config.DataBaseConfig.getDriver();
	private String url = Config.DataBaseConfig.getUrl();
	private String user = Config.DataBaseConfig.getUser();
	private String password = Config.DataBaseConfig.getPassword();
	static private boolean closed = false;

	Connection connection = null;

	public DatabaseConnection() {
		try {
			Class.forName(driver);
			//设置参数
			Properties properties =  new Properties();
			properties.put("user", user);
			properties.put("password", password);
			//获取连接
			connection = DriverManager.getConnection(url, properties);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			connection.close();
			this.closed = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isClosed() { return closed; }

}
