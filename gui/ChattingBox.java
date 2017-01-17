//package gui;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTML.Tag;

public class ChattingBox extends JPanel
{
	private JTextPane myPane=new JTextPane();
	private HTMLEditorKit he=new HTMLEditorKit();
	private HTMLDocument hd=(HTMLDocument) he.createDefaultDocument();

	public static final void main(String[] args)
	{
		JFrame f=new JFrame("myApplication");
		ChattingBox c=new ChattingBox();
		f.getContentPane().add(c);
		f.pack();
		f.setVisible(true);
		c.pushMessage("");
		c.pushMessage("");
		c.pushMessage("");
	}

	ChattingBox()
	{
		myPane.setPreferredSize(new Dimension(100,200));
		myPane.setEditorKit(he);
		myPane.setContentType("text/html");
		myPane.setDocument(hd);
		myPane.setVisible(true);
		this.add(myPane);
	}

	public void pushMessage(String message)
	{
		try{
			he.insertHTML(hd,hd.getLength(),
				"<table border=\"0\"><tr><td>000</td><td>123</td><td>456</td></tr></table>",
				0,0,Tag.TABLE);
			he.insertHTML(hd,hd.getLength(),"<br>",0,0,Tag.BR);
		}catch(Exception e){}
	}
}