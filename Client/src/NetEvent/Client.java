package NetEvent;

import Cos.CosHttpClient;
import Cos.FileOP;
import NetEvent.dataPack.NetPackageCodeFacotry;
import com.ClientSendMessage;
import com.ServerResponseMessage;
import com.qcloud.cos.request.GetFileLocalRequest;
import com.qcloud.cos.request.UploadFileRequest;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
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
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xy16 on 17-1-31.
 */
public class Client extends Thread{

	private static String host = "localhost";
	private static int port = 8972;

	//connection object
	private ConnectFuture connectFuture = null;
	private NioSocketConnector connector = null;
	private IoSession session = null;

	//当前用户名
	private String username = "";

	//连接是否中断
	boolean isOver;
	private boolean connected = false;
	public boolean isConnected() {return connected;}

	//文件路径
	public static final String MAINPATH=bin.test.class.getResource("").getPath()
			  .substring(0, bin.test.class.getResource("").getPath().length()-4);
	public static final String PICTPATH=MAINPATH+"pictures/";
	public static final String FILEPATH=MAINPATH+"files/";

	//COS 文件操作
	public static FileOP fileOP = new FileOP(
			  CosHttpClient.getDefaultConfig(),
			  "",
			  new CosHttpClient(CosHttpClient.getDefaultConfig())
	);

	public void run() {
		//create tcp/ip connector
		connector = new NioSocketConnector();

		//create the filter
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		chain.addLast("codec", new ProtocolCodecFilter(new NetPackageCodeFacotry(true)));

		connector.setHandler(new ClientHandler());

		connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 20);

		//set the default buffersize
		connector.getSessionConfig().setSendBufferSize(10240);
		connector.getSessionConfig().setReceiveBufferSize(10240);

		//connect to the server
		connectFuture = connector.connect(new InetSocketAddress(host, port));

		//set the flag
		connected = true;

		//wait for the connection attempt to be finished
		connectFuture.awaitUninterruptibly();

		connectFuture.getSession().getCloseFuture().awaitUninterruptibly();

