import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.exception.AbstractCosException;
import com.qcloud.cos.http.*;
import org.apache.http.client.methods.HttpPost;

import java.io.InputStream;

/**
 * Created by xy16 on 17-1-25.
 */
public class CosClient extends AbstractCosHttpClient {
	static private String bucketName = "sihdatabase";
	static private String region = "tj";

	CosClient(ClientConfig config) {
		super(config);
	}

	protected String sendPostRequest(HttpRequest httpRequest) throws AbstractCosException {

		return null;
	}

	protected String sendGetRequest(HttpRequest httpRequest) throws AbstractCosException {
		return null;
	}

	public InputStream getFileInputStream(HttpRequest httpRequest) throws AbstractCosException {
		return null;
	}

	static HttpRequest uploadPost(String url, String sig) {
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(url);
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setContentType(HttpContentType.MULTIPART_FORM_DATA);
		httpRequest.addHeader("Host", region+".file.myqcloud.com");
		httpRequest.addHeader("Authorization", sig);
		return httpRequest;
	}
}
