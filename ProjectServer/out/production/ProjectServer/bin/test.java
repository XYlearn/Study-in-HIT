package bin;

import javax.swing.JFrame;
import java.util.ArrayList;
import util.MyMessage;
//import gui.ChattingBox;
import gui.InputBox;
import java.util.Scanner;
import util.AudioTools;
//import NetEvent.Client;
import java.awt.Dimension;
import java.awt.BorderLayout;
import org.jsoup.Jsoup;

public class test
{
	public static final String MAINPATH=test.class.getResource("").getPath()
		.substring(0,test.class.getResource("").getPath().length()-4);
	public static final String PICTPATH=MAINPATH+"pictures/";
	public static final String FILEPATH=MAINPATH+"files/";
	//public static final Client client=new Client();
	
	public static final void main()
	{
		JFrame f=new JFrame("Hello, world!");
		//ChattingBox c=new ChattingBox();
		InputBox box=new InputBox();
		f.getContentPane().add(box,BorderLayout.CENTER);
		f.setVisible(true);
		//c.setSize(500,550);
		box.setSize(500,300);
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
		String cmd,tmp=null;
		while(s.hasNext())
		{
			cmd=s.nextLine();
			if(cmd.equals("exit"))
				return;
			else if(cmd.equals("show"))
			{
				System.out.println(box.myPane.getText());
				tmp=box.myPane.getText();
			}
			else if(cmd.equals("get"))
				System.out.println(box.myPane.getCaretPosition());
			else if(cmd.equals("send"))
				box.sendMessage();
			else if(cmd.equals("reset"))
				tmp=box.myPane.getText();
			else
			{
				tmp=tmp.replaceAll(cmd,"");
				System.out.println(tmp);
			}
		}
	}
}