package gui;

import NetEvent.Client.CONTENT_MARK;
import NetEvent.eventcom.ContentMessageEvent;
import NetEvent.eventcom.EnterQuestionEvent;
import NetEvent.eventcom.NetEvent;
import NetEvent.eventcom.SolvedQuestionEvent;
import NetEvent.messagecom.Record;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.File;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import util.AudioTools;
import bin.test;
import java.awt.Font;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import util.Dispatcher;
import util.UserInfo;

public class ChattingBox extends JPanel implements Dispatcher
{
	/*
	元素id格式：recordID-MESSAGE_TYPE-labelName-labelIndex
	例如：3-plain-img-2
	*/
	public static final String MESSAGE_TYPE_PLAIN="plain";
	public static final String MESSAGE_TYPE_AUDIO="audio";
	public static final String MESSAGE_TYPE_FILE="file";
	public static final String MESSAGE_TYPE_ANNOUNCEMENT="anno";
	
	private static final Map<Long, ChattingBox> map=new ConcurrentHashMap<Long, ChattingBox>();

	private final ChattingBoxRightAction rightAction=new ChattingBoxRightAction();
	private final ChattingBoxHyperlinkListener hyperlinkAction=new ChattingBoxHyperlinkListener();
	private boolean onHyperlink=false;
	private String mouseHyperlink;
	private String currentHyperlink;

	private HTMLEditorKit kit=new HTMLEditorKit();
	private HTMLDocument doc;
	public JTextPane myPane=new JTextPane();
	private final JScrollPane myScroll=new JScrollPane(myPane);
	private final ArrayList<Record> records=new ArrayList<>();

	private final JPopupMenu textMenu=new JPopupMenu();
	private final JPopupMenu userMenu=new JPopupMenu();
	private final JMenuItem copy=new JMenuItem("复制");
	private final JMenuItem paste=new JMenuItem("粘贴");
	private final JMenuItem reset=new JMenuItem("清屏");
	private final JMenuItem abspeak=new JMenuItem("禁言");
	private final JMenuItem getInfo=new JMenuItem("个人资料");

	private long questionID=-1;

	private static final String PROPICTPATH="pictures/";
	private static final String PROFILEPATH="files/";

	public ChattingBox()
	{
		myPane.setContentType("text/html");
		myPane.setEditorKit(kit);
		kit.install(myPane);
		myPane.setEditable(false);
		myPane.setFont(Font.getFont("宋体"));
		//myPane.setEditorKit(kit);
		myPane.setContentType("text/html;charset=unicode");
		myPane.setText("<html><p id='-1'></p></html>");
		doc=(HTMLDocument)myPane.getStyledDocument();
		try
		{
			doc.setBase(new URL("file:"+test.MAINPATH));
		} catch (MalformedURLException ex)
		{
			Logger.getLogger(InputBox.class.getName()).log(Level.SEVERE, null, ex);
		}

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

		setLayout(new BorderLayout());
		add(myScroll, BorderLayout.CENTER);
	}

	public void bind(long questionID)
	{
		this.questionID=questionID;
		map.put(questionID, this);
	}
	
	public void unbind()
	{
		map.remove(this.questionID);
	}

	public void requestQuestionRecord()
	{
		try
		{
			test.client.enterQuestion(questionID);
		} catch (IOException ex)
		{
			System.out.println(ex);
		}
	}

	public void clear()
	{
		myPane.setText("<html><p id='0'></p></html>");
		doc=(HTMLDocument)myPane.getStyledDocument();
	}

	public void pushAnnouncement(String anno)
	{
		try
		{
			doc.insertBeforeStart(
					doc.getElement("-1"),
					"<p align='center'>"+anno+"</p>");
		} catch (IOException|BadLocationException e)
		{
			System.out.println(e);
		}
	}

