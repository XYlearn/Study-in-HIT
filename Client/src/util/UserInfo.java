package util;

import bin.test;
import com.ServerResponseMessage.UserMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserInfo implements Dispatcher
{
	private static final Map<String,UserMessage> map=new HashMap<String,UserMessage>();
	private static long startTime;
	private static long delayTime=3000;
	
	public static void requestUserInfo(String username)
	{
		try
		{
			test.client.requestUserInfo(username);
			test.client.downloadFile(username+".jpg");
		} catch (IOException ex)
		{
			System.out.println(ex);
		}
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
		startTime=System.currentTimeMillis();
		if(!map.containsKey(username)) requestUserInfo(username);
		while(!map.containsKey(username))
			if(System.currentTimeMillis()>startTime+delayTime)
				throw new IOException("Request Time Out");
		return map.get(username).getMailAddress();
	}

	public static String getSignature(String username) throws IOException
	{
		startTime=System.currentTimeMillis();
		if(!map.containsKey(username)) requestUserInfo(username);
		while(!map.containsKey(username))
			if(System.currentTimeMillis()>startTime+delayTime)
				throw new IOException("Request Time Out");
		return map.get(username).getSignature();
	}

	
	public static String getPicURL(String username) throws IOException
	{
		startTime=System.currentTimeMillis();
		if(!map.containsKey(username)) requestUserInfo(username);
		while(!map.containsKey(username))
			if(System.currentTimeMillis()>startTime+delayTime)
				throw new IOException("Request Time Out");
		return map.get(username).getPicUrl();
	}

	
	public static int getGood(String username) throws IOException
	{
		startTime=System.currentTimeMillis();
		if(!map.containsKey(username)) requestUserInfo(username);
		while(!map.containsKey(username))
			if(System.currentTimeMillis()>startTime+delayTime)
				throw new IOException("Request Time Out");
		return map.get(username).getGood();
	}

	
	public static int getQuestionNum(String username) throws IOException
	{
		startTime=System.currentTimeMillis();
		if(!map.containsKey(username)) requestUserInfo(username);
		while(!map.containsKey(username))
			if(System.currentTimeMillis()>startTime+delayTime)
				throw new IOException("Request Time Out");
		return map.get(username).getQuestionNum();
	}

	
	public static int getSolvedQuestionNum(String username) throws IOException
	{
		startTime=System.currentTimeMillis();
		if(!map.containsKey(username)) requestUserInfo(username);
		while(!map.containsKey(username))
			if(System.currentTimeMillis()>startTime+delayTime)
				throw new IOException("Request Time Out");
		return map.get(username).getSolvedQuestionNum();
	}

	
	public static int getBonus(String username) throws IOException
	{
		startTime=System.currentTimeMillis();
		if(!map.containsKey(username)) requestUserInfo(username);
		while(!map.containsKey(username))
			if(System.currentTimeMillis()>startTime+delayTime)
				throw new IOException("Request Time Out");
		return map.get(username).getBonus();
	}
}
