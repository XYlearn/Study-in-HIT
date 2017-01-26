import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.qcloud.cos.*;
import com.qcloud.cos.common_utils.CommonFileUtils;
import com.qcloud.cos.meta.FileAuthority;
import com.qcloud.cos.meta.InsertOnly;
import com.qcloud.cos.request.CreateFolderRequest;
import com.qcloud.cos.request.DelFileRequest;
import com.qcloud.cos.request.DelFolderRequest;
import com.qcloud.cos.request.GetFileLocalRequest;
import com.qcloud.cos.request.ListFolderRequest;
import com.qcloud.cos.request.MoveFileRequest;
import com.qcloud.cos.request.StatFileRequest;
import com.qcloud.cos.request.StatFolderRequest;
import com.qcloud.cos.request.UpdateFileRequest;
import com.qcloud.cos.request.UpdateFolderRequest;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;
import com.qcloud.cos.sign.Sign;


/**
 * @author chengwu cos Demo代码
 */
public class Cos {
	private String bucketName = "sihdatabase";
	private String region = "tj";

	private ClientConfig clientConfig;
	private COSClient cosClient;
	private String sign;

	public Cos(String sign) {
		this.sign = sign;
	}

	String uploadFile(String cosFilePath, String localFilePath) {

		UploadFileRequest uploadFileRequest =
				  new UploadFileRequest(bucketName, cosFilePath, localFilePath);
		uploadFileRequest.setEnableSavePoint(false);
		uploadFileRequest.setEnableShaDigest(false);
		return cosClient.uploadFile(uploadFileRequest);
	}

	String downloadFile(String localPathDown, String cosFilePath, String referer) {
		GetFileLocalRequest getFileLocalRequest =
				  new GetFileLocalRequest(bucketName, cosFilePath, localPathDown);
		getFileLocalRequest.setUseCDN(false);
		getFileLocalRequest.setReferer(referer);
		return cosClient.getFileLocal(getFileLocalRequest);
	}

	void shutdownCos() {
		cosClient.shutdown();
	}

	private String sendPost(String url) {
		try {


		} catch (Exception e) {

		}
		return null;
	}

}
