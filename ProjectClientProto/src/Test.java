import com.ClientSendMessage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by xy16 on 17-1-22.
 */
public class Test {
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("127.0.0.1", 6666);
			ProjectClientProto proto = new ProjectClientProto(socket);

			ProjectClientProto.LaunchResponse launchResponse =
			proto.launch("xy16", "123456");
			if(launchResponse!=null) {
				System.out.println(launchResponse.information);
				System.out.println("bonus:" + launchResponse.bonus);
				System.out.println("good:" + launchResponse.good);
			}

			ClientSendMessage.Message message = ClientSendMessage.Message.newBuilder()
					  .setMsgType(ClientSendMessage.MSG.SEND_CONTENT)
					  .setSendContent(
								 ClientSendMessage.SendContent.newBuilder()
								 .setContent("sad")
								 .setDate("2017-1-26")
					  ).build();

			socket.close();
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}
}
