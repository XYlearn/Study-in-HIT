package bin;

import javax.swing.JFrame;
import java.util.Scanner;
import NetEvent.Client;
import gui.InputBox;
import gui.dao.MainFrame;
import java.awt.BorderLayout;
import java.awt.*;
import gui.DrawImageFrame;
import gui.WhiteBoard;
import gui.dao.LoginFrame;
import java.io.IOException;
import util.Settings;

public class test
{

	public static final String MAINPATH=test.class.getResource("").getPath()
			.substring(0, test.class.getResource("").getPath().length()-4);
	public static final String PICTPATH=MAINPATH+"pictures/";
	public static final String FILEPATH=MAINPATH+"files/";
	public static final String IMGPATH=MAINPATH+"img_src/";
	public static final String AUDIOPATH=MAINPATH+"audio/";
	public static final Client client=new Client();
	public static MainFrame mainFrame;
	public static LoginFrame loginFrame;

	public static final void Board()
	{
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		WhiteBoard frame=new WhiteBoard();
		frame.setTitle("White Board");
		frame.setSize(screenSize.width/2, screenSize.height/2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLocation(screenSize.width/4, screenSize.height/4);
	}

	public static final void Image() throws IOException
	{
		DrawImageFrame frame=new DrawImageFrame();
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		frame.setTitle("图片绘画");
		frame.setSize((int)screenSize.getWidth()/2, (int)screenSize.getHeight()*3/4);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static final void startProgram()
	{
		//client.start();
		loginFrame=new LoginFrame();
		loginFrame.setVisible(true);
		/*client.start();
		try
		{
			while(!client.isConnected());
			UserInfo.setMyUserName("test");
			client.launchRequest("test", "123456");
		} catch (IOException ex)
		{
			Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
		}
		//new UserInformation("test").setVisible(true);
		mainFrame=new MainFrame();
		mainFrame.setVisible(true);*/
	}

	public static final void testmain()
	{
		JFrame f=new JFrame("Hello, world!");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//ChattingBox c=new ChattingBox();
		InputBox c=new InputBox();
		c.bind(1);
		f.getContentPane().add(c, BorderLayout.CENTER);
		f.setVisible(true);
		//c.setSize(500,550);
		c.setSize(new Dimension(500, 300));
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
					System.exit(0);
					break;
				case "show":
					try
					{
						//System.out.println(StringEscapeUtils.escapeHtml("啊好"));
						System.out.println(c.myPane.getText());
					} catch (Exception ex)
					{
						System.out.println(ex);
					}
					break;
				case "get":
					System.out.println();
					break;
				/*case "send":
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
						ArrayList<String> tmparray=new ArrayList<>();
						tmparray.add("");
						tmparray.add("第");
						tmparray.add("一");
						tmparray.add("个");
						tmparray.add("问题");
						client.createQuestion("第一个问题", "描述", tmparray);
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
					break;*/
				default:
					//cmd=StringEscapeUtils.escapeHtml(cmd);
					//System.out.println(cmd);
					//c.pushMessage(new MyMessage("a","16:41:05",cmd));
					break;
			}
		}
		/*JFrame f=new JFrame("My Application");
		ListBox listbox=new ListBox();
		f.add(listbox);
		listbox.setSize(300,600);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","","kqwyf","这是无图问题","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","bigpic.jpg","说正事专用问题","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","widepic.jpg","宽屏专用问题","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		listbox.add(new ListElementData("1","22:03:07","kqwyf","ask.jpg","stem","addition",1,2,"22:03:07"));
		*/
		/*JFrame f=new JFrame("myApplication");
		SearchBox sbox=new SearchBox();
		f.getContentPane().add(sbox);
		sbox.setColumns(30);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		Scanner s=new Scanner(System.in);
		while(s.hasNext())
		{
			switch(s.nextLine())
			{
				case "exit":
					System.exit(0);
					break;
				case "search":
					sbox.search();
					break;
				default:
					break;
			}
		}*/
	}
}