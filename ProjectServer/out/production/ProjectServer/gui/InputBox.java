package gui;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.util.ArrayList;
import util.MyMessage;
import java.io.File;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.*;
import util.MyMessage;
import bin.test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class InputBox extends JPanel
{
	//private ChattingBoxRightAction rightAction=new ChattingBoxRightAction();
	//private ChattingBoxHyperlinkListener hyperlinkAction=new ChattingBoxHyperlinkListener();

	public JTextPane myPane=new JTextPane();
	private JScrollPane myScroll=new JScrollPane(myPane);

	private JPopupMenu textMenu=new JPopupMenu();
	private JMenuItem copy=new JMenuItem("复制");
	private JMenuItem cut=new JMenuItem("剪切");
	private JMenuItem paste=new JMenuItem("粘贴");

	private static final String CLASSPATH=InputBox.class.getResource("").getPath();
	private static final String PATH="file:"+CLASSPATH;

	public InputBox()
	{
		myPane.setContentType("text/html");
		myPane.setEditable(true);
		myPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));

		this.add(myScroll,BorderLayout.CENTER);
	}

	public void setSize(int width,int height)
	{
		myPane.setPreferredSize(new Dimension(width,height));
	}

	public void insertImage(String filepath)
	{
		insertImage(new File(filepath));
	}

	public void insertImage(File f)
	{
		insertImage(myPane.getCaretPosition(),f);
	}

	public void insertImage(int pos,String filepath)
	{
		insertImage(pos,new File(filepath));
	}

	public void insertImage(int pos,File f)
	{
		//Document doc=Jsoup.parse();
	}

	public void sendMessage()
	{
		ArrayList<String> pictures=new ArrayList<String>();
		int count=0;
		String str=myPane.getText();
		//clear the textpane
		myPane.setText("");
		//protect the newlines ('\n')
		str=str.replaceAll("\\n *","")
			.replaceAll("</p>","\n")
		//replace the % that user inputted
			.replaceAll("%","%%");
		//get the path of the images
		Pattern pat=Pattern.compile("<img[^>]*? src=\"(.*?)\".*?>");
		Matcher mat=pat.matcher(str);
		while(mat.find())
			pictures.add(mat.group(1));
		if(pictures.size()==0) pictures=null;
		//replace the images with "%>"
		str=str.replaceAll("<img.*?>","%>")
		//clear the HTML format
			.replaceAll("<.*?>","");
		//figure out the problem about %
		StringBuilder message=new StringBuilder(str);
		for(int i=0;i+1<message.length();i++)
			if(message.charAt(i)=='%'&&message.charAt(i+1)=='>')
				message=message.replace(i,i+2,"%"+(count++)+" \n");
		//remove the last "</p>"(newline)
		if(message.charAt(message.length()-1)=='\n')
			message.setLength(message.length()-1);
		//System.out.println(message.toString());
		//send the message
		MyMessage msg=
			new MyMessage("ask","19:18:32",message.toString(),pictures);
		//test.client.sendContent(msg);
	}
}