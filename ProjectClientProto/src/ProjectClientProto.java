import com.ClientSendMessage;
import com.ServerResponseMessage;

import java.io.*;
import java.net.Socket;
import java.util.List;

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
	private String username;
	public class LaunchResponse {
		public boolean status;
		public String information;
		public int good;
		public int questionNum;
		public int solvedQuestionNum;
		public int bonus;
		public String signature;
		public String mail_address;
		public String launchInfo;
		LaunchResponse(boolean status, String information, int good, int questionNum,
							int solvedQuestionNum, int bonus, String launchInfo,
							String signature, String mail_address) {
			this.status = status;
			this.information = information;
			this.good = good;
			this.questionNum = questionNum;
			this.solvedQuestionNum = solvedQuestionNum;
			this.bonus = bonus;
			this.signature = signature;
			this.mail_address = mail_address;
		}
		public LaunchResponse(){}
	}
	public class RecieveContent {
		public String username;
		public String record;
	}
	public class GoodUserResponse {

	}

	public ProjectClientProto(Socket socket) {
		try {
			this.socket = socket;
			is = socket.getInputStream();
			os = socket.getOutputStream();
			sendMessage = null;
			recvMessage = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LaunchResponse launch(String username, String password) {
		try {
			//发送登录请求
			sendMessage = ClientSendMessage.Message.newBuilder()
					  .setMsgType(ClientSendMessage.MSG.LAUNCH_REQUEST)
					  .setUsername(username)
					  .setLauchRequest(
								 ClientSendMessage.LaunchRequest.newBuilder()
											.setPassword(password)
					  ).build();
			sendMessage.writeDelimitedTo(os);
			os.flush();
			recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
			if(recvMessage==null)
				return null;
			ServerResponseMessage.LaunchResponse lr = recvMessage.getLauchResponse();
			ServerResponseMessage.UserMessage um = lr.getUserMessage();
			LaunchResponse launchResponse = new LaunchResponse(
				lr.getStatus(), lr.getInformation(), um.getGood(), um.getQuestionNum(),
					  um.getSolvedQuestionNum(), um.getBonus(), lr.getInformation(),
					  um.getSignature(), um.getMailAddress()
			);
			return launchResponse;
		} catch (Exception e) {
			return null;
		}
	}

	public void Logout() {
		try {
			sendMessage = ClientSendMessage.Message.newBuilder()
					  .setMsgType(ClientSendMessage.MSG.LOGOUT_MESSAGE)
					  .build();
			sendMessage.writeTo(os);
			os.close();
			is.close();
		} catch (IOException e) {}
	}

	public RecieveContent send(List<String> contentList, String questionID) {
		StringBuffer sb = new StringBuffer("");
		for(String s : contentList)
			sb.append(s);
		sendMessage = ClientSendMessage.Message.newBuilder().setMsgType(ClientSendMessage.MSG.SEND_CONTENT)
				  .setSendContent(
				  		  ClientSendMessage.SendContent.newBuilder()
									 .setQuestionID(Long.valueOf(questionID))
				  .setContent(sb.toString())).build();
		return null;
	}

	public boolean goodUser(String user) throws IOException{
		sendMessage = ClientSendMessage.Message.newBuilder().setMsgType(ClientSendMessage.MSG.GOOD_USER_REQUEST)
				  .setGoodUserRequest(ClientSendMessage.GoodUserRequest.newBuilder().setUser(user)).build();
		sendMessage.writeDelimitedTo(os);
		recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
		return recvMessage.getGoodQuestionResponse().getSuccess();
	}

	public boolean goodQuestion(String questionID) throws IOException {
		sendMessage = ClientSendMessage.Message.newBuilder().setMsgType(ClientSendMessage.MSG.GOOD_QUESTION_REQUEST)
				  .setGoodQuestionRequest(ClientSendMessage.GoodQuestionRequest.newBuilder()
				  .setQuestionID(Long.valueOf(questionID)).build()).build();
		sendMessage.writeTo(os);
		recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
		return recvMessage.getGoodQuestionResponse().getSuccess();
	}
}
