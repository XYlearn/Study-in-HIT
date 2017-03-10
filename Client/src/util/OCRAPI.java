package util;

import com.baidu.aip.ocr.AipOcr;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class OCRAPI
{
	private static final String APP_ID = "9381150";
	private static final String API_KEY = "kQoF2SbSoqfmqZhit0LgLkac";
	private static final String SECRET_KEY = "EobsSpLSrAGgN9ukGXY2htfHpwTDmjPh";
	
	private AipOcr ocrClient=new AipOcr(APP_ID,API_KEY,SECRET_KEY);
	private static final HashMap<String, String> options = new HashMap<>();
	
	public OCRAPI()
	{
		ocrClient.setConnectionTimeoutInMillis(2000);
		ocrClient.setSocketTimeoutInMillis(20000);
		options.put("detect_direction", "true");
	}
	
	public String getStringFromPict(String pictpath)
	{
		int n,i;
		StringBuilder ans=new StringBuilder();
		JSONObject obj=ocrClient.general(pictpath, options);
		n=obj.getInt("words_result_num");
		JSONArray arr=obj.getJSONArray("words_result");
		for(i=0;i<n;i++)
			ans.append(arr.getJSONObject(i).getString("words"));
		return ans.toString();
	}
}
