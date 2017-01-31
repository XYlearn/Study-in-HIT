
import com.ClientSendMessage;
import com.MD5;
import com.ServerResponseMessage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xy16 on 17-1-20.
 */
public class ProjectClientProto {
	static final String token = "([\\\\S\\\\s]*\uD834\uDE19)([\\\\S\\\\s]*\uD834\uDE19)";
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
		public String pic_url;
		LaunchResponse(boolean status, String information, int good, int questionNum,
							int solvedQuestionNum, int bonus,
							String signature, String mail_address, String pic_url) {
			this.status = status;
			this.information = information;
			this.good = good;
			this.questionNum = questionNum;
			this.solvedQuestionNum = solvedQuestionNum;
			this.bonus = bonus;
			this.signature = signature;
			this.mail_address = mail_address;
			this.pic_url = pic_url;
		}
		public LaunchResponse(){}
	}
	public class UserMessage {
		public int good;
		public int questionNum;
		public int solvedQuestionNum;
		public int bonus;
		public String signature;
		public String mail_address;
		UserMessage(int good, int questionNum,
							int solvedQuestionNum, int bonus,
							String signature, String mail_address) {
			this.good = good;
			this.questionNum = questionNum;
			this.solvedQuestionNum = solvedQuestionNum;
			this.bonus = bonus;
			this.signature = signature;
			this.mail_address = mail_address;
		}
	}
	public class RecieveContent {
		public String user;
		public String content;
		public String questionID;
		public String time;
		RecieveContent(String user, String content, String questionID
		, String time) {
			this.user = user;
			this.content = content;
			this.questionID = questionID;
			this.time = time;
		}
	}
	public class QuestionMessage {
		public String owner;
		public String id;
		public String stem;
		public String addition;
		public String time;
		boolean solved;
		int good;
		QuestionMessage(String owner, String id, String stem, String addtion,
							 String time, boolean solved, int good) {
			this.owner = owner;
			this.id = id;
			this.stem = stem;
			this.addition = addtion;
			this.time = time;
			this.solved = solved;
			this.good = good;
		}
	}

	public ProjectClientProto(Socket socket) {
		this.socket = socket;
		try {
			this.is = socket.getInputStream();
			this.os = socket.getOutputStream();
		} catch (IOException e) {

		}
		sendMessage = null;
		recvMessage = null;
	}

	public LaunchResponse launch(String username, String password) {
		try {
			this.username = username;
			//发送登录请求
			sendMessage = ClientSendMessage.Message.newBuilder()
					  .setMsgType(ClientSendMessage.MSG.LAUNCH_REQUEST)
					  .setUsername(username)
					  .setLauchRequest(
								 ClientSendMessage.LaunchRequest.newBuilder()
											.setPassword(MD5.getMd5(password))
					  ).build();
			sendMessage.writeDelimitedTo(os);
			recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
			if(recvMessage==null)
				return null;
			ServerResponseMessage.LaunchResponse lr = recvMessage.getLauchResponse();
			ServerResponseMessage.UserMessage um = lr.getUserMessage();
			LaunchResponse launchResponse = new LaunchResponse(
				lr.getStatus(), lr.getInformation(), um.getGood(), um.getQuestionNum(),
					  um.getSolvedQuestionNum(), um.getBonus(),
					  um.getSignature(), um.getMailAddress(), um.getPicUrl()
			);
			return launchResponse;
		} catch (Exception e) {
			return null;
		}
	}

	public void logout() throws IOException {
		sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.LOGOUT_MESSAGE)
				  .build();
		sendMessage.writeDelimitedTo(os);
	}

	public RecieveContent send(String contents, String questionID) throws IOException {
		sendMessage = ClientSendMessage.Message.newBuilder().setMsgType(ClientSendMessage.MSG.SEND_CONTENT)
				  .setSendContent(
				  		  ClientSendMessage.SendContent.newBuilder()
									 .setQuestionID(Long.valueOf(questionID))
									 .setContent(contents)
				  ).build();
		sendMessage.writeDelimitedTo(os);
		recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
		ServerResponseMessage.SendContent sc = recvMessage.getSendContent();
		RecieveContent recieveContent = null;
		if( sc!=null ) {
			return new RecieveContent(sc.getUser(), sc.getContent(),
					  String.valueOf(sc.getQuestionID()), sc.getTime());
		}
		return null;
	}

	public boolean goodUser(String user) throws IOException {
		sendMessage = ClientSendMessage.Message.newBuilder().setMsgType(ClientSendMessage.MSG.GOOD_USER_REQUEST)
				  .setGoodUserRequest(ClientSendMessage.GoodUserRequest.newBuilder().setUser(user)).build();
		sendMessage.writeDelimitedTo(os);
		recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
		return recvMessage.getGoodUserResponse().getSuccess();
	}

	public boolean goodQuestion(String questionID) throws IOException {
		sendMessage = ClientSendMessage.Message.newBuilder().setMsgType(ClientSendMessage.MSG.GOOD_QUESTION_REQUEST)
				  .setGoodQuestionRequest(ClientSendMessage.GoodQuestionRequest.newBuilder()
				  .setQuestionID(Long.valueOf(questionID)).build()).build();
		sendMessage.writeDelimitedTo(os);
		recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
		return recvMessage.getGoodQuestionResponse().getSuccess();
	}

	public QuestionMessage enterQuestion(String questionID) throws IOException {

		sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.QUESTION_ENTER_REQUEST)
				  .setUsername(username)
				  .setQuestionEnterRequest(
							 ClientSendMessage.QuestionEnterRequest.newBuilder()
							 .setQuestionID(Long.valueOf(questionID))
				  ).build();
		sendMessage.writeDelimitedTo(os);
		recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
		if(recvMessage.getQuestionEnterResponse().getAllow()) {
			ServerResponseMessage.QuestionMessage qm =
					  recvMessage.getQuestionEnterResponse().getQuestionMessage();
			String owner = qm.getOwner();
			String id = questionID;
			String stem = qm.getStem();
			String addition = qm.getAddition();
			String time = qm.getTime();
			boolean solved = qm.getSolved();
			int good = qm.getGood();
			return new QuestionMessage(owner, id, stem, addition, time, solved, good);
		} else {
			System.out.println("房间号不存在");
			return null;
		}
	}

	public QuestionMessage requestQuestionInfo(String questionID) throws IOException {
		sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.QUESTION_INFORMATION_REQUEST)
				  .setUsername(username)
				  .setQuestionInformationRequest(
							 ClientSendMessage.QuestionInformationRequest.newBuilder()
										.setQuestionID(Long.valueOf(questionID))
				  ).build();
		sendMessage.writeDelimitedTo(os);
		recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
		if(recvMessage.getQuestionInformationResponse().isInitialized()) {
			ServerResponseMessage.QuestionMessage qm =
					  recvMessage.getQuestionEnterResponse().getQuestionMessage();
			String owner = qm.getOwner();
			String id = questionID;
			String stem = qm.getStem();
			String addition = qm.getAddition();
			String time = qm.getTime();
			boolean solved = qm.getSolved();
			int good = qm.getGood();
			return new QuestionMessage(owner, id, stem, addition, time, solved, good);
		} else {
			System.out.println("房间号不存在");
			return null;
		}
	}

	public UserMessage requestUserInfo(String user) throws IOException {
		sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.USER_INFORMATION_REQUEST)
				  .setUsername(username)
				  .setUserInformationRequest(
							 ClientSendMessage.UserInformationRequest.newBuilder()
							 .setUsername(user)
				  ).build();
		sendMessage.writeDelimitedTo(os);
		recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
		ServerResponseMessage.UserMessage um =
				  recvMessage.getUserInformationResponse().getUserMessage();
		UserMessage userMessage = new UserMessage(
				  um.getGood(), um.getQuestionNum(),
				  um.getSolvedQuestionNum(), um.getBonus(),
				  um.getSignature(), um.getMailAddress()
		);
		return userMessage;
	}

	public boolean createQuestion(String stem, String addition) throws IOException {
		sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.CREATE_QUESTION_REQUEST)
				  .setUsername(username)
				  .setCreateQuestionRequest(
							 ClientSendMessage.CreateQuestionRequest.newBuilder()
							 .setStem(stem)
							 .setAddition(addition)
				  ).build();
		sendMessage.writeDelimitedTo(os);
		recvMessage = ServerResponseMessage.Message.parseDelimitedFrom(is);
		return recvMessage.getCreateQuestionResponse().getSuccess();
	}

	public ArrayList<Content> parseContents(String contents) {
		ArrayList<Content> contentList = new ArrayList<Content>();

		Pattern pattern = Pattern.compile(token);
		Matcher matcher = pattern.matcher(contents);
		if(matcher.matches()) {
			while (matcher.find()) {
				contentList.add(
						  new Content(matcher.group(1).split("\uD834\uDE19")[0], Content.Type.FILE)
				);
				contentList.add(
						  new Content(matcher.group(2).split("\uD834\uDE19")[1], Content.Type.TEXT)
				);
			}
		}

		return contentList;
	}
}
