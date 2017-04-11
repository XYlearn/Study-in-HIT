package com;

import com.google.protobuf.ProtocolStringList;
import org.apache.mina.core.session.IoSession;
import util.AcquaintanceParser;
import util.SQLStringParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.ServerResponseMessage.GetUserListResponse.USER_LIST_TYPE.ACQUAINTANCE_LIST;
import static com.ServerResponseMessage.GetUserListResponse.USER_LIST_TYPE.USERS_IN_ROOM_LIST;

public class ServerItem {
	private IoSession session;
	private String username = null;
	private String password = null;
	private DatabaseConnection dbconn;

	/*SQL statement*/
	private PreparedStatement pstmtSelectUser;
	private PreparedStatement pstmtUpdateUserLaunchTime;
	private PreparedStatement pstmtInsertUser;
	private PreparedStatement pstmtSelectQuestion;
	private PreparedStatement pstmtInsertQuestion;
	private PreparedStatement pstmtUpdateQuestionSendTime;
	private PreparedStatement pstmtInsertQuestionID;
	private PreparedStatement pstmtSelectQuestionID;
	private PreparedStatement pstmtMaxQuestionID;
	private PreparedStatement pstmtInsertFiles;
	private PreparedStatement pstmtSelectFiles;
	private PreparedStatement pstmtUpdateGoodQuestion;
	private PreparedStatement pstmtUpdateGoodUser;
	private PreparedStatement pstmtGetMaxQuestionID;
	private PreparedStatement pstmtCreateQuestionID;
	private PreparedStatement pstmtUpdateUserBonus;
	private PreparedStatement pstmtInsertWordList;
	private PreparedStatement pstmtDeleteFromQuestion;
	private PreparedStatement pstmtDropQuestionID;
	private PreparedStatement pstmtDeleteFromWordList;
	private PreparedStatement pstmtSelectWordList;
	private PreparedStatement pstmtQuestionSolved;
	private PreparedStatement pstmtSelectAcquaintance;
	private Statement stmtSelectQuestionByOrder;

	Cos cos;

	//帐号登录反馈消息字符串
	private enum LAUNCH_INFORMATION {
		USER_NOT_EXIST("帐号不存在"),
		PASSWORD_NOT_MATCH("密码错误"),
		LAUNCH_SUCCESS("成功登录"),
		UNKOWN_ERROR("未知错误，登录失败");

		private final String value;

