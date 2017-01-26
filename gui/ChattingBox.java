//package gui;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.Dimension;
//import javax.swing.text.html.HTMLEditorKit;
//import javax.swing.text.html.HTMLDocument;
//import javax.swing.text.html.HTML.Tag;

public class ChattingBox extends JPanel
{
	public JTextPane myPane=new JTextPane();
	private JScrollPane myScroll=new JScrollPane(myPane);
	//private HTMLEditorKit he=new HTMLEditorKit();
	//private HTMLDocument hd=(HTMLDocument) he.createDefaultDocument();
	private StringBuilder html=new StringBuilder("");
	private static final String PATH="file:"+ChattingBox.class.getResource("").getPath();

	public static final void main(String[] args)
	{
		JFrame f=new JFrame("myApplication");
		ChattingBox c=new ChattingBox();
		f.getContentPane().add(c);
		f.pack();
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		c.pushMessage(true,"第一题会做么？");
		c.pushMessage(true,"我看不懂答案");
		c.pushMessage(false,"啊？");
		c.pushMessage(false,"不会啊");
		c.pushMessage(false,"要不你去问问肖浩宇");
		c.pushMessage(false,"他绝对会");
		c.pushMessage(true,"。。。行吧谢谢");
		c.pushMessage(false,"没事，对了你那个什么，\n就是昨天说的那个不是这个是那个就是我你的它的那个什么的什么的什么的之类的是啥的啊");
		c.pushMessage(true,"我去问问他");
		c.pushMessage(false,"替我膜一下");
	}

	ChattingBox()
	{
		myPane.setPreferredSize(new Dimension(500,550));
		myPane.setContentType("text/html");
		//myScroll.setVisible(true);
		//myPane.setVisible(true);
		this.add(myScroll);
	}

	public void pushMessage(boolean ismyself,String message)
	{
		message=message.replace("\n","<br>");
		html.append("<table border=\"0\" white-space=\"0\" "+
			"align=\""+(ismyself?"right":"left")+"\" "+
			"cellspacing=\"0\" cellpadding=\"0\" "+
			"style=\"font-size:0;-webkit-user-select:none;"+
			"-moz-user-select:none;"+
			"-ms-user-select:none;user-select:none;\">"+
			"<tr><td rowspan=\"3\">"+
			(ismyself?"":"<img src=\""+PATH+"ask.jpg\">")+
			"</td>"+
			"<td><img src=\""+PATH+"bubble_lu.jpg\"></td>"+
			"<td style=\"background-image:url("+PATH+"bubble_up.jpg);"+
			"background-repeat:repeat-x;\">&nbsp;</td>"+
			"<td><img src=\""+PATH+"bubble_ru.jpg\"></td>"+
			"<td rowspan=\"3\">"+
			(ismyself?"<img src=\""+PATH+"ask.jpg\">":"")+
			"</td></tr>"+
			"<tr><td style=\"background-image:url("+PATH+"bubble_le.jpg)\">&nbsp;</td>"+
			"<td style=\"-webkit-user-select:text;"+
			"-moz-user-select:text;-ms-user-select:text;"+
			"user-select:text;font-size:12px;\">"+message+"</td>"+
			"<td style=\"background-image:url("+PATH+"bubble_ri.jpg)\">&nbsp;</td></tr>"+
			"<tr><td><img src=\""+PATH+"bubble_ld.jpg\"></td>"+
			"<td style=\"background-image:url("+PATH+"bubble_do.jpg)\">&nbsp;</td>"+
			"<td><img src=\""+PATH+"bubble_rd.jpg\"></td></tr></table><br>");
		myPane.setText(html.toString());
	}
}