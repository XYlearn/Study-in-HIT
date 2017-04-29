package NetEvent;

import Cos.CosHttpClient;
import Cos.FileOP;
import NetEvent.dataPack.NetPackageCodeFacotry;
import com.ClientSendMessage;
import com.ServerResponseMessage;
import jdk.nashorn.internal.objects.annotations.Function;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import util.MD5Tools;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by xy16 on 17-1-31.
 */
public class Client extends Thread{

	private static String host = "123.207.159.156";
	private static int port = 8972;

	//connection object
	private ConnectFuture connectFuture = null;
	private NioSocketConnector connector = null;
	private IoSession session = null;
	private boolean launched = false;

	//当前用户名
	private String username = "";

	//连接是否中断
	boolean isOver;
	private boolean connected = false;
	public boolean isConnected() {return connected;}
	public void setConnected(boolean connected) {this.connected = connected;}
	public boolean isLaunched() {return this.launched;}
	public void setLaunched(boolean launched) {this.launched = launched;}

	//文件路径
	public static final String MAINPATH=bin.test.class.getResource("").getPath()
			  .substring(0, bin.test.class.getResource("").getPath().length()-4);
	public static final String PICTPATH=MAINPATH+"pictures/";
	public static final String FILEPATH=MAINPATH+"files/";

	//聊天记录属性
	public enum CONTENT_MARK {
		DEFAULT(0),	//默认
		DOUBTED(1),	//被质疑
		FURTHURASKED(2),	//被追问
		DOUBT(4),	//质疑
		FURTHERASK(8),	//追问
		ANONYMOUS(16),	//匿名
		AUDIO(32), //音频
		FILE(64); //文件

		private final int value;

		CONTENT_MARK(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	//COS 文件操作
	public static FileOP fileOP = new FileOP(
			  CosHttpClient.getDefaultConfig(),
			  "",
			  new CosHttpClient(CosHttpClient.getDefaultConfig())
	);

	@Override
	public void run() {
		//create tcp/ip connector
		connector = new NioSocketConnector();

		//create the filter
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		chain.addLast("codec", new ProtocolCodecFilter(new NetPackageCodeFacotry(true)));

		connector.setHandler(new ClientHandler(this));

		connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 20);
		connector.getSessionConfig().setWriteTimeout(1000);

		//set the default buffersize
		connector.getSessionConfig().setSendBufferSize(10240);
		connector.getSessionConfig().setReadBufferSize(10240);
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

	@Function
	/*等待链接*/
	public boolean waitUntilConnected() {
		if(isConnected())
			return true;
		else {
			try {
				synchronized (this) {
					wait(10000);
				}
			} catch (InterruptedException e) {
				return false;
			}
			if(isConnected())
				return true;
			else return false;
		}
	}

	public boolean waitUntilLaunched() {
		if(isLaunched()) {
			return true;
		} else {
			try {
				synchronized (this) {
					wait(100000);
					if(isLaunched())
						return true;
					else return false;
				}
			} catch (InterruptedException e) {
				return false;
			}
		}
	}

	//发送消息（一般形式
	private void sendIt(ClientSendMessage.Message sendMessage) throws IOException {
		if(connected) {
			connectFuture.getSession().write(sendMessage);
		}
		else throw new IOException("尚未连接");
	}

	//发送请求

	public boolean registerRequest(String username, String password, String mailAddress, String signature) {
		if(password.length() < 6) {
			System.out.println("密码长度过短");
			return false;
		}
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.REGISTER_REQUEST)
				  .setUsername("")
				  .setRegisterRequest(
				  		  ClientSendMessage.RegisterRequest.newBuilder()
							 .setUsername(username)
							 .setPassword(MD5Tools.StringToMD5(password))
							 .setMailAddress(mailAddress)
							 .setSignature(signature)
				  ).build();
		try {
			sendIt(sendMessage);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

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


	//not recommend
	public void sendContent(String contents,ArrayList<String> pictures,long questionID) throws IOException {
		ClientSendMessage.Message send = null;
		ClientSendMessage.SendContent.Builder contentBuider = ClientSendMessage.SendContent.newBuilder()
				  .setContent(contents)
				  .setQuestionID(questionID);

		if(pictures!=null) {
			ArrayList<String> md5s = new ArrayList<>();
			for (Iterator<String> it = pictures.iterator(); it.hasNext(); ) {
				File file = new File(it.next());
				if(!file.exists()) {
					it.remove();
					continue;
				}

				String md5 = MD5Tools.FileToMD5(file);
				md5s.add(md5);
				file.renameTo(new File(PICTPATH+md5));
			}
			contentBuider.addAllPictures(md5s);
			for(String md5 : md5s) {
				md5=PICTPATH+md5;
			}

			uploadFiles(md5s);
		}

		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.SEND_CONTENT)
				  .setUsername(username)
				  .setSendContent(contentBuider)
				  .build();

		sendIt(sendMessage);
	}

	public void sendContent(String contents, ArrayList<String> pictures, long questionID,
									Map<Integer, Long> markMap) throws IOException {
		ClientSendMessage.Message send = null;
		ClientSendMessage.SendContent.Builder contentBuider = ClientSendMessage.SendContent.newBuilder()
				  .setContent(contents)
				  .setQuestionID(questionID);

		if(markMap!=null) {
			contentBuider.putAllMarkMap(markMap);
		} else {
			contentBuider.putAllMarkMap(new HashMap<>());
		}

		//图片处理
		if(pictures!=null) {
			ArrayList<String> md5s = new ArrayList<>();
			ArrayList<String> picturesExist = new ArrayList<>();
			for (String picture : pictures) {
				File file = new File(picture);
				if(!file.exists()) {
					continue;
				} else {
					picturesExist.add(picture);
				}

				String md5 = MD5Tools.FileToMD5(file);
				md5s.add(md5);
				file.renameTo(new File(md5));
			}
			contentBuider.addAllPictures(md5s);

			uploadFiles(picturesExist);
		}

		//发送消息
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.SEND_CONTENT)
				  .setUsername(username)
				  .setSendContent(contentBuider)
				  .build();

		sendIt(sendMessage);

	}