	public void pushMessage(Record msg)
	{
		synchronized (records)
		{
			Element e=doc.getElement("-1");
			boolean ismyself=UserInfo.getMyUserName().equals(msg.getUser());
			String tmpUser=msg.getMarkMap()
					.containsKey(CONTENT_MARK.ANONYMOUS.getValue())?"匿名":msg.getUser();
			String message=msg.getContent().replaceAll("\n", "<br>");
			if (msg.getPictures()!=null)
				for (int i=0; i<msg.getPictures().size(); i++)
					//if(!(new File(test.PICTPATH+msg.pictures.get(i)).exists()))
					//{}//调用网络接口下载图片，下载完成时刷新
					message=message.replaceAll("[^%]%"+i+" ",
							"<a href='pict:"+PROPICTPATH+msg.getPictureAt(i)+"'>"
							+"<img id='"+msg.getRecordID()+"-"+i
							+"' border='0' width=160px height=100px "
							+"src='"+PROPICTPATH+msg.getPictureAt(i)+"' "
							+"alt='正在加载图片'></a>");
			message=message.replaceAll("%%", "%");
			try
			{
				doc.insertBeforeStart(e, "<p align='center'>"+msg.getTime()+"</p>"
						+"<table id='"+msg.getRecordID()+"' border='0' white-space='0' "
						+"align='"+(ismyself?"right":"left")+"' "
						+"cellspacing='0' cellpadding='0' "
						+"style='font-size:0;-webkit-user-select:none;"
						+"-moz-user-select:none;"
						+"-ms-user-select:none;user-select:none;'>"
						+"<tr><td id='"+msg.getRecordID()+"-leftHead' rowspan='3'>"
						+(ismyself?"":getUserHead(tmpUser))
						+"</td>"
						+"<td><img src='"+test.IMGPATH+"bubble_lu.jpg'></td>"
						+"<td style='background-image:url("+test.IMGPATH+"bubble_up.jpg);"
						+"background-repeat:repeat-x;'>&nbsp;</td>"
						+"<td><img src='"+test.IMGPATH+"bubble_ru.jpg'></td>"
						+"<td id='"+msg.getRecordID()+"-rightHead' rowspan='3'>"
						+(ismyself?getUserHead(msg.getUser()):"")
						+"</td></tr>"
						+"<tr><td style='background-image:url("+test.IMGPATH+"bubble_le.jpg)'>&nbsp;</td>"
						+"<td id='"+msg.getRecordID()+"-message' style='-webkit-user-select:text;"
						+"-moz-user-select:text;-ms-user-select:text;"
						+"user-select:text;font-size:12px;'>"+message+"</td>"
						+"<td style='background-image:url("+test.IMGPATH+"bubble_ri.jpg)'>&nbsp;</td></tr>"
						+"<tr><td><img src='"+test.IMGPATH+"bubble_ld.jpg'></td>"
						+"<td style='background-image:url("+test.IMGPATH+"bubble_do.jpg)'>&nbsp;</td>"
						+"<td><img src='"+test.IMGPATH+"bubble_rd.jpg'></td></tr></table><br>");
			} catch (IOException|BadLocationException ex)
			{
				System.out.println(ex);
			}
			records.add(msg);
		}
		myPane.setSelectionStart(myPane.getText().length());
	}

	public void pushAudio(Record msg)
	{
		synchronized (records)
		{
			Record tmpRecord=new Record(
					msg.getUser(),
					"<a id='"+msg.getRecordID()+"-audio' "
					+"href='audi:"+msg.getContent()+"'>"
					+"<img border='0' "
					+"src='"+test.IMGPATH+"button_play.gif'></a>",
					msg.getTime(),
					msg.getRecordID(),
					msg.getPictures(),
					msg.getMarkMap());
			pushMessage(tmpRecord);
		}
	}

	public void pushFile(Record msg)
	{
		synchronized (records)
		{
			Record tmpRecord=new Record(
					msg.getUser(),
					"<a id='"+msg.getRecordID()+"-file' href='file:"+msg.getContent()+"'>"
					+"[文件]"+msg.getContent()+"</a>",
					msg.getTime(),
					msg.getRecordID(),
					msg.getPictures(),
					msg.getMarkMap());
			pushMessage(tmpRecord);
		}
	}

	public static void dispatch(NetEvent e)
	{
		switch (e.type)
		{
			case CONTENT_MESSAGE_EVENT:
			{
				ContentMessageEvent ex=(ContentMessageEvent)e;
				if (ex.isSuccess())
					if (ex.getRecord().getMarkMap().containsKey(CONTENT_MARK.AUDIO.getValue()))
						map.get(ex.getQuestionID()).pushAudio(ex.getRecord());
					else if (ex.getRecord().getMarkMap().containsKey(CONTENT_MARK.FILE.getValue()))
						map.get(ex.getQuestionID()).pushFile(ex.getRecord());
					else
						map.get(ex.getQuestionID()).pushMessage(ex.getRecord());
				break;
			}
			case ENTER_QUESTION_EVENT:
			{
				EnterQuestionEvent ex=(EnterQuestionEvent)e;
				if (ex.isSuccess())
				{
					map.get(ex.getQuestionMessage().getId()).pushMessage(
						new Record(
							ex.getQuestionMessage().getOwner(),
							ex.getQuestionMessage().getStem()+"\n"
							+ex.getQuestionMessage().getAddition(),
							ex.getQuestionMessage().getTime(),
							0,
							ex.getQuestionMessage().getAdditionpic(), //PROBLEM!!!
							new ConcurrentHashMap()));
					ex.getQuestionMessage().getRecords().forEach(
						(Record r)->
					{
						if (r.getMarkMap().containsKey(CONTENT_MARK.AUDIO.getValue()))
							map.get(ex.getQuestionMessage().getId()).pushAudio(r);
						else if (r.getMarkMap().containsKey(CONTENT_MARK.FILE.getValue()))
							map.get(ex.getQuestionMessage().getId()).pushFile(r);
						else
							map.get(ex.getQuestionMessage().getId()).pushMessage(r);
					});
				}
				break;
			}
			case SOLVED_QUESTION_EVENT:
			{
				SolvedQuestionEvent ex=(SolvedQuestionEvent)e;
				if (ex.isSuccess())
					map.get(ex.getQuestionID()).pushAnnouncement(
							"该问题已被提问者标记为【已解决】");
				break;
			}
			default:
				break;
		}
	}

	private static String getUserHead(String userName)
	{
		if (!"匿名".equals(userName))
		{
			try
			{
				UserInfo.getPicURL(userName);
			} catch (IOException ex)
			{
				System.out.println("Failed getting userhead.");
				System.out.println(ex);
			}
		}
		return "<a href='user:"+userName+"'>"
				+"<img border='0' src='"+PROPICTPATH+userName+".jpg'></a>";
	}
	
	/*private static Element insertBubbledMessage(Record msg)
	{
		
	}*/

	private class ChattingBoxMouseListener implements MouseListener
	{

		ChattingBoxMouseListener(){}

		@Override
		public void mousePressed(MouseEvent e)
		{
			//System.out.println("pressed!!");
			if (onHyperlink)
				currentHyperlink=mouseHyperlink;
			if (e.getButton()==MouseEvent.BUTTON3)
				//判断用户身份
				if (onHyperlink)
				{
					String cmd=currentHyperlink.substring(0, 4);
				switch (cmd)
				{
					case "user":
						//user.add(abspeak)
						//abspeak.setEnabled(true);
						getInfo.setEnabled(true);
						reset.setEnabled(true);
						userMenu.show(ChattingBox.this, e.getX(), e.getY());
						break;
					case "pict":
						break;
					case "audi":
						break;
					case "file":
						break;
					default:
						break;
				}
				} else
				{
					copy.setEnabled(true);
					reset.setEnabled(true);
					textMenu.show(ChattingBox.this, e.getX(), e.getY());
				}
		}

		public void mouseReleased(MouseEvent e)
		{
		}

		public void mouseClicked(MouseEvent e)
		{
			//System.out.println("Clicked!!");
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}
	}

	private class ChattingBoxRightAction implements ActionListener
	{

		public void actionPerformed(ActionEvent e)
		{
			if (e.getActionCommand().equals("复制"))
			{
				if (myPane.getSelectionStart()!=myPane.getSelectionEnd())
					myPane.copy();
			} else if (e.getActionCommand().equals("清屏"))
				clear();
		}
	}

	private class ChattingBoxHyperlinkListener implements HyperlinkListener
	{

		public void hyperlinkUpdate(HyperlinkEvent e)
		{
			if (e.getEventType()==HyperlinkEvent.EventType.ENTERED)
			{
				//System.out.println("HyperlinkEntered!!");
				onHyperlink=true;
				mouseHyperlink=e.getDescription();
			} else if (e.getEventType()==HyperlinkEvent.EventType.EXITED)
				//System.out.println("HyperlinkExited!!");
				onHyperlink=false;
			else
			{
				//System.out.println("HyperlinkActivated!!");
				String cmd=currentHyperlink.substring(0, 4);
				if (cmd.equals("user"))
				{
					//利用currentHyperlink.substring(5)打开个人资料
				} else if (cmd.equals("pict"))
				{
					//利用图片框打开大图
				} else if (cmd.equals("audi"))
					//System.out.println("激活超链接："+currentHyperlink);
					if (AudioTools.isPlaying())
						try
						{
							doc.setInnerHTML(e.getSourceElement(),
									"<img src='"+PROPICTPATH+"button_play.gif'>");
						} catch (Exception ex)
						{
							System.out.println(ex);
						}
					else
						try
						{
							doc.setInnerHTML(e.getSourceElement(),
									"<img src='"+PROPICTPATH+"button_stop.gif'>");
							AudioTools.playAudio(
									test.AUDIOPATH+currentHyperlink.substring(5),
									(String currentPlayingAudio)->
							{
								try
								{
									doc.setInnerHTML(e.getSourceElement(),
											"<img src='"+PROPICTPATH+"button_play.gif'>");
								} catch (Exception ex)
								{
									System.out.println(ex);
								}
							});
						} catch (Exception ex)
						{
							System.out.println(ex);
						}
				else if (cmd.equals("file"))
				{
					File f;
					if ((f=new File(test.FILEPATH+currentHyperlink.substring(5))).exists())
					{
						try
						{
							java.awt.Desktop.getDesktop().open(f);
						} catch (Exception ex)
						{
							System.out.println("无法打开文件："+ex);
						}
						return;
					}
					try
					{
						//下载文件
						test.client.downloadFile(currentHyperlink.substring(5));
					} catch (IOException ex)
					{
						System.out.println(ex);
					}
				}
			}
		}
	}
}
