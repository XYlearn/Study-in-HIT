package com;
import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xy16 on 16-12-31.
 */
public class ServerThread implements Runnable {
	private Socket clientSocket;
	private InputStream is;
	private OutputStream os;
	private DatabaseConnection dbconn;

	public ServerThread(Socket socket) throws IOException {
		clientSocket = socket;
		is = socket.getInputStream();
		os = socket.getOutputStream();
		dbconn = new DatabaseConnection();
		this.run();
	}

	public void run() {
		ClientSendMessage.Message recvMessage = null;
		ServerResponseMessage.Message respMessage = null;
		ServerItem serverItem = new ServerItem(clientSocket, recvMessage, dbconn);
		while (clientSocket.isConnected()) {
			try {
				recvMessage = ClientSendMessage.Message.parseDelimitedFrom(is);
				respMessage = serverItem.handleMessage(recvMessage);
				//若为登出消息则退出
				if (respMessage == null || recvMessage==null)
					continue;

				respMessage.writeDelimitedTo(os);
				os.flush();

				if (!serverItem.isLaunched())
					break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}

		}

		//将用户设置为未登录状态
		try {
			String sql ="DELETE FROM online_user WHERE username='?';".replace("?", recvMessage.getUsername());
			PreparedStatement pstmt = dbconn.connection.prepareStatement(sql);
			pstmt.setString(1, recvMessage.getUsername());
			pstmt.execute();
			//关闭输入输出流和数据库链接
			is.close();
			os.close();
			dbconn.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