		LAUNCH_INFORMATION(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	//聊天记录属性
	private enum CONTENT_MARK {
		DEFAULT(0),
		DOUBTED(1),
		FURTHURASKED(2),
		DOUBT(4),
		FURTHERASK(8),
		ANONIMOUS(16);

		private final int value;

		CONTENT_MARK(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/*init serveritem*/
	public ServerItem(IoSession session, DatabaseConnection dbconn) {
		this.session = session;
		this.dbconn = dbconn;
		this.cos = new Cos();
		pstmtInit();
	}
	private void pstmtInit() {
		try {
			pstmtSelectUser = dbconn.connection.prepareStatement("SELECT * FROM user WHERE username=?;");
			pstmtUpdateUserLaunchTime = dbconn.connection.prepareStatement("UPDATE user SET last_launch_time=now() WHERE username=?;");
			pstmtInsertUser = dbconn.connection.prepareStatement("INSERT INTO user (username, userkey, signature, mail_address)VALUES(?,?,?,?);");
			pstmtSelectQuestion = dbconn.connection.prepareStatement("SELECT * FROM question WHERE id=?;");
			pstmtSelectQuestionID = dbconn.connection.prepareStatement("SELECT * FROM question_id?;");
			pstmtUpdateQuestionSendTime = dbconn.connection.prepareStatement("UPDATE question SET last_send_time=now() WHERE id=?");
			pstmtInsertQuestionID = dbconn.connection.prepareStatement("INSERT INTO question_id? (record, time, username, markMap, recordpic) VALUES(?,now(),?,?,?);");
			pstmtMaxQuestionID = dbconn.connection.prepareStatement("SELECT max(record_id) FROM question_id?;");
			pstmtInsertFiles = dbconn.connection.prepareStatement("INSERT INTO files (md5, filename, user) VALUES(?,?,?);");
			pstmtSelectFiles = dbconn.connection.prepareStatement("SELECT * FROM files WHERE md5=?;");
			pstmtUpdateGoodQuestion = dbconn.connection.prepareStatement("UPDATE question SET praise_num=?;");
			pstmtUpdateGoodUser = dbconn.connection.prepareStatement("UPDATE user SET praise_num=? WHERE username=?;");
			pstmtGetMaxQuestionID = dbconn.connection.prepareStatement("SELECT max(id) FROM question;");
			pstmtInsertQuestion = dbconn.connection.prepareStatement("INSERT INTO question (owner, id, stem, addition, solved, stempic, additionpic) VALUES(?,?,?,?,?,?,?);");
			pstmtCreateQuestionID = dbconn.connection.prepareStatement(
					"CREATE TABLE question_id?"+"(\n" +
							"record_id BIGINT AUTO_INCREMENT,\n" +
							"record VARCHAR(255) NOT NULL,\n" +
							"recordpic VARCHAR(255) DEFAULT '',\n" +
							"username VARCHAR(20) NOT NULL,\n" +
							"time DATETIME DEFAULT now(),\n" +
							"markMap VARCHAR(255) DEFAULT \"\",\n" +
							"PRIMARY KEY(record_id)\n" +
							");");
			pstmtUpdateUserBonus = dbconn.connection.prepareStatement("UPDATE user SET bonus=?, question_num=? WHERE username=?;");
			pstmtInsertWordList = dbconn.connection.prepareStatement("INSERT INTO words_list? (word, question) VALUES(?,?);");

			pstmtDeleteFromWordList = dbconn.connection.prepareStatement("DELETE FROM words_list? WHERE question=?;");
			pstmtDeleteFromQuestion = dbconn.connection.prepareStatement("DELETE FROM question WHERE id=?;");
			pstmtDropQuestionID = dbconn.connection.prepareStatement("DROP TABLE question_id?;");
			pstmtSelectWordList = dbconn.connection.prepareStatement("SELECT * FROM words_list? WHERE word=?;");
			pstmtQuestionSolved = dbconn.connection.prepareStatement("UPDATE question SET solved=1 WHERE id=?;");
			pstmtSelectAcquaintance = dbconn.connection.prepareStatement("SELECT acquaintance FROM acquaintance_table WHERE username=?;");
			stmtSelectQuestionByOrder = dbconn.connection.createStatement();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//judge by session_user_map
	public boolean isLaunched(){
		String name = ServerHandler.session_user_map.get(session);
		if(null != name) {
			return name.equals(this.username);
		} else {
			return false;
		}
	}

	static ServerResponseMessage.Message BadMessage() {
		return ServerResponseMessage.Message.newBuilder().setUsername("")
				  .setMsgType(ServerResponseMessage.MSG.BAD_MESSAGE).build();
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
		//处理消息
		try {
			switch (msgType) {
				case LAUNCH_REQUEST:	//登录消息
					try {
						if (message.hasLauchRequest()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.LAUNCH_RESPONSE)
									  .setLauchResponse(handleLaunch(message.getLauchRequest()))
									  .setUsername(username)
									  .build();
						} else
							return null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case LOGOUT_MESSAGE:	//登出消息
					if(isLaunched())
						handleLogout();
					return null;
				case REGISTER_REQUEST: //注册
					try {
						if (message.hasRegisterRequest()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.REGISTER_RESPONSE)
									  .setRegisterResponse(handleRegisterRequest(message.getRegisterRequest()))
									  .build();
						} else {
							return null;
						}
					} catch (Exception e){
						e.printStackTrace();
						return null;
					}
				case SEND_CONTENT:	//发送对话消息
					try {
						if (message.hasSendContent() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.SEND_CONTENT)
									  .setUsername(username)
									  .setSendContent(handleSendContent(message.getSendContent()))
									  .build();
						} else
							return null;
					} catch (Exception e){
						e.printStackTrace();
						return null;
					}
				case ANNOUNCEMENT_MESSAGE:	//发布公告
					break;
				case GOOD_USER_REQUEST:	//赞用户
					try {
						if (message.hasGoodUserRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.GOOD_USER_RESPONSE)
									  .setUsername(username)
									  .setGoodUserResponse(handleGoodUserMessage(message.getGoodUserRequest()))
									  .build();
						} else {
							return null;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case GOOD_QUESTION_REQUEST:	//赞问题
					try {
						if (message.hasGoodQuestionRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.GOOD_QUESTION_RESPONSE)
									  .setUsername(username)
									  .setGoodQuestionResponse(handleGoodQuestionMessage(message.getGoodQuestionRequest()))
									  .build();
						} else
							return null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case QUESTION_INFORMATION_REQUEST:	//请求问题信息
					try {
						if (message.hasQuestionInformationRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.QUESTION_INFORMATION_RESPONSE)
									  .setUsername(username)
									  .setQuestionInformationResponse(handleQuestionInformationRequest(message.getQuestionInformationRequest()))
									  .build();
						} else return null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case USER_INFORMATION_REQUEST:	//请求用户信息
					try {
						if (message.hasUserInformationRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.USER_INFORMATION_RESPONSE)
									  .setUsername(username)
									  .setUserInformationResponse(handleUserInformationRequest(message.getUserInformationRequest()))
									  .build();
						} else
							return null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case GET_QUESTION_LIST_REQUEST:	//获取问题列表
					try {
						if (message.hasGetQuestionListRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.GET_QUESTION_LIST_RESPONSE)
									  .setUsername(username)
									  .setGetQuestionListResponse(
												 handleGetQuestionListRequest(message.getGetQuestionListRequest())
									  ).build();
						} else {
							return null;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case CREATE_QUESTION_REQUEST:	//新建问题
					try {
						if (message.hasCreateQuestionRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.CREATE_QUESTION_RESPONSE)
									  .setUsername(username)
									  .setCreateQuestionResponse(handleCreateQuestion(message.getCreateQuestionRequest()))
									  .build();
						} else
							return null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				case QUESTION_ENTER_REQUEST:	//进入房间
					try {
						if (message.hasQuestionEnterRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.QUESTION_ENTER_RESPONSE)
									  .setUsername(username)
									  .setQuestionEnterResponse(handleQuestionEnterRequest(message.getQuestionEnterRequest()))
									  .build();
						} else
							return null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case ABANDON_QUESTION_REQUEST:	//删除问题
					try {
						if (message.hasAbandonQuestionRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.ABANDON_QUESTION_RESPONSE)
									  .setUsername(username)
									  .setAbandonQuestionResponse(handleAbandonQuestion(message.getAbandonQuestionRequest()))
									  .build();
						} else
							return null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case SEARCH_INFORMATION_REQUEST:	//搜索信息
					try {
						if (message.hasSearchInformationRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.SEARCH_INFORMATION_RESPONSE)
									  .setUsername(username)
									  .setSearchInformationResponse(
												 handleSearchInformationRequest(message.getSearchInformationRequest())
									  ).build();
						} else {
							return null;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case FILE_REQUEST:	//获取签名请求
					if(message.hasFileRequest() && isLaunched()) {
						try {
							return ServerResponseMessage.Message.newBuilder()
								  .setMsgType(ServerResponseMessage.MSG.FILE_RESPONSE)
								  .setUsername(username)
								  .setFileResponse(handleFileRequest(message.getFileRequest()))
								  .build();
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}
					break;
				case SOLVED_QUESTION_REQUEST:
					try {
						if (message.hasSolvedQuestionRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.SOLVED_QUESTION_RESPONSE)
									  .setUsername(username)
									  .setSolvedQuestionResponse(
												 handleSolvedQuestionRequest(message.getSolvedQuestionRequest())
									  ).build();
						} else return null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				case GET_USER_LIST_REQUEST:
					try {
						if(message.hasGetUserListRequest() && isLaunched()) {
							return ServerResponseMessage.Message.newBuilder()
									  .setMsgType(ServerResponseMessage.MSG.GET_USER_LIST_RESPONSE)
									  .setUsername(username)
									  .setGetUserListResponse(handleGetUserListRequest(message.getGetUserListRequest()))
									  .build();
						}
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				default:
					throw new Exception("MSG type cant be recognized\n"+message.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	//处理信息
	private ServerResponseMessage.LaunchResponse
	handleLaunch(ClientSendMessage.LaunchRequest launchRequest)
			  throws SQLException{
		ServerResponseMessage.LaunchResponse responseLaunch = null;
		ServerResponseMessage.UserMessage userMessage = null;
		String key = launchRequest.getPassword();
		String realkey = null;
		boolean inOnlineUser = false;

		//获取密钥
		pstmtSelectUser.setString(1, username);
		ResultSet rs = pstmtSelectUser.executeQuery();
		if (rs.next()) {
			realkey = rs.getString("userkey");
		}
		rs.close();
		//执行操作
		if (realkey == null) {
			responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
					  .setStatus(false)
					  .setInformation(LAUNCH_INFORMATION.USER_NOT_EXIST.getValue()).build();
		} else {
			//比较密码
			if (key.equals(realkey)) {

				//更新最后登录时间
				pstmtUpdateUserLaunchTime.setString(1, username);
				pstmtUpdateUserLaunchTime.execute();
				userMessage = handleUserInformationRequest(
						  ClientSendMessage.UserInformationRequest.newBuilder()
									 .setUsername(username).build()
				).getUserMessage();
				responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
						  .setStatus(true)
						  .setInformation(LAUNCH_INFORMATION.LAUNCH_SUCCESS.getValue())
						  .setUserMessage(userMessage)
						  .build();
				//添加登录用户
				ServerHandler.session_user_map.put(session, username);
				return responseLaunch;
			} else {
				responseLaunch = ServerResponseMessage.LaunchResponse.newBuilder()
						  .setStatus(false)
						  .setInformation(LAUNCH_INFORMATION.PASSWORD_NOT_MATCH.getValue()).build();
				return responseLaunch;
			}
		}

		return ServerResponseMessage.LaunchResponse.newBuilder()
				  .setStatus(false)
				  .setInformation(LAUNCH_INFORMATION.UNKOWN_ERROR.getValue())
				  .build();
	}

	private void
	handleLogout()
			  throws SQLException {
		ServerHandler.serviceMap.remove(session);
		ArrayList<String> questions = ServerHandler.session_questions_map.get(session);
		if(!(null == questions)) {
			for (String question : questions) {
				ArrayList<IoSession> sessions = ServerHandler.question_sessions_map.get(question);
				if(!(null == sessions)) {
					break;
				} else {
					sessions.remove(question);
				}
			}
		}
		ServerHandler.log.info(username+" Log out");
	}

	private ServerResponseMessage.RegisterResponse
	handleRegisterRequest(ClientSendMessage.RegisterRequest request)
				throws SQLException{
		ServerResponseMessage.RegisterResponse response = null;
		String username = request.getUsername();
		String password = request.getPassword();
		String mail_address = request.getMailAddress();
		String signature = request.getSignature();

		pstmtSelectUser.setString(1,username);
		ResultSet rs = pstmtSelectUser.executeQuery();
		//用户已存在
		if(rs.next()) {
			response = ServerResponseMessage.RegisterResponse.newBuilder()
					  .setSuccess(false)
					  .setInformation("用户已存在")
					  .build();
			return response;
		}
		rs.close();
		//邮箱已被注册

		//注册成功
		pstmtInsertUser.setString(1,username);
		pstmtInsertUser.setString(2,password);
		pstmtInsertUser.setString(3,signature);
		pstmtInsertUser.setString(4,mail_address);
		pstmtInsertUser.execute();

		response = ServerResponseMessage.RegisterResponse.newBuilder()
				  .setSuccess(true)
				  .setInformation("注册成功")
				  .build();
		return response;
	}

	private ServerResponseMessage.SendContent
	handleSendContent(ClientSendMessage.SendContent sendMessage)
			  throws SQLException 	{
		ServerResponseMessage.SendContent responseSend = null;
		ServerResponseMessage.SendContent.Builder sendBuider = ServerResponseMessage.SendContent.newBuilder();
		//解析数据
		long questionID = sendMessage.getQuestionID();
		String time = sendMessage.getTime();
		String record = sendMessage.getContent();
		Map<Integer, Long> markMap = sendMessage.getMarkMapMap();
		ProtocolStringList pictures = sendMessage.getPicturesList();

		pstmtSelectQuestion.setLong(1,questionID);
		ResultSet rs = pstmtSelectQuestion.executeQuery();
		if(!rs.next()) {
			return ServerResponseMessage.SendContent.newBuilder().setSuccess(false).build();
		}
		rs.close();

		pstmtUpdateQuestionSendTime.setLong(1, questionID);
		pstmtUpdateQuestionSendTime.execute();

		//将markMap转化为String
		StringBuilder markMapStrBuider = new StringBuilder("");
		if(markMap.size() > 0) {
			for (Map.Entry<Integer, Long> entry : markMap.entrySet()) {
				markMapStrBuider.append(entry.getKey())
						  .append(":").append(entry.getValue()).append(":");
			}
			markMapStrBuider.deleteCharAt(markMapStrBuider.length()-1);
		}

		//将pictureMap转化为String
		StringBuilder recordpicStrBuider = new StringBuilder("");
		if(pictures.size()>0) {
			for (String picture : pictures) {
				recordpicStrBuider.append(picture).append(":");
			}
			recordpicStrBuider.deleteCharAt(recordpicStrBuider.length() - 1);
		}

		//在数据库中记录
		pstmtInsertQuestionID.setLong(1,questionID);
		pstmtInsertQuestionID.setString(2,record);
		pstmtInsertQuestionID.setString(3, username);
		pstmtInsertQuestionID.setString(4, markMap.toString());
		pstmtInsertQuestionID.setString(5, recordpicStrBuider.toString());
		pstmtInsertQuestionID.execute();

		//获取id
		long recordID;
		pstmtMaxQuestionID.setLong(1, questionID);
		rs = pstmtMaxQuestionID.executeQuery();
		if(rs.next()) {
			recordID = rs.getLong(1);
		} else {
			recordID = 1;
		}
		rs.close();

		//返回服务器回复
		//匿名检查
		if(markMap!=null && markMap.get(CONTENT_MARK.ANONIMOUS)!=null) {
			sendBuider.setUser(sendMessage.getUser());
		} else {
			sendBuider.setUser("匿名");
		}

		sendBuider.setQuestionID(questionID);
		sendBuider.setTime(time);
		sendBuider.setContent(record);
		sendBuider.putAllMarkMap(markMap);

		//对每一图片(文件名为md5)将信息存入数据库中
		for(String pic : sendMessage.getPicturesList()) {
			//数据库操作
			pstmtSelectFiles.setString(1,pic);
			rs = pstmtSelectFiles.executeQuery();
			if(!rs.next()) {
				pstmtInsertFiles.setString(1,pic);
				pstmtInsertFiles.setString(2,pic);
				pstmtInsertFiles.setString(3,username);
				pstmtInsertFiles.execute();
			}

		}
		sendBuider.setSuccess(true);
		sendBuider.setIsmyself(false);
		sendBuider.setRecordID(recordID);
		responseSend  = sendBuider.build();
		ArrayList<IoSession> ioSessions = ServerHandler.question_sessions_map.get(questionID+"");

		//给每一个处于房间中的用户发送信息（自己除外）
		if(null != ioSessions) {
			for (IoSession is : ioSessions) {
				if (!is.equals(session) && is.isConnected()) {
					is.write(
							  ServerResponseMessage.Message.newBuilder()
										 .setUsername(username)
										 .setMsgType(ServerResponseMessage.MSG.SEND_CONTENT)
										 .setSendContent(sendBuider)
										 .build()
					);
				}
			}
		}

		sendBuider.setIsmyself(true);
		responseSend = sendBuider.build();
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
		pstmtSelectQuestion.setLong(1, questionID);
		ResultSet rs = pstmtSelectQuestion.executeQuery();
		String owner,stem,addition,time,user,contentMessage, markMapStr, recordpic;
		Map<Integer, Long> markMap = null;
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
			rs.close();
		} else {
			rs.close();
			return ServerResponseMessage.QuestionInformationResponse.newBuilder()
					  .setExist(false).build();
		}

		//获得问题记录
		pstmtSelectQuestionID.setLong(1, questionID);
		rs = pstmtSelectQuestionID.executeQuery();

		while (rs.next()) {
			contentMessage = rs.getString("record");
			user = rs.getString("username");
			time =  rs.getString("time");
			markMapStr = rs.getString("markMap");
			recordpic = rs.getString("recordpic");

			//解析
			markMap = getMarkMap(markMapStr);
			boolean isAnoimous = false;
			if(markMap.get(CONTENT_MARK.ANONIMOUS) != null) {
				isAnoimous = true;
			}
			//将图片还原为列表
			List<String> pics = new ArrayList<>();
			for(String s : recordpic.split(":")) {
				pics.add(s);
			}
			//添加返回记录
			questionMessageBuider.addRecord(
					  ServerResponseMessage.Record.newBuilder()
					  .setTime(time)
					  .setContentMessage(contentMessage)
								 //若匿名则设置用户名为匿名
					  .setUser(isAnoimous?"匿名" : user)
					  .putAllMarkMap(markMap)
					  .addAllRecordpic(pics)
			);
		}
		rs.close();

		response = builder.setQuestionMessage(questionMessageBuider).setExist(true).build();

		return response;
	}

	//将数据库中markMap表项还原
	private Map<Integer, Long> getMarkMap(String str) {
		Map<Integer, Long> markMap = new HashMap<>();
		String[] strs = str.split(":");
		for(int i=0; i<strs.length-1; i++) {
			markMap.put(Integer.valueOf(strs[i]), Long.valueOf(strs[i+1]));
		}
		return markMap;
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
			//将用户session添加进question_sessions列表中
			ArrayList<IoSession> ioSessions = ServerHandler.question_sessions_map.get(questionID.toString());
			if(null==ioSessions) {
				ioSessions = new ArrayList<>();
				ioSessions.add(session);
				ServerHandler.question_sessions_map.put(questionID.toString(), ioSessions);
			} else {
				ioSessions.add(session);
				ServerHandler.question_sessions_map.replace(questionID.toString(), ioSessions);
			}

			//向用户问题表中添加问题
			ArrayList<String> questions = ServerHandler.session_questions_map.get(session);
			if(null == questions) {
				questions = new ArrayList<>();
				questions.add(questionID.toString());
				ServerHandler.session_questions_map.replace(session, questions);
			}

			for(IoSession is : ioSessions) {
				//若客户端链接中断
				if(is.isClosing())
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
				is.write(sendMessage);
			}
		}
		return response;
	}

	private ServerResponseMessage.GoodQuestionResponse
	handleGoodQuestionMessage(ClientSendMessage.GoodQuestionRequest goodQuestionRequest)
		     throws SQLException {
		ServerResponseMessage.GoodQuestionResponse goodQuestionResponse = null;
		Long questionID = goodQuestionRequest.getQuestionID();
		Integer good;

		pstmtSelectQuestion.setLong(1, questionID);
		ResultSet rs = pstmtSelectQuestion.executeQuery();
		if(rs.next()) {
			good = rs.getInt("praise_num")+1;
			pstmtUpdateGoodQuestion.setInt(1,good);
			pstmtUpdateGoodQuestion.execute();
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
		pstmtSelectUser.setString(1, user);
		ResultSet rs = pstmtSelectUser.executeQuery();
		if(rs.next()) {
			good = rs.getInt("praise_num")+1;
			pstmtUpdateGoodUser.setInt(1,good);
			pstmtUpdateGoodUser.setString(2,user);
			pstmtUpdateGoodUser.execute();
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
		pstmtSelectUser.setString(1,username);
		ResultSet rs = pstmtSelectUser.executeQuery();
		if(rs.next()) {
			bonus = rs.getInt("bonus");
			question_num = rs.getInt("question_num");
		}
		rs.close();

		if(bonus<3) {
			createQuestionResponse = ServerResponseMessage.CreateQuestionResponse.newBuilder().setSuccess(false).build();
		}
		else{

			StringBuffer record = new StringBuffer("");
			String stem = createQuestionRequest.getStem();
			String addition = createQuestionRequest.getAddition();
			ProtocolStringList stempics = createQuestionRequest.getStempicList();
			ProtocolStringList additionpics = createQuestionRequest.getAdditionpicList();

			//在数据库中记录
			String time = createQuestionRequest.getTime();
			long questionID = 1;
			rs = pstmtGetMaxQuestionID.executeQuery();
			if(rs.next())
			{
				questionID = rs.getLong(1)+1;
			} else {
				questionID = 0;
			}
			rs.close();

			//将图片合并为字符串
			StringBuilder stempic = new StringBuilder("");
			StringBuilder additionpic = new StringBuilder("");
			if(stempics.size()>0) {
				for (String s : stempics) {
					stempic.append(s).append(":");
				}
				stempic.deleteCharAt(stempic.length() - 1);
			}
			if(additionpics.size()>0) {
				for (String s : additionpics) {
					additionpic.append(s).append(":");
				}
				additionpic.deleteCharAt(additionpic.length() - 1);
			}

			/*sql = "INSERT INTO question (owner, id, stem, addition, solved, stempic, additionpic) VALUES" +
					  "('"+username+"','"+questionID+"','"+stem+"','"+addition+"',0, '"+stempic+"', '"+additionpic+"');";
			stmt.execute(sql);*/
			pstmtInsertQuestion.setString(1,username);
			pstmtInsertQuestion.setLong(2,questionID);
			pstmtInsertQuestion.setString(3, stem);
			pstmtInsertQuestion.setString(4,addition);
			pstmtInsertQuestion.setBoolean(5, false);
			pstmtInsertQuestion.setString(6, stempic.toString());
			pstmtInsertQuestion.setString(7, additionpic.toString());
			pstmtInsertQuestion.execute();

			//创建问题记录表
			pstmtCreateQuestionID.setLong(1,questionID);
			pstmtCreateQuestionID.execute();

			//扣除点数,增加提问数量
			pstmtUpdateUserBonus.setInt(1, bonus);
			pstmtUpdateUserBonus.setLong(2, question_num+1);
			pstmtUpdateUserBonus.setString(3,username);
			pstmtUpdateUserBonus.execute();

			//插入分词列表
			ProtocolStringList keywords = createQuestionRequest.getKeywordsList();
			Iterator<String> ite =keywords.iterator();
			String keyword = "";
			if(ite.hasNext()) {
				keyword = ite.next();
			}

			if(keyword.equals("")) {
			} else {
				pstmtInsertWordList.setInt(1,1);
				pstmtInsertWordList.setString(2,keyword);
				pstmtInsertWordList.setLong(3,questionID);
				pstmtInsertWordList.execute();
			}

			while (ite.hasNext()){
				keyword = ite.next();
				pstmtInsertWordList.setInt(1,2);
				pstmtInsertWordList.setString(2,keyword);
				pstmtInsertWordList.setLong(3,questionID);
				pstmtInsertWordList.execute();
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
		boolean auth;

		long questionID = abandonQuestionRequest.getQuestionID();
		String owner = null;
		pstmtSelectQuestion.setLong(1,questionID);
		ResultSet rs = pstmtSelectQuestion.executeQuery();
		if(rs.next()) {
			owner=rs.getString("owner");
		} else {
			auth = false;
			abandonQuestionResponse = ServerResponseMessage.AbandonQuestionResponse
					.newBuilder().setSuccess(auth).build();

			return abandonQuestionResponse;
		}
		rs.close();

		auth = owner.equals(this.username);
		if(auth) {
			//删除问题项
			pstmtDeleteFromQuestion.setLong(1, questionID);
			pstmtDeleteFromQuestion.execute();
			//删除问题记录
			pstmtDropQuestionID.setLong(1,questionID);
			pstmtDropQuestionID.execute();
			//删除分词列表中指向问题的项
			pstmtDeleteFromWordList.setInt(1, 1);
			pstmtDeleteFromWordList.setLong(2,questionID);
			pstmtDeleteFromWordList.execute();
			pstmtDeleteFromWordList.setInt(1, 2);
			pstmtDeleteFromWordList.setLong(2,questionID);
			pstmtDeleteFromWordList.execute();
		} else {
			auth = false;
		}

		abandonQuestionResponse = ServerResponseMessage.AbandonQuestionResponse
				  .newBuilder().setSuccess(auth).build();

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

		pstmtSelectUser.setString(1,username);
		ResultSet rs = pstmtSelectUser.executeQuery();
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

	private ServerResponseMessage.FileResponse
	handleFileRequest(ClientSendMessage.FileRequest fileRequest)
			  throws Exception {
		ServerResponseMessage.FileResponse response = null;
		String sign = null;
		ProtocolStringList files = fileRequest.getFilenameList();

		ServerResponseMessage.FileResponse.Builder builder =
				  ServerResponseMessage.FileResponse.newBuilder();

		if(isLaunched()) {
			switch (fileRequest.getSignType()) {
				case DOWNLOAD:
					for(String filename : files) {
						sign = cos.getDownloadSign(filename, Cos.TYPE.PICTURE);
						builder.putSign(filename, sign);
						builder.setSignType(ServerResponseMessage.FileResponse.SIGNTYPE.DOWNLOAD);
					}
					builder.setSuccess(true);
					break;
				case UPLOAD:
					for(String filename : files) {
						sign = cos.getUploadSign(filename, Cos.TYPE.PICTURE);
						builder.putSign(filename, sign);
					}
					builder.setSuccess(true);
					builder.setSignType(ServerResponseMessage.FileResponse.SIGNTYPE.UPLOAD);
					builder.addAllLocalFilePath(fileRequest.getLocalFilePathList());
					break;
				default:
					throw new Exception("MSG is invalid");
			}
			return builder.build();

		} else {
			response = builder.setSuccess(false).build();
		}
		return response;
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

		ResultSet rs = stmtSelectQuestionByOrder.executeQuery("SELECT * FROM question ORDER BY "+ref+" "+order+";");
		int i;
		for(i=0;i<questionNum && rs.next();i++) {
			int userNum = 0;
			Iterator<IoSession> ite = ServerHandler.question_sessions_map.get(rs.getString("id")).iterator();
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

		try {
			Set<Long> set1 = new HashSet<>();
			if (ite.hasNext()) {
				keyword = SQLStringParser.parse(ite.next());
				pstmtSelectWordList.setInt(1,1);
				pstmtSelectWordList.setString(2, keyword);
				rs = pstmtSelectWordList.executeQuery();
				while (rs.next()) {
					set1.add(new Long(rs.getLong("question")));
				}
				rs.close();
			}
			Set<Long> set2 = new HashSet<>();
			while (ite.hasNext()) {
				try {
					keyword = SQLStringParser.parse(ite.next());
					pstmtSelectWordList.setInt(1,2);
					pstmtSelectWordList.setString(2,keyword);
					rs = pstmtSelectWordList.executeQuery();
					while (rs.next()) {
						set2.add(new Long(rs.getLong("question")));
					}
					rs.close();
				} catch (SQLException e) {
					continue;
				}
			}
			set1.addAll(set2);

			//获得问题消息
			for (Long question : set1) {
				int userNum = ServerHandler.question_sessions_map.size();
				pstmtSelectQuestion.setLong(1,question);
				rs = pstmtSelectQuestion.executeQuery();

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
			builder.setSearchID(request.getSearchID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		response = builder.build();
		return response;

	}

	private ServerResponseMessage.SolvedQuestionResponse
	handleSolvedQuestionRequest (ClientSendMessage.SolvedQuestionRequest request)
				throws SQLException {
		ServerResponseMessage.SolvedQuestionResponse response = null;

		Long questionID = request.getQuestionID();
		pstmtSelectQuestion.setLong(1,questionID);
		ResultSet rs = pstmtSelectQuestion.executeQuery();
		if(rs.next()) {
			String owner = rs.getString("owner");
			rs.close();
			if(owner.equals(username)) {
				pstmtQuestionSolved.setLong(1,questionID);
				pstmtQuestionSolved.execute();
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

	private ServerResponseMessage.GetUserListResponse
	handleGetUserListRequest (ClientSendMessage.GetUserListRequest request)
				throws SQLException {
		ServerResponseMessage.GetUserListResponse response = null;

		ClientSendMessage.GetUserListRequest.USER_LIST_TYPE
				  userListType = request.getUserListType();
		String param = request.getParam();

		Map<String, String> userAndPictureMap = new HashMap<>();

		ResultSet rs = null;

		String user = null;
		String pic_url = null;

		switch (userListType) {
			case ACQUAINTANCE_LIST:
				String myname = param;
				String acquaintanceStr = null;
				pstmtSelectAcquaintance.setString(1, myname);
				rs = pstmtSelectAcquaintance.executeQuery();
				if(rs.next()) {
					acquaintanceStr = rs.getString("acquaintance");
					rs.close();

					List<String> acquaintanceList = AcquaintanceParser.parse(acquaintanceStr);
					if(!acquaintanceList.isEmpty()) {
						for (String acquser : acquaintanceList) {
							user = acquser;
							pstmtSelectUser.setString(1,user);
							rs = pstmtSelectUser.executeQuery();
							if (rs.next()) {
								pic_url = rs.getString("pic_url");
							}
							rs.close();
							userAndPictureMap.put(user, pic_url);
						}
					}
				}
				response = ServerResponseMessage.GetUserListResponse.newBuilder()
						.setSuccess(true)
						.setUserListType(ACQUAINTANCE_LIST)
						.build();
				break;
			case USERS_IN_ROOM_LIST:
				Long questionID = Long.valueOf(param);
				ArrayList<IoSession> sessions = ServerHandler.question_sessions_map.get(questionID);
				if(null != sessions) {

					for (IoSession session : sessions) {
						user = ServerHandler.session_user_map.get(session);
						pstmtSelectUser.setString(1,user);
						rs = pstmtSelectUser.executeQuery();
						if (rs.next()) {
							pic_url = rs.getString("pic_url");
						}
						rs.close();
						userAndPictureMap.put(user, pic_url);
					}
				}
				response = ServerResponseMessage.GetUserListResponse.newBuilder()
						.setSuccess(true)
						.setQuestionID(questionID)
						.setUserListType(USERS_IN_ROOM_LIST)
						.build();
				break;
			default:
				response = ServerResponseMessage.GetUserListResponse.newBuilder()
						.setSuccess(false)
						.build();
		}

		return response;
	}

}