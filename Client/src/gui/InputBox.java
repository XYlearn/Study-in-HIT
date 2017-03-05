package gui;

import NetEvent.Client;
import NetEvent.Client.CONTENT_MARK;
import NetEvent.eventcom.ContentMessageEvent;
import NetEvent.eventcom.EnterQuestionEvent;
import NetEvent.eventcom.NetEvent;
import NetEvent.messagecom.Record;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.io.File;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Cursor;
import bin.test;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.html.HTMLEditorKit;
import util.Dispatcher;
import util.MyExpression;

public class InputBox extends JPanel implements Dispatcher
{
	private static Queue<InputBox> listenerQueue=new LinkedList<InputBox>();
	private static Map<Long,InputBox> map=new HashMap<Long,InputBox>();

	private final JTextPane myPane=new JTextPane();
	private final JScrollPane myScroll=new JScrollPane(myPane);
	private final HTMLEditorKit kit=new HTMLEditorKit();

	private final JPopupMenu textMenu=new JPopupMenu();
	private final JMenuItem copy=new JMenuItem("复制");
	private final JMenuItem cut=new JMenuItem("剪切");
	private final JMenuItem paste=new JMenuItem("粘贴");

	private long questionID=-1;
	Map<Integer,Long> markMap=new HashMap<Integer,Long>();

	public InputBox()
	{
		myPane.setContentType("text/html");
		//myPane.setEditable(false);
		kit.setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));
		kit.install(myPane);
		myPane.setEditorKit(kit);
		
		myPane.addKeyListener(new KeyAdapter()
				{
					@Override
					public void keyPressed(KeyEvent e)
					{
						//System.out.println("按下了"+e.getKeyCode());
						if(e.getKeyCode()==9)
						{
							MyExpression exp=new MyExpression();
							exp.read(getExpressionAtCaret());
							insertImage(test.PICTPATH+exp.toFile());
						}
					}
				});
		
		this.add(myScroll, BorderLayout.CENTER);
	}

	@Override
	public void setSize(int width, int height)
	{
		myPane.setPreferredSize(new Dimension(width, height));
	}

	public void bind(long questionID)
	{
		this.questionID=questionID;
		map.put(questionID,this);
	}

	public long getQuestionID()
	{
		return questionID;
	}
	
	public void setAnonymous()
	{
		markMap.put(CONTENT_MARK.ANONYMOUS.getValue(), -1l);
	}
	
	public void cancelAnonymous()
	{
		markMap.remove(CONTENT_MARK.ANONYMOUS.getValue());
	}
	
	public void setDoubt(long recordID)
	{
		markMap.put(CONTENT_MARK.DOUBT.getValue(),recordID);
	}
	
	public void cancelDoubt()
	{
		markMap.remove(CONTENT_MARK.DOUBT.getValue());
	}
	
	public void setFurtherAsk(long recordID)
	{
		markMap.put(CONTENT_MARK.FURTHERASK.getValue(), recordID);
	}
	
	public void cancelFurtherAsk()
	{
		markMap.remove(CONTENT_MARK.FURTHERASK.getValue());
	}

	public void insertImage(File f)
	{
		insertImage(f.getPath());
	}

	public void insertImage(String filepath)
	{
		try
		{
			((HTMLDocument)myPane.getStyledDocument())
					.insertAfterEnd(myPane.getStyledDocument()
							.getCharacterElement(myPane.getCaretPosition()),
							"<br>");
			int tmppos=myPane.getCaretPosition();
			myPane.setText(myPane.getText().replaceAll("<br>",
					"</p><p style=\"margin-top: 0\"><img src=\"file:"+filepath+"\">"));
			myPane.setCaretPosition(tmppos+2);
		} catch (Exception e)
		{
			System.out.println("bad position");
		}
	}
	
	public String getExpressionAtCaret()
	{
		int i,begin=0,end=0;
		String str=myPane.getText();
		//protect the newlines ('\n')
		str=         str.replaceAll("\\n *", "")
				.replaceAll("</p>", "\n")
				//keep the image or Chinese character as a character
				.replaceAll("&#[0-9]*?;", "`")
				.replaceAll("<img.*?>", "`")
				//clear the HTML format
				.replaceAll("<.*?>", "")
				.replaceAll("&lt;", "<")
				.replaceAll("&gt;", ">")
				.replaceAll("\r", "");
		//System.out.println(str);
		//System.out.println(str.charAt(myPane.getCaretPosition()));
		for(i=myPane.getCaretPosition();i<str.length();i++)
			if(str.charAt(i)<=32||str.charAt(i)>=127||str.charAt(i)=='`')
			{
				end=i;
				break;
			}
		for(i=myPane.getCaretPosition()-1;i>=0;i--)
			if(str.charAt(i)<=32||str.charAt(i)>=127||str.charAt(i)=='`')
			{
				begin=i+1;
				break;
			}
		if(begin>=end) return "";
		else
		{
			String tmpstr=myPane.getText();
			myPane.setText(tmpstr.substring(0,getHTMLOffsetAtCaret(tmpstr,begin))+
						tmpstr.substring(getHTMLOffsetAtCaret(tmpstr,end)));
			myPane.setCaretPosition(begin);
			return str.substring(begin,end);
		}
	}

	public void sendMessage()
	{
		ArrayList<String> pictures=new ArrayList<>();
		int count=0;
		String str=myPane.getText();
		//protect the newlines ('\n')
		str=str.replaceAll("\\n *", "")
				.replaceAll("</p>", "\n")
				//replace the % that user inputted
				.replaceAll("%", "%%");
		//get the path of the images
		Pattern pat=Pattern.compile("<img[^>]*? src=\".*?([^/]*?)\".*?>");
		Matcher mat=pat.matcher(str);
		while (mat.find())
			pictures.add(mat.group(1));
		if (pictures.isEmpty())
			pictures=null;
		//replace the images with "%>"
		str=str.replaceAll("<img.*?>", "%>")
				//clear the HTML format
				.replaceAll("<.*?>", "");
		//figure out the problem about %
		StringBuilder message=new StringBuilder(str);
		for (int i=0; i+1<message.length(); i++)
			if (message.charAt(i)=='%'&&message.charAt(i+1)=='>')
				message=message.replace(i, i+2, "%"+(count++)+" \n");
		//remove the last "</p>"(newline)
		if (message.charAt(message.length()-1)=='\n')
			message.setLength(message.length()-1);
		//System.out.println(message.toString());
		//send the message
		try
		{
			test.client.sendContent(message.toString(), pictures, questionID,markMap);
		} catch (IOException e)
		{
			System.out.println("网络异常");
		}
		finally
		{
			markMap.clear();
		}
	}
	
	public void sendAudio(String audioFile)
	{
		//set the markMap
		markMap.put(CONTENT_MARK.AUDIO.getValue(), -1l);
		//send the message
		try
		{
			test.client.sendContent(audioFile, null, questionID);
		} catch (IOException e)
		{
			System.out.println("网络异常");
		}
	}
	
	public void sendFile(String filepath)
	{
		//set the markMap
		markMap.put(CONTENT_MARK.FILE.getValue(), -1l);
		//send the message
		try
		{
			test.client.sendContent(filepath, null, questionID);
		} catch (IOException e)
		{
			System.out.println("网络异常");
		}
	}
	
	public static void dispatch(NetEvent e)
	{
		switch (e.type)
		{
			case CONTENT_MESSAGE_EVENT:
			{
				ContentMessageEvent ex=(ContentMessageEvent)e;
				//if(ex.getUser()==getUser())
				if(listenerQueue.peek()==map.get(ex.getQuestionID()))
				{
					System.out.println("InputBox收到成功发送消息反馈，消息已发送。");
					map.get(ex.getQuestionID()).myPane.setText("");
				}
				else System.out.println("InputBox收到成功发送消息反馈，但事件队列顺序有误！");
					break;
			}
			case ENTER_QUESTION_EVENT:
			{
				EnterQuestionEvent ex=(EnterQuestionEvent)e;
				map.get(ex.getQuestionMessage().getId()).myPane.setEditable(true);
					break;
			}
			default:
				break;
		}
	}
	
	public static int getHTMLOffsetAtCaret(String html,int caret)
	{
		int i;
		for(i=0;i<html.length();i++)
		{
			if(html.charAt(i)=='<')
			{
				if((html.charAt(i+1)=='/'&&html.charAt(i+2)=='p'&&html.charAt(i+3)=='>')||
				    (html.charAt(i+1)=='i'&&html.charAt(i+2)=='m'&&html.charAt(i+3)=='g'))
					caret--;
				while(i<html.length()&&html.charAt(i)!='>') i++;
			}
			else if(html.charAt(i)=='&'&&html.charAt(i+1)=='#')
			{
				caret--;
				while(i<html.length()&&html.charAt(i)!=';') i++;
			}
			else if(html.charAt(i)==' '||html.charAt(i)=='\n'||html.charAt(i)=='\r')
				continue;
			else
				caret--;
			if(caret==0) break;
		}
		return i;
	}
}
