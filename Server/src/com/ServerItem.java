package com;

import com.google.protobuf.ProtocolStringList;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ServerItem {
	private SocketChannel client;
	private String username = null;
	private DatabaseConnection dbconn;
	private String sql;
	private Statement stmt;
	Cos cos;

	public ServerItem(SocketChannel socketChannel, DatabaseConnection dbconn) {
		this.client = socketChannel;
		this.dbconn = dbconn;
		this.cos = new Cos();
		try {
			stmt = dbconn.connection.createStatement();
		} catch (SQLException e) {
			NIOServer.logger.write(e.getMessage());
			e.printStackTrace();
		}
	}
	public boolean isLaunched(){
		return NIOServer.user_socket_list.get(username).equals(client);
	}

	public String getUsername() {return this.username;}

	public ServerResponseMessage.Message
	handleMessage(ClientSendMessage.Message message) {
		//获取消息种类
		if(message==null)
			return ServerResponseMessage.Message.newBuilder().build();
		ClientSendMessage.MSG msgType = message.getMsgType();
		//获取用户名
		this.username = message.getUsername();
		if(username.equals("")) {
			return ServerResponseMessage.Message.newBuilder().build();
		}
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
				case REGISTER_REQUEST: //注册
					if(message.hasRegisterRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.REGISTER_RESPONSE)
								  .setRegisterResponse(handleRegisterRequest(message.getRegisterRequest()))
								  .build();
					}
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
					if(message.hasGoodQuestionRequest()) {
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
					if(message.hasGetQuestionListRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.GET_QUESTION_LIST_RESPONSE)
								  .setUsername(username)
								  .setGetQuestionListResponse(
								  		  handleGetQuestionListRequest(message.getGetQuestionListRequest())
								  ).build();
					} else {
						return null;
					}
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
					if(message.hasSearchInformationRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.SEARCH_INFORMATION_RESPONSE)
								  .setUsername(username)
								  .setSearchInformationResponse(
								  		  handleSearchInformationRequest(message.getSearchInformationRequest())
								  ).build();
					}
					break;
				case GET_COS_SIGN_REQUEST:	//获取签名请求
					if(message.hasGetCosSignRequest()) {
						try {
							return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.GET_COS_SIGN_RESPONSE)
								  .setUsername(username)
								  .setGetCosSignResponse(handleGetCosCredRequest(message.getGetCosSignRequest()))
								  .build();
						} catch (Exception e) {
							NIOServer.logger.write(e.getMessage());
							e.printStackTrace();
							return ServerResponseMessage.Message.newBuilder().build();
						}
					}
					break;
				case SOLVED_QUESTION_REQUEST:
					if(message.hasSolvedQuestionRequest()) {
						return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.SOLVED_QUESTION_RESPONSE)
								  .setUsername(username)
								  .setSolvedQuestionResponse(
								  		  handleSolvedQuestionRequest(message.getSolvedQuestionRequest())
								  ).build();
					}
					break;
				default:
					return ServerResponseMessage.Message.newBuilder()
							  .setMsgType(ServerResponseMessage.MSG.UNRECOGNIZED).build();
			}
		} catch (Exception e) {
			NIOServer.logger.write(e.getMessage());
			e.printStackTrace();
			return ServerResponseMessage.Message.newBuilder()
					  .setMsgType(ServerResponseMessage.MSG.UNRECOGNIZED).build();
		}

		return ServerResponseMessage.Message.newBuilder()
				  .setMsgType(ServerResponseMessage.MSG.UNRECOGNIZED).build();
	}

	private ServerResponseMessage.LaunchResponse
	handleLaunch(ClientSendMessage.LaunchRequest launchRequest)
			  throws SQLException{
		ServerResponseMessage.LaunchResponse responseLaunch = null;
		ServerResponseMessage.UserMessage userMessage = null;
		String key = launchRequest.getPassword();
		String realkey = null;
		boolean inOnlineUser = false;

		//获取密钥
		sql = "SELECT userkey FROM user WHERE username='?';".replace("?", username);
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			realkey = rs.getString("userkey");
		}
		rs.close();
		//执行操作
		if (realkey == null) {
			responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
					  .setStatus(false)
					  .setInformation("帐号不存在").build();
		} else {
			//比较密码
			if (key.equals(realkey)) {
				sql = "SELECT username FROM online_user WHERE username='?'".replace("?", username);

				rs = stmt.executeQuery(sql);

				//判断用户是否处于登录状态
				if (rs.next()) {
					inOnlineUser = true;
					NIOServer.user_socket_list.replace(username, client);
				}
				rs.close();

				//在已登录用户表中记录
				if(!inOnlineUser) {
					sql = "INSERT INTO online_user(username) VALUES('?');".replace("?", username);
					stmt.execute(sql);
				}

				//更新最后登录时间
				sql = "UPDATE user SET last_launch_time=now() WHERE username='?';".replace("?", username);
				stmt.execute(sql);
				userMessage = handleUserInformationRequest(
						  ClientSendMessage.UserInformationRequest.newBuilder()
									 .setUsername(username).build()
				).getUserMessage();
				responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
						  .setStatus(true)
						  .setInformation("成功登录")
						  .setUserMessage(userMessage)
						  .build();
				NIOServer.user_socket_list.put(username, client);
				return responseLaunch;
			} else {
				responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
						  .setStatus(false)
						  .setInformation("帐号或密码错误").build();
				return responseLaunch;
			}
		}

		return ServerResponseMessage.LaunchResponse.newBuilder()
				  .setStatus(false)
				  .setInformation("登录失败")
				  .build();
	}

	private void
	handleLogout()
			  throws SQLException {
		sql = "DELETE FROM online_user WHERE username='?';".replace("?",username);
		stmt.execute(sql);
		NIOServer.user_socket_list.remove(username);
	}

	private ServerResponseMessage.RegisterResponse
	handleRegisterRequest(ClientSendMessage.RegisterRequest request)
				throws SQLException{
		ServerResponseMessage.RegisterResponse response = null;
		String username = request.getUsername();
		String password = request.getPassword();
		String mail_address = request.getMailAddress();
		String signature = request.getSignature();

		sql = "SELECT * FROM user WHERE username = "+username;
		ResultSet rs = stmt.executeQuery(sql);
		//用户已存在
		if(!rs.next()) {
			response = ServerResponseMessage.RegisterResponse.newBuilder()
					  .setSuccess(false)
					  .setInformation("用户已存在")
					  .build();
			return response;
		}
		rs.close();
		//邮箱已被注册

		//注册成功
		sql = sql = "INSERT INTO user (username, userkey, signature, mail_address)"
				  + "VALUES('" + username + "','" + password + "','" + signature + "','" + mail_address + "');";
		stmt.execute(sql);

		response = ServerResponseMessage.RegisterResponse.newBuilder()
				  .setSuccess(true)
				  .setInformation("注册成功")
				  .build();
		return response;
	}

	private ServerResponseMessage.SendContent
	handleSendContent(ClientSendMessage.SendContent sendMessage)
			  throws SQLException {
		ServerResponseMessage.SendContent responseSend = null;
		ServerResponseMessage.SendContent.Builder sendBuider = ServerResponseMessage.SendContent.newBuilder();
		//解析数据
		long questionID = sendMessage.getQuestionID();
		String time = sendMessage.getTime();
		String record = sendMessage.getContent();

		sql = "SELECT id FROM question WHERE id="+questionID+";";
		ResultSet rs = stmt.executeQuery(sql);
		if(!rs.next()) {
			return ServerResponseMessage.SendContent.newBuilder().setSuccess(false).build();
		}

		sql = "UPDATE question SET last_send_time=now() WHERE id = "+questionID;
		stmt.execute(sql);

		//在数据库中记录
		sql = "INSERT INTO question_id"+questionID+" (record, time, username) "
				  + "VALUES ('"+record+"',"+"now()"+",'"+username+"');";
		stmt.execute(sql);

		//返回服务器回复
		sendBuider.setQuestionID(questionID);
		sendBuider.setTime(time);
		sendBuider.setContent(record);
		sendBuider.setUser(username);
		Map<String , String> picturesMap = sendBuider.getPicturesMap();

		//对每一图片附加腾讯云cos下载签名处理
		for(String pic : sendMessage.getPicturesList()) {
			sendBuider.putPictures(pic,cos.getDownloadSign(pic, Cos.TYPE.PICTURE));
		}
		sendBuider.setSuccess(true);
		sendBuider.setIsmyself(false);
		responseSend  = sendBuider.build();
		ArrayList<SocketChannel> clients = NIOServer.question_socket_list.get(questionID+"");
		//给每一个处于房间中的用户发送信息（自己除外）
		try {
			for (SocketChannel sc : clients) {
				if(!sc.equals(client) && !sc.socket().isClosed()) {
					byte[] sendBytes =
							  ServerResponseMessage.Message.newBuilder()
										 .setMsgType(ServerResponseMessage.MSG.SEND_CONTENT)
										 .setSendContent(responseSend)
										 .build().toByteArray();
					ByteBuffer sendBB = ByteBuffer.allocate(Integer.BYTES+sendBytes.length);
					sendBB.putInt(sendBytes.length).put(sendBytes);
					sendBB.flip();
					while (sendBB.hasRemaining())
						sc.write(sendBB);
				}
			}
		} catch (IOException e) {
			sendBuider.setSuccess(false);
			e.printStackTrace();
			NIOServer.logger.write(e.getMessage());
		} finally {
			//对于用户本身返回上传签名
			sendBuider.clearPictures();
			for(String pic : sendMessage.getPicturesList()) {
				sendBuider.putPictures(pic, cos.getUploadSign(pic, Cos.TYPE.PICTURE));
			}
			sendBuider.setIsmyself(true);
			responseSend = sendBuider.build();
			return responseSend;
		}
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
		sql = "SELECT * FROM question WHERE id = ?;".replace("?", questionID.toString());
		ResultSet rs = stmt.executeQuery(sql);
		String owner,stem,addition,time,user,contentMessage;
		boolean solved;
		int good;
		if(rs.next()) {
			owner = rs.getString("owner");
			stem = rs.getString("stem");
			addition = rs.getString("addition");
			time = rs.getString("create_time");
			solved = rs.getBoolean("solved");
			good = rs.getInt("praise_num");
			questionMessageBuider
					  .setId(questionID)
					  .setOwner(owner)
					  .setStem(stem)
					  .setAddition(addition)
					  .setTime(time)
					  .setSolved(solved)
					  .setGood(good);
		} else {
			return ServerResponseMessage.QuestionInformationResponse.newBuilder()
					  .setExist(false).build();
		}
		rs.close();

		//获得问题记录
		sql = "SELECT * FROM question_id?".replace("?", questionID.toString());
		rs = stmt.executeQuery(sql);

		while (rs.next()) {
			contentMessage = rs.getString("record");
			user = rs.getString("username");
			time =  rs.getString("time");
			questionMessageBuider.addRecord(
					  ServerResponseMessage.Record.newBuilder()
					  .setTime(time)
					  .setContentMessage(contentMessage)
					  .setUser(user)
			);
		}
		rs.close();

		response = builder.setQuestionMessage(questionMessageBuider).setExist(true).build();

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

		//获得房间信息
		ServerResponseMessage.QuestionInformationResponse questionInformationResponse =
				  handleQuestionInformationRequest(informationRequest);

		//若房间不存在则返回失败消息
		if(!questionInformationResponse.getExist()) {
			response = ServerResponseMessage.QuestionEnterResponse.newBuilder()
					  .setAllow(false).build();
		} else {
			response = ServerResponseMessage.QuestionEnterResponse.newBuilder()
					  .setQuestionMessage(questionInformationResponse.getQuestionMessage())
					  .setAllow(true)
					  .build();
			//将用户socket添加进问题socket列表中
			ArrayList<SocketChannel> socketChannels = NIOServer.question_socket_list.get(questionID.toString());
			if(null==socketChannels) {
				socketChannels = new ArrayList<>();
				socketChannels.add(client);
				NIOServer.question_socket_list.put(questionID.toString(), socketChannels);
			} else {
				socketChannels.add(client);
				NIOServer.question_socket_list.replace(questionID.toString(), socketChannels);
			}

			//向用户问题表中添加问题
			ArrayList<String> questions = NIOServer.socket_question_list.get(client);
			if(null == questions) {
				questions = new ArrayList<>();
				questions.add(questionID.toString());
				NIOServer.socket_question_list.replace(client, questions);
			}

			for(SocketChannel sc : socketChannels) {
				try {
					//若客户端链接中断
					if(sc.socket().isClosed())
						break;

					ServerResponseMessage.Message sendMessage =
							  ServerResponseMessage.Message.newBuilder()
										 .setMsgType(ServerResponseMessage.MSG.UPDATE_MESSAGE)
										 .setUpdateMessage(
													ServerResponseMessage.UpdateMessage.newBuilder()
															  .setUserEnter(
																		 ServerResponseMessage.UpdateMessage.UserEnter.newBuilder()
																					.setQuestionID(questionID)
																					.setUsername(username).build()
															  ).build()
										 ).build();
					byte[] sendByte = sendMessage.toByteArray();
					ByteBuffer sendBB = ByteBuffer.allocate(sendByte.length+Integer.BYTES);
					sendBB.putInt(sendByte.length).put(sendByte);
					sendBB.flip();
					while (sendBB.hasRemaining())
						sc.write(sendBB);
				} catch (IOException e) {
					e.printStackTrace();
					NIOServer.logger.write(e.getMessage());
				}
			}
		}
		return response;
	}

	private ServerResponseMessage.GoodQuestionResponse
	handleGoodQuestionMessage(ClientSendMessage.GoodQuestionRequest goodQuestionRequest)
		     throws SQLException {
		ServerResponseMessage.GoodQuestionResponse goodQuestionResponse = null;
		Long questionID = goodQuestionRequest.getQuestionID();
		Integer good=0;

		sql = "Select praise_num FROM question WHERE id=?;".replace("?", questionID.toString());
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			good = rs.getInt(1)+1;
			sql = "UPDATE question SET praise_num=?".replace("?", good.toString())
					  +" WHERE id=?;".replace("?", questionID.toString());
			stmt.execute(sql);
			goodQuestionResponse = ServerResponseMessage.GoodQuestionResponse.newBuilder()
					  .setSuccess(true).build();
		} else {
			goodQuestionResponse = ServerResponseMessage.GoodQuestionResponse.newBuilder()
					  .setSuccess(false).build();
		}
		rs.close();

		return goodQuestionResponse;
	}

	private ServerResponseMessage.GoodUserResponse
	handleGoodUserMessage(ClientSendMessage.GoodUserRequest goodUserRequest)
			  throws SQLException {
		ServerResponseMessage.GoodUserResponse goodUserResponse = null;
		String user = goodUserRequest.getUser();
		Integer good=0;
		sql = "Select praise_num FROM user WHERE username='?';".replace("?", user);
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			good = rs.getInt("praise_num")+1;
			sql = "UPDATE user SET praise_num=?".replace("?", good.toString())
					  +" WHERE username='?';".replace("?", user);
			stmt.execute(sql);
			goodUserResponse = ServerResponseMessage.GoodUserResponse.newBuilder()
					  .setSuccess(true).build();
		} else {
			goodUserResponse = ServerResponseMessage.GoodUserResponse.newBuilder()
					  .setSuccess(false).build();
		}
		rs.close();

		return goodUserResponse;
	}

	private ServerResponseMessage.CreateQuestionResponse
	handleCreateQuestion(ClientSendMessage.CreateQuestionRequest createQuestionRequest)
				throws SQLException {
		ServerResponseMessage.CreateQuestionResponse createQuestionResponse = null;

		//检查积分是否足够
		int bonus=0;
		int question_num = 0;
		sql = "SELECT bonus, question_num FROM user WHERE username = '?';".replace("?", username);
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			bonus = rs.getInt(1);
			question_num = rs.getInt(2);
		}
		rs.close();

		if(bonus<3) {
			createQuestionResponse = ServerResponseMessage.CreateQuestionResponse.newBuilder().setSuccess(false).build();
		}
		else{

			StringBuffer record = new StringBuffer("");
			String stem = createQuestionRequest.getStem();
			String addition = createQuestionRequest.getAddition();

			//在数据库中记录
			String time = createQuestionRequest.getTime();
			sql = "SELECT max(id) FROM question;";
			long questionID = 1;
			rs = stmt.executeQuery(sql);
			if(rs.next())
			{
				questionID = rs.getLong(1)+1;
			} else {
				questionID = 0;
			}
			sql = "INSERT INTO question (owner, id, stem, addition, solved) VALUES" +
					  "('"+username+"','"+questionID+"','"+stem+"','"+addition+"',0);";
			stmt.execute(sql);

			//创建问题记录表
			sql = "CREATE TABLE question_id?(\n".replace("?", questionID+"")+
					  "record VARCHAR(800) NOT NULL,\n" +
					  "username VARCHAR(20) NOT NULL,\n" +
					  "time DATETIME DEFAULT now()\n" +
					  ");";
			stmt.execute(sql);

			//扣除点数,增加提问数量
			sql = "UPDATE user SET bonus="+(bonus-3)+", question_num="+(question_num+1)+" WHERE username = '"+username+"';";
			stmt.execute(sql);

			//插入分词列表
			ProtocolStringList keywords = createQuestionRequest.getKeywordsList();
			Iterator<String> ite =keywords.iterator();
			String keyword = "";
			if(ite.hasNext()) {
				keyword = ite.next();
			}

			if(keyword.equals("")) {
			} else {
				sql = "INSERT INTO words_list1 (word, question) VALUES('"+keyword+"',"+questionID+")";
				stmt.execute(sql);
			}

			while (ite.hasNext()){
				keyword = ite.next();
				sql = "INSERT INTO words_list2 (word, question) VALUES('"+keyword+"',"+questionID+");";
				stmt.execute(sql);
			}

			ServerResponseMessage.QuestionMessage questionMessage =
					  handleQuestionInformationRequest(
								 ClientSendMessage.QuestionInformationRequest.newBuilder()
								 .setQuestionID(questionID).build()
					  ).getQuestionMessage();

			//返回成功消息
			createQuestionResponse = ServerResponseMessage.CreateQuestionResponse.newBuilder()
					  .setSuccess(true)
					  .setQuestionMessage(questionMessage).build();
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
		sql = "SELECT owner FROM user WHERE id = "+questionID+";";
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			owner=rs.getString("id");
		} else
			return null;
		rs.close();

		ok = owner.equals(username);
		if(ok) {
			//删除问题项
			sql = "DELETE FROM question WHERE id = "+questionID+";";
			stmt.execute(sql);
			//删除问题记录
			sql = "DROP TABLE question_id="+questionID+";";
			stmt.execute(sql);
			//删除分词列表中指向问题的项
			sql = "DELETE FROM word_list1 WHERE question="+questionID+";";
			stmt.execute(sql);
			sql = "DELETE FROM word_list2 WHERE question="+questionID+";";
			stmt.execute(sql);
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
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			good = rs.getInt("praise_num");
			questionNum = rs.getInt("question_num");
			solvedQuesitonNum = rs.getInt("solved_question_num");
			bonus = rs.getInt("bonus");
			signature = rs.getString("signature");
			mail_address = rs.getString("mail_address");
		} else {
			return ServerResponseMessage.UserInformationResponse.newBuilder()
					  .setExist(false)
					  .setUserMessage(
								 ServerResponseMessage.UserMessage.newBuilder()
								 .setUsername(username)
					  )
					  .build();
		}
		rs.close();

		userInformationResponse = ServerResponseMessage.UserInformationResponse.newBuilder()
				  .setUserMessage(
				  		  ServerResponseMessage.UserMessage.newBuilder()
							 .setGood(good).setQuestionNum(questionNum)
							 .setSolvedQuestionNum(solvedQuesitonNum).setBonus(bonus)
							 .setSignature(signature).setMailAddress(mail_address)
							 .setUsername(username)
				  ).build();

		return userInformationResponse;
	}

	private ServerResponseMessage.GetCosSignResponse
	handleGetCosCredRequest(ClientSendMessage.GetCosSignRequest getCosCredRequest)
			  throws Exception
	{
		ServerResponseMessage.GetCosSignResponse getCosCredResponse = null;
		String sign = null;
		ProtocolStringList files = getCosCredRequest.getFilenameList();

		ServerResponseMessage.GetCosSignResponse.Builder builder =
				  ServerResponseMessage.GetCosSignResponse.newBuilder()
				  .setSuccess(true);

		if(isLaunched()) {
			switch (getCosCredRequest.getSignType()) {
				case DOWNLOAD:
					for(String filename : files) {
						sign = cos.getDownloadSign(filename, Cos.TYPE.PICTURE);
						builder.putSign(filename, sign);
					}
					break;
				case UPLOAD:
					for(String filename : files) {
						sign = cos.getUploadSign(filename, Cos.TYPE.PICTURE);
						builder.putSign(filename, sign);
					}
					break;
				default:
					throw new Exception("MSG is invalid");
			}
			return builder.build();

		} else {
			getCosCredResponse = ServerResponseMessage.GetCosSignResponse.newBuilder()
					  .setSuccess(false).build();
		}
		return getCosCredResponse;
	}

	private ServerResponseMessage.GetQuestionListResponse
	handleGetQuestionListRequest (ClientSendMessage.GetQuestionListRequest request)
				throws SQLException {
		ServerResponseMessage.GetQuestionListResponse response = null;
		ServerResponseMessage.GetQuestionListResponse.Builder builder =
				  ServerResponseMessage.GetQuestionListResponse.newBuilder();

		int questionNum = request.getQuestionNumber();
		ClientSendMessage.RANKORDER rankorder = request.getRankorder();
		ClientSendMessage.LIST_REFERENCE reference = request.getReference();
		String ref,order;
		switch (rankorder) {
			case ASCENDING:
				order = "ASC";
				break;
			case DESCENDING:
				order = "DESC";
				break;
			default:
				order = "DESC";
		}
		switch (reference) {
			case PRAISE_TIMES:
				ref = "praise_num";
				break;
			case TIME:
				ref = "create_time";
				break;
			default:
				ref = "praise_num";
		}
		sql = "SELECT * FROM question ORDER BY "+ref+" "+order+";";
		ResultSet rs = stmt.executeQuery(sql);
		int i;
		for(i=0;i<questionNum && rs.next();i++) {
			int userNum = 0;
			Iterator<SocketChannel> ite = NIOServer.question_socket_list.get(rs.getLong("id")).iterator();
			while (ite.next()!=null) {
				userNum++;
			}
			builder.addQuestionListMessage(
					  ServerResponseMessage.QuestionListMessage.newBuilder()
					  .setQuestionID(rs.getLong("id"))
					  .setGood(rs.getInt("praise_num"))
					  .setOwner(rs.getString("owner"))
					  .setQuestionDescription(rs.getString("stem"))
					  .setTime(rs.getString("create_time"))
					  .setUserNum(userNum)
			);
		}
		rs.close();

		builder.setNum(i);
		response = builder.build();
		return response;
	}

	private ServerResponseMessage.SearchInformationResponse
	handleSearchInformationRequest (ClientSendMessage.SearchInformationRequest request)
				throws SQLException {
		ServerResponseMessage.SearchInformationResponse response = null;
		ServerResponseMessage.SearchInformationResponse.Builder builder =
				  ServerResponseMessage.SearchInformationResponse.newBuilder();
		Iterator<String> ite  = request.getKeywordsList().iterator();
		String keyword = ite.next();
		ResultSet rs;

		if(!keyword.equals("")) {
			sql = "SELECT * FROM word_list1 WHERE keyword LIKE \""+keyword+"\";";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String dbkeyword = rs.getString("keyword");

			}
			rs.close();
		}

		Set<Long> set1 = new HashSet<>();
		if(ite.hasNext()) {
			sql = "SELECT * FROM word_list2 WHERE keyword='"+keyword+"';";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				set1.add( rs.getLong("question") );
			}
			rs.close();
		}
		while (ite.hasNext()) {
			sql = "SELECT * FROM word_list2 WHERE keyword='"+keyword+"';";
			rs = stmt.executeQuery(sql);
			Set<Long> set2 = new HashSet<>();
			while (rs.next()) {
				set2.add( rs.getLong("question") );
			}
			set1.retainAll(set2);
			rs.close();
		}

		//获得问题消息
		for(Long question : set1) {
			int userNum = NIOServer.question_socket_list.size();
			sql = "SELECT * FROM question WHERE id="+question+";";
			rs = stmt.executeQuery(sql);

			builder.addQuestionListMessage(
					  ServerResponseMessage.QuestionListMessage.newBuilder()
					  .setQuestionID(question)
					  .setGood(rs.getInt("praise_num"))
					  .setOwner(rs.getString("owner"))
					  .setQuestionDescription(rs.getString("stem"))
					  .setTime(rs.getString("create_time"))
					  .setUserNum(userNum)
			);
			rs.close();
		}
		response = builder.build();
		return response;

	}

	private ServerResponseMessage.SolvedQuestionResponse
	handleSolvedQuestionRequest (ClientSendMessage.SolvedQuestionRequest request)
				throws SQLException {
		ServerResponseMessage.SolvedQuestionResponse response = null;

		Long questionID = request.getQuestionID();
		sql = "SELECT owner FROM question WHERE id="+questionID+";";
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			String owner = rs.getString("owner");
			rs.close();
			if(owner.equals(username)) {
				sql = "UPDATE question SET solved=1 WHERE id="+questionID+";";
				stmt.execute(sql);
				return ServerResponseMessage.SolvedQuestionResponse.newBuilder()
						  .setSuccess(true).setQuestionID(questionID).build();
			}
			//权限不足
			else {
				return ServerResponseMessage.SolvedQuestionResponse.newBuilder()
						  .setSuccess(false).setQuestionID(questionID).build();
			}
		}
		//房间不存在
		return ServerResponseMessage.SolvedQuestionResponse.newBuilder()
					  .setSuccess(false).setQuestionID(0).build();
	}
}