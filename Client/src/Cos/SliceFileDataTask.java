package Cos;

import com.qcloud.cos.common_utils.CommonFileUtils;
import com.qcloud.cos.exception.UnknownException;
import com.qcloud.cos.http.*;
import com.qcloud.cos.meta.SliceCheckPoint;
import com.qcloud.cos.meta.SlicePart;
import com.qcloud.cos.meta.UploadSliceFileContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.Callable;

/**
 * Created by xy16 on 17-2-5.
 */
public class SliceFileDataTask implements Callable<JSONObject> {
	private static final Logger LOG = LoggerFactory.getLogger(com.qcloud.cos.meta.SliceFileDataTask.class);
	private int TaskId;
	private int sliceIndex;
	private SliceCheckPoint scp;
	private UploadSliceFileContext context;
	private AbstractCosHttpClient httpClient;
	private String sign;
	private String url;
	private long signExpired;

	public SliceFileDataTask(int taskId, int sliceIndex, SliceCheckPoint scp, UploadSliceFileContext context, AbstractCosHttpClient httpClient, String sign, String url, long signExpired) {
		this.TaskId = taskId;
		this.sliceIndex = sliceIndex;
		this.scp = scp;
		this.context = context;
		this.httpClient = httpClient;
		this.sign = sign;
		this.url = url;
		this.signExpired = signExpired;
	}

	public JSONObject call() throws Exception {
		JSONObject resultJson = null;

		try {
			com.qcloud.cos.http.HttpRequest e = new com.qcloud.cos.http.HttpRequest();
			SlicePart errMsg1 = (SlicePart)this.scp.sliceParts.get(this.sliceIndex);
			e.addParam("op", "upload_slice_data");
			if(this.context.isEnableShaDigest()) {
				e.addParam("sha", this.context.getEntireFileSha());
			}

			e.addParam("session", this.scp.sessionId);
			e.addParam("offset", String.valueOf(errMsg1.getOffset()));
			String sliceContent = "";
			if(this.context.isUploadFromBuffer()) {
				sliceContent = new String(this.context.getContentBuffer(), (new Long(errMsg1.getOffset())).intValue(), errMsg1.getSliceSize(), Charset.forName("ISO-8859-1"));
			} else {
				sliceContent = CommonFileUtils.getFileContent(this.scp.uploadFile, errMsg1.getOffset(), errMsg1.getSliceSize());
			}

			e.addParam("fileContent", sliceContent);
			long signExpired = System.currentTimeMillis() / 1000L + this.signExpired;
			String sign = this.sign;
			e.addHeader("Authorization", sign);
			e.setUrl(this.url);
			e.setMethod(HttpMethod.POST);
			e.setContentType(HttpContentType.MULTIPART_FORM_DATA);
			String resultStr = this.httpClient.sendHttpRequest(e);
			resultJson = new JSONObject(resultStr);
			if(resultJson.getInt("code") == 0) {
				this.scp.update(this.sliceIndex, true);
				if(this.context.isEnableSavePoint()) {
					this.scp.dump(this.context.getSavePointFile());
				}
			}

			LOG.debug("sliceFileDataTask: " + this.toString() + ", result: " + resultStr);
			return resultJson;
		} catch (Exception var9) {
			String errMsg = "taskInfo:" + this.toString() + ", Exception:" + var9.toString();
			LOG.error(errMsg);
			throw new UnknownException(errMsg);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TaskId:").append(this.TaskId).append(", SliceIndex:").append(this.sliceIndex).append(", localPath:").append(this.context.getLocalPath()).append(", uploadUrl:").append(this.url);
		return sb.toString();
	}
}