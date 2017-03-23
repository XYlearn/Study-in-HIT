package gui;

import bin.test;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import util.SegmentAPI;

public class SearchBox extends JPanel
{
	private JTextArea mytext=new JTextArea();
	public SearchBox()
	{
		setLayout(new BorderLayout());
		add(mytext,BorderLayout.CENTER);
	}
	
	public void setColumns(int column)
	{
		mytext.setColumns(column);
	}
	
	@SuppressWarnings("empty-statement")
	public void search()
	{
		if("".equals(mytext.getText())) return;
		StringBuilder eng=new StringBuilder();
		StringBuilder chn=new StringBuilder(mytext.getText().replaceAll(" ", ""));
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
		while(i-->0&&"".equals(tmpSegment=SegmentAPI.segment(chn.toString())));
		if(tmpSegment.equals(""))
		{
			System.out.println("分词异常");
			return;
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
		try
		{
			test.client.searchInformation(keywords);
		}
		catch(IOException e)
		{
			System.out.println("搜索异常");
		}
		System.out.println(keywords.toString());
	}
	
	private static boolean isASCII(char c)
	{
		return c>=32&&c<127;
	}
}
