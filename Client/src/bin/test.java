package bin;

import javax.swing.JFrame;
import java.util.ArrayList;
import util.MyMessage;
import gui.ChattingBox;
import gui.InputBox;
import java.util.Scanner;
import util.AudioTools;
import NetEvent.Client;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.io.IOException;
import java.lang.Thread;
import java.util.logging.Level;
import java.util.logging.Logger;

public class test
{

	public static final String MAINPATH=test.class.getResource("").getPath()
			.substring(0, test.class.getResource("").getPath().length()-4);
	public static final String PICTPATH=MAINPATH+"pictures/";
	public static final String FILEPATH=MAINPATH+"files/";
	public static Client client=new Client();

	public static final void main()
	{
		new Thread(client).start();
		JFrame f=new JFrame("Hello, world!");
		//ChattingBox c=new ChattingBox();
		InputBox box=new InputBox();
		box.setQuestionID("1");
		f.getContentPane().add(box, BorderLayout.CENTER);
		f.setVisible(true);
		//c.setSize(500,550);
		box.setSize(500, 300);
		f.pack();
		f.setLocationRelativeTo(null);
		/*ArrayList<String> a=new ArrayList<String>();
		a.add("ask.jpg");
		c.pushMessage(new MyMessage("ask","22:01:25","what?"));
		c.pushMessage(new MyMessage("a","22:01:25","this\n%0",a));
		Scanner s=new Scanner(System.in);
		String cmd;
		while(s.hasNext())
		{
			cmd=s.nextLine();
			if(cmd.equals("start"))
				AudioTools.startCapture();
			else if(cmd.equals("stop"))
				c.pushAudio(new MyMessage("a","22:02:05",
					AudioTools.stopAndSaveCapture()));
			else if(cmd.equals("exit"))
				return;
			else if(cmd.equals("show"))
				System.out.println(c.myPane.getText());
			else
				c.pushMessage(new MyMessage("a","22:02:24",cmd));
		}*/
		//c.pushMessage(new MyMessage("ask","22:11:15","en"));
		//c.pushMessage(new MyMessage("a","22:11:15","en"));
		//System.out.println(c.myPane.getText());
		Scanner s=new Scanner(System.in);
		String cmd, tmp=null;
		while (s.hasNext())
		{
			cmd=s.nextLine();
			switch (cmd)
			{
				case "exit":
					return;
				case "show":
					System.out.println(box.myPane.getText());
					tmp=box.myPane.getText();
					break;
				case "get":
					System.out.println(box.myPane.getCaretPosition());
					break;
				case "send":
					box.sendMessage();
					break;
				case "reset":
					tmp=box.myPane.getText();
					break;
				case "insert":
					box.insertImage(PICTPATH+"ask.jpg");
					break;
				case "create":
					try
					{
						client.createQuestion("第一个问题", "描述", new ArrayList<>(1));
					} catch (IOException ex)
					{
						System.out.println("网络异常");
					}
					break;
				case "launch":
					try
					{
						client.launchRequest("Test", "123456");
					} catch (IOException e)
					{
						System.out.println("登录失败："+e);
					}
					break;
				case "enter":
					try
					{
						client.enterQuestion("1");
					} catch (IOException e)
					{
						System.out.println("进入房间失败："+e);
					}
					break;
				default:
					tmp=tmp.replaceAll(cmd, "");
					System.out.println(tmp);
					break;
			}
		}
	}
}
