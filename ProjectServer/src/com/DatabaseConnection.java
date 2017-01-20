package com;

import java.io.IOException;
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
	String driver = "org.mariadb.jdbc.Driver";
	String url = "jdbc:mariadb://localhost:3306/sih";
	String user = "root";
	String password = "aa199877";

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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
