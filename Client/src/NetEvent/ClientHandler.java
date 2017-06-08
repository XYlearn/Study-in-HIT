package NetEvent;

import NetEvent.eventcom.*;
import bin.test;
import com.ClientSendMessage;
import com.ServerResponseMessage;
import com.google.protobuf.ProtocolStringList;
import com.qcloud.cos.request.GetFileLocalRequest;
import com.qcloud.cos.request.UploadFileRequest;
import gui.ChattingBox;
import gui.InputBox;
import gui.ListBox;
import gui.WhiteBoard;
import jdk.nashorn.internal.objects.annotations.Function;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import util.UserInfo;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Native;
import java.util.*;

/**
 * Created by xy16 on 17-2-25.
 */
public class ClientHandler extends IoHandlerAdapter {
	ClientHandler(Client client) {
		this.client = client;
	}

	@Override
	public void sessionOpened(IoSession session) {
		System.out.println("Connect to "+session);
		synchronized (client) {
			client.setConnected(true);
			client.notifyAll();
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		ServerResponseMessage.Message recvMessage = (ServerResponseMessage.Message) message;
		System.out.println("Received Message:\n"+recvMessage.toString());
		//处理数据
		NetEvent netEvent = null;
		if (recvMessage != null) {
			switch (recvMessage.getMsgType()) {
				case REGISTER_RESPONSE:	//
					netEvent = handleRegisterResponse(recvMessage);
					break;
				case LAUNCH_RESPONSE:	//
					netEvent = handleResponseLaunch(recvMessage);
					synchronized (client) {
						client.setLaunched(((LaunchEvent) netEvent).isSuccess());
						client.notifyAll();
					}
					test.loginFrame.dispatch(netEvent);
					break;
				case SEND_CONTENT:
					netEvent = handleResponseSendContent(recvMessage);
					ChattingBox.dispatch(netEvent);
					InputBox.dispatch(netEvent);	// if received message is from myself then delete it from input box
					break;
				case ANNOUNCEMENT_MESSAGE:	//
					break;
				case QUESTION_ENTER_RESPONSE:	//
					netEvent = handleResponseEnterQuestion(recvMessage);
					InputBox.dispatch(netEvent);
					ChattingBox.dispatch(netEvent);
					break;
				case GOOD_QUESTION_RESPONSE:	//
					netEvent = handleResponseGoodQuestion(recvMessage);
					break;
				case GOOD_USER_RESPONSE:	//
					netEvent = handleResponseGoodUser(recvMessage);
					break;
				case QUESTION_INFORMATION_RESPONSE:	//
					netEvent = handleResponseQuestionInfo(recvMessage);
					break;
				case USER_INFORMATION_RESPONSE:	//
					netEvent = handleResponseUserInformation(recvMessage);
					UserInfo.dispatch((UserInfoEvent) netEvent);
					break;
				case GET_QUESTION_LIST_RESPONSE:	//
					netEvent = handleResponseQuestionList(recvMessage);
					ListBox.dispatch(netEvent);
					break;
				case CREATE_QUESTION_RESPONSE:	//
					netEvent = handleResponseCreateQuestion(recvMessage);
					test.mainFrame.dispatch(netEvent);
					break;
				case ABANDON_QUESTION_RESPONSE:	//
					break;
				case SEARCH_INFORMATION_RESPONSE:	//
					netEvent = handleResponseSearchInformation(recvMessage);
					ListBox.dispatch(netEvent);
					break;
				case FILE_RESPONSE:	//
					handleFileResponse(recvMessage);
					break;
				case UPDATE_MESSAGE:	//
					handleUpdateMessage(recvMessage);
					break;
				case SOLVED_QUESTION_RESPONSE:
					netEvent = handleResponseSolvedQuestion(recvMessage);
					ChattingBox.dispatch(netEvent);
					break;
				case GET_USER_LIST_RESPONSE:	//
					netEvent = handleResponseGetUserList(recvMessage);
					break;
				case WHITE_BOARD_MESSAGE:
					netEvent = handleResponseWhiteBoardMessage(recvMessage);
					WhiteBoard.dispatch(netEvent);
					break;
				case BAD_MESSAGE:	//
					System.out.println("未知消息:\n" + recvMessage);
					break;
				default:
					throw new Exception("Unknown Message Type");
			}
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) {
		System.out.println("Send Message:\n"+((ClientSendMessage.Message)message).toString());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("Connection End");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("Connection Begin");
	}

	/*handle functions*/

	@Function
	private NetEvent handleRegisterResponse(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.RegisterResponse registerResponse = recvMessage.getRegisterResponse();

		return (NetEvent) new RegisterEvent(registerResponse);
	}

	private NetEvent handleResponseLaunch(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.LaunchResponse lr = recvMessage.getLauchResponse();

		return (NetEvent) new LaunchEvent(lr);
	}

	private NetEvent handleResponseSendContent(ServerResponseMessage.Message recvMessage) throws IOException{
		return (NetEvent) new ContentMessageEvent(recvMessage.getSendContent());
	}

	private NetEvent handleResponseGoodUser(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new GoodUserEvent(recvMessage.getGoodUserResponse());
	}

	private NetEvent handleResponseGoodQuestion(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new GoodQuestionEvent(recvMessage.getGoodQuestionResponse());
	}

	private NetEvent handleResponseEnterQuestion(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new EnterQuestionEvent(recvMessage.getQuestionEnterResponse());
	}

	private NetEvent handleResponseQuestionInfo(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new GetQuestionInfoEvent(recvMessage.getQuestionInformationResponse());
	}

	private NetEvent handleResponseQuestionList(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new QuestionListEvent(recvMessage.getGetQuestionListResponse());
	}

	private NetEvent handleResponseCreateQuestion(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new CreateQuestionEvent(recvMessage.getCreateQuestionResponse());
	}

	private void handleFileResponse (ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.FileResponse fileResponse =
				  recvMessage.getFileResponse();

		if(fileResponse.getSuccess()) {
			Set<Map.Entry<String, String>> file_sigs = fileResponse.getSignMap().entrySet();
			int i;
			switch (fileResponse.getSignType()) {
				case UPLOAD:
					i=0;
					for (Map.Entry<String, String> entry: file_sigs) {
						try {
							Client.fileOP.changeSign(entry.getValue());
							String s = Client.fileOP.uploadSingleFile(
									  new UploadFileRequest(
												 Client.fileOP.getBucktName(),
												 "/" + entry.getKey(),
												 fileResponse.getLocalFilePath(i++)
									  )
							);
							System.out.println(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
				case DOWNLOAD:
					ProtocolStringList md5s = fileResponse.getMd5List();
					String localPath = "";
					i=0;
					for (Map.Entry<String, String> entry : file_sigs) {
						try {
							File f = new File(PICTPATH);
							if(!f.exists()) {
								f.mkdir();
							}
							f = new File(PICTPATH + entry.getKey());
							if(!f.exists()) {
								f.createNewFile();
							}
							Client.fileOP.changeSign(entry.getValue());
							Client.fileOP.getFileLocal(new GetFileLocalRequest(
									  Client.fileOP.getBucktName(),
									  "/" + md5s.get(i++),
									  PICTPATH + entry.getValue()+util.FileOperator.getExtension(entry.getKey())
							));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
				case UNRECOGNIZED:
					System.out.println("无效的FileResponse");
					break;
				default:
			}
		} else
			System.out.println("下载失败");
	}

	private void handleUpdateMessage(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.UpdateMessage.UserEnter userEnter =
				  recvMessage.getUpdateMessage().getUserEnter();
		String user = userEnter.getUsername();
		long questionID = userEnter.getQuestionID();
		System.out.println(user+"进入了问题"+questionID);
		System.out.println();
	}

	private NetEvent handleResponseSolvedQuestion(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new SolvedQuestionEvent(recvMessage.getSolvedQuestionResponse());
	}

	private NetEvent handleResponseSearchInformation(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new SearchQuestionEvent(recvMessage.getSearchInformationResponse());
	}

	private NetEvent handleResponseGetUserList(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.GetUserListResponse getUserListResponse = recvMessage.getGetUserListResponse();
		switch (getUserListResponse.getUserListType()) {
			case USERS_IN_ROOM_LIST:
				return (NetEvent) new AcquaintanceUserListEvent(getUserListResponse);
			case ACQUAINTANCE_LIST:
				return (NetEvent) new QuestionUserListEvent(getUserListResponse);
			default:
				return null;
		}
	}

	private NetEvent handleResponseUserInformation(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new UserInfoEvent(recvMessage.getUserInformationResponse());
	}

	private NetEvent handleResponseWhiteBoardMessage(ServerResponseMessage.Message recvMessage) {
		return (NetEvent) new WhiteBoardEvent(recvMessage.getWhiteBoardMessage());
	}

	@Native
	private Client client;
	private String username = "";

	public static final String MAINPATH=bin.test.class.getResource("").getPath()
			  .substring(0, bin.test.class.getResource("").getPath().length()-4);
	public static final String PICTPATH=MAINPATH+"pictures/";
	public static final String FILEPATH=MAINPATH+"files/";

}
