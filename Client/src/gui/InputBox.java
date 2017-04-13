package gui;

import NetEvent.Client.CONTENT_MARK;
import NetEvent.eventcom.ContentMessageEvent;
import NetEvent.eventcom.EnterQuestionEvent;
import NetEvent.eventcom.NetEvent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.io.File;
import java.awt.BorderLayout;
import java.awt.Cursor;
import bin.test;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;
import util.Dispatcher;
import util.MyExpression;

public class InputBox extends JPanel implements Dispatcher
{
	private static final Map<Long,InputBox> map=new ConcurrentHashMap<Long,InputBox>();

	public final JTextPane myPane=new JTextPane();
	private final HTMLDocument doc;
	private final JScrollPane myScroll=new JScrollPane(myPane);
	private final HTMLEditorKit kit=new HTMLEditorKit();
	private final UndoManager undoManager;

	private final JPopupMenu textMenu=new JPopupMenu();
	private final JMenuItem copy=new JMenuItem("复制");
	private final JMenuItem cut=new JMenuItem("剪切");
	private final JMenuItem paste=new JMenuItem("粘贴");
	private final JMenuItem undo=new JMenuItem("撤销");
	private final JMenuItem redo=new JMenuItem("恢复");

	private long questionID=-1;
	private Map<Integer,Long> markMap;

	public InputBox()
	{
		myPane.setContentType("text/html");
		//myPane.setEditable(false);
		kit.setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));
		kit.setLinkCursor(new Cursor(Cursor.HAND_CURSOR));
		kit.install(myPane);
		myPane.setEditorKit(kit);
		doc=(HTMLDocument)myPane.getStyledDocument();
		try
		{
			doc.setBase(new URL("file:"+test.MAINPATH));
		} catch (MalformedURLException ex)
		{
			Logger.getLogger(InputBox.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		undoManager=new UndoManager()
		{
			@Override
			public void undoableEditHappened(UndoableEditEvent e)
			{
				undoManager.addEdit(e.getEdit());
			}
		};
		doc.addUndoableEditListener(undoManager);
		
		myPane.addKeyListener(new KeyAdapter()
			{
				@Override
				public void keyPressed(KeyEvent e)
				{
					if(e.getKeyCode()==KeyEvent.VK_TAB)
					{
						myPane.setEditable(false);
						readAndInsertExpression();
						myPane.setEditable(true);
					}
					else if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_ENTER)
					{
						try {
							doc.insertString(myPane.getCaretPosition(), "\n", null);
						} catch (BadLocationException ex) {
							Logger.getLogger(InputBox.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					else if(e.getKeyCode()==KeyEvent.VK_ENTER)
						sendMessage();
					else if(e.isControlDown()
						&&e.getKeyCode()==KeyEvent.VK_Z
						&&undoManager.canUndo())
						undoManager.undo();
					else if(e.isControlDown()
						&&e.getKeyCode()==KeyEvent.VK_Y
						&&undoManager.canRedo())
						undoManager.redo();
						
				}
			});
		setLayout(new BorderLayout());
		add(myScroll, BorderLayout.CENTER);
		markMap=new ConcurrentHashMap<>()	;
	}

	public void bind(long questionID)
	{
		this.questionID=questionID;
		map.put(questionID,this);
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
	
	public void setEditable(boolean able)
	{
		myPane.setEditable(able);
	}
	
	public void readAndInsertExpression()
	{
		MyExpression exp=new MyExpression();
		exp.read(getExpressionAtCaret());
		insertImage(test.PICTPATH+exp.toFile());
	}

	public void insertImage(File f)
	{
		if(!f.exists()) return;
		try
		{
			kit.insertHTML(doc, myPane.getCaretPosition(),
					"<a href='pict:"+f.getPath()+"'><img border='0' src='file:/"+f.getPath()+"'></a>",
					0, 0, HTML.Tag.A);
		} catch (BadLocationException|IOException ex)
		{
			Logger.getLogger(InputBox.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void insertImage(String filepath)
	{
		insertImage(new File(filepath));
	}
	
	public String getExpressionAtCaret()
	{
		int i,begin=0,end=0;
		String str;
		try
		{
			str=doc.getText(0,doc.getLength());
		} catch (BadLocationException ex)
		{
			Logger.getLogger(InputBox.class.getName()).log(Level.SEVERE, null, ex);
			return "";
		}
		if(myPane.getCaretPosition()==str.length()) end=str.length();
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
			try
			{
				doc.replace(begin, end-begin, "", null);
			} catch (BadLocationException ex)
			{
				Logger.getLogger(InputBox.class.getName()).log(Level.SEVERE, null, ex);
			}
			myPane.setCaretPosition(begin);
			//System.out.println(str.substring(begin,end));
			return str.substring(begin,end);
		}
	}

	public void sendMessage()
	{
		myPane.setEditable(false);
		ArrayList<String> pictures=new ArrayList<>();
		int count=0;
		String str=myPane.getText();
		//protect the newlines ('\n')
		str=str.replaceAll("\\n *", "")
				.replaceAll("</p>", "\n")
				//replace the % that user inputted
				.replaceAll("%", "%%");
		//get the path of the images
		Pattern pat=Pattern.compile("<img[^>]*? src=\"file:(.*?)\".*?>");
		Matcher mat=pat.matcher(str);
		while (mat.find())
			pictures.add(mat.group(1));
		if (pictures.isEmpty())
			pictures=null;
		//replace the images with "%>"
		str=str.replaceAll("<img.*?>", "%>")
			.replaceAll("<.*?>", "");
		//figure out the problem about %
		StringBuilder message=new StringBuilder(str);
		for (int i=0; i+1<message.length(); i++)
			if (message.charAt(i)=='%'&&message.charAt(i+1)=='>')
				message=message.replace(i, i+2, "%"+(count++)+" \n");
		//remove the last "</p>"(newline)
		if (message.charAt(message.length()-1)=='\n')
			message.setLength(message.length()-1);
		//send the message
		try
		{
			test.client.sendContent(message.toString(), pictures, questionID,markMap);
		} catch (IOException e)
		{
			System.out.println("消息发送失败");
		}
		finally
		{
			clearMarkMap();
		}
	}
	
	public void sendAudio(String audioFile)
	{
		markMap.put(CONTENT_MARK.AUDIO.getValue(), -1L);
		try
		{
			test.client.sendContent(audioFile, null, questionID,markMap);
		} catch (IOException e)
		{
			System.out.println("消息发送失败");
		}
		finally
		{
			clearMarkMap();
		}
	}
	
	public void sendFile(String filepath)
	{
		markMap.put(CONTENT_MARK.FILE.getValue(), -1L);
		boolean tmpAnonymous=markMap.containsKey(CONTENT_MARK.ANONYMOUS.getValue());
		markMap.remove(CONTENT_MARK.ANONYMOUS.getValue());
		try
		{
			test.client.sendContent(filepath, null, questionID,markMap);
		} catch (IOException e)
		{
			System.out.println("消息发送失败");
		}
		finally
		{
			if(tmpAnonymous) markMap.put(CONTENT_MARK.ANONYMOUS.getValue(), -1L);
			clearMarkMap();
		}
	}
	
	public static void dispatch(NetEvent e)
	{
		switch (e.type)
		{
			case CONTENT_MESSAGE_EVENT:
			{
				ContentMessageEvent ex=(ContentMessageEvent)e;
				if (ex.isSuccess()==true)
					if (ex.isMyself())
						if (ex.isSuccess())
						{
							System.out.println("已发送：\n"+ex.getRecord().getContent());
							map.get(ex.getQuestionID()).myPane.setText("");
							map.get(ex.getQuestionID()).myPane.setEditable(true);
						} else
							System.out.println("发送消息失败。");
				break;
			}
			case ENTER_QUESTION_EVENT:
			{
				EnterQuestionEvent ex=(EnterQuestionEvent)e;
				if(ex.isSuccess())
					map.get(ex.getQuestionMessage().getId()).myPane.setEditable(true);
				else
					System.out.println("进入房间失败。");
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
			if(caret==0) break;
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
			else
				caret--;
		}
		return i;
	}
	
	private void clearMarkMap()
	{
		markMap.remove(CONTENT_MARK.DOUBT.getValue());
		markMap.remove(CONTENT_MARK.FURTHERASK.getValue());
		markMap.remove(CONTENT_MARK.AUDIO.getValue());
		markMap.remove(CONTENT_MARK.FILE.getValue());
	}
}
