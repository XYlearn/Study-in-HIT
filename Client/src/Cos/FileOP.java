package Cos;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.common_utils.CommonCodecUtils;
import com.qcloud.cos.common_utils.CommonFileUtils;
import com.qcloud.cos.common_utils.CommonPathUtils;
import com.qcloud.cos.exception.AbstractCosException;
import com.qcloud.cos.exception.ParamException;
import com.qcloud.cos.exception.UnknownException;
import com.qcloud.cos.http.AbstractCosHttpClient;
import com.qcloud.cos.http.HttpContentType;
import com.qcloud.cos.http.HttpMethod;
import com.qcloud.cos.http.HttpRequest;
import com.qcloud.cos.meta.FileStat;
import com.qcloud.cos.meta.InsertOnly;
import com.qcloud.cos.meta.SliceCheckPoint;
import com.qcloud.cos.meta.SliceFileDataTask;
import com.qcloud.cos.meta.SlicePart;
import com.qcloud.cos.meta.UploadSliceFileContext;
import com.qcloud.cos.op.BaseOp;
import com.qcloud.cos.request.*;
import com.qcloud.cos.sign.Credentials;
import com.qcloud.cos.sign.Sign;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileOP {
	private static final String bucktName = "sih";
	private static final long appID = 1253199804;

	private static final Logger LOG = LoggerFactory.getLogger(com.qcloud.cos.op.FileOp.class);
	private String sign;
	private ClientConfig config;
	private AbstractCosHttpClient httpClient;

	public FileOP(ClientConfig config, String sign, AbstractCosHttpClient client) {
		this.config = config;
		this.httpClient = client;
		this.sign = sign;
	}

	public void changeSign(String sign) {
		this.sign = sign;
	}

	public long getAppId() { return appID; }
	public String getBucktName() {return bucktName;}

	private String buildUrl(AbstractBaseRequest request) throws AbstractCosException {
		String endPoint = this.config.getUploadCosEndPointPrefix() + this.config.getUploadCosEndPointDomain() + this.config.getUploadCosEndPointSuffix();
		long appId = this.getAppId();
		String bucketName = request.getBucketName();
		String cosPath = request.getCosPath();
		cosPath = CommonPathUtils.encodeRemotePath(cosPath);
		return String.format("%s/%d/%s%s", new Object[]{endPoint, Long.valueOf(appId), bucketName, cosPath});
	}

	private String buildGetFileUrl(GetFileInputStreamRequest request) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(this.config.getDownCosEndPointPrefix()).append(request.getBucketName()).append("-").append(getAppId()).append(".");
		if(request.isUseCDN()) {
			strBuilder.append("file.myqcloud.com");
		} else {
			strBuilder.append(this.config.getDownCosEndPointDomain());
		}

		strBuilder.append(request.getCosPath()).toString();
		String url = strBuilder.toString();
		return url;
	}

	public String updateFile(UpdateFileRequest request) throws AbstractCosException {
		request.check_param();
		String url = this.buildUrl(request);
		String sign = this.sign;
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(url);
		httpRequest.addHeader("Authorization", sign);
		httpRequest.addHeader("Content-Type", "application/json");
		httpRequest.addHeader("User-Agent", this.config.getUserAgent());
		httpRequest.addParam("op", "update");
		int updateFlag = request.getUpdateFlag();
		if((updateFlag & 1) != 0) {
			httpRequest.addParam("biz_attr", request.getBizAttr());
		}

		if((updateFlag & 64) != 0) {
			String customHeaderStr = (new JSONObject(request.getCustomHeaders())).toString();
			httpRequest.addParam("custom_headers", customHeaderStr);
		}

		if((updateFlag & 128) != 0) {
			httpRequest.addParam("authority", request.getAuthority().toString());
		}

		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setContentType(HttpContentType.APPLICATION_JSON);
		return this.httpClient.sendHttpRequest(httpRequest);
	}

	public String delFile(DelFileRequest request) throws AbstractCosException {
		request.check_param();
		String url = this.buildUrl(request);
		String sign = this.sign;
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(url);
		httpRequest.addHeader("Authorization", sign);
		httpRequest.addHeader("Content-Type", "application/json");
		httpRequest.addHeader("User-Agent", this.config.getUserAgent());
		httpRequest.addParam("op", "delete");
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setContentType(HttpContentType.APPLICATION_JSON);
		return this.httpClient.sendHttpRequest(httpRequest);
	}

	public String statFile(StatFileRequest request) throws AbstractCosException {
		request.check_param();
		String url = this.buildUrl(request);
		long signExpired = System.currentTimeMillis() / 1000L + (long)this.config.getSignExpired();
		String sign = this.sign;
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(url);
		httpRequest.addHeader("Authorization", sign);
		httpRequest.addHeader("User-Agent", this.config.getUserAgent());
		httpRequest.addParam("op", "stat");
		httpRequest.setMethod(HttpMethod.GET);
		return this.httpClient.sendHttpRequest(httpRequest);
	}

	public String uploadFile(UploadFileRequest request) throws AbstractCosException {
		request.check_param();
		String localPath = request.getLocalPath();
		long fileSize = 0L;
		if(request.isUploadFromBuffer()) {
			fileSize = (long)request.getContentBufer().length;
		} else {
			try {
				fileSize = CommonFileUtils.getFileLength(localPath);
			} catch (Exception var8) {
				throw new UnknownException(var8.toString());
			}
		}

		long suitSingleFileSize = 8388608L;
		if(fileSize < suitSingleFileSize) {
			return this.uploadSingleFile(request);
		} else {
			UploadSliceFileRequest sliceRequest = new UploadSliceFileRequest(request);
			sliceRequest.setInsertOnly(request.getInsertOnly());
			if(request.isUploadFromBuffer()) {
				sliceRequest.setContentBufer(request.getContentBufer());
			}

			sliceRequest.setEnableSavePoint(request.isEnableSavePoint());
			sliceRequest.setEnableShaDigest(request.isEnableShaDigest());
			sliceRequest.setTaskNum(request.getTaskNum());
			return this.uploadSliceFile(sliceRequest);
		}
	}

	public String uploadSingleFile(UploadFileRequest request) throws AbstractCosException {
		request.check_param();
		String localPath = request.getLocalPath();
		long fileSize = 0L;
		if(request.isUploadFromBuffer()) {
			fileSize = (long)request.getContentBufer().length;
		} else {
			try {
				fileSize = CommonFileUtils.getFileLength(localPath);
			} catch (Exception var18) {
				throw new UnknownException(var18.toString());
			}
		}

		if(fileSize > 20971520L) {
			throw new ParamException("file is to big, please use uploadFile interface!");
		} else {
			String fileContent = "";
			String shaDigest = "";

			try {
				if(request.isUploadFromBuffer()) {
					fileContent = new String(request.getContentBufer(), Charset.forName("ISO-8859-1"));
					shaDigest = CommonCodecUtils.getBufferSha1(request.getContentBufer());
				} else {
					fileContent = CommonFileUtils.getFileContent(localPath);
					shaDigest = CommonCodecUtils.getEntireFileSha1(localPath);
				}
			} catch (Exception var17) {
				throw new UnknownException(var17.toString());
			}

			String url = this.buildUrl(request);
			long signExpired = System.currentTimeMillis() / 1000L + (long)this.config.getSignExpired();
			String sign = this.sign;
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.setUrl(url);
			httpRequest.addHeader("Authorization", sign);
			httpRequest.addHeader("User-Agent", this.config.getUserAgent());
			httpRequest.addParam("op", "upload");
			httpRequest.addParam("sha", shaDigest);
			httpRequest.addParam("biz_attr", request.getBizAttr());
			httpRequest.addParam("fileContent", fileContent);
			httpRequest.addParam("insertOnly", String.valueOf(request.getInsertOnly().ordinal()));
			httpRequest.setMethod(HttpMethod.POST);
			httpRequest.setContentType(HttpContentType.MULTIPART_FORM_DATA);
			String retStr = this.httpClient.sendHttpRequest(httpRequest);
			if(request.getInsertOnly() != InsertOnly.OVER_WRITE) {
				return retStr;
			} else {
				JSONObject retJson = new JSONObject(retStr);
				if(retJson.getInt("code") == 0) {
					return retStr;
				} else {
					DelFileRequest del_request = new DelFileRequest(request.getBucketName(), request.getCosPath());
					String delRet = this.delFile(del_request);
					JSONObject delJson = new JSONObject(delRet);
					return delJson.getInt("code") != 0?retStr:this.httpClient.sendHttpRequest(httpRequest);
				}
			}
		}
	}

	public String uploadSliceFile(UploadSliceFileRequest request) throws AbstractCosException {
		request.check_param();
		UploadSliceFileContext context = new UploadSliceFileContext(request);
		context.setUrl(this.buildUrl(request));
		String retStr = this.uploadFileWithCheckPoint(context);
		if(request.getInsertOnly() != InsertOnly.OVER_WRITE) {
			return retStr;
		} else {
			JSONObject retJson = new JSONObject(retStr);
			if(retJson.getInt("code") == 0) {
				return retStr;
			} else {
				DelFileRequest del_request = new DelFileRequest(request.getBucketName(), request.getCosPath());
				String delRet = this.delFile(del_request);
				JSONObject delJson = new JSONObject(delRet);
				if(delJson.getInt("code") != 0) {
					return retStr;
				} else {
					retStr = this.uploadFileWithCheckPoint(context);
					retJson = new JSONObject(retStr);
					if(retJson.getInt("code") != 0) {
						del_request = new DelFileRequest(request.getBucketName(), request.getCosPath());
						this.delFile(del_request);
					}

					return retStr;
				}
			}
		}
	}

	public String moveFile(MoveFileRequest request) throws AbstractCosException {
		request.check_param();
		String url = this.buildUrl(request);
		String sign = this.sign;
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(url);
		httpRequest.addHeader("Authorization", sign);
		httpRequest.addHeader("Content-Type", "application/json");
		httpRequest.addHeader("User-Agent", this.config.getUserAgent());
		httpRequest.addParam("op", "move");
		httpRequest.addParam("dest_fileid", request.getDstCosPath());
		httpRequest.addParam("to_over_write", String.valueOf(request.getOverWrite().ordinal()));
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setContentType(HttpContentType.APPLICATION_JSON);
		return this.httpClient.sendHttpRequest(httpRequest);
	}

	private String uploadFileWithCheckPoint(UploadSliceFileContext context) throws AbstractCosException {
		SliceCheckPoint scp = new SliceCheckPoint();
		if(context.isEnableSavePoint()) {
			try {
				scp.load(context.getSavePointFile());
			} catch (Exception var4) {
				CommonFileUtils.remove(context.getSavePointFile());
			}

			if(!scp.isValid(context.getLocalPath())) {
				CommonFileUtils.remove(context.getSavePointFile());
				this.prepare(context, scp);
			}
		}

		JSONObject uploadResult = this.upload(context, scp);
		if(context.isEnableSavePoint() && uploadResult.getInt("code") == 0) {
			CommonFileUtils.remove(context.getSavePointFile());
		}

		return uploadResult.toString();
	}

	private void prepare(UploadSliceFileContext context, SliceCheckPoint scp) throws AbstractCosException {
		try {
			long e = 0L;
			if(context.isUploadFromBuffer()) {
				e = (long)context.getContentBuffer().length;
			} else {
				try {
					e = CommonFileUtils.getFileLength(context.getLocalPath());
					scp.uploadFile = context.getLocalPath();
					scp.uploadFileStat = FileStat.getFileStat(scp.uploadFile);
				} catch (Exception var6) {
					throw new UnknownException(var6.toString());
				}
			}

			int sliceSize = context.getSliceSize();
			scp.magic = "B61BAAF89E3FD039F1279C4440AD8A7F0250300E";
			scp.cosPath = context.getCosPath();
			scp.sessionId = context.getSessionId();
			scp.enableShaDigest = context.isEnableSavePoint();
			scp.sliceParts = this.splitFile(e, sliceSize);
			scp.initFlag = false;
		} catch (Exception var7) {
			throw new UnknownException(var7.getMessage());
		}
	}

	private ArrayList<SlicePart> splitFile(long fileSize, int sliceSize) {
		ArrayList sliceParts = new ArrayList();
		int sliceCount = (new Long((fileSize + (long)(sliceSize - 1)) / (long)sliceSize)).intValue();

		for(int sliceIndex = 0; sliceIndex < sliceCount; ++sliceIndex) {
			SlicePart part = new SlicePart();
			long offset = Long.valueOf((long)sliceIndex).longValue() * (long)sliceSize;
			part.setOffset(offset);
			if(sliceIndex != sliceCount - 1) {
				part.setSliceSize(sliceSize);
			} else {
				part.setSliceSize((new Long(fileSize - offset)).intValue());
			}

			part.setUploadCompleted(false);
			sliceParts.add(part);
		}

		return sliceParts;
	}

	private void recover(UploadSliceFileContext context, SliceCheckPoint scp) throws AbstractCosException {
		try {
			long e = CommonFileUtils.getFileLength(context.getLocalPath());
			context.setFileSize(e);
			context.setSessionId(scp.sessionId);
			context.setEntireFileSha(scp.shaDigest);
			context.setEnableShaDigest(scp.enableShaDigest);
			context.setSliceSize(scp.sliceSize);
		} catch (Exception var5) {
			throw new UnknownException(var5.getMessage());
		}
	}

	private JSONObject upload(UploadSliceFileContext context, SliceCheckPoint scp) throws AbstractCosException {
		JSONObject sendParallelRet;
		JSONObject finishRet;
		if(!scp.initFlag) {
			sendParallelRet = this.sendSliceInit(context);
			if(sendParallelRet.getInt("code") != 0) {
				return sendParallelRet;
			}

			finishRet = sendParallelRet.getJSONObject("data");
			if(finishRet.has("access_url")) {
				return sendParallelRet;
			}

			if(finishRet.has("serial_upload") && finishRet.getInt("serial_upload") == 1) {
				LOG.debug("SERIAL_UPLOAD is true");
				context.setSerialUpload(true);
			} else {
				LOG.debug("SERIAL_UPLOAD is false");
				context.setSerialUpload(false);
			}

			context.setSessionId(finishRet.getString("session"));
			if(finishRet.getInt("slice_size") != context.getSliceSize()) {
				context.setSliceSize(finishRet.getInt("slice_size"));
			}

			this.prepare(context, scp);
			scp.updateAfterInit(context);
		} else {
			this.recover(context, scp);
		}

		sendParallelRet = this.sendSliceDataParallel(context, scp);
		if(sendParallelRet.getInt("code") != 0) {
			return sendParallelRet;
		} else {
			finishRet = this.sendSliceFinish(context);
			return finishRet;
		}
	}

	private JSONObject sendSliceInit(UploadSliceFileContext context) throws AbstractCosException {
		String localPath = context.getLocalPath();
		long fileSize = 0L;

		try {
			if(context.isUploadFromBuffer()) {
				fileSize = (long)context.getContentBuffer().length;
			} else {
				fileSize = CommonFileUtils.getFileLength(localPath);
			}

			context.setFileSize(fileSize);
		} catch (Exception var15) {
			throw new UnknownException(var15.toString());
		}

		int sliceSize = context.getSliceSize();
		StringBuilder entireDigestSb = new StringBuilder();
		String slicePartDigest = "";

		try {
			if(context.isEnableShaDigest()) {
				if(context.isUploadFromBuffer()) {
					slicePartDigest = CommonCodecUtils.getSlicePartSha1(context.getContentBuffer(), sliceSize, entireDigestSb);
				} else {
					slicePartDigest = CommonCodecUtils.getSlicePartSha1(localPath, sliceSize, entireDigestSb);
				}

				context.setEntireFileSha(entireDigestSb.toString());
				LOG.debug("slicePartDigest: " + slicePartDigest);
			}
		} catch (Exception var16) {
			throw new UnknownException(var16.getMessage());
		}

		String url = context.getUrl();
		long signExpired = System.currentTimeMillis() / 1000L + (long)this.config.getSignExpired();
		String sign = this.sign;
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(url);
		httpRequest.addHeader("Authorization", sign);
		httpRequest.addHeader("User-Agent", this.config.getUserAgent());
		httpRequest.addParam("filesize", String.valueOf(fileSize));
		httpRequest.addParam("slice_size", String.valueOf(sliceSize));
		httpRequest.addParam("op", "upload_slice_init");
		httpRequest.addParam("insertOnly", String.valueOf(context.getInsertOnly().ordinal()));
		if(context.isEnableShaDigest()) {
			httpRequest.addParam("sha", entireDigestSb.toString());
			httpRequest.addParam("uploadparts", slicePartDigest);
		}

		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setContentType(HttpContentType.MULTIPART_FORM_DATA);
		JSONObject resultJson = null;
		String resultStr = this.httpClient.sendHttpRequest(httpRequest);
		LOG.debug("sendSliceInit, resultStr: " + resultStr);
		resultJson = new JSONObject(resultStr);
		return resultJson;
	}

	private JSONObject sendSliceDataParallel(UploadSliceFileContext context, SliceCheckPoint scp) throws AbstractCosException {
		ArrayList allSliceTasks = new ArrayList();
		int threadNum = 1;
		if(!context.isSerialUpload()) {
			threadNum = context.getTaskNum();
		}

		ExecutorService service = Executors.newFixedThreadPool(threadNum);
		String url = context.getUrl();
		long signExpired = (long)this.config.getSignExpired();

		for(int taskResult = 0; taskResult < scp.sliceParts.size(); ++taskResult) {
			if(!((SlicePart)scp.sliceParts.get(taskResult)).isUploadCompleted()) {
				Cos.SliceFileDataTask dataTask = new Cos.SliceFileDataTask(taskResult, taskResult, scp, context, this.httpClient, this.sign, url, signExpired);
				allSliceTasks.add(service.submit(dataTask));
			}
		}

		service.shutdown();

		try {
			service.awaitTermination(9223372036854775807L, TimeUnit.SECONDS);
			service.shutdownNow();
		} catch (Exception var14) {
			throw new UnknownException(var14.getMessage());
		}

		JSONObject var15 = null;
		if(allSliceTasks.size() == 0) {
			var15 = new JSONObject();
			var15.put("code", 0);
			return var15;
		} else {
			Iterator var16 = allSliceTasks.iterator();

			while(var16.hasNext()) {
				Future task = (Future)var16.next();

				try {
					var15 = (JSONObject)task.get();
				} catch (Exception var13) {
					throw new UnknownException(var13.getMessage());
				}

				if(var15.getInt("code") != 0) {
					return var15;
				}
			}

			return var15;
		}
	}

	private JSONObject sendSliceFinish(UploadSliceFileContext context) throws AbstractCosException {
		String url = context.getUrl();
		long signExpired = System.currentTimeMillis() / 1000L + (long)this.config.getSignExpired();
		String sign = this.sign;
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(url);
		httpRequest.addHeader("Authorization", sign);
		httpRequest.addHeader("User-Agent", this.config.getUserAgent());
		httpRequest.addParam("session", context.getSessionId());
		httpRequest.addParam("op", "upload_slice_finish");
		if(context.isEnableShaDigest()) {
			httpRequest.addParam("sha", context.getEntireFileSha());
		}

		httpRequest.addParam("filesize", String.valueOf(context.getFileSize()));
		httpRequest.setContentType(HttpContentType.MULTIPART_FORM_DATA);
		httpRequest.setMethod(HttpMethod.POST);
		JSONObject resultJson = null;
		String resultStr = this.httpClient.sendHttpRequest(httpRequest);
		resultJson = new JSONObject(resultStr);
		LOG.debug("sendSliceFinish, resultStr: " + resultStr);
		return resultJson;
	}

	public String getFileLocal(GetFileLocalRequest request) throws AbstractCosException {
		InputStream in = this.getFileInputStream(request);
		BufferedInputStream bis = new BufferedInputStream(in);
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(new File(request.getLocalPath()));
		} catch (FileNotFoundException var16) {
			throw new UnknownException(var16.getMessage());
		}

		BufferedOutputStream bos = new BufferedOutputStream(out);

		try {
			int inByte;
			try {
				while((inByte = bis.read()) != -1) {
					bos.write(inByte);
				}
			} catch (IOException var17) {
				throw new UnknownException(var17.getMessage());
			}
		} finally {
			try {
				bis.close();
				bos.close();
			} catch (IOException var15) {
				throw new UnknownException(var15.getMessage());
			}
		}

		JSONObject retJson = new JSONObject();
		retJson.put("code", 0);
		retJson.put("message", "SUCCESS");
		return retJson.toString();
	}

	public InputStream getFileInputStream(GetFileInputStreamRequest request) throws AbstractCosException {
		String url = this.buildGetFileUrl(request);
		long signExpired = System.currentTimeMillis() / 1000L + (long)this.config.getSignExpired();
		String sign = this.sign;
		StringBuilder rangeBuilder = new StringBuilder();
		if(request.getRangeStart() != 0L || request.getRangeEnd() != 9223372036854775807L) {
			rangeBuilder.append("bytes=").append(request.getRangeStart()).append("-");
			rangeBuilder.append(request.getRangeEnd());
		}

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(url);
		httpRequest.addHeader("User-Agent", this.config.getUserAgent());
		if(!rangeBuilder.toString().isEmpty()) {
			httpRequest.addHeader("Range", rangeBuilder.toString());
		}

		if(!request.getReferer().isEmpty()) {
			httpRequest.addHeader("Referer", request.getReferer());
		}

		httpRequest.addParam("sign", sign);
		return this.httpClient.getFileInputStream(httpRequest);
	}
}