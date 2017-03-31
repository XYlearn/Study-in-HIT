package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SegmentTools
{
	private static final String APIKEY="M958l0z19wDT3MxfMHSgBJtsQdZJsNwNQyxMJYXf";
	private static final String PATTERN="ws";
	private static final String FORMAT="plain";
	
	private SegmentTools(){}

	public static String segment(String text)
	{
		String line,ans="";
		try
		{
			text = URLEncoder.encode(text, "utf-8");
			URL url = new URL("http://api.ltp-cloud.com/analysis/?"
				+ "api_key=" + APIKEY + "&"
				+ "text=" + text + "&"
				+ "format=" + FORMAT + "&"
				+ "pattern=" + PATTERN);
			URLConnection conn = url.openConnection();
			conn.connect();
			try (BufferedReader innet=new BufferedReader(
				new InputStreamReader(conn.getInputStream(),"utf-8")))
			{
				while ((line = innet.readLine())!= null)
					ans+=line+"\n";
			}
		}
		catch(IOException e)
		{
			ans="";
		}
		return ans;
	}
}