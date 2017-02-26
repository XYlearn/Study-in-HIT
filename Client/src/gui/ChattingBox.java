package gui;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.util.ArrayList;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import util.MyMessage;
import util.AudioTools;
import util.stopListener;
import NetEvent.Client;
import bin.test;

public class ChattingBox extends JPanel
{
	private ChattingBoxRightAction rightAction=new ChattingBoxRightAction();
	private ChattingBoxHyperlinkListener hyperlinkAction=new ChattingBoxHyperlinkListener();
	private boolean onHyperlink=false;
	private String mouseHyperlink;
	private String currentHyperlink;

	public JTextPane myPane=new JTextPane();
	private JScrollPane myScroll=new JScrollPane(myPane);
	private StringBuilder html=new StringBuilder("");

	private JPopupMenu textMenu=new JPopupMenu();
	private JPopupMenu userMenu=new JPopupMenu();
	private JMenuItem copy=new JMenuItem("复制");
	private JMenuItem paste=new JMenuItem("粘贴");
	private JMenuItem reset=new JMenuItem("清屏");
	private JMenuItem abspeak=new JMenuItem("禁言");
	private JMenuItem getInfo=new JMenuItem("个人资料");

	private static final String CLASSPATH=ChattingBox.class.getResource("").getPath();
	private static final String PROPATH="file:"+CLASSPATH;
	private static final String PROPICTPATH="file:"+test.PICTPATH;
	private static final String PROFILEPATH="file:"+test.FILEPATH;

	public ChattingBox()
	{
		myPane.setContentType("text/html");
		myPane.setEditable(false);

		myPane.addMouseListener(new ChattingBoxMouseListener());
		myPane.addHyperlinkListener(hyperlinkAction);
		textMenu.add(copy);
		copy.addActionListener(rightAction);
		textMenu.add(reset);
		reset.addActionListener(rightAction);
		userMenu.add(getInfo);
		getInfo.addActionListener(rightAction);
		myPane.add(textMenu);
		myPane.add(userMenu);

		this.add(myScroll,BorderLayout.CENTER);
	}

	public void setSize(int width,int height)
	{
		myPane.setPreferredSize(new Dimension(width,height));
	}

	public void pushMessage(MyMessage msg)
	{
		boolean ismyself=msg.userName=="a";//Personal.username;
		msg.message=msg.message.replaceAll("\n","<br>");
		if(msg.pictures!=null)
			for(int i=0;i<msg.pictures.size();i++)
			{
				if(!(new File(test.PICTPATH+msg.pictures.get(i)).exists()))
					{}//调用网络接口下载图片，下载完成时刷新
				msg.message=msg.message.replaceAll("[^%]%"+i+" ",
					"<a href=\"pict:"+PROPICTPATH+msg.pictures.get(i)+"\">"+
					"<img border=\"0\" src=\""+PROPICTPATH+msg.pictures.get(i)+"\" "+
					"alt=\"正在加载图片\"></a>");
				msg.message=msg.message.replaceAll("%%","%");
			}
		html.append("<p align=\"center\">"+msg.messageTime+"</p>"+
			"<table border=\"0\" white-space=\"0\" "+
			"align=\""+(ismyself?"right":"left")+"\" "+
			"cellspacing=\"0\" cellpadding=\"0\" "+
			"style=\"font-size:0;-webkit-user-select:none;"+
			"-moz-user-select:none;"+
			"-ms-user-select:none;user-select:none;\">"+
			"<tr><td rowspan=\"3\">"+
			(ismyself?"":getUserHead(msg.userName))+
			"</td>"+
			"<td><img src=\""+PROPATH+"bubble_lu.jpg\"></td>"+
			"<td style=\"background-image:url("+PROPATH+"bubble_up.jpg);"+
			"background-repeat:repeat-x;\">&nbsp;</td>"+
			"<td><img src=\""+PROPATH+"bubble_ru.jpg\"></td>"+
			"<td rowspan=\"3\">"+
			(ismyself?getUserHead(msg.userName):"")+
			"</td></tr>"+
			"<tr><td style=\"background-image:url("+PROPATH+"bubble_le.jpg)\">&nbsp;</td>"+
			"<td style=\"-webkit-user-select:text;"+
			"-moz-user-select:text;-ms-user-select:text;"+
			"user-select:text;font-size:12px;\">"+msg.message+"</td>"+
			"<td style=\"background-image:url("+PROPATH+"bubble_ri.jpg)\">&nbsp;</td></tr>"+
			"<tr><td><img src=\""+PROPATH+"bubble_ld.jpg\"></td>"+
			"<td style=\"background-image:url("+PROPATH+"bubble_do.jpg)\">&nbsp;</td>"+
			"<td><img src=\""+PROPATH+"bubble_rd.jpg\"></td></tr></table><br>");
		myPane.setText(html.toString());
		myPane.setSelectionStart(myPane.getText().length());
	}

