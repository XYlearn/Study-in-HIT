package NetEvent;

import com.ServerResponseMessage;
import com.google.protobuf.ProtocolStringList;
import com.qcloud.cos.request.GetFileLocalRequest;
import com.qcloud.cos.request.UploadFileRequest;
import jdk.nashorn.internal.objects.annotations.Function;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Native;
import java.util.*;
import java.util.function.Consumer;

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
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		ServerResponseMessage.Message recvMessage = (ServerResponseMessage.Message) message;
		//处理数据
		if (recvMessage != null) {
			switch (recvMessage.getMsgType()) {
				case REGISTER_RESPONSE:
					handleRegisterResponse(recvMessage);
					break;
				case LAUNCH_RESPONSE:
					handleResponseLaunch(recvMessage);
					break;
				case SEND_CONTENT:
					handleResponseSendContent(recvMessage);
					break;
				case ANNOUNCEMENT_MESSAGE:
				case QUESTION_ENTER_RESPONSE:
					handleResponseEnterQuestion(recvMessage);
					break;
				case GOOD_QUESTION_RESPONSE:
					handleResponseGoodQuestion(recvMessage);
					break;
				case GOOD_USER_RESPONSE:
					handleResponseGoodUser(recvMessage);
					break;
				case QUESTION_INFORMATION_RESPONSE:
					handleResponseQuestionInfo(recvMessage);
					break;
				case USER_INFORMATION_RESPONSE:

					break;
				case GET_QUESTION_LIST_RESPONSE:
					handleResponseQuestionList(recvMessage);
					break;
				case CREATE_QUESTION_RESPONSE:
					handleResponseCreateQuestion(recvMessage);
					break;
				case ABANDON_QUESTION_RESPONSE:
					break;
				case SEARCH_INFORMATION_RESPONSE:
					break;
				case FILE_RESPONSE:
					handleFileResponse(recvMessage);
					break;
				case UPDATE_MESSAGE:
					handleUpdateMessage(recvMessage);
					break;
				case SOLVED_QUESTION_RESPONSE:
					handleResponseSolvedQuestion(recvMessage);
					break;
				case BAD_MESSAGE:
					System.out.println("未知消息");
					break;
				default:
					throw new Exception("Unknown Message Type");
			}
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) {
		System.out.println("Send message");
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
	private void handleRegisterResponse(ServerResponseMessage.Message recvMessage)
	{
		ServerResponseMessage.RegisterResponse registerResponse = recvMessage.getRegisterResponse();

		System.out.println(registerResponse.getInformation());
	}

	private void handleResponseLaunch(ServerResponseMessage.Message recvMessage) {
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
		if (status) {
			this.username = um.getUsername();
			System.out.println("你好" + username);
			System.out.println("签名：\t" + signature);
			System.out.println("点数：\t" + bonus);
			System.out.println("赞：\t" + good);
			System.out.println("问题数：\t" + questionNum);
			System.out.println("已解决问题数：\t" + solvedQuestionNum);
			System.out.println("邮箱：\t" + mail_address);
		}
		System.out.println();
	}

	private void handleResponseSendContent(ServerResponseMessage.Message recvMessage) throws IOException{
		ServerResponseMessage.SendContent sendContent =
				  recvMessage.getSendContent();

		if(!sendContent.getSuccess()) {
			System.out.println("发送失败");
			return;
		}

		boolean isMyself = sendContent.getIsmyself();
		String content = sendContent.getContent();
		ArrayList<String> pictures = new ArrayList<>();

		//若为他人发送的消息
		if(!isMyself) {
			client.downloadFiles(pictures);
		}

		System.out.println((isMyself?"你":recvMessage.getSendContent().getUser())
				  +"在问题房"+recvMessage.getSendContent().getQuestionID()+"发送了:"+content);
	}

	private void handleResponseGoodUser(ServerResponseMessage.Message recvMessage) {
		if (recvMessage.getGoodUserResponse().getSuccess()) {
			System.out.println("成功");
		} else {
			System.out.println("用户不存在");
		}
		System.out.println();
	}

	private void handleResponseGoodQuestion(ServerResponseMessage.Message recvMessage) {
		if (recvMessage.getGoodQuestionResponse().getSuccess()) {
			System.out.println("成功");
		} else {
			System.out.println("问题不存在");
		}
		System.out.println();
	}

	public void handleResponseEnterQuestion(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.QuestionEnterResponse questionEnterResponse =
				  recvMessage.getQuestionEnterResponse();
		ServerResponseMessage.QuestionMessage qm = questionEnterResponse.getQuestionMessage();
		if (!questionEnterResponse.getAllow()) {
			System.out.println("房间不存在");
		} else {
			System.out.println("成功进入");
			System.out.println("问题号：" + qm.getId());
			System.out.println("拥有者：" + qm.getOwner());
			System.out.println("题干：" + qm.getStem());
			System.out.println("补充：" + qm.getAddition());
			System.out.println("已解决：" + (qm.getSolved() ? "是" : "否"));
			System.out.println("发布时间：" + qm.getTime());
			System.out.println("赞：" + qm.getGood() + "次");
		}
		System.out.println();
	}

	private void handleResponseQuestionInfo(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.QuestionInformationResponse questionInformationResponse =
				  recvMessage.getQuestionInformationResponse();
		ServerResponseMessage.QuestionMessage qm = questionInformationResponse.getQuestionMessage();

		if (!questionInformationResponse.getExist()) {
			System.out.println("房间不存在");
		} else {
			System.out.println("问题号：" + qm.getId());
			System.out.println("拥有者：" + qm.getOwner());
			System.out.println("题干：" + qm.getStem());
			System.out.println("补充：" + qm.getAddition());
			System.out.println("已解决：" + (qm.getSolved() ? "是" : "否"));
			System.out.println("发布时间：" + qm.getTime());
			System.out.println("赞：" + qm.getGood() + "次");
		}
		System.out.println();
	}

	public void handleResponseQuestionList(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.GetQuestionListResponse questionListResponse =
				  recvMessage.getGetQuestionListResponse();
		List<ServerResponseMessage.QuestionListMessage> questionList =
				  questionListResponse.getQuestionListMessageList();
		if(null==questionList) {
			System.out.println("无消息返回");
			return;
		}
		for(ServerResponseMessage.QuestionListMessage qlm : questionList) {
			long questionID = qlm.getQuestionID();
			String owner = qlm.getOwner();
			String questionDescription = qlm.getQuestionDescription();
			int good = qlm.getGood();
			int userNum = qlm.getUserNum();
			String time = qlm.getTime();
			System.out.println("问题号:"+questionID);
			System.out.println("创建者:"+owner);
			System.out.println("创建时间:"+time);
			System.out.println("问题描述:"+questionDescription);
			System.out.println("用户数量:"+userNum);
			System.out.println();
		}
	}

	private void handleResponseCreateQuestion(ServerResponseMessage.Message recvMessage) {
		if(recvMessage.getCreateQuestionResponse().getSuccess()) {
			System.out.println("成功创建问题");
			ServerResponseMessage.QuestionMessage qm =
					  recvMessage.getCreateQuestionResponse().getQuestionMessage();

			System.out.println("问题号：" + qm.getId());
			System.out.println("拥有者：" + qm.getOwner());
			System.out.println("题干：" + qm.getStem());
			System.out.println("补充：" + qm.getAddition());
			System.out.println("已解决：" + (qm.getSolved() ? "是" : "否"));
			System.out.println("发布时间：" + qm.getTime());
			System.out.println("赞：" + qm.getGood() + "次");

		} else {
			System.out.println("点数不足");
		}
		System.out.println();
	}

	public void handleFileResponse (ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.FileResponse fileResponse =
				  recvMessage.getFileResponse();

		if(fileResponse.getSuccess()) {
			Set<Map.Entry<String, String>> file_sigs = fileResponse.getSignMap().entrySet();
			switch (fileResponse.getSignType()) {
				case UPLOAD:
					int i=0;
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
					for (Map.Entry<String, String> entry : file_sigs) {
						try {
							File f = new File(PICTPATH);
							if(!f.exists()) {
								f.mkdir();
							}
							f = new File(PICTPATH + entry.getKey());
							if(f.exists()) {
								break;
							} else {
								f.createNewFile();
							}
							Client.fileOP.changeSign(entry.getValue());
							Client.fileOP.getFileLocal(new GetFileLocalRequest(
									  Client.fileOP.getBucktName(),
									  "/" + entry.getKey(),
									  PICTPATH + entry.getKey()
							));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
				case UNRECOGNIZED:
					break;
				default:
			}
		}
	}

	private void handleUpdateMessage(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.UpdateMessage.UserEnter userEnter =
				  recvMessage.getUpdateMessage().getUserEnter();
		String user = userEnter.getUsername();
		long questionID = userEnter.getQuestionID();
		System.out.println(user+"进入了问题"+questionID);
		System.out.println();
	}

	private void handleResponseSolvedQuestion(ServerResponseMessage.Message recvMessage) {
		ServerResponseMessage.SolvedQuestionResponse solvedQuestionResponse =
				  recvMessage.getSolvedQuestionResponse();
		boolean success = solvedQuestionResponse.getSuccess();
		long questionID = solvedQuestionResponse.getQuestionID();
		if(success) {
			System.out.println("成功将问题"+questionID+"标志为解决状态");
		} else {
			if(questionID==0) {
				System.out.println("问题不存在");
			} else {
				System.out.println("权限不足，无法将问题"+questionID+"标志为解决状态");
			}
		}
		System.out.println();
	}

	@Native
	private Client client;
	private String username = "";

	public static final String MAINPATH=bin.test.class.getResource("").getPath()
			  .substring(0, bin.test.class.getResource("").getPath().length()-4);
	public static final String PICTPATH=MAINPATH+"pictures/";
	public static final String FILEPATH=MAINPATH+"files/";

}
