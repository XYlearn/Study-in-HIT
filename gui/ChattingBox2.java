package gui;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTML.Tag;
import java.awt.Dimension;
import java.util.ArrayList;
import java.io.File;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import util.MyMessage;

public class ChattingBox extends JPanel
{
	//private ChattingBoxRightAction rightAction=new ChattingBoxRightAction();
	//private ChattingBoxHyperlinkListener hyperlinkAction=new ChattingBoxHyperlinkListener();
	private boolean onHyperlink=false;
	private String mouseHyperlink;
	private String currentHyperlink;
	private int messageNumber;

	public JTextPane myPane=new JTextPane();
	private JScrollPane myScroll=new JScrollPane(myPane);
	private HTMLEditorKit kit=new HTMLEditorKit();
	private HTMLDocument html=(HTMLDocument)kit.createDefaultDocument();

	private JPopupMenu textMenu=new JPopupMenu();
	private JPopupMenu userMenu=new JPopupMenu();
	private JMenuItem copy=new JMenuItem("复制");
	private JMenuItem paste=new JMenuItem("粘贴");
	private JMenuItem reset=new JMenuItem("清屏");
	private JMenuItem abspeak=new JMenuItem("禁言");
	private JMenuItem getInfo=new JMenuItem("个人资料");

	private static final String CLASSPATH=ChattingBox.class.getResource("").getPath();
	private static final String PATH="file:"+CLASSPATH;

	public ChattingBox()
	{
		myPane.setPreferredSize(new Dimension(500,550));
		myPane.setContentType("text/html");
		myPane.setEditable(false);
		myPane.setDocument(html);
		myPane.setEditorKit(kit);
		//kit.install(myPane);
		messageNumber=0;

		/*myPane.addMouseListener(new ChattingBoxMouseListener());
		myPane.addHyperlinkListener(hyperlinkAction);
		textMenu.add(copy);
		copy.addActionListener(rightAction);
		textMenu.add(reset);
		reset.addActionListener(rightAction);
		userMenu.add(getInfo);
		getInfo.addActionListener(rightAction);
		myPane.add(textMenu);
		myPane.add(userMenu);*/

		this.add(myScroll);
		initMessage("那个啥");
	}

	public void initMessage(String msg)
	{
		boolean ismyself=true;
		try
		{
			html.insertBeforeEnd(html.getDefaultRootElement()
				.getElement(0)
				.getElement(0),
				"<table id=\"msg0\" border=\"0\" white-space=\"0\" "+
				"align=\""+(ismyself?"right":"left")+"\" "+
				"cellspacing=\"0\" cellpadding=\"0\" "+
				"style=\"font-size:0;-webkit-user-select:none;"+
				"-moz-user-select:none;"+
				"-ms-user-select:none;user-select:none;\">"+
				"<tr><td rowspan=\"3\">"+
				(ismyself?"":getUserHead("ask"))+
				"</td>"+
				"<td><img src=\""+PATH+"bubble_lu.jpg\"></td>"+
				"<td style=\"background-image:url("+PATH+"bubble_up.jpg);"+
				"background-repeat:repeat-x;\">&nbsp;</td>"+
				"<td><img src=\""+PATH+"bubble_ru.jpg\"></td>"+
				"<td rowspan=\"3\">"+
				(ismyself?getUserHead("ask"):"")+
				"</td></tr>"+
				"<tr><td style=\"background-image:url("+PATH+"bubble_le.jpg)\">&nbsp;</td>"+
				"<td style=\"-webkit-user-select:text;"+
				"-moz-user-select:text;-ms-user-select:text;"+
				"user-select:text;font-size:12px;\">"+msg+"</td>"+
				"<td style=\"background-image:url("+PATH+"bubble_ri.jpg)\">&nbsp;</td></tr>"+
				"<tr><td><img src=\""+PATH+"bubble_ld.jpg\"></td>"+
				"<td style=\"background-image:url("+PATH+"bubble_do.jpg)\">&nbsp;</td>"+
				"<td><img src=\""+PATH+"bubble_rd.jpg\"></td></tr></table>");
			System.out.println(myPane.getText());
			System.out.println(html.getDefaultRootElement()
				.getElement(0)
				.getElement(0));
			html.insertBeforeEnd(html.getDefaultRootElement()
				.getElement(0)
				.getElement(0),"<br>");
			messageNumber++;
		}
		catch(Exception e)
		{
			System.out.println("error!");
		}
	}

	public void pushMessage(String msg)
	{
		boolean ismyself=false;
		try
		{
			html.insertBeforeEnd(
				html.getDefaultRootElement()
				.getElement(0)
				.getElement(0),
				"<table id=\"msg"+messageNumber+"\" "+
				"border=\"0\" white-space=\"0\" "+
				"align=\""+(ismyself?"right":"left")+"\" "+
				"cellspacing=\"0\" cellpadding=\"0\" "+
				"style=\"font-size:0;-webkit-user-select:none;"+
				"-moz-user-select:none;"+
				"-ms-user-select:none;user-select:none;\">"+
				"<tr><td rowspan=\"3\">"+
				(ismyself?"":getUserHead("ask"))+
				"</td>"+
				"<td><img src=\""+PATH+"bubble_lu.jpg\"></td>"+
				"<td style=\"background-image:url("+PATH+"bubble_up.jpg);"+
				"background-repeat:repeat-x;\">&nbsp;</td>"+
				"<td><img src=\""+PATH+"bubble_ru.jpg\"></td>"+
				"<td rowspan=\"3\">"+
				(ismyself?getUserHead("ask"):"")+
				"</td></tr>"+
				"<tr><td style=\"background-image:url("+PATH+"bubble_le.jpg)\">&nbsp;</td>"+
				"<td style=\"-webkit-user-select:text;"+
				"-moz-user-select:text;-ms-user-select:text;"+
				"user-select:text;font-size:12px;\">"+msg+"</td>"+
				"<td style=\"background-image:url("+PATH+"bubble_ri.jpg)\">&nbsp;</td></tr>"+
				"<tr><td><img src=\""+PATH+"bubble_ld.jpg\"></td>"+
				"<td style=\"background-image:url("+PATH+"bubble_do.jpg)\">&nbsp;</td>"+
				"<td><img src=\""+PATH+"bubble_rd.jpg\"></td></tr></table><br>");
			messageNumber++;
		}
		catch(Exception e)
		{
			System.out.println("errorrrrrrr!");
		}
	}

	private static String getUserHead(String userName)
	{
		return "<a href=\"user:"+userName+"\">"+
			"<img src=\""+PATH+userName+".jpg\"></a>";
	}
}