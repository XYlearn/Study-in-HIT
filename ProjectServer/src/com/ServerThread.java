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
		ServerItem serverItem = new ServerItem(clientSocket, recvMessage);
		do {
			try {
				byte[] len = new byte[1024];
				int count = clientSocket.getInputStream().read(len);
				byte[] messageByte = new byte[count];
				for(int i=0;i<count;i++)
					messageByte[i]=len[i];

				recvMessage = ClientSendMessage.Message.parseFrom(messageByte);
				respMessage = serverItem.handleMessage(recvMessage);
				//若为登出消息则退出
				if(respMessage==null)
					break;

				respMessage.writeTo(os);

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}while(true);

		//将用户设置为未登录状态
		try {
			PreparedStatement pstmt = dbconn.connection.prepareStatement(
					  "DELETE FROM online_user WHERE username=\"?\"");
			pstmt.setString(1,recvMessage.getUsername());
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//关闭输入输出流和数据库链接
		try {
			is.close();
			os.close();
			dbconn.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