		connector.dispose();

	}

	//发送消息（一般形式
	private void sendIt(ClientSendMessage.Message sendMessage) throws IOException {
		if(connected) {
			connectFuture.getSession().write(sendMessage);
		}
		else throw new IOException("尚未连接");
	}

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

		//发送消息
		sendIt(sendMessage);
	}


	public void logout() throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.LOGOUT_MESSAGE)
				  .setUsername(username)
				  .build();
		sendIt(sendMessage);
	}


	public void sendContent(String contents,ArrayList<String> pictures,String questionID) throws IOException {
		ClientSendMessage.Message send = null;
		ClientSendMessage.SendContent.Builder contentBuider = ClientSendMessage.SendContent.newBuilder()
				  .setContent(contents)
				  .setQuestionID(Long.valueOf(questionID));

		if(pictures!=null) {
			for (String picture : pictures) {
				contentBuider.addPictures(picture);
			}
		}
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.SEND_CONTENT)
				  .setUsername(username)
				  .setSendContent(contentBuider)
				  .build();

		sendIt(sendMessage);

		//在自己的页面上显示
	}


	public void goodUser(String user) throws IOException {
		ClientSendMessage.Message sendMessage =
				  ClientSendMessage.Message.newBuilder()
							 .setMsgType(ClientSendMessage.MSG.GOOD_USER_REQUEST)
							 .setGoodUserRequest(
										ClientSendMessage.GoodUserRequest.newBuilder()
												  .setUser(user)
							 ).build();
		sendIt(sendMessage);
	}

	public void goodQuestion(String questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder().setMsgType(ClientSendMessage.MSG.GOOD_QUESTION_REQUEST)
				  .setGoodQuestionRequest(ClientSendMessage.GoodQuestionRequest.newBuilder()
							 .setQuestionID(Long.valueOf(questionID)).build()).build();
		sendIt(sendMessage);
	}


	public void enterQuestion(String questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.QUESTION_ENTER_REQUEST)
				  .setUsername(username)
				  .setQuestionEnterRequest(
							 ClientSendMessage.QuestionEnterRequest.newBuilder()
										.setQuestionID(Long.valueOf(questionID))
				  ).build();
		sendIt(sendMessage);
	}

	public void requestQuestionInfo(String questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.QUESTION_INFORMATION_REQUEST)
				  .setUsername(username)
				  .setQuestionInformationRequest(
							 ClientSendMessage.QuestionInformationRequest.newBuilder()
										.setQuestionID(Long.valueOf(questionID))
				  ).build();
		sendIt(sendMessage);
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
		sendIt(sendMessage);
	}

	public void requestUserInfo(String user) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.USER_INFORMATION_REQUEST)
				  .setUsername(username)
				  .setUserInformationRequest(
							 ClientSendMessage.UserInformationRequest.newBuilder()
										.setUsername(user)
				  ).build();
		sendIt(sendMessage);
	}

	private void responseUserInfo(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.UserInformationResponse userInformationResponse =
				  recvMessage.getUserInformationResponse();
		if(!userInformationResponse.getExist()) {
			System.out.println("请求的用户不存在");
		} else {
			ServerResponseMessage.UserMessage userMessage = userInformationResponse.getUserMessage();
			String user = userMessage.getUsername();
			int good = userMessage.getGood();
			int questionNum = userMessage.getQuestionNum();
			int solvedQuestionNum = userMessage.getSolvedQuestionNum();
			int bonus = userMessage.getBonus();
			String signature = userMessage.getSignature();
			String mail_address = userMessage.getMailAddress();
			String pic_url = userMessage.getPicUrl();

			System.out.println("用户名：" + user);
			System.out.println("签名：\t" + signature);
			System.out.println("点数：\t" + bonus);
			System.out.println("赞：\t" + bonus);
			System.out.println("问题数：\t" + questionNum);
			System.out.println("已解决问题数：\t" + solvedQuestionNum);
			System.out.println("邮箱：\t" + mail_address);
		}
		System.out.println();
	}

	public void createQuestion(String stem, String addition, ArrayList<String> keywords) throws IOException {
		//创建问题字消息builder
		ClientSendMessage.CreateQuestionRequest.Builder createBuilder =
				  ClientSendMessage.CreateQuestionRequest.newBuilder()
							 .setStem(stem)
							 .setAddition(addition);
		//添加关键字
		for(String keyword : keywords) {
			createBuilder.addKeywords(keyword);
		}

		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.CREATE_QUESTION_REQUEST)
				  .setUsername(username)
				  .setCreateQuestionRequest(createBuilder).build();
		sendIt(sendMessage);
	}

	public void abandonQuestion(long questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.ABANDON_QUESTION_REQUEST)
				  .setUsername(username)
				  .setAbandonQuestionRequest(
							 ClientSendMessage.AbandonQuestionRequest.newBuilder()
										.setQuestionID(questionID)
				  ).build();
		sendIt(sendMessage);
	}

	public void searchInformation(ArrayList<String> keywords) throws IOException {
		ClientSendMessage.SearchInformationRequest.Builder searchBuider =
				  ClientSendMessage.SearchInformationRequest.newBuilder();

		for(String keyword : keywords) {
			searchBuider.addKeywords(keyword);
		}

		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.SEARCH_INFORMATION_REQUEST)
				  .setUsername(username)
				  .setSearchInformationRequest(searchBuider).build();
		sendIt(sendMessage);
	}

	public void solveQuestion(long questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.SOLVED_QUESTION_REQUEST)
				  .setUsername(username)
				  .setSolvedQuestionRequest(
							 ClientSendMessage.SolvedQuestionRequest.newBuilder()
							 .setQuestionID(questionID)
				  ).build();
		sendIt(sendMessage);
	}



}
