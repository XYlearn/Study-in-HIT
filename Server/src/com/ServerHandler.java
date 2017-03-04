package com;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by xy16 on 17-2-24.
 */
public class ServerHandler extends IoHandlerAdapter {
	//the logger
	public static Logger log = Logger.getLogger(ServerHandler.class);

	private DatabaseConnection dbconn = Server.databaseConnection;
	//set down the map between session and service
	public static Map<IoSession, ServerItem> serviceMap = new HashMap<>();

	//set down the map between question and users
	public static Map<String, ArrayList<IoSession>> question_sessions_map = new HashMap<>();

	//set down the map between user and questions
	public static Map <IoSession, ArrayList<String>> session_questions_map = new HashMap<>();

	//set down the map between username and session
	public static Map<IoSession, String> session_user_map = new HashMap<>();

	@ Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		log.info(cause.getLocalizedMessage(), cause);
	}

	@ Override
	public void messageReceived(IoSession session, Object message) {
		ClientSendMessage.Message recvMessage = (ClientSendMessage.Message) message;

		ServerItem serverItem = serviceMap.get(session);
		if(serverItem.equals(null)) {
			serverItem = new ServerItem(session, dbconn);
			serviceMap.put(session, serverItem);
		}
		ServerResponseMessage.Message response =
				  serverItem.handleMessage(recvMessage);

		log.info("Received From "+recvMessage.getUsername()+ " Message Type: "+recvMessage.getMsgType());
		if(session.isConnected()) {
			session.write(response);
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) {
		ServerResponseMessage.Message sendMessage = (ServerResponseMessage.Message)message;

		log.info("Send To "+sendMessage.getUsername() +" Message Type: "+sendMessage.getMsgType());
	}

	@Override
	public void sessionCreated(IoSession session) {
		serviceMap.put(session, new ServerItem(session, dbconn));
		log.info("Accept: " + session);
	}

	@Override
	public void sessionClosed(IoSession session) {
		serviceMap.remove(session);
		ArrayList<String> questions = session_questions_map.get(session);
		if(!(null == questions)) {
			for (String question : questions) {
				ArrayList<IoSession> sessions = question_sessions_map.get(question);
				if(!(null == sessions)) {
					break;
				} else {
					sessions.remove(question);
				}
			}
		}
		session_user_map.remove(session);
		log.info("Terminated: " + session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {

	}

	@Override
	public void sessionOpened(IoSession session) {

	}
}

