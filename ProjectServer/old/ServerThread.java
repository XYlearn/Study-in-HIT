package old;
import com.ClientSendMessage;
import com.DatabaseConnection;
import com.ServerItem;
import com.ServerResponseMessage;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
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
	String username = null;

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
		ServerItem serverItem = new ServerItem(clientSocket, dbconn);

		ByteBuffer sbuffer = null;
		ByteBuffer rbuffer = null;
		byte[] b = new byte[1024];

		while (!clientSocket.isClosed()) {
			try {
				recvMessage = ClientSendMessage.Message.parseFrom(is);

				if(null == username)
					username = recvMessage.getUsername();
				respMessage = serverItem.handleMessage(recvMessage);
				//若为登出消息则退出
				if (respMessage == null || recvMessage==null)
					break;

				respMessage.writeTo(os);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}

		}

		//将用户设置为未登录状态
		try {
			if(!dbconn.isClosed()) {
				String sql = "DELETE FROM online_user WHERE username='?';".replace("?", username);
				PreparedStatement pstmt = dbconn.connection.prepareStatement(sql);
				pstmt.execute();
				dbconn.closeConnection();
			}
			//关闭输入输出流和数据库链接
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Terminated:"+clientSocket);
		}
	}
}
