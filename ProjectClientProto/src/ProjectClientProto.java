import com.ClientSendMessage;
import com.ServerResponseMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by xy16 on 17-1-20.
 */
public class ProjectClientProto {
	static final int port = 6666;
	static final String ip = "127.0.0.1";
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private ClientSendMessage.Message sendMessage;
	private ServerResponseMessage.Message recvMessage;

	public ProjectClientProto() {
		try {
			socket = new Socket(ip, port);
			is = socket.getInputStream();
			os = socket.getOutputStream();
			sendMessage = null;
			recvMessage = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean launch(String username, String password) {
		try {
			//发送登录请求
			sendMessage = ClientSendMessage.Message.newBuilder()
					  .setMsgType(ClientSendMessage.MSG.LAUNCH_REQUEST)
					  .setUsername(username)
					  .setLauchRequest(
								 ClientSendMessage.LaunchRequest.newBuilder()
											.setPassword(password)
					  ).build();
			sendMessage.writeTo(os);
			recvMessage = ServerResponseMessage.Message.parseFrom(is);

			//获取基本信息
			sendMessage = ClientSendMessage.Message.newBuilder()
					  .setMsgType(ClientSendMessage.MSG.USER_INFORMATION_REQUEST)
					  .setUsername(username)
					  .setUserInformationRequest(
					  		  ClientSendMessage.UserInformationRequest.newBuilder()
								 .setUsername(username);
					  )
		} catch (IOException e) {
			return false;
		}
	}

}