	public void goodUser(String user) throws IOException {
		ClientSendMessage.Message sendMessage =
				  ClientSendMessage.Message.newBuilder()
							 .setMsgType(ClientSendMessage.MSG.GOOD_USER_REQUEST)
							 .setUsername(username)
							 .setGoodUserRequest(
										ClientSendMessage.GoodUserRequest.newBuilder()
												  .setUser(user)
							 ).build();
		sendIt(sendMessage);
	}

	public void goodQuestion(long questionID) throws IOException {
		ClientSendMessage.Message sendMessage =
				  ClientSendMessage.Message.newBuilder()
							 .setMsgType(ClientSendMessage.MSG.GOOD_QUESTION_REQUEST)
							 .setUsername(username)
							 .setGoodQuestionRequest(
							 		  ClientSendMessage.GoodQuestionRequest.newBuilder()
							 			.setQuestionID(Long.valueOf(questionID))).build();
		sendIt(sendMessage);
	}


	public void enterQuestion(long questionID) throws IOException {
		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.QUESTION_ENTER_REQUEST)
				  .setUsername(username)
				  .setQuestionEnterRequest(
							 ClientSendMessage.QuestionEnterRequest.newBuilder()
										.setQuestionID(Long.valueOf(questionID))
				  ).build();
		sendIt(sendMessage);
	}

