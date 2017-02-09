package NetEvent;

import Cos.CosHttpClient;
import Cos.FileOP;
import com.ClientSendMessage;
import com.ServerResponseMessage;
import com.qcloud.cos.request.GetFileLocalRequest;
import com.qcloud.cos.request.UploadFileRequest;
import gui.ChattingBox;
import util.MD5Tools;

import java.io.File;
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
import java.util.Map;
import java.util.Set;

/**
 * Created by xy16 on 17-1-31.
 */
public class Client implements Runnable {

	private static String host = "localhost";
	private static int port = 6666;
	private static ByteBuffer rbuffer = ByteBuffer.allocate(1024);

	private SocketChannel channel = null;
	private Selector selector = null;

	private Socket socket = null;
	private OutputStream os = null;
	private InputStream is = null;

	private String username = null;

	boolean isOver;
	private boolean connected = false;
	public boolean isConnected() {return connected;}

	//顶级包文件路径
	private static final String CLASSPATH=ChattingBox.class.getResource("").getPath();
	private static final String PATH="file:"+CLASSPATH;

	//COS 文件操作
	FileOP fileOP = new FileOP(
			  CosHttpClient.getDefaultConfig(),
			  "",
			  new CosHttpClient(CosHttpClient.getDefaultConfig())
	);

	public void run() {
		try {
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			selector = Selector.open();
			//请求连接
			channel.connect(new InetSocketAddress(host, port));
			channel.register(selector, SelectionKey.OP_CONNECT);

			isOver = false;

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
									System.out.println(information);
									if(status) {
										this.username = recvMessage.getUsername();
									}
									break;
								case SEND_CONTENT:
									ServerResponseMessage.SendContent sendContent =
											  recvMessage.getSendContent();

									boolean isMyself = sendContent.getIsmyself();
									String content = sendContent.getContent();
									ArrayList<String> pictures = new ArrayList<>();

									//若为他人发送的消息
									if(!isMyself) {
										//对每一个图片进行处理
										for (Map.Entry<String, String> entry : sendContent.getPicturesMap().entrySet()) {
											//若文件在本地不存在则下载
											if (!(new File(CLASSPATH + entry.getKey()).exists())) {
												fileOP.changeSign(entry.getValue());
												try {
													fileOP.getFileLocal(
															  new GetFileLocalRequest(
																		 fileOP.getBucktName(),
																		 "/" + entry.getKey(),
																		 CLASSPATH + entry.getKey())
													);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
											pictures.add(entry.getKey());
										}
										ChattingBox.c.pushMessage(isMyself, content, pictures);
									}
									break;
								case ANNOUNCEMENT_MESSAGE:
								case QUESTION_ENTER_RESPONSE:
									ServerResponseMessage.QuestionEnterResponse questionEnterResponse =
											  recvMessage.getQuestionEnterResponse();
									System.out.println(questionEnterResponse.getAllow());
									break;
								case GOOD_QUESTION_RESPONSE:
								case GOOD_USER_RESPONSE:
								case QUESTION_INFORMATION_RESPONSE:
								case USER_INFORMATION_RESPONSE:
								case GET_QUESTION_LIST_RESPONSE:
								case CREATE_QUESTION_RESPONSE:
								case ABANDON_QUESTION_RESPONSE:
								case SEARCH_INFORMATION_RESPONSE:
								case GET_COS_SIGN_RESPONSE:
									ServerResponseMessage.GetCosSignResponse getCosSignResponse =
											  recvMessage.getGetCosSignResponse();

									if(getCosSignResponse.getSuccess()) {
										Set<Map.Entry<String, String>> file_sig = getCosSignResponse.getSignMap().entrySet();
										switch(getCosSignResponse.getSignType()) {
											case UPLOAD:
												for(Map.Entry<String, String> entry : file_sig) {
													try {
														fileOP.changeSign(entry.getValue());
														fileOP.uploadFile(new UploadFileRequest(
																  fileOP.getBucktName(),
																  "/" + entry.getKey(),
																  CLASSPATH + entry.getKey()
														)
														);
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
												break;
											case DOWNLOAD:
												for(Map.Entry<String, String> entry : file_sig) {
													try {
														fileOP.changeSign(entry.getValue());
														fileOP.getFileLocal(new GetFileLocalRequest(
																  fileOP.getBucktName(),
																  "/"+entry.getKey(),
																  CLASSPATH+entry.getValue()
														));
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
												break;
											case UNRECOGNIZED:
										}
									}


									break;
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

	public void close() { isOver = true; }

	//发送请求

	public void launchRequest(String username, String password) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.LAUNCH_REQUEST)
				  .setUsername(username)
				  .setLauchRequest(
							 ClientSendMessage.LaunchRequest.newBuilder()
										.setPassword(MD5Tools.StringToMD5(password))
				  ).build();
		this.username = username;
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
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
		else throw new IOException("尚未连接");
	}

	public void sendContent(String contents,ArrayList<String> pictures,String questionID) throws IOException {
		ClientSendMessage.Message send = null;
		ClientSendMessage.SendContent.Builder contentBuider = ClientSendMessage.SendContent.newBuilder()
				  .setContent(contents)
				  .setQuestionID(Long.valueOf(questionID));

		for(String picture : pictures) {
			contentBuider.addPictures(picture);
		}
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.SEND_CONTENT)
				  .setUsername(username)
				  .setSendContent(contentBuider)
				  .build();

		if(connected) {
			ChattingBox.c.pushMessage(true, contents, pictures);
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
		}
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
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
		else throw new IOException("尚未连接");
	}

	public void goodQuestion(String questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder().setMsgType(ClientSendMessage.MSG.GOOD_QUESTION_REQUEST)
				  .setGoodQuestionRequest(ClientSendMessage.GoodQuestionRequest.newBuilder()
							 .setQuestionID(Long.valueOf(questionID)).build()).build();
		if(connected)
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
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
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
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
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
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
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
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
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
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
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
		else throw new IOException("尚未连接");
	}

	public void abandonQuestion(long questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.ABANDON_QUESTION_REQUEST)
				  .setUsername(username)
				  .setAbandonQuestionRequest(
							 ClientSendMessage.AbandonQuestionRequest.newBuilder()
							 .setQuestionID(questionID)
				  ).build();
		if(connected)
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
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
			channel.write(ByteBuffer.wrap(sendMessage.toByteArray()));
		else throw new IOException("尚未连接");
	}

}
