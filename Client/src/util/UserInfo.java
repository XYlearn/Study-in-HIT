package util;

import NetEvent.eventcom.UserInfoEvent;
import NetEvent.messagecom.UserMessage;
import bin.test;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserInfo implements Dispatcher
{
	private static final Map<String,UserMessage> map=new ConcurrentHashMap<>();
	private static final Map<String,Boolean> requesting=new ConcurrentHashMap<>();
	private static long startTime=0;
	private static long delayTime=500;
	private static String myUser="";
	
	public static void setMyUserName(String username)
	{
		myUser=username;
	}
	
	public static String getMyUserName()
	{
		return myUser;
	}
	
	public static void requestUserInfo(String username) throws IOException
	{
		if(requesting.getOrDefault(username, Boolean.FALSE))
		{
			test.client.requestUserInfo(username);
			requesting.put(username, Boolean.TRUE);
		}
	}
	
	public static void dispatch(UserInfoEvent e)
	{
		if(e.isExist())
		{
			map.put(e.getUserMessage().getUsername(),e.getUserMessage());
			requesting.put(e.getUserMessage().getUsername(), Boolean.FALSE);
			try
			{
				test.client.downloadFile(e.getUserMessage().getPicUrl(), false, -1);
			} catch (IOException ex)
			{
				Logger.getLogger(UserInfo.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	public static void setDelay(long delay)
	{
		delayTime=delay;
	}

	public static String getMailAddress(String username) throws IOException
	{
		requestUserInfoWithTimeOut(username);
		return map.get(username).getMailAddress();
	}

	public static String getSignature(String username) throws IOException
	{
		requestUserInfoWithTimeOut(username);
		return map.get(username).getSignature();
	}


	public static String getPicURL(String username) throws IOException
	{
		requestUserInfoWithTimeOut(username);
		return map.get(username).getPicUrl();
	}

	
	public static int getGood(String username) throws IOException
	{
		requestUserInfoWithTimeOut(username);
		return map.get(username).getGood();
	}

	
	public static int getQuestionNum(String username) throws IOException
	{
		requestUserInfoWithTimeOut(username);
		return map.get(username).getQuestionNum();
	}

	
	public static int getSolvedQuestionNum(String username) throws IOException
	{
		requestUserInfoWithTimeOut(username);
		return map.get(username).getSolvedQuestionNum();
	}

	
	public static int getBonus(String username) throws IOException
	{
		requestUserInfoWithTimeOut(username);
		return map.get(username).getBonus();
	}
	
	private static void requestUserInfoWithTimeOut(String username) throws IOException
	{
		startTime=System.currentTimeMillis();
		if(!map.containsKey(username)) requestUserInfo(username);
		while(!map.containsKey(username))
			if(System.currentTimeMillis()>startTime+delayTime)
				throw new IOException("Request Time Out");
	}
}
