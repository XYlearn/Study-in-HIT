package com;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ServerItem {
	private Socket client;
	private String username;
	private ClientSendMessage.MSG msgType;
	private ClientSendMessage.Message message;
	private DatabaseConnection dbconn;

	public ServerItem(Socket client, ClientSendMessage.Message message) {
		this.client = client;
		this.message = message;
		dbconn = new DatabaseConnection();
	}

	public ServerResponseMessage.Message
	handleMessage(ClientSendMessage.Message message) {
		//获取消息种类
		ClientSendMessage.MSG msgType = message.getMsgType();
		//获取用户名
		this.username = message.getUsername();
		//处理消息
		try {
			switch (msgType) {
				case LAUNCH_REQUEST:	//登录消息
					return ServerResponseMessage.Message.newBuilder()
							  .setMsgType(ServerResponseMessage.MSG.LAUNCH_RESPONSE)
							  .setLauchResponse(handleLaunch(message.getLauchRequest()))
							  .build();
				case LOGOUT_MESSAGE:	//登出消息
					handleLogout(message.getLogoutMessage());
					return null;
				case SEND_CONTENT:	//发送对话消息
					return ServerResponseMessage.Message.newBuilder()
							  .setMsgType(ServerResponseMessage.MSG.SEND_CONTENT)
							  .setUsername(username)
							  .setSendContent(handleSendContent(message.getSendContent()))
							  .build();
				case ANNOUNCEMENT_MESSAGE:	//发布公告
				case GOOD_QUESTION_REQUEST:	//赞问题
				case BAD_QUESTION_REQUEST:		//踩问题
				case GOOD_USER_REQUEST:	//赞用户
				case QUESTION_INFORMATION_REQUEST:	//踩用户
				case USER_INFORMATION_REQUEST:	//请求用户信息
				case CREATE_QUESTION_REQUEST:	//新建问题
					return ServerResponseMessage.Message.newBuilder()
							  .setMsgType(ServerResponseMessage.MSG.CREATE_QUESTION_RESPONSE)
							  .setUsername(username)
							  .setCreateQuestionResponse(handleCreateQuestion(message.getCreateQuestionRequest()))
							  .build();
				case ABANDON_QUESTION_REQUEST:	//删除问题
				case SEARCH_INFORMATION_REQUEST:	//搜索信息
				default:
					return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ServerResponseMessage.LaunchResponse
	handleLaunch(ClientSendMessage.LaunchRequest launchRequest)
			  throws SQLException {
		ServerResponseMessage.LaunchResponse responseLaunch = null;
		//@@需要密码加密//

		String key = launchRequest.getPassword();
		String realkey = null;

		//获取密钥
		java.sql.PreparedStatement pstmt = dbconn.connection.prepareStatement(
				  "Select userkey FROM user WHERE username=?");
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			realkey = rs.getString(1);
		}

		//执行操作
		if (realkey == null) {
			responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
					  .setStatus(ServerResponseMessage.LOGIN_STATUS.OFFLINE)
					  .setInformation("帐号不存在").build();
		} else {
			//比较密码
			if (key.equals(realkey)) {
				pstmt = dbconn.connection.prepareStatement("INSERT INTO online_user VALUES(\"?\")");
				pstmt.setString(1, username);
				pstmt.execute();
				responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
						  .setStatus(ServerResponseMessage.LOGIN_STATUS.ONLINE)
						  .setInformation("成功登录").build();
			} else {
				responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
						  .setStatus(ServerResponseMessage.LOGIN_STATUS.OFFLINE)
						  .setInformation("帐号或密码错误").build();
			}
		}
		return responseLaunch;
	}

	private void
	handleLogout(ClientSendMessage.LogoutMessage logoutMessage)
			  throws SQLException {
		PreparedStatement pstmt = dbconn.connection.prepareStatement(
				  "DELETE FROM online_user WHERE username=?"
		);
		pstmt.setString(1, username);
		pstmt.execute();
	}

	private ServerResponseMessage.SendContent
	handleSendContent(ClientSendMessage.SendContent sendMessage)
			  throws SQLException {
		ServerResponseMessage.SendContent responseSend = null;
		ServerResponseMessage.SendContent.Builder sendBuider = ServerResponseMessage.SendContent.newBuilder();
		ServerResponseMessage.ContentMessage.Builder contentBuider = ServerResponseMessage.ContentMessage.newBuilder();
		//解析数据
		long questionID = sendMessage.getQuestionID();
		String time = sendMessage.getTime();
		String date = sendMessage.getDate();
		List<ClientSendMessage.ContentMessage> contentMessageList = sendMessage.getContentMessageList();
		StringBuffer record = new StringBuffer("");

		//循环处理消息内容
		for(ClientSendMessage.ContentMessage element : contentMessageList) {
			ClientSendMessage.CONTENT_TYPE contentType = element.getSendType();
			switch (contentType) {
				//文字
				case TEXT_MESSAGE:
					String textContent = element.getTextMessage().getText();
					record.append(textContent);
					break;

				//图片
				case PICTURE_MESSAGE:
					Cos cos = new Cos();
					ClientSendMessage.PICTURETYPE pictureType = element.getPictureType();
					List<Long> pictureBinaryList = element.getPictureMessage().getPictureBinaryList();

					//获取图片
					String picString = "";
					try {
						FileOutputStream fos = new FileOutputStream("./temp/" + username);
						for (Long b : pictureBinaryList) {
							picString+=b.toString();
						}
						byte[] picBytes = picString.getBytes();
						fos.write(picBytes);
					} catch (IOException e) {
						e.printStackTrace();
					}
					//上传图片
					String[] pictureExtension = new String[3];
					pictureExtension[ClientSendMessage.PICTURETYPE.JPEG_VALUE] = ".JPEG";
					pictureExtension[ClientSendMessage.PICTURETYPE.PNG_VALUE] = ".PNG";
					pictureExtension[ClientSendMessage.PICTURETYPE.GIF_VALUE] = ".GIF";

					String picMD5 = MD5.getMd5(picString);
					String cosFilePath = "/pic/"+picMD5+pictureExtension[pictureType.getNumber()];
					String localFilePath = "./temp/"+username;
					cos.uploadFile(cosFilePath, localFilePath);

					contentBuider.addPictureMessage(
							  ServerResponseMessage.ContentMessage.PictureMessage.newBuilder().setPath(cosFilePath));

					//记录消息
					record.append("%"+cosFilePath+"%");
					//添加回复图片信息
					sendBuider.addContentMessage(contentBuider);
					contentBuider.clearPictureMessage();
					break;
				case VOICE_MESSAGE:
					//@@语音处理未写@@//
			}
		}
		//在数据库中记录
		PreparedStatement pstmt = dbconn.connection.prepareStatement(
				  "INSERT INTO record (questionID, date, time, username, strRecord) " +
							 "VALUES (?,SELECT str_to_date （'?','%Y-%m-%d'）,SELECT str_to_date （'?','%H:%i:%s'）,'?','?')"
		);
		pstmt.setLong(1, questionID);
		pstmt.setString(2, date);
		pstmt.setString(3, time);
		pstmt.setString(4, username);
		pstmt.setString(5, record.toString());
		pstmt.execute();
		//返回服务器回复
		sendBuider.setQuestionID(questionID);
		sendBuider.setTime(time);
		sendBuider.setDate(date);
		return responseSend;
	}

	private ServerResponseMessage.CreateQuestionResponse
	handleCreateQuestion(ClientSendMessage.CreateQuestionRequest createQuestionRequest)
				throws SQLException {
		ServerResponseMessage.CreateQuestionResponse createQuestionResponse = null;

		//检查积分是否足够
		int bonus=0;
		PreparedStatement pstmt = dbconn.connection.prepareStatement(
				  "SELECT bonus FROM user WHERE username = '?'"
		);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			bonus = rs.getInt(1);
		}
		if(bonus<3) {
			createQuestionResponse = ServerResponseMessage.CreateQuestionResponse.newBuilder().setSuccess(false).build();
		}
		else{

			StringBuffer record = new StringBuffer("");
			String stem = createQuestionRequest.getStem();
			List<ClientSendMessage.ContentMessage> contentMessageList = createQuestionRequest.getAdditonList();

			//循环处理消息内容
			for (ClientSendMessage.ContentMessage element : contentMessageList) {
				ClientSendMessage.CONTENT_TYPE contentType = element.getSendType();
				switch (contentType) {
					//文字
					case TEXT_MESSAGE:
						String textContent = element.getTextMessage().getText();
						record.append(textContent);
						break;

					//图片
					case PICTURE_MESSAGE:
						Cos cos = new Cos();
						ClientSendMessage.PICTURETYPE pictureType = element.getPictureType();
						List<Long> pictureBinaryList = element.getPictureMessage().getPictureBinaryList();

						//获取图片
						String picString = "";
						try {
							FileOutputStream fos = new FileOutputStream("./temp/" + username);
							for (Long b : pictureBinaryList) {
								picString += b.toString();
							}
							byte[] picBytes = picString.getBytes();
							fos.write(picBytes);
						} catch (IOException e) {
							e.printStackTrace();
						}
						//上传图片
						String[] pictureExtension = new String[3];
						pictureExtension[ClientSendMessage.PICTURETYPE.JPEG_VALUE] = ".JPEG";
						pictureExtension[ClientSendMessage.PICTURETYPE.PNG_VALUE] = ".PNG";
						pictureExtension[ClientSendMessage.PICTURETYPE.GIF_VALUE] = ".GIF";

						String picMD5 = MD5.getMd5(picString);
						String cosFilePath = "/pic/" + picMD5 + pictureExtension[pictureType.getNumber()];
						String localFilePath = "./temp/" + username;
						cos.uploadFile(cosFilePath, localFilePath);

						//记录消息
						record.append("%" + cosFilePath + "%");
						break;
					case VOICE_MESSAGE:
						//@@语音处理未写@@//
				}
			}

			//在数据库中记录
			String date = createQuestionRequest.getDate();
			String time = createQuestionRequest.getTime();
			long questionID = 1;
			pstmt = dbconn.connection.prepareStatement(
					  "SELECT max(questionID) FROM question"
			);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				questionID = rs.getLong(1)+1;
			}
			pstmt = dbconn.connection.prepareStatement(
					  "INSERT INTO question (owner, id, stem, addition, date, time, solved) " +
								 "VALUES ('?',?,'?','?',SELECT str_to_date （'?','%Y-%m-%d'）,SELECT str_to_date （'?','%H:%i:%s'）,'?')"
			);
			pstmt.setString(1, username);
			pstmt.setLong(2, questionID);
			pstmt.setString(3, stem);
			pstmt.setString(4, record.toString());
			pstmt.setString(5, date);
			pstmt.setString(6, time);
			pstmt.setBoolean(7, true);
			pstmt.execute();
			//创建问题记录表
			pstmt = dbconn.connection.prepareStatement(
					  "CREATE TABLE question_id=?(\n" +
								 "record VARCHAR NOT NULL,\n" +
								 "username VARCHAR(20) NOT NULL,\n" +
								 "date DATE NOT NULL,\n" +
								 "time TIME NOT NULL,\n" +
								 ");"
			);
			pstmt.setLong(1, questionID);
			pstmt.execute();
			//扣除点数
			pstmt = dbconn.connection.prepareStatement(
					  "UPDATE user SET bonus=? WHERE username = '?'"
			);
			pstmt.setInt(1, bonus - 3);
			pstmt.setString(2, username);
			pstmt.execute();
			createQuestionResponse = ServerResponseMessage.CreateQuestionResponse.newBuilder().setSuccess(true).build();
		}
		return createQuestionResponse;
	}
}