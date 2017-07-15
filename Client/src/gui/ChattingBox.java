package gui;

import NetEvent.Client.CONTENT_MARK;
import NetEvent.eventcom.ContentMessageEvent;
import NetEvent.eventcom.EnterQuestionEvent;
import NetEvent.eventcom.NetEvent;
import NetEvent.eventcom.SolvedQuestionEvent;
import NetEvent.messagecom.Record;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.File;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.dao.MainFrame;
import util.AudioTools;
import bin.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import util.Dispatcher;
import util.Settings;
import util.UserInfo;

public class ChattingBox extends JPanel implements Dispatcher
{
	/*
	元素id格式：recordID-MESSAGE_TYPE-labelName-labelIndex
	例如：3-plain-img-2
	*/
	private static final String MESSAGE_TYPE_PLAIN;
	private static final String MESSAGE_TYPE_AUDIO;
	private static final String MESSAGE_TYPE_FILE;
	private static final String MESSAGE_TYPE_ANNOUNCEMENT;
	
	private static final String HYPERLINK_TYPE_USER;
	private static final String HYPERLINK_TYPE_PICTURE;
	private static final String HYPERLINK_TYPE_AUDIO;
	private static final String HYPERLINK_TYPE_FILE;
	
	private static int HISTORY_RECORD_COUNT;
	private static final String HTML_FILE_PATH;
	private static final String HTML_INIT;
	private static String HTML_MESSAGE_TIME;
	private static String HTML_MESSAGE_BUBBLE;
	private static String HTML_MESSAGE_ANNOUNCEMENT;
	private static String HTML_MESSAGE_PICTURE;
	
	private static final String HTML_TAG_SPLIT;
	private static final String HTML_TAG_TIME;
	private static final String HTML_TAG_ANNOUNCEMENT;
	private static final String HTML_TAG_RECORD_ID;
	private static final String HTML_TAG_DIRECTION;
	private static final String HTML_TAG_LEFT_HEAD;
	private static final String HTML_TAG_IMG_PATH;
	private static final String HTML_TAG_RIGHT_HEAD;
	private static final String HTML_TAG_MESSAGE;
	private static final String HTML_TAG_PICT_PATH;
	private static final String HTML_TAG_PICTURE_AT_I;
	private static final String HTML_TAG_I;
	
	private static final String ALIGN_LEFT="left";
	private static final String ALIGN_RIGHT="right";
	
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
	private static final String PROIMGPATH="img_src/";

	public ChattingBox()
	{
		myPane.setContentType("text/html");
		myPane.setEditorKit(kit);
		kit.install(myPane);
		myPane.setEditable(false);
		myPane.setFont(Font.getFont("宋体"));
		//myPane.setEditorKit(kit);
		myPane.setText(HTML_INIT);
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
	public static void unbind(long questionID)
	{
		map.remove(questionID);
	}
	
	public static boolean isExist(long questionID)
	{
		return map.containsKey(questionID);
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
		myPane.setText(HTML_INIT);
		doc=(HTMLDocument)myPane.getStyledDocument();
	}

	public void pushAnnouncement(String anno)
	{
		try
		{
			doc.insertBeforeStart(
					doc.getElement("-1"),
					HTML_MESSAGE_ANNOUNCEMENT.replaceAll(HTML_TAG_ANNOUNCEMENT, anno));
		} catch (IOException|BadLocationException e)
		{
			System.out.println(e);
		}
	}
	
	public void pushMessage(Record msg)
	{
		ArrayList<Record> msgs=new ArrayList<>();
		msgs.add(msg);
		pushMessages(msgs);
	}
	
	public void pushMessages(List<Record> msgs)
	{
		synchronized(records)
		{
			StringBuilder message=new StringBuilder();
			msgs.forEach((Record msg)->message.append(addMessage(msg)));
			try
			{
				doc.insertBeforeStart(doc.getElement("-1"),message.toString());
			} catch (BadLocationException|IOException ex)
			{
				Logger.getLogger(ChattingBox.class.getName()).log(Level.SEVERE, null, ex);
			}
			myPane.setSelectionStart(myPane.getText().length());
		}
	}

	public String addMessage(Record msg)
	{
		boolean ismyself=UserInfo.getMyUserName().equals(msg.getUser());
		String tmpUser=msg.getMarkMap()
					.containsKey(CONTENT_MARK.ANONYMOUS.getValue())?"匿名":msg.getUser();
		String message=msg.getContent().replaceAll("\n", "<br>");
		message=message.replaceAll(HTML_TAG_RECORD_ID, String.valueOf(msg.getRecordID()));
		if (msg.getPictures()!=null)
			for (int i=0; i<msg.getPictures().size(); i++)
				message=message.replaceAll("[^%]%"+i+" ",
					HTML_MESSAGE_PICTURE
					.replaceAll(HTML_TAG_PICTURE_AT_I, msg.getPictureAt(i))
					.replaceAll(HTML_TAG_I, String.valueOf(i)));
		message=message.replaceAll("%%", "%");
		records.add(msg);
		return HTML_MESSAGE_BUBBLE
				.replaceAll(HTML_TAG_RECORD_ID, String.valueOf(msg.getRecordID()))
				.replaceAll(HTML_TAG_DIRECTION, ismyself?ALIGN_RIGHT:ALIGN_LEFT)
				.replaceAll(HTML_TAG_LEFT_HEAD, ismyself?"":getUserHead(tmpUser))
				.replaceAll(HTML_TAG_RIGHT_HEAD, ismyself?getUserHead(tmpUser):"")
				.replaceAll(HTML_TAG_MESSAGE, message);
	}
	
	public void pushAudio(Record msg)
	{
		pushAudio(msg,UserInfo.getMyUserName().equals(msg.getUser()));
	}

	public void pushAudio(Record msg,boolean ismyself)
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
						map.get(ex.getQuestionID()).pushAudio(ex.getRecord(),ex.isMyself());
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
					map.get(ex.getQuestionMessage().getId()).pushMessages(ex.getQuestionMessage().getRecords());
					/*ex.getQuestionMessage().getRecords().forEach(
						(Record r)->
					{
						if (r.getMarkMap().containsKey(CONTENT_MARK.AUDIO.getValue()))
							map.get(ex.getQuestionMessage().getId()).pushAudio(r);
						else if (r.getMarkMap().containsKey(CONTENT_MARK.FILE.getValue()))
							map.get(ex.getQuestionMessage().getId()).pushFile(r);
						else
							map.get(ex.getQuestionMessage().getId()).pushMessage(r);
					});*/
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
	
	static
	{
		HISTORY_RECORD_COUNT=Integer.parseInt(Settings.getProperty(Settings.ChattingBox.HISTORY_RECORD_COUNT.getKey()));
		Settings.bind(Settings.ChattingBox.HISTORY_RECORD_COUNT.getKey(),
				   (String s)->HISTORY_RECORD_COUNT=Integer.parseInt(s));
		HTML_FILE_PATH=Settings.getProperty(Settings.ChattingBox.HTML_FILE_PATH.getKey());
		MESSAGE_TYPE_PLAIN=Settings.getProperty(Settings.ChattingBox.MESSAGE_TYPE_PLAIN.getKey());
		MESSAGE_TYPE_AUDIO=Settings.getProperty(Settings.ChattingBox.MESSAGE_TYPE_AUDIO.getKey());
		MESSAGE_TYPE_FILE=Settings.getProperty(Settings.ChattingBox.MESSAGE_TYPE_FILE.getKey());
		MESSAGE_TYPE_ANNOUNCEMENT=Settings.getProperty(Settings.ChattingBox.MESSAGE_TYPE_ANNOUNCEMENT.getKey());
		
		HYPERLINK_TYPE_USER=Settings.getProperty(Settings.ChattingBox.HYPERLINK_TYPE_USER.getKey());
		HYPERLINK_TYPE_PICTURE=Settings.getProperty(Settings.ChattingBox.HYPERLINK_TYPE_PICTURE.getKey());
		HYPERLINK_TYPE_AUDIO=Settings.getProperty(Settings.ChattingBox.HYPERLINK_TYPE_AUDIO.getKey());
		HYPERLINK_TYPE_FILE=Settings.getProperty(Settings.ChattingBox.HYPERLINK_TYPE_FILE.getKey());
		
		HTML_TAG_SPLIT=Settings.getProperty(Settings.ChattingBox.TAG_SPLIT.getKey());
		HTML_TAG_TIME=Settings.getProperty(Settings.ChattingBox.TAG_TIME.getKey());
		HTML_TAG_ANNOUNCEMENT=Settings.getProperty(Settings.ChattingBox.TAG_ANNOUNCEMENT.getKey());
		HTML_TAG_RECORD_ID=Settings.getProperty(Settings.ChattingBox.TAG_RECORD_ID.getKey());
		HTML_TAG_DIRECTION=Settings.getProperty(Settings.ChattingBox.TAG_DIRECTION.getKey());
		HTML_TAG_LEFT_HEAD=Settings.getProperty(Settings.ChattingBox.TAG_LEFT_HEAD.getKey());
		HTML_TAG_IMG_PATH=Settings.getProperty(Settings.ChattingBox.TAG_IMG_PATH.getKey());
		HTML_TAG_RIGHT_HEAD=Settings.getProperty(Settings.ChattingBox.TAG_RIGHT_HEAD.getKey());
		HTML_TAG_MESSAGE=Settings.getProperty(Settings.ChattingBox.TAG_MESSAGE.getKey());
		HTML_TAG_PICT_PATH=Settings.getProperty(Settings.ChattingBox.TAG_PICT_PATH.getKey());
		HTML_TAG_PICTURE_AT_I=Settings.getProperty(Settings.ChattingBox.TAG_PICTURE_AT_I.getKey());
		HTML_TAG_I=Settings.getProperty(Settings.ChattingBox.TAG_I.getKey());
		
		File f=new File(test.MAINPATH+HTML_FILE_PATH);
		BufferedReader reader;
		StringBuilder html=new StringBuilder();
		String str;
		try
		{
			reader=new BufferedReader(new FileReader(f));
			while(reader.ready())
				html.append(reader.readLine());
			str=html.toString();
		} catch (Exception ex)
		{
			Logger.getLogger(ChattingBox.class.getName()).log(Level.SEVERE, null, ex);
			str=Settings.ChattingBox.getDefaultHTML();
		}
		String[] htmlList=str.split(HTML_TAG_SPLIT);
		HTML_INIT=htmlList[0];
		HTML_MESSAGE_ANNOUNCEMENT=htmlList[1];
		HTML_MESSAGE_TIME=htmlList[2];
		HTML_MESSAGE_BUBBLE=htmlList[3].replaceAll(HTML_TAG_IMG_PATH, PROIMGPATH);
		HTML_MESSAGE_PICTURE=htmlList[4].replaceAll(HTML_TAG_PICT_PATH, PROPICTPATH);
	}

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
					if(HYPERLINK_TYPE_USER.equals(cmd))
					{
						//user.add(abspeak)
						//abspeak.setEnabled(true);
						getInfo.setEnabled(true);
						reset.setEnabled(true);
						userMenu.show(myPane, e.getX(), e.getY());
					}
					else if(HYPERLINK_TYPE_PICTURE.equals(cmd))
					{}
					else if(HYPERLINK_TYPE_AUDIO.equals(cmd))
					{}
					else if(HYPERLINK_TYPE_FILE.equals(cmd))
					{}
					else
					{}
				}
				else
				{
					copy.setEnabled(true);
					reset.setEnabled(true);
					textMenu.show(myPane, e.getX(), e.getY());
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
				if (HYPERLINK_TYPE_USER.equals(cmd))
				{
					//利用currentHyperlink.substring(5)打开个人资料
				}
				else if (HYPERLINK_TYPE_PICTURE.equals(cmd))
				{
					//利用图片框打开大图
				}
				else if (HYPERLINK_TYPE_AUDIO.equals(cmd))
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
				else if (HYPERLINK_TYPE_FILE.equals(cmd))
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
