package util;

import bin.test;
import com.ServerResponseMessage.UserMessage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserInfo implements Dispatcher
{
	private static final Map<String,UserMessage> map=new ConcurrentHashMap<String,UserMessage>();
	private static long startTime=0;
	private static long delayTime=3000;
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
		test.client.requestUserInfo(username);
		test.client.downloadFile(username+".jpg");
	}
	
	public static void dispatch(UserMessage msg)
	{
		map.put(msg.getUsername(),msg);
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
