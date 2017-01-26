package com;

import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ServerItem {
	private Socket client;
	private String username;
	private boolean launched;
	private ClientSendMessage.Message message;
	private DatabaseConnection dbconn;
	private String sql;
	Cos cos;

	public ServerItem(Socket client, ClientSendMessage.Message message, DatabaseConnection dbconn) {
		this.client = client;
		this.message = message;
		this.dbconn = dbconn;
		this.cos = new Cos();
	}
	public boolean isLaunched(){return launched;}

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
					if(message.hasLauchRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.LAUNCH_RESPONSE)
								  .setLauchResponse(handleLaunch(message.getLauchRequest()))
								  .build();
					} else
						return null;
				case LOGOUT_MESSAGE:	//登出消息
					handleLogout();
					return null;
				case SEND_CONTENT:	//发送对话消息
					if(message.hasSendContent()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.SEND_CONTENT)
								  .setUsername(username)
								  .setSendContent(handleSendContent(message.getSendContent()))
								  .build();
					} else
						return null;
				case ANNOUNCEMENT_MESSAGE:	//发布公告
				case GOOD_USER_REQUEST:	//赞用户
					if(message.hasGoodUserRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.GOOD_USER_RESPONSE)
								  .setUsername(username)
								  .setGoodUserResponse(handleGoodUserMessage(message.getGoodUserRequest()))
								  .build();
					}
				case GOOD_QUESTION_REQUEST:	//赞问题
					if(message.hasGetQuestionListRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.GOOD_QUESTION_RESPONSE)
								  .setUsername(username)
								  .setGoodQuestionResponse(handleGoodQuestionMessage(message.getGoodQuestionRequest()))
								  .build();
					}
				case QUESTION_INFORMATION_REQUEST:	//请求问题信息
					if(message.hasQuestionInformationRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.QUESTION_INFORMATION_RESPONSE)
								  .setUsername(username)
								  .setQuestionInformationResponse(handleQuestionInformationRequest(message.getQuestionInformationRequest()))
								  .build();
					}
				case USER_INFORMATION_REQUEST:	//请求用户信息
					if(message.hasUserInformationRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.USER_INFORMATION_RESPONSE)
								  .setUsername(username)
								  .setUserInformationResponse(handleUserInformationRequest(message.getUserInformationRequest()))
								  .build();
					} else
						return null;
				case GET_QUESTION_LIST_REQUEST:	//获取问题列表
				case CREATE_QUESTION_REQUEST:	//新建问题
					if(message.hasCreateQuestionRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.CREATE_QUESTION_RESPONSE)
								  .setUsername(username)
								  .setCreateQuestionResponse(handleCreateQuestion(message.getCreateQuestionRequest()))
								  .build();
					} else
						return null;
				case QUESTION_ENTER_REQUEST:	//进入房间
					if(message.hasQuestionEnterRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.QUESTION_ENTER_RESPONSE)
								  .setUsername(username)
								  .setQuestionEnterResponse(handleQuestionEnterRequest(message.getQuestionEnterRequest()))
								  .build();
					} else
						return null;
				case ABANDON_QUESTION_REQUEST:	//删除问题
					if(message.hasAbandonQuestionRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.ABANDON_QUESTION_RESPONSE)
								  .setUsername(username)
								  .setAbandonQuestionResponse(handleAbandonQuestion(message.getAbandonQuestionRequest()))
								  .build();
					} else
						return null;
				case SEARCH_INFORMATION_REQUEST:	//搜索信息
				case GET_COS_SIGN_REQUEST:	//获取签名请求
					if(message.hasGetCosSignRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.GET_COS_SIGN_RESPONSE)
								  .setUsername(username)
								  .setGetCosSignResponse(handleGetCosCredRequest(message.getGetCosSignRequest()))
								  .build();
					}

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
		ServerResponseMessage.UserMessage userMessage = null;
		//@@需要密码加密//

		String key = launchRequest.getPassword();
		String realkey = null;

		//获取密钥
		sql = "Select userkey FROM user WHERE username='?';".replace("?", username);
		java.sql.PreparedStatement pstmt = dbconn.connection.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			realkey = rs.getString("userkey");
		}
		rs.close();
		pstmt.close();

		//执行操作
		if (realkey == null) {
			responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
					  .setStatus(false)
					  .setInformation("帐号不存在").build();
			this.launched = false;
		} else {
			//比较密码
			if (key.equals(realkey)) {
				sql = "SELECT username FROM online_user WHERE username='?'".replace("?", username);
				pstmt = dbconn.connection.prepareStatement(sql);
				rs = pstmt.executeQuery();
				if(rs.next()) {

				}
				rs.close();
				pstmt.close();

				sql = "INSERT INTO online_user(username) VALUES('?');".replace("?", username);
				pstmt = dbconn.connection.prepareStatement(sql);
				pstmt.execute();
				pstmt.close();

				sql = "UPDATE user SET last_launch_time=now() WHERE username='?';".replace("?", username);
				pstmt = dbconn.connection.prepareStatement(sql);
				pstmt.execute();
				pstmt.close();
				userMessage = handleUserInformationRequest(
						  ClientSendMessage.UserInformationRequest.newBuilder()
						  .setUsername(username).build()
				).getUserMessage();
				responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
						  .setStatus(true)
						  .setInformation("成功登录")
						  .setUserMessage(userMessage)
						  .build();
				this.launched = true;
			} else {
				responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
						  .setStatus(false)
						  .setInformation("帐号或密码错误").build();
				this.launched = false;
			}
		}
		return responseLaunch;
	}

	private void
	handleLogout()
			  throws SQLException {
		PreparedStatement pstmt = dbconn.connection.prepareStatement(
				  "DELETE FROM online_user WHERE username=?;"
		);
		pstmt.setString(1, username);
		pstmt.execute();
		pstmt.close();
	}

	private ServerResponseMessage.SendContent
	handleSendContent(ClientSendMessage.SendContent sendMessage)
			  throws SQLException {
		ServerResponseMessage.SendContent responseSend = null;
		ServerResponseMessage.SendContent.Builder sendBuider = ServerResponseMessage.SendContent.newBuilder();
		//解析数据
		long questionID = sendMessage.getQuestionID();
		String time = sendMessage.getTime();
		String date = sendMessage.getDate();
		String record = sendMessage.getContent();


		//在数据库中记录
		PreparedStatement pstmt = dbconn.connection.prepareStatement(
				  "INSERT INTO record (questionID, date, time, username, strRecord) " +
							 "VALUES (?,SELECT str_to_date （'?','%Y-%m-%d'）,SELECT str_to_date （'?','%H:%i:%s'）,'?','?');"
		);
		pstmt.setLong(1, questionID);
		pstmt.setString(2, date);
		pstmt.setString(3, time);
		pstmt.setString(4, username);
		pstmt.setString(5, record);
		pstmt.execute();
		pstmt.close();
		//返回服务器回复
		sendBuider.setQuestionID(questionID);
		sendBuider.setTime(time);
		sendBuider.setDate(date);
		return responseSend;
	}

	private ServerResponseMessage.QuestionInformationResponse
	handleQuestionInformationRequest(ClientSendMessage.QuestionInformationRequest request)
		     throws SQLException {
		ServerResponseMessage.QuestionInformationResponse response = null;
		ServerResponseMessage.QuestionInformationResponse.Builder builder =
				  ServerResponseMessage.QuestionInformationResponse.newBuilder();

		ServerResponseMessage.QuestionMessage.Builder questionMessageBuider =
				  ServerResponseMessage.QuestionMessage.newBuilder();
		Long questionID = request.getQuestionID();

		//获得问题基本信息
		sql = "SELECT * FROM question WHERE id = ?".replace("?", questionID.toString());
		PreparedStatement pstmt = dbconn.connection.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		String owner,stem,addition,time,date,user,contentMessage;
		boolean solved;
		int good;
		if(rs.next()) {
			owner = rs.getString("owner");
			stem = rs.getString("stem");
			addition = rs.getString("addition");
			time = rs.getString("time");
			date = rs.getString("date");
			solved = rs.getBoolean("solved");
			good = rs.getInt("good");
			questionMessageBuider
					  .setOwner(owner)
					  .setStem(stem)
					  .setAddition(addition)
					  .setTime(time)
					  .setDate(date)
					  .setSolved(solved)
					  .setGood(good);
		} else {
			return null;
		}
		pstmt.close();
		rs.close();

		//获得问题记录
		sql = "SELECT * FROM question_id=?".replace("?", questionID.toString());
		pstmt = dbconn.connection.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			contentMessage = rs.getString("record");
			user = rs.getString("username");
			time =  rs.getString("time");
			date = rs.getString("date");
			questionMessageBuider.addRecord(
					  ServerResponseMessage.Record.newBuilder()
					  .setTime(time)
					  .setDate(date)
					  .setContentMessage(contentMessage)
					  .setUser(user)
			);
		}
		rs.close();
		pstmt.close();

		response = builder.setQuestionMessage(questionMessageBuider).build();

		return response;
	}

	private ServerResponseMessage.QuestionEnterResponse
	handleQuestionEnterRequest(ClientSendMessage.QuestionEnterRequest request)
				throws SQLException{
		ServerResponseMessage.QuestionEnterResponse response = null;
		Long questionID = request.getQuestionID();
		ClientSendMessage.QuestionInformationRequest informationRequest =
				  ClientSendMessage.QuestionInformationRequest.newBuilder()
				  .setQuestionID(questionID).build();
		ServerResponseMessage.QuestionInformationResponse questionInformationResponse =
				  handleQuestionInformationRequest(informationRequest);
		if(questionInformationResponse == null) {
			response = ServerResponseMessage.QuestionEnterResponse.newBuilder()
					  .setAllow(false).build();
		} else {
			response = ServerResponseMessage.QuestionEnterResponse.newBuilder()
					  .setQuestionMessage(questionInformationResponse.getQuestionMessage())
					  .setAllow(true)
					  .build();
		}
		return response;
	}

	private ServerResponseMessage.GoodQuestionResponse
	handleGoodQuestionMessage(ClientSendMessage.GoodQuestionRequest goodQuestionRequest)
		     throws SQLException {
		ServerResponseMessage.GoodQuestionResponse goodQuestionResponse = null;
		Long questionID = goodQuestionRequest.getQuestionID();
		Integer good=0;
		PreparedStatement pstmt = null;
		sql = "Select good FROM question WHERE questionID=?;".replace("?", questionID.toString());
		pstmt = dbconn.connection.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		pstmt.close();
		if(rs.next()) {
			good = rs.getInt(1)+1;
			sql = "UPDATE question SET good=?".replace("?", good.toString())
					  +"WHERE questionID=?;".replace("?", questionID.toString());
			pstmt = dbconn.connection.prepareStatement(sql);
			pstmt.execute();
			goodQuestionResponse = ServerResponseMessage.GoodQuestionResponse.newBuilder()
					  .setSuccess(true).build();
		} else {
			goodQuestionResponse = ServerResponseMessage.GoodQuestionResponse.newBuilder()
					  .setSuccess(false).build();
		}

		return goodQuestionResponse;
	}

	private ServerResponseMessage.GoodUserResponse
	handleGoodUserMessage(ClientSendMessage.GoodUserRequest goodUserRequest)
			  throws SQLException {
		ServerResponseMessage.GoodUserResponse goodUserResponse = null;
		String user = goodUserRequest.getUser();
		Integer good=0;
		PreparedStatement pstmt = null;
		sql = "Select good FROM user WHERE username='?';".replace("?", user);
		pstmt = dbconn.connection.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			good = rs.getInt("praise_num")+1;
			sql = "UPDATE user SET good=?".replace("?", good.toString())
					  +"WHERE username=?;".replace("?", user);
			pstmt = dbconn.connection.prepareStatement(sql);
			pstmt.execute();
			goodUserResponse = ServerResponseMessage.GoodUserResponse.newBuilder()
					  .setSuccess(true).build();
		} else {
			goodUserResponse = ServerResponseMessage.GoodUserResponse.newBuilder()
					  .setSuccess(false).build();
		}

		return goodUserResponse;
	}

	private ServerResponseMessage.CreateQuestionResponse
	handleCreateQuestion(ClientSendMessage.CreateQuestionRequest createQuestionRequest)
				throws SQLException {
		ServerResponseMessage.CreateQuestionResponse createQuestionResponse = null;

		//检查积分是否足够
		int bonus=0;
		int question_num = 0;
		PreparedStatement pstmt = dbconn.connection.prepareStatement(
				  "SELECT bonus, question_num FROM user WHERE username = '?';"
		);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			bonus = rs.getInt(1);
			question_num = rs.getInt(2);
		}
		rs.close();
		pstmt.close();

		if(bonus<3) {
			createQuestionResponse = ServerResponseMessage.CreateQuestionResponse.newBuilder().setSuccess(false).build();
		}
		else{

			StringBuffer record = new StringBuffer("");
			String stem = createQuestionRequest.getStem();
			List<ClientSendMessage.ContentMessage> contentMessageList = createQuestionRequest.getAdditonList();

			//循环处理消息内容
			for (ClientSendMessage.ContentMessage element : contentMessageList) {
				record.append(element.getContent());
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
								 "VALUES ('?',?,'?','?',SELECT str_to_date （'?','%Y-%m-%d'）,SELECT str_to_date （'?','%H:%i:%s'）,'?');"
			);
			pstmt.setString(1, username);
			pstmt.setLong(2, questionID);
			pstmt.setString(3, stem);
			pstmt.setString(4, record.toString());
			pstmt.setString(5, date);
			pstmt.setString(6, time);
			pstmt.setBoolean(7, true);
			pstmt.execute();
			pstmt.close();
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
			pstmt.close();
			//扣除点数,增加提问数量
			pstmt = dbconn.connection.prepareStatement(
					  "UPDATE user SET bonus=?, question_num=? WHERE username = '?';"
			);
			pstmt.setInt(1, bonus - 3);
			pstmt.setInt(2, question_num+1);
			pstmt.setString(3, username);
			pstmt.execute();
			pstmt.close();
			createQuestionResponse = ServerResponseMessage.CreateQuestionResponse.newBuilder().setSuccess(true).build();
		}
		return createQuestionResponse;
	}

	private ServerResponseMessage.AbandonQuestionResponse
	handleAbandonQuestion(ClientSendMessage.AbandonQuestionRequest abandonQuestionRequest)
			  throws SQLException {
		ServerResponseMessage.AbandonQuestionResponse abandonQuestionResponse = null;
		boolean ok;

		long questionID = abandonQuestionRequest.getQuestionID();
		String owner = null;
		PreparedStatement pstmt = dbconn.connection.prepareStatement(
				  "SELECT owner FROM user WHERE id = ?;"
		);
		pstmt.setLong(1, questionID);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			owner=rs.getString("id");
		} else
			return null;
		rs.close();
		pstmt.close();

		ok = owner.equals(username);
		if(ok) {
			//删除问题项
			pstmt = dbconn.connection.prepareStatement(
					  "DELETE FROM question WHERE id = ?;"
			);
			pstmt.setLong(1, questionID);
			pstmt.execute();
			pstmt.close();
			//删除问题记录
			pstmt = dbconn.connection.prepareStatement(
					  "DROP TABLE question_id=?;"
			);
			pstmt.setLong(1, questionID);
			pstmt.execute();
			pstmt.close();
		} else
			ok = false;

		abandonQuestionResponse = ServerResponseMessage.AbandonQuestionResponse
				  .newBuilder().setSuccess(ok).build();

		return abandonQuestionResponse;
	}

	private ServerResponseMessage.UserInformationResponse
	handleUserInformationRequest(ClientSendMessage.UserInformationRequest userInformationRequest)
				throws SQLException {
		ServerResponseMessage.UserInformationResponse userInformationResponse = null;

		String username = userInformationRequest.getUsername();
		int good;
		int questionNum;
		int solvedQuesitonNum;
		int bonus;
		String signature;
		String mail_address;

		sql = ("SELECT praise_num, question_num, solved_question_num, bonus, signature, mail_address" +
				  " FROM user WHERE username = '?'").replace("?", username);
		PreparedStatement pstmt = dbconn.connection.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			good = rs.getInt("praise_num");
			questionNum = rs.getInt("question_num");
			solvedQuesitonNum = rs.getInt("solved_quesiton_num");
			bonus = rs.getInt("bonus");
			signature = rs.getString("signature");
			mail_address = rs.getString("mail_address");
		} else {
			return null;
		}
		rs.close();
		pstmt.close();

		userInformationResponse = ServerResponseMessage.UserInformationResponse.newBuilder()
				  .setUserMessage(
				  		  ServerResponseMessage.UserMessage.newBuilder()
							 .setGood(good).setQuestionNum(questionNum)
							 .setSolvedQuestionNum(solvedQuesitonNum).setBonus(bonus)
							 .setSignature(signature).setMailAddress(mail_address)
				  ).build();

		return userInformationResponse;
	}

	private ServerResponseMessage.GetCosSignResponse
	handleGetCosCredRequest(ClientSendMessage.GetCosSignRequest getCosCredRequest)
				throws SQLException {
		ServerResponseMessage.GetCosSignResponse getCosCredResponse = null;

		sql = "SELECT user FROM online_user WHERE username=?".replace("?", username);
		PreparedStatement pstmt = dbconn.connection.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			getCosCredResponse = ServerResponseMessage.GetCosSignResponse.newBuilder()
					  .setSuccess(true).setSign(cos.getSign("/"+username+"/"+getCosCredRequest.getFilename()))
					  .build();
		} else {
			getCosCredResponse = ServerResponseMessage.GetCosSignResponse.newBuilder()
					  .setSuccess(false).build();
		}
		return getCosCredResponse;
	}
}