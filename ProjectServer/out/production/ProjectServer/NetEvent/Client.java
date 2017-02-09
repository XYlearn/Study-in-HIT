package NetEvent;

import com.ClientSendMessage;
import com.MD5;
import com.ServerResponseMessage;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.http.client.fluent.Content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by xy16 on 17-1-31.
 */
public class Client implements Runnable {

	private static ByteBuffer sbuffer = ByteBuffer.allocate(1024);
	private static ByteBuffer rbuffer = ByteBuffer.allocate(1024);

	private SocketChannel channel = null;
	private Selector selector = null;

	private Socket socket = null;
	private OutputStream os = null;
	private InputStream is = null;

	private String username = null;

	private boolean connected = false;

	public static void main(String[] args) {
		Client client = new Client();
		new Thread(client).start();
		try {
			client.launchRequest("xy16", "123456");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			selector = Selector.open();
			//请求连接
			channel.connect(new InetSocketAddress("127.0.0.1", 6666));
			channel.register(selector, SelectionKey.OP_CONNECT);

			boolean isOver = false;

			while (! isOver) {
				selector.select();
				Iterator ite = selector.selectedKeys().iterator();
				while (ite.hasNext()) {
					SelectionKey key = (SelectionKey)ite.next();
					ite.remove();

					if(key.isConnectable()) {
						if(channel.isConnectionPending()) {
							if(channel.finishConnect()) {
								//获取io
								this.socket = channel.socket();
								this.is = socket.getInputStream();
								this.os = socket.getOutputStream();
								connected = true;
								//连接后才可读
								key.interestOps(SelectionKey.OP_READ) ;
							} else {
								key.cancel();
							}
						}
					} else if(key.isReadable() && connected) {
						//读取数据
						rbuffer.clear();
						channel.read(rbuffer);
						rbuffer.flip();
						byte[] readByte = new byte[rbuffer.remaining()];
						rbuffer.get(readByte);
						ServerResponseMessage.Message recvMessage =
								  ServerResponseMessage.Message.parseFrom(readByte);

						//处理数据
						if(recvMessage!=null) {
							switch (recvMessage.getMsgType()) {
								case LAUNCH_RESPONSE:
									ServerResponseMessage.LaunchResponse lr = recvMessage.getLauchResponse();
									ServerResponseMessage.UserMessage um = lr.getUserMessage();
									boolean status = lr.getStatus();
									String information = lr.getInformation();
									int good = um.getGood();
									int questionNum = um.getQuestionNum();
									int solvedQuestionNum = um.getSolvedQuestionNum();
									int bonus = um.getBonus();
									String signature = um.getSignature();
									String mail_address = um.getMailAddress();
									String pic_url = um.getPicUrl();

									if(status) {
										this.username = recvMessage.getUsername();
									}
									break;
								case SEND_CONTENT:
									//pushMessage(info, String text, Arraylist<String> pic)
								case ANNOUNCEMENT_MESSAGE:
								case QUESTION_ENTER_RESPONSE:
								case GOOD_QUESTION_RESPONSE:
								case GOOD_USER_RESPONSE:
								case QUESTION_INFORMATION_RESPONSE:
								case USER_INFORMATION_RESPONSE:
								case GET_QUESTION_LIST_RESPONSE:
								case CREATE_QUESTION_RESPONSE:
								case ABANDON_QUESTION_RESPONSE:
								case SEARCH_INFORMATION_RESPONSE:
								case GET_COS_SIGN_RESPONSE:
								default:
							}
						} else break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//发送请求

	public void launchRequest(String username, String password) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.LAUNCH_REQUEST)
				  .setUsername(username)
				  .setLauchRequest(
							 ClientSendMessage.LaunchRequest.newBuilder()
										.setPassword(MD5.getMd5(password))
				  ).build();
		if(connected) {
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
		}
		else throw new IOException("尚未连接");
	}

	public void logout() throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.LOGOUT_MESSAGE)
				  .setUsername(username)
				  .build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void sendContent(String contents, String questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.SEND_CONTENT)
				  .setUsername(username)
				  .setSendContent(
							 ClientSendMessage.SendContent.newBuilder()
										.setQuestionID(Long.valueOf(questionID))
										.setContent(contents)
				  ).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void goodUser(String user) throws IOException {
		ClientSendMessage.Message sendMessage =
				  ClientSendMessage.Message.newBuilder()
							 .setMsgType(ClientSendMessage.MSG.GOOD_USER_REQUEST)
							 .setGoodUserRequest(
							 		  ClientSendMessage.GoodUserRequest.newBuilder()
												 .setUser(user)
							 ).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void goodQuestion(String questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder().setMsgType(ClientSendMessage.MSG.GOOD_QUESTION_REQUEST)
				  .setGoodQuestionRequest(ClientSendMessage.GoodQuestionRequest.newBuilder()
							 .setQuestionID(Long.valueOf(questionID)).build()).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void enterQuestion(String questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.QUESTION_ENTER_REQUEST)
				  .setUsername(username)
				  .setQuestionEnterRequest(
							 ClientSendMessage.QuestionEnterRequest.newBuilder()
										.setQuestionID(Long.valueOf(questionID))
				  ).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void requestQuestionInfo(String questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.QUESTION_INFORMATION_REQUEST)
				  .setUsername(username)
				  .setQuestionInformationRequest(
							 ClientSendMessage.QuestionInformationRequest.newBuilder()
										.setQuestionID(Long.valueOf(questionID))
				  ).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void requestQuestionList(ClientSendMessage.LIST_REFERENCE reference,
											  ClientSendMessage.RANKORDER rankorder,
											  int	questionNum) throws  IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.GET_QUESTION_LIST_REQUEST)
				  .setUsername(username)
				  .setGetQuestionListRequest(
				  		  ClientSendMessage.GetQuestionListRequest.newBuilder()
							 .setRankorder(rankorder)
							 .setReference(reference)
							 .setQuestionNumber(questionNum)
				  ).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void requestUserInfo(String user) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.USER_INFORMATION_REQUEST)
				  .setUsername(username)
				  .setUserInformationRequest(
							 ClientSendMessage.UserInformationRequest.newBuilder()
										.setUsername(user)
				  ).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void createQuestion(String stem, String addition) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.CREATE_QUESTION_REQUEST)
				  .setUsername(username)
				  .setCreateQuestionRequest(
							 ClientSendMessage.CreateQuestionRequest.newBuilder()
										.setStem(stem)
										.setAddition(addition)
				  ).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void abandonQuestion(Long questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.ABANDON_QUESTION_REQUEST)
				  .setUsername(username)
				  .setAbandonQuestionRequest(
							 ClientSendMessage.AbandonQuestionRequest.newBuilder()
							 .setQuestionID(questionID)
				  ).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}

	public void searchInformation(String keyword) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.SEARCH_INFORMATION_REQUEST)
				  .setUsername(username)
				  .setSearchInformationRequest(
							 ClientSendMessage.SearchInformationRequest.newBuilder()
							 .setKeyword(keyword)
				  ).build();
		if(connected)
			sendMessage.writeDelimitedTo(os);
		else throw new IOException("尚未连接");
	}
}
