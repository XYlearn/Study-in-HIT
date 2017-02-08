//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.exception.AbstractCosException;
import com.qcloud.cos.exception.ParamException;
import com.qcloud.cos.exception.ServerException;
import com.qcloud.cos.exception.UnknownException;
import com.qcloud.cos.http.AbstractCosHttpClient;
import com.qcloud.cos.http.HttpContentType;
import com.qcloud.cos.http.HttpRequest;
import com.qcloud.cos.meta.COSObjectInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import com.qcloud.cos.op.FileOp;
import com.qcloud.cos.request.GetFileLocalRequest;
import com.qcloud.cos.sign.Credentials;
import com.qcloud.cos.sign.Sign;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CosHttpClient extends AbstractCosHttpClient {
	private static final Logger LOG = LoggerFactory.getLogger(CosHttpClient.class);

	public static ClientConfig getDefaultConfig() {
		ClientConfig config = new ClientConfig();
		config.setRegion("tj");
		return config;
	}

	public CosHttpClient(ClientConfig config) {
		super(config);
	}

	private String getExceptionMsg(HttpRequest httpRequest, String exceptionStr) {
		String errMsg = "HttpRequest:" + httpRequest.toString() + "\nException:" + exceptionStr;
		LOG.error(errMsg);
		return errMsg;
	}

	protected String sendGetRequest(HttpRequest httpRequest) throws AbstractCosException {
		String url = httpRequest.getUrl();
		HttpGet httpGet = null;
		String responseStr = "";
		int retry = 0;
		int maxRetryCount = this.config.getMaxFailedRetry();

		while(true) {
			if(retry < maxRetryCount) {
				URIBuilder httpResponse;
				String errMsg;
				try {
					httpResponse = new URIBuilder(url);
					Iterator var20 = httpRequest.getParams().keySet().iterator();

					while(true) {
						if(!var20.hasNext()) {
							httpGet = new HttpGet(httpResponse.build());
							break;
						}

						errMsg = (String)var20.next();
						httpResponse.addParameter(errMsg, (String)httpRequest.getParams().get(errMsg));
					}
				} catch (URISyntaxException var18) {
					String e = "Invalid url:" + url;
					LOG.error(e);
					throw new ParamException(e);
				}

				httpGet.setConfig(this.requestConfig);
				this.setHeaders(httpGet, httpRequest.getHeaders());
				httpResponse = null;

				try {
					HttpResponse var19 = this.httpClient.execute(httpGet);
					int var21 = var19.getStatusLine().getStatusCode();
					if(var21 >= 500 && var21 <= 599) {
						errMsg = String.format("http status code is %d", new Object[]{Integer.valueOf(var21)});
						throw new IOException(errMsg);
					}

					responseStr = EntityUtils.toString(var19.getEntity(), "UTF-8");
					new JSONObject(responseStr);
					errMsg = responseStr;
				} catch (IOException | ParseException var15) {
					httpGet.abort();
					++retry;
					if(retry != maxRetryCount) {
						continue;
					}

					errMsg = this.getExceptionMsg(httpRequest, var15.toString());
					throw new ServerException(errMsg);
				} catch (JSONException var16) {
					errMsg = String.format("server response is not json, httpRequest: %s, httpResponse: %s, responseStr: %s", new Object[]{httpRequest.toString(), httpResponse.toString(), responseStr});
					throw new ServerException(errMsg);
				} finally {
					httpGet.releaseConnection();
				}

				return errMsg;
			}

			return responseStr;
		}
	}

	protected String sendPostRequest(HttpRequest httpRequest) throws AbstractCosException {
		String url = httpRequest.getUrl();
		String responseStr = "";
		int retry = 0;
		int maxRetryCount = this.config.getMaxFailedRetry();

		while(retry < maxRetryCount) {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(this.requestConfig);
			Map params = httpRequest.getParams();
			this.setHeaders(httpPost, httpRequest.getHeaders());
			if(httpRequest.getContentType() == HttpContentType.APPLICATION_JSON) {
				this.setJsonEntity(httpPost, params);
			} else if(httpRequest.getContentType() == HttpContentType.MULTIPART_FORM_DATA) {
				try {
					this.setMultiPartEntity(httpPost, params);
				} catch (Exception var16) {
					throw new UnknownException(var16.toString());
				}
			}

			HttpResponse httpResponse = null;

			try {
				String errMsg;
				try {
					httpResponse = this.httpClient.execute(httpPost);
					int e = httpResponse.getStatusLine().getStatusCode();
					if(e >= 500 && e <= 599) {
						errMsg = String.format("http status code is %d", new Object[]{Integer.valueOf(e)});
						throw new IOException(errMsg);
					}

					responseStr = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
					new JSONObject(responseStr);
					errMsg = responseStr;
					return errMsg;
				} catch (IOException | ParseException var17) {
					httpPost.abort();
					++retry;
					if(retry == maxRetryCount) {
						errMsg = this.getExceptionMsg(httpRequest, var17.toString());
						throw new ServerException(errMsg);
					}
				} catch (JSONException var18) {
					errMsg = String.format("server response is not json, httpRequest: %s, httpResponse: %s, responseStr: %s", new Object[]{httpRequest.toString(), httpResponse.toString(), responseStr});
					throw new ServerException(errMsg);
				}
			} finally {
				httpPost.releaseConnection();
			}
		}

		return responseStr;
	}

	public InputStream getFileInputStream(HttpRequest httpRequest) throws AbstractCosException {
		String url = httpRequest.getUrl();
		int retry = 0;
		int maxRetryCount = this.config.getMaxFailedRetry();

		while(true) {
			if(retry < maxRetryCount) {
				HttpGet httpGet = null;

				String errMsg;
				String entity;
				try {
					URIBuilder e = new URIBuilder(url);
					Iterator var13 = httpRequest.getParams().keySet().iterator();

					while(true) {
						if(!var13.hasNext()) {
							httpGet = new HttpGet(e.build());
							break;
						}

						entity = (String)var13.next();
						e.addParameter(entity, (String)httpRequest.getParams().get(entity));
					}
				} catch (URISyntaxException var11) {
					errMsg = "Invalid url:" + url;
					LOG.error(errMsg);
					throw new ParamException(errMsg);
				}

				httpGet.setConfig(this.requestConfig);
				this.setHeaders(httpGet, httpRequest.getHeaders());

				try {
					HttpResponse var12 = this.httpClient.execute(httpGet);
					int var14 = var12.getStatusLine().getStatusCode();
					if(var14 >= 500 && var14 <= 599) {
						entity = String.format("http status code is %d", new Object[]{Integer.valueOf(var14)});
						throw new IOException(entity);
					}

					if(var14 != 200 && var14 != 206) {
						entity = EntityUtils.toString(var12.getEntity(), "UTF-8");
						String var16 = String.format("getFileinputstream failed, httpRequest: %s, httpResponse: %s, responseStr: %s", new Object[]{httpRequest.toString(), var12.toString(), entity});
						httpGet.releaseConnection();
						throw new ServerException(var16);
					}

					HttpEntity var15 = var12.getEntity();
					COSObjectInputStream cosObjectInputStream = new COSObjectInputStream(var15.getContent(), httpGet);
					return cosObjectInputStream;
				} catch (IOException | ParseException var10) {
					++retry;
					httpGet.abort();
					httpGet.releaseConnection();
					if(retry != maxRetryCount) {
						continue;
					}

					errMsg = this.getExceptionMsg(httpRequest, var10.toString());
					throw new ServerException(errMsg);
				}
			}

			return null;
		}
	}

	private void setJsonEntity(HttpPost httpPost, Map<String, String> params) {
		ContentType utf8TextPlain = ContentType.create("text/plain", Consts.UTF_8);
		String postJsonStr = (new JSONObject(params)).toString();
		StringEntity stringEntity = new StringEntity(postJsonStr, utf8TextPlain);
		httpPost.setEntity(stringEntity);
	}

	private void setMultiPartEntity(HttpPost httpPost, Map<String, String> params) throws Exception {
		ContentType utf8TextPlain = ContentType.create("text/plain", Consts.UTF_8);
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		Iterator var5 = params.keySet().iterator();

		while(var5.hasNext()) {
			String paramKey = (String)var5.next();
			if(paramKey.equals("fileContent")) {
				entityBuilder.addBinaryBody("fileContent", ((String)params.get("fileContent")).getBytes(Charset.forName("ISO-8859-1")));
			} else {
				entityBuilder.addTextBody(paramKey, (String)params.get(paramKey), utf8TextPlain);
			}
		}

		httpPost.setEntity(entityBuilder.build());
	}

	private void setHeaders(HttpMessage message, Map<String, String> headers) {
		message.setHeader("Accept", "*/*");
		message.setHeader("Connection", "Keep-Alive");
		message.setHeader("User-Agent", this.config.getUserAgent());
		if(headers != null) {
			Iterator var3 = headers.keySet().iterator();

			while(var3.hasNext()) {
				String headerKey = (String)var3.next();
				message.setHeader(headerKey, (String)headers.get(headerKey));
			}
		}

	}
}