	public void pushAudio(MyMessage msg)
	{
		msg.message="<a href=\"audi:"+msg.message+"\">"+
			"<img border=\"0\" src=\""+PROPATH+"button_play.gif\"></a>";
		pushMessage(msg);
	}

	public void pushFile(MyMessage msg)
	{
		msg.message="<a href=\"file:"+msg.message+"\">"+
			"[文件]"+msg.message+"</a>";
	}

	private static String getUserHead(String userName)
	{
		return "<a href=\"user:"+userName+"\">"+
			"<img border=\"0\" src=\""+PROPICTPATH+userName+".jpg\"></a>";
	}

	private class ChattingBoxMouseListener implements MouseListener
	{
		ChattingBoxMouseListener(){}
		public void mousePressed(MouseEvent e)
		{
			//System.out.println("pressed!!");
			if(onHyperlink) currentHyperlink=mouseHyperlink;
			if(e.getButton()==MouseEvent.BUTTON3)
			{
				//判断用户身份
				if(onHyperlink)
				{
					String cmd=currentHyperlink.substring(0,4);
					if(cmd.equals("user"))
					{
						//user.add(abspeak)
						//abspeak.setEnabled(true);
						getInfo.setEnabled(true);
						reset.setEnabled(true);
						userMenu.show(ChattingBox.this,e.getX(),e.getY());
					}
					else if(cmd.equals("pict"))
					{

					}
					else if(cmd.equals("audi"))
					{

					}
					else if(cmd.equals("file"))
					{

					}
				}
				else
				{
					copy.setEnabled(true);
					reset.setEnabled(true);
					textMenu.show(ChattingBox.this,e.getX(),e.getY());
				}
			}
		}
		public void mouseReleased(MouseEvent e){}
		public void mouseClicked(MouseEvent e)
		{
			//System.out.println("Clicked!!");
		}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
	}

	private class ChattingBoxRightAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("复制"))
			{
				if(myPane.getSelectionStart()!=myPane.getSelectionEnd())
					myPane.copy();
			}
			else if(e.getActionCommand().equals("清屏"))
			{
				html.delete(0,html.length());
				myPane.setText("");
			}
		}
	}

	private class ChattingBoxHyperlinkListener implements HyperlinkListener
	{
		public void hyperlinkUpdate(HyperlinkEvent e)
		{
			if(e.getEventType()==HyperlinkEvent.EventType.ENTERED)
			{
				//System.out.println("HyperlinkEntered!!");
				onHyperlink=true;
				mouseHyperlink=e.getDescription();
			}
			else if(e.getEventType()==HyperlinkEvent.EventType.EXITED)
			{
				//System.out.println("HyperlinkExited!!");
				onHyperlink=false;
			}
			else
			{
				//System.out.println("HyperlinkActivated!!");
				String cmd=currentHyperlink.substring(0,4);
				if(cmd.equals("user"))
				{
					//利用currentHyperlink.substring(5)打开个人资料
				}
				else if(cmd.equals("pict"))
				{
					//利用图片框打开大图
				}
				else if(cmd.equals("audi"))
				{
					//System.out.println("激活超链接："+currentHyperlink);
					String tmpstr;
					if(AudioTools.isPlaying())
					{
						tmpstr="<a href=\"audi:"+
							AudioTools.getCurrentPlayingAudio()+"\">"+
							"<img src=\""+PROPATH+"button_stop.gif";
						int tmpindex=html.lastIndexOf(tmpstr);
						tmpindex+=tmpstr.length()-8;
						html.replace(tmpindex,tmpindex+4,"play");
					}
					tmpstr="<a href=\""+currentHyperlink+"\">"+
						"<img src=\""+PROPATH+"button_play.gif";
					int tmpindex=html.lastIndexOf(tmpstr);
					tmpindex+=tmpstr.length()-8;
					html.replace(tmpindex,tmpindex+4,"stop");
					myPane.setText(html.toString());
					AudioTools.playAudio(
						AudioTools.CLASSPATH+currentHyperlink.substring(5),
						(String currentPlayingAudio)->
						{
							String tmps="<a href=\"audi:"+
								currentPlayingAudio+"\">"+
								"<img src=\""+PROPATH+"button_stop.gif";
							int tmpi=html.lastIndexOf(tmps);
							tmpi+=tmps.length()-8;
							html.replace(tmpi,tmpi+4,"play");
							myPane.setText(html.toString());
						}
					);
				}
				else if(cmd.equals("file"))
				{
					File f;
					if((f=new File(test.FILEPATH+currentHyperlink.substring(5))).exists())
					{
						try
						{
							java.awt.Desktop.getDesktop().open(f);
						}
						catch(Exception ex)
						{
							System.out.println("无法打开文件："+ex);
						}
						return;
					}
					//下载文件
				}
			}
		}
	}
}