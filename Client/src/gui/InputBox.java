package gui;

import NetEvent.NetEvent;
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
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.html.HTMLEditorKit;
import util.MyExpression;

public class InputBox extends JPanel
{
	//private ChattingBoxRightAction rightAction=new ChattingBoxRightAction();
	private static Queue<InputBox> listenerQueue=new LinkedList<InputBox>();

	public JTextPane myPane=new JTextPane();
	private final JScrollPane myScroll=new JScrollPane(myPane);
	private final HTMLEditorKit kit=new HTMLEditorKit();

	private final JPopupMenu textMenu=new JPopupMenu();
	private final JMenuItem copy=new JMenuItem("复制");
	private final JMenuItem cut=new JMenuItem("剪切");
	private final JMenuItem paste=new JMenuItem("粘贴");

	private String questionID="";

	public InputBox()
	{
		myPane.setContentType("text/html");
		myPane.setEditable(true);
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

	public void setQuestionID(String qID)
	{
		questionID=qID;
	}

	public String getQuestionID()
	{
		return questionID;
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
		//clear the textpane
		myPane.setText("");
		//send the message
		try
		{
			test.client.sendContent(message.toString(), pictures, questionID);
		} catch (IOException e)
		{
			System.out.println("网络异常");
		}
	}
	
	public static void dispatch(NetEvent e)
	{
		
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
