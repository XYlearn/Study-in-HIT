package com;
import java.nio.charset.Charset;
import java.util.Random;

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
	private static long appId = 1252826460;
	private static String secretId = "AKIDdAWpO0Ur57yhKqAYDhz8BapyymQtPrbC";
	private static String secretKey = "hEbnuxupcDcpvqkWKBrKfw66vkNoHPZa";
	private static String bucketName = "sihdatabase";
	private static String region = "tj";

	private ClientConfig clientConfig;
	private Credentials cred;
	private COSClient cosClient;

	String getSign(String path) {
		try {
			return Sign.getPeriodEffectiveSign(bucketName, path, cred, 21600);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Cos() {
		clientConfig = new ClientConfig();
		clientConfig.setRegion(region);
		cred = new Credentials(appId, secretId, secretKey);
		cosClient = new COSClient(clientConfig, cred);
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
      /*  // 2. 下载文件
        String localPathDown = "/home/xy16/Study-in-Hit/test5";
        GetFileLocalRequest getFileLocalRequest =
                new GetFileLocalRequest(bucketName, cosFilePath, localPathDown);
        getFileLocalRequest.setUseCDN(false);
        getFileLocalRequest.setReferer("");
        String getFileResult = cosClient.getFileLocal(getFileLocalRequest);
        System.out.println("getFileResult:" + getFileResult);

        // 3. 上传文件(覆盖)
        // 将本地的local_file_2.txt上传到bucket下的根分区下,并命名为sample_file.txt
        String localFilePath2 = "src/test/resources/local_file_2.txt";
        byte[] contentBuffer = CommonFileUtils.getFileContent(localFilePath2)
                .getBytes(Charset.forName(("ISO-8859-1")));
        UploadFileRequest overWriteFileRequest =
                new UploadFileRequest(bucketName, cosFilePath, contentBuffer);
        overWriteFileRequest.setInsertOnly(InsertOnly.OVER_WRITE);
        String overWriteFileRet = cosClient.uploadFile(overWriteFileRequest);
        System.out.println("overwrite file ret:" + overWriteFileRet);

        // 4. 获取文件属性
        StatFileRequest statFileRequest = new StatFileRequest(bucketName, cosFilePath);
        String statFileRet = cosClient.statFile(statFileRequest);
        System.out.println("stat file ret:" + statFileRet);

        // 5. 更新文件属性
        UpdateFileRequest updateFileRequest = new UpdateFileRequest(bucketName, cosFilePath);
        updateFileRequest.setBizAttr("测试目录");
        updateFileRequest.setAuthority(FileAuthority.WPRIVATE);
        updateFileRequest.setCacheControl("no cache");
        updateFileRequest.setContentDisposition("cos_sample.txt");
        updateFileRequest.setContentLanguage("english");
        updateFileRequest.setContentType("application/json");
        updateFileRequest.setXCosMeta("x-cos-meta-xxx", "xxx");
        updateFileRequest.setXCosMeta("x-cos-meta-yyy", "yyy");
        updateFileRequest.setContentEncoding("gzip");
        String updateFileRet = cosClient.updateFile(updateFileRequest);
        System.out.println("update file ret:" + updateFileRet);

        // 6. 更新文件后再次获取属性
        statFileRet = cosClient.statFile(statFileRequest);
        System.out.println("stat file ret:" + statFileRet);

        // 6.1 move文件，从/sample_file.txt移动为./sample_file.txt.bak
        String dstFilePath = cosFilePath + ".bak";
        MoveFileRequest moveRequest = new MoveFileRequest(bucketName, cosFilePath, dstFilePath);
        String moveFileRet = cosClient.moveFile(moveRequest);
        System.out.println("first move file ret:" + moveFileRet);
        // 6.2 在从/sample_file.txt.bak移动为/sample_file.txt
        moveRequest = new MoveFileRequest(bucketName, dstFilePath, cosFilePath);
        moveFileRet = cosClient.moveFile(moveRequest);
        System.out.println("second move file ret:" + moveFileRet);

        // 7. 删除文件
        DelFileRequest delFileRequest = new DelFileRequest(bucketName, cosFilePath);
        String delFileRet = cosClient.delFile(delFileRequest);
        System.out.println("del file ret:" + delFileRet);

        ///////////////////////////////////////////////////////////////
        // 目录操作 //
        ///////////////////////////////////////////////////////////////
        // 1. 生成目录, 目录名为sample_folder
        String cosFolderPath = "/xxsample_folder/";
        CreateFolderRequest createFolderRequest =
                new CreateFolderRequest(bucketName, cosFolderPath);
        String createFolderRet = cosClient.createFolder(createFolderRequest);
        System.out.println("create folder ret:" + createFolderRet);

        // 2. 更新目录的biz_attr属性
        UpdateFolderRequest updateFolderRequest =
                new UpdateFolderRequest(bucketName, cosFolderPath);
        updateFolderRequest.setBizAttr("这是一个测试目录");
        String updateFolderRet = cosClient.updateFolder(updateFolderRequest);
        System.out.println("update folder ret:" + updateFolderRet);

        // 3. 获取目录属性
        StatFolderRequest statFolderRequest = new StatFolderRequest(bucketName, cosFolderPath);
        String statFolderRet = cosClient.statFolder(statFolderRequest);
        System.out.println("stat folder ret:" + statFolderRet);

        // 4. list目录, 获取目录下的成员
        ListFolderRequest listFolderRequest = new ListFolderRequest(bucketName, cosFolderPath);
        String listFolderRet = cosClient.listFolder(listFolderRequest);
        System.out.println("list folder ret:" + listFolderRet);

        // 5. 删除目录
        DelFolderRequest delFolderRequest = new DelFolderRequest(bucketName, cosFolderPath);
        String delFolderRet = cosClient.delFolder(delFolderRequest);
        System.out.println("del folder ret:" + delFolderRet);
        */
}