	public void requestQuestionInfo(long questionID) throws IOException {
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

	public void createQuestion(String stem, String addition, List<String> keywords) throws IOException {
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

	public void createQuestion(String stem, String addition,
										ArrayList<String> keywords, List<String> stempics, List<String> additionpics)
			  throws IOException {
		//创建问题字消息builder
		ClientSendMessage.CreateQuestionRequest.Builder createBuilder =
				  ClientSendMessage.CreateQuestionRequest.newBuilder()
							 .setStem(stem)
							 .setAddition(addition);
		if(stempics!=null) {
			this.uploadFiles(stempics);
			createBuilder.addAllStempic(stempics);
		}
		if(additionpics!=null) {
			this.uploadFiles(additionpics);
			createBuilder.addAllAdditionpic(additionpics);
		}
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

	public void searchInformation(ArrayList<String> keywords, int searchID) throws IOException {
		ClientSendMessage.SearchInformationRequest.Builder searchBuider =
				  ClientSendMessage.SearchInformationRequest.newBuilder();

		for(String keyword : keywords) {
			searchBuider.addKeywords(keyword);
		}
		searchBuider.setSearchID(searchID);

		ClientSendMessage.Message sendMessage = ClientSendMessage.Message.newBuilder()
				  .setMsgType(ClientSendMessage.MSG.SEARCH_INFORMATION_REQUEST)
				  .setUsername(username)
				  .setSearchInformationRequest(searchBuider).build();
		sendIt(sendMessage);
	}

	public void searchInformation(ArrayList<String> keywords) throws IOException {
		searchInformation(keywords, 0);
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

	public boolean uploadFile(String filePath) throws IOException {
		ClientSendMessage.Message request = null;
		ClientSendMessage.FileRequest.Builder builder = ClientSendMessage.FileRequest.newBuilder();

		File file = new File(filePath);
		if(!file.exists()) {
			System.out.println("文件不存在");
			return false;
		}

		String filename = file.getName();
		ArrayList<String> filenames = new ArrayList<>();
		filenames.add(filename);
		ArrayList<String> localFilePaths = new ArrayList<>();
		localFilePaths.add(filePath);
		ArrayList<String> md5s = new ArrayList<>();
		md5s.add(MD5Tools.FileToMD5(file));

		builder.addAllFilename(filenames)
				  .addAllLocalFilePath(localFilePaths)
				  .addAllMd5(md5s)
				  .setSignType(ClientSendMessage.FileRequest.SIGNTYPE.UPLOAD);

		request = ClientSendMessage.Message.newBuilder()
				  .setUsername(username)
				  .setMsgType(ClientSendMessage.MSG.FILE_REQUEST)
				  .setFileRequest(builder)
				  .build();

		sendIt(request);

		return true;
	}

	public boolean uploadFiles(Iterable<String> filePaths) throws IOException {
		ClientSendMessage.Message request = null;
		ClientSendMessage.FileRequest.Builder builder = ClientSendMessage.FileRequest.newBuilder();

		ArrayList<String> localFilePaths = new ArrayList<>();
		ArrayList<String> filenames = new ArrayList<>();
		ArrayList<String> md5s = new ArrayList<>();

		for(String filePath : filePaths) {
			File file = new File(filePath);
			if (!file.exists()) {
				System.out.println("文件不存在");
				return false;
			} else {
				String filename = file.getName();
				filenames.add(filename);
				localFilePaths.add(filePath);
				md5s.add(MD5Tools.FileToMD5(file));
			}

		}
		builder.addAllFilename(filenames)
				  .addAllLocalFilePath(localFilePaths)
				  .addAllMd5(md5s)
				  .setSignType(ClientSendMessage.FileRequest.SIGNTYPE.UPLOAD);

		request = ClientSendMessage.Message.newBuilder()
				  .setUsername(username)
				  .setMsgType(ClientSendMessage.MSG.FILE_REQUEST)
				  .setFileRequest(builder)
				  .build();

		sendIt(request);

		return true;
	}

	public void downloadFile(String filename) throws IOException {
		ClientSendMessage.Message request = null;
		ClientSendMessage.FileRequest.Builder builder = ClientSendMessage.FileRequest.newBuilder();

		ArrayList<String> filenames = new ArrayList<>();
		filenames.add(filename);

		builder.addAllFilename(filenames).setSignType(ClientSendMessage.FileRequest.SIGNTYPE.DOWNLOAD);

		request = ClientSendMessage.Message.newBuilder()
				.setMsgType(ClientSendMessage.MSG.FILE_REQUEST)
				.setUsername(username)
				.setFileRequest(builder)
				.build();

		sendIt(request);
	}

	public void downloadFiles(Iterable<String> filenames) throws IOException {
		ClientSendMessage.Message request = null;
		ClientSendMessage.FileRequest.Builder builder = ClientSendMessage.FileRequest.newBuilder();

		builder.addAllFilename(filenames).setSignType(ClientSendMessage.FileRequest.SIGNTYPE.DOWNLOAD);

		request = ClientSendMessage.Message.newBuilder()
				.setMsgType(ClientSendMessage.MSG.FILE_REQUEST)
				.setUsername(username)
				.setFileRequest(builder)
				.build();

		sendIt(request);
	}

	public void getAcquaintanceList() throws IOException {
		ClientSendMessage.Message request = ClientSendMessage.Message.newBuilder()
				.setMsgType(ClientSendMessage.MSG.GET_USER_LIST_REQUEST)
				.setUsername(username)
				.setGetUserListRequest(
						ClientSendMessage.GetUserListRequest.newBuilder()
								.setUserListType(ClientSendMessage.GetUserListRequest.USER_LIST_TYPE.ACQUAINTANCE_LIST)
								.setParam(this.username)
				).build();

		sendIt(request);
	}

	public void getQuestionUserList(long questionID) throws IOException {
		ClientSendMessage.Message request = ClientSendMessage.Message.newBuilder()
				.setMsgType(ClientSendMessage.MSG.GET_USER_LIST_REQUEST)
				.setUsername(username)
				.setGetUserListRequest(
						ClientSendMessage.GetUserListRequest.newBuilder()
								.setUserListType(ClientSendMessage.GetUserListRequest.USER_LIST_TYPE.USERS_IN_ROOM_LIST)
								.setParam(String.valueOf(questionID))
				).build();

		sendIt(request);
	}

}
