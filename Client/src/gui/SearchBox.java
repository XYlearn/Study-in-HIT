package gui;

import bin.test;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import util.SegmentAPI;

public class SearchBox extends JPanel
{
	private JTextArea mytext=new JTextArea();
	//private Consumer<String> searchListener;
	public SearchBox()
	{
		add(mytext,BorderLayout.CENTER);
	}
	
	public void setColumns(int column)
	{
		mytext.setColumns(column);
	}
	
	public void search()
	{
		if("".equals(mytext.getText())) return;
		StringBuilder eng=new StringBuilder();
		StringBuilder chn=new StringBuilder(mytext.getText().replaceAll(" ", ""));
		int i;
		for(i=0;i<chn.length()&&!isASCII(chn.charAt(i));i++);
		if(i<chn.length())
		{
			eng.append(chn.charAt(i));
			chn.replace(i, i+1, "");
		}
		for(i++;i<chn.length();i++)
			if(isASCII(chn.charAt(i)))
			{
				if(!isASCII(chn.charAt(i-1))) eng.append('*');
				eng.append(chn.charAt(i));
				chn.replace(i, i+1, "");
			}
		ArrayList<String> keywords=new ArrayList<>(
				Arrays.asList(SegmentAPI.segment(chn.toString())
						.replaceAll(" 。", "")
						.replaceAll(" ，", "")
						.replaceAll(" ！", "")
						.replaceAll(" ？", "")
						.replaceAll(" ：", "")
						.replaceAll(" …", "").split(" ")));
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
