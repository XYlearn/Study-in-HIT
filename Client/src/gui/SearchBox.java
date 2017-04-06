package gui;

import bin.test;
import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.JTextField;
import util.SegmentTools;

public class SearchBox extends JPanel
{
	private JTextField mytext=new JTextField();
	private boolean needSelectAll;
	private static int searchID=1;
	
	public SearchBox()
	{
		setLayout(new BorderLayout());
		add(mytext,BorderLayout.CENTER);
		
		needSelectAll=false;
		
		mytext.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode()==10)
					search();
			}
		});
		
		mytext.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(needSelectAll)
				{
					needSelectAll=false;
					mytext.setSelectionStart(0);
					mytext.setSelectionEnd(mytext.getText().length());
				}
			}
		});
		
		mytext.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				needSelectAll=true;
			}
		});
	}
	
	public void setColumns(int column)
	{
		mytext.setColumns(column);
	}
	
	@SuppressWarnings("empty-statement")
	public synchronized void search()
	{
		if("".equals(mytext.getText())) return;
		needSelectAll=true;
		ArrayList<String> keywords=getKeyWords(mytext.getText());
		if(keywords==null)
		{
			System.out.println("分词异常");
			return;
		}
		/*try
		{
			test.client.searchInformation(keywords,searchID++);
		}
		catch(IOException e)
		{
			System.out.println("搜索异常");
		}*/
	}
	
	public static ArrayList<String> getKeyWords(String content)
	{
		StringBuilder eng=new StringBuilder();
		StringBuilder chn=new StringBuilder(escapeRegExpWords(content.replaceAll(" ", "")));
		boolean last=true;
		for(int i=0;i<chn.length();i++)
			if(isASCII(chn.charAt(i)))
			{
				if(!last) eng.append('*');
				last=true;
				eng.append(chn.charAt(i));
				chn.replace(i, i+1, "");
				i--;
			}
			else
				last=false;
		int i=3;
		String tmpSegment="";
		if(!"".equals(chn.toString()))
		{
			while(i-->0&&"".equals(tmpSegment=SegmentTools.segment(chn.toString())));
			if(tmpSegment.equals(""))
			{
				System.out.println("分词异常，连接服务器失败。");
				return null;
			}
		}
		ArrayList<String> keywords=new ArrayList<>(
				Arrays.asList(tmpSegment
						.replaceAll(" 。", "")
						.replaceAll(" ，", "")
						.replaceAll(" ！", "")
						.replaceAll(" ？", "")
						.replaceAll(" ：", "")
						.replaceAll(" …", "")
						.replaceAll("\n", "").split(" ")));
		keywords.add(0, eng.toString());
		return keywords;
	}
	
	private static boolean isASCII(char c)
	{
		return c>=32&&c<127;
	}
	
	private static String escapeRegExpWords(String keyword)
	{
		String[] fbsArr=
		{"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
		for (String key:fbsArr)
			if (keyword.contains(key))
				keyword=keyword.replace(key, "\\"+key);
		return keyword;
	}
}
