package bin;

import javax.swing.JFrame;
import java.util.Scanner;
import NetEvent.Client;
import gui.ChattingBox;
import gui.InputBox;
import java.awt.BorderLayout;

public class test
{

	public static final String MAINPATH=test.class.getResource("").getPath()
			.substring(0, test.class.getResource("").getPath().length()-4);
	public static final String PICTPATH=MAINPATH+"pictures/";
	public static final String FILEPATH=MAINPATH+"files/";
	public static Client client=new Client();

	public static final void main()
	{
		/*client.start();
		synchronized(client)
		{
			try
			{
				client.wait();
			}catch(Exception e)
			{
				System.out.println("网络连接错误");
				System.exit(0);
			}
		}*/
		JFrame f=new JFrame("Hello, world!");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//ChattingBox c=new ChattingBox();
		InputBox c=new InputBox();
		c.setQuestionID("1");
		f.getContentPane().add(c, BorderLayout.CENTER);
		f.setVisible(true);
		//c.setSize(500,550);
		c.setSize(500, 300);
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
					try//&#21834;&#22909;
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