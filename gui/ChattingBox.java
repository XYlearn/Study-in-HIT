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
	public JTextPane myPane=new JTextPane();
	private HTMLEditorKit he=new HTMLEditorKit();
	private HTMLDocument hd=(HTMLDocument) he.createDefaultDocument();
	private StringBuilder html=new StringBuilder("");

	public static final void main(String[] args)
	{
		JFrame f=new JFrame("myApplication");
		ChattingBox c=new ChattingBox();
		f.getContentPane().add(c);
		f.pack();
		f.setVisible(true);
		c.pushMessage(true,"1");
		c.pushMessage(true,"2");
		c.pushMessage(false,"3");
		c.pushMessage(false,"4");
		c.pushMessage(false,"5");
		c.pushMessage(false,"6");
		c.pushMessage(true,"7");
		c.pushMessage(false,"8");
		c.pushMessage(true,"9");
		System.out.println(c.myPane.getText());
	}

	ChattingBox()
	{
		myPane.setPreferredSize(new Dimension(300,600));
		//myPane.setEditorKit(he);
		myPane.setContentType("text/html");
		//myPane.setDocument(hd);
		myPane.setVisible(true);
		this.add(myPane);
	}

	public void pushMessage(boolean ismyself,String message)
	{
		html.append("<table border=\"0\" style=\"width:100%;\" cellspacing=\"0\">"+
			"<tr align=\"center\"><td width=\"50px\">"+
			((!ismyself)?"<img"+
			" src=\""+this.getClass().getResource("")+"ask.jpg\">":"")+
			"</td>"+
			"<td style=\"white-space:nowrap;background-image:url("+this.getClass().getResource("")+"bg"+
			(ismyself?"right":"left")+
			".jpg);\">"+
			message+
			"</td>"+
			"<td width=\"50px\">"+
			(ismyself?"<img"+
			" src=\""+this.getClass().getResource("")+"ask.jpg\">":"")+
			"</td></tr></table><br>");
		myPane.setText(html.toString());
	}
}