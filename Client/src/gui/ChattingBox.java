package gui;

import NetEvent.eventcom.NetEvent;
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
import java.awt.Dimension;
import util.AudioTools;
import bin.test;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import util.Dispatcher;

public class ChattingBox extends JPanel implements Dispatcher
{
	private static final Queue<ChattingBox> listenerQueue=new LinkedList<ChattingBox>();
	private static final Map<Long,ChattingBox> map=new HashMap<Long,ChattingBox>();
	
	private final ChattingBoxRightAction rightAction=new ChattingBoxRightAction();
	private final ChattingBoxHyperlinkListener hyperlinkAction=new ChattingBoxHyperlinkListener();
	private boolean onHyperlink=false;
	private String mouseHyperlink;
	private String currentHyperlink;

	private HTMLEditorKit kit=new HTMLEditorKit();
	private HTMLDocument doc=new HTMLDocument();
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

	private static final String CLASSPATH=ChattingBox.class.getResource("").getPath();
	private static final String PROPATH="file:"+CLASSPATH;
	private static final String PROPICTPATH="file:"+test.PICTPATH;
	private static final String PROFILEPATH="file:"+test.FILEPATH;

	public ChattingBox()
	{
		myPane.setContentType("text/html");
		myPane.setEditorKit(kit);
		kit.install(myPane);
		myPane.setEditable(false);
		myPane.setFont(Font.getFont("宋体"));
		//myPane.setEditorKit(kit);
		myPane.setContentType("text/html;charset=unicode");
		myPane.setText("<html><p id='0'></p></html>");
		doc=(HTMLDocument)myPane.getStyledDocument();

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

		this.add(myScroll, BorderLayout.CENTER);
	}
	
	public void bind(long questionID)
	{
		this.questionID=questionID;
		map.put(questionID, this);
	}
	
	public void requestQuestionRecord()
	{
		try
		{
			synchronized(listenerQueue)
			{
				listenerQueue.add(this);
				test.client.enterQuestion(questionID);
			}
		} catch (IOException ex)
		{
			System.out.println(ex);
		}
	}

	@Override
	public void setSize(int width, int height)
	{
		myPane.setPreferredSize(new Dimension(width, height));
	}

	public void clear()
	{
		myPane.setText("<html><p id='0'></p></html>");
		doc=(HTMLDocument)myPane.getStyledDocument();
	}

	public void pushMessage(Record msg)
	{
		Element e=doc.getElement("0");
		boolean ismyself="a".equals(msg.getUser());//Personal.username;
		String message=msg.getContent().replaceAll("\n", "<br>");
		if (msg.getPictures()!=null)
			for (int i=0; i<msg.getPictures().size(); i++)
			{
				//if(!(new File(test.PICTPATH+msg.pictures.get(i)).exists()))
				//{}//调用网络接口下载图片，下载完成时刷新
				message=message.replaceAll("[^%]%"+i+" ",
						"<a href=\"pict:"+PROPICTPATH+msg.getPictureAt(i)+"\">"
						+"<img border=\"0\" src=\""+PROPICTPATH+msg.getPictureAt(i)+"\" "
						+"alt=\"正在加载图片\"></a>");
			}
		message=message.replaceAll("%%", "%");
		try
		{
			doc.insertBeforeStart(e, "<p align=\"center\">"+msg.getTime()+"</p>"
					+"<table border=\"0\" white-space=\"0\" "
					+"align=\""+(ismyself?"right":"left")+"\" "
					+"cellspacing=\"0\" cellpadding=\"0\" "
					+"style=\"font-size:0;-webkit-user-select:none;"
					+"-moz-user-select:none;"
					+"-ms-user-select:none;user-select:none;\">"
					+"<tr><td rowspan=\"3\">"
					+(ismyself?"":getUserHead(msg.getUser()))
					+"</td>"
					+"<td><img src=\""+PROPATH+"bubble_lu.jpg\"></td>"
					+"<td style=\"background-image:url("+PROPATH+"bubble_up.jpg);"
					+"background-repeat:repeat-x;\">&nbsp;</td>"
					+"<td><img src=\""+PROPATH+"bubble_ru.jpg\"></td>"
					+"<td rowspan=\"3\">"
					+(ismyself?getUserHead(msg.getUser()):"")
					+"</td></tr>"
					+"<tr><td style=\"background-image:url("+PROPATH+"bubble_le.jpg)\">&nbsp;</td>"
					+"<td style=\"-webkit-user-select:text;"
					+"-moz-user-select:text;-ms-user-select:text;"
					+"user-select:text;font-size:12px;\">"+message+"</td>"
					+"<td style=\"background-image:url("+PROPATH+"bubble_ri.jpg)\">&nbsp;</td></tr>"
					+"<tr><td><img src=\""+PROPATH+"bubble_ld.jpg\"></td>"
					+"<td style=\"background-image:url("+PROPATH+"bubble_do.jpg)\">&nbsp;</td>"
					+"<td><img src=\""+PROPATH+"bubble_rd.jpg\"></td></tr></table><br>");
		}catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		//myPane.setText(html.toString());

		myPane.setSelectionStart(myPane.getText().length());
	}

	public void pushAudio(Record msg)
	{
		Record tmpRecord=new Record(
				msg.getUser(),
				"<a href=\"audi:"+msg.getContent()+"\">"
				+"<img border=\"0\" src=\""+PROPATH+"button_play.gif\"></a>",
				msg.getTime(),
				msg.getRecordID(),
				msg.getPictures(),
				msg.getMarkMap());
		pushMessage(tmpRecord);
	}

	public void pushFile(Record msg)
	{
		Record tmpRecord=new Record(
				msg.getUser(),
				"<a href=\"file:"+msg.getContent()+"\">"
				+"[文件]"+msg.getContent()+"</a>",
				msg.getTime(),
				msg.getRecordID(),
				msg.getPictures(),
				msg.getMarkMap());
		pushMessage(tmpRecord);
	}
	
	public static void dispatch(NetEvent e)
	{
		switch (e.type)
		{
			case CONTENT_MESSAGE_EVENT:
			{
				break;
			}
			case ENTER_QUESTION_EVENT:
			{
				break;
			}
			case SOLVED_QUESTION_EVENT:
			{
				break;
			}
			default:
				break;
		}
	}

	private static String getUserHead(String userName)
	{
		return "<a href=\"user:"+userName+"\">"
				+"<img border=\"0\" src=\""+PROPICTPATH+userName+".jpg\"></a>";
	}

	private class ChattingBoxMouseListener implements MouseListener
	{

		ChattingBoxMouseListener(){}

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
					if (cmd.equals("user"))
					{
						//user.add(abspeak)
						//abspeak.setEnabled(true);
						getInfo.setEnabled(true);
						reset.setEnabled(true);
						userMenu.show(ChattingBox.this, e.getX(), e.getY());
					} else if (cmd.equals("pict"))
					{

					} else if (cmd.equals("audi"))
					{

					} else if (cmd.equals("file"))
					{

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
				{
					//System.out.println("激活超链接："+currentHyperlink);
					if (AudioTools.isPlaying())
						try
						{
							doc.setInnerHTML(e.getSourceElement(),
										"<img src=\""+PROPICTPATH+"button_play.gif");
						}
						catch (Exception ex)
						{
							System.out.println(ex);
						}
					else
						try
						{
							doc.setInnerHTML(e.getSourceElement(),
										"<img src=\""+PROPICTPATH+"button_stop.gif");
							AudioTools.playAudio(
								AudioTools.CLASSPATH+currentHyperlink.substring(5),
								(String currentPlayingAudio)->
								{
									try
									{
										doc.setInnerHTML(e.getSourceElement(),
												"<img src=\""+PROPICTPATH+"button_play.gif");
									}
									catch (Exception ex)
									{
										System.out.println(ex);
									}
								});
						}
						catch (Exception ex)
						{
							System.out.println(ex);
						}
				} else if (cmd.equals("file"))
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
