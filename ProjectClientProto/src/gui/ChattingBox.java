//package gui;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.Dimension;
import java.util.ArrayList;
import java.io.File;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChattingBox extends JPanel
{
	private ChattingBoxRightAction rightAction=new ChattingBoxRightAction();
	private ChattingBoxHyperlinkListener hyperlinkAction=new ChattingBoxHyperlinkListener();
	private boolean onHyperlink=false;
	private String mouseHyperlink;
	private String currentHyperlink;

	public JTextPane myPane=new JTextPane();
	private JScrollPane myScroll=new JScrollPane(myPane);
	private StringBuilder html=new StringBuilder("");

	private JPopupMenu textMenu=new JPopupMenu();
	private JPopupMenu userMenu=new JPopupMenu();
	private JMenuItem copy=new JMenuItem("复制");
	private JMenuItem paste=new JMenuItem("粘贴");
	private JMenuItem reset=new JMenuItem("清屏");
	private JMenuItem abspeak=new JMenuItem("禁言");
	private JMenuItem getInfo=new JMenuItem("个人资料");

	private static final String CLASSPATH=ChattingBox.class.getResource("").getPath();
	private static final String PATH="file:"+CLASSPATH;

	public static final void main(String[] args)
	{
		JFrame f=new JFrame("myApplication");
		ChattingBox c=new ChattingBox();
		f.getContentPane().add(c);
		f.pack();
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		c.pushMessage(true,"第一题会做么？",new ArrayList<String>());
		c.pushMessage(true,"我看不懂答案",new ArrayList<String>());
		c.pushMessage(false,"啊？",new ArrayList<String>());
		c.pushMessage(false,"不会啊",new ArrayList<String>());
		c.pushMessage(false,"要不你去问问肖浩宇",new ArrayList<String>());
		c.pushMessage(false,"他绝对会",new ArrayList<String>());
		c.pushMessage(true,"。。。行吧谢谢",new ArrayList<String>());
		c.pushMessage(false,"没事，对了你那个什么，\n就是昨天说的那个不是这个是那个就是我你的它的那个什么的什么的什么的之类的是啥的啊",new ArrayList<String>());
		c.pushMessage(true,"我去问问他",new ArrayList<String>());
		c.pushMessage(false,"替我膜一下",new ArrayList<String>());
		System.out.println(c.myPane.getText());
	}

	ChattingBox()
	{
		myPane.setPreferredSize(new Dimension(500,550));
		myPane.setContentType("text/html");
		myPane.setEditable(false);

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

		this.add(myScroll);
	}

	public void pushMessage(boolean ismyself,String message,ArrayList<String> pictures)
	{
		message=message.replaceAll("\n","<br>");
		for(int i=0;i<pictures.size();i++)
		{
			if(!(new File(CLASSPATH+pictures.get(i)).exists()))
				//调用网络接口下载图片，下载完成时刷新
			message=message.replaceAll("[^%]%"+i,"<img src=\""+PATH+pictures.get(i)+"\">");
		}
		html.append("<table border=\"0\" white-space=\"0\" "+
			"align=\""+(ismyself?"right":"left")+"\" "+
			"cellspacing=\"0\" cellpadding=\"0\" "+
			"style=\"font-size:0;-webkit-user-select:none;"+
			"-moz-user-select:none;"+
			"-ms-user-select:none;user-select:none;\">"+
			"<tr><td rowspan=\"3\">"+
			(ismyself?"":"<a href=\"user:用户名A\">"+
			"<img src=\""+PATH+"ask.jpg\"></a>")+
			"</td>"+
			"<td><img src=\""+PATH+"bubble_lu.jpg\"></td>"+
			"<td style=\"background-image:url("+PATH+"bubble_up.jpg);"+
			"background-repeat:repeat-x;\">&nbsp;</td>"+
			"<td><img src=\""+PATH+"bubble_ru.jpg\"></td>"+
			"<td rowspan=\"3\">"+
			(ismyself?"<a href=\"user:用户名B\">"+
			"<img src=\""+PATH+"ask.jpg\"></a>":"")+
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

	private class ChattingBoxMouseListener implements MouseListener
	{
		ChattingBoxMouseListener(){}
		public void mousePressed(MouseEvent e)
		{
			if(onHyperlink) currentHyperlink=mouseHyperlink;
			if(e.getButton()==MouseEvent.BUTTON3)
			{
				//判断用户身份
				if(onHyperlink)
				{
					//user.add(abspeak)
					//abspeak.setEnabled(true);
					getInfo.setEnabled(true);
					reset.setEnabled(true);
					userMenu.show(ChattingBox.this,e.getX(),e.getY());
				}
				else
				{
					copy.setEnabled(true);
					reset.setEnabled(true);
					textMenu.show(ChattingBox.this,e.getX(),e.getY());
				}
			}
		}
		public void mouseReleased(MouseEvent e){}
		public void mouseClicked(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
	}

	private class ChattingBoxRightAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("复制"))
			{
				if(myPane.getSelectionStart()!=myPane.getSelectionEnd())
					myPane.copy();
			}
			else if(e.getActionCommand().equals("清屏"))
			{
				html.delete(0,html.length());
				myPane.setText("");
			}
		}
	}

	private class ChattingBoxHyperlinkListener implements HyperlinkListener
	{
		public void hyperlinkUpdate(HyperlinkEvent e)
		{
			if(e.getEventType()==HyperlinkEvent.EventType.ENTERED)
			{
				onHyperlink=true;
				mouseHyperlink=e.getDescription();
			}
			else if(e.getEventType()==HyperlinkEvent.EventType.EXITED)
				onHyperlink=false;
			else
			{
				if(currentHyperlink.substring(0,4).equals("user"))
				{
					//利用currentHyperlink.substring(5)打开个人资料
				}
			}
		}
	}
}