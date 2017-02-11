import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SegmentAPI
{
	private static String apikey="M958l0z19wDT3MxfMHSgBJtsQdZJsNwNQyxMJYXf";
	private static String pattern="ws";
	private static String format="plain";

	public static String segment(String text)
	{
		String line,ans="";
		try
		{
			text = URLEncoder.encode(text, "utf-8");
			URL url = new URL("http://api.ltp-cloud.com/analysis/?"
				+ "api_key=" + apikey + "&"
				+ "text=" + text + "&"
				+ "format=" + format + "&"
				+ "pattern=" + pattern);
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader innet = new BufferedReader(new InputStreamReader(
				conn.getInputStream(),"utf-8"));
			while ((line = innet.readLine())!= null)
				ans+=line+"\n";
			innet.close();
		}
		catch(Exception e)
		{
			ans="";
		}
		finally
		{
			return ans;
		}
	}
}