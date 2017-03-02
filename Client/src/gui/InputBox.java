package gui;

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
import java.util.regex.*;
import bin.test;
import java.io.IOException;
import javax.swing.text.html.HTMLEditorKit;

public class InputBox extends JPanel
{
	//private ChattingBoxRightAction rightAction=new ChattingBoxRightAction();

	public JTextPane myPane=new JTextPane();
	private final JScrollPane myScroll=new JScrollPane(myPane);
	private final HTMLEditorKit kit=new HTMLEditorKit();

	private final JPopupMenu textMenu=new JPopupMenu();
	private final JMenuItem copy=new JMenuItem("复制");
	private final JMenuItem cut=new JMenuItem("剪切");
	private final JMenuItem paste=new JMenuItem("粘贴");

	private String questionID="";

	private static final String CLASSPATH=InputBox.class.getResource("").getPath();
	private static final String PATH="file:"+CLASSPATH;

	public InputBox()
	{
		myPane.setContentType("text/html");
		myPane.setEditable(true);
		kit.setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));
		kit.install(myPane);
		myPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		myPane.setEditorKit(kit);

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
			return;
		}
	}
}
