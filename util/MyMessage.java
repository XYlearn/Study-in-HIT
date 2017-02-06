package util;

import java.util.ArrayList;

 public class MyMessage
{
	public String userName;//用户名
	//public String userHead;//用户头像MD5
	public String messageTime;//时间
	public String message;//消息内容，图片以%0，%1，……表示
	public ArrayList<String> pictures;//图片MD5

	public MyMessage(String un,String mt,String m,ArrayList<String> p)
	{
		userName=un;
		messageTime=mt;
		message=m;
		pictures=p;
	}

	public MyMessage(String un,String mt,String m)
	{
		userName=un;
		messageTime=mt;
		message=m;
	}
}