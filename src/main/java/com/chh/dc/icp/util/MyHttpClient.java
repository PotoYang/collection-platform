package com.chh.dc.icp.util;

import java.io.IOException;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author fulr
 * 单例模式
 * 自带连接池
 *
 */
public class MyHttpClient {

	private static final Logger LOG = Logger.getLogger(MyHttpClient.class);

//	private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

	private CloseableHttpClient client;

	private RequestConfig requestConfig;

	private ContentType TEXT_PLAIN_UTF8 = ContentType.TEXT_PLAIN.withCharset(Consts.UTF_8);

	private static MyHttpClient instance= new MyHttpClient();

	private MyHttpClient(){
//		client = new HttpClient(connectionManager);
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
//                .setMalformedInputAction(CodingErrorAction.IGNORE)
//                .setUnmappableInputAction(CodingErrorAction.IGNORE)
				.setCharset(Consts.UTF_8)
				.build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setDefaultConnectionConfig(connectionConfig);
		cm.setMaxTotal(50);
//        cm.setValidateAfterInactivity(3000);
		client = HttpClients.custom().setConnectionManager(cm).build();

		requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();//设置请求和传输超时时间
	}

	public static MyHttpClient getInstance(){
		return instance;
	}

	public Object getJsonRequest(String url) throws Exception {
		CloseableHttpResponse response = null;
		try {
			HttpGet httpget = new HttpGet(url);
			httpget.setConfig(requestConfig);
			response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			String res = EntityUtils.toString(entity, Consts.UTF_8);
			LOG.debug(url + " 返回数据:" + res);
			return JSON.parse(res);

		} catch (Exception e) {
			throw e;
		}
		finally {
			if(response!=null){
				response.close();
			}
		}
	}

	public <T> T getJsonRequest(String url, Class<T> cls)
			throws IOException {
		CloseableHttpResponse response = null;
		try {
			HttpGet httpget = new HttpGet(url);
			httpget.setConfig(requestConfig);
			response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			String res = EntityUtils.toString(entity, Consts.UTF_8);
			LOG.debug(url + " 返回数据:" + res);
			return JSONObject.parseObject(res, cls);
		} finally {
			if(response!=null){
				response.close();
			}
		}
	}


	public <T> T postJsonRequest(String url,Map<String, String> paramMap, Class<T> cls)
			throws IOException {
		CloseableHttpResponse response = null;
		try {
			HttpPost httppost = new HttpPost(url);
//			ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
			if(paramMap!=null&&paramMap.size()>0){
				MultipartEntityBuilder reqB = MultipartEntityBuilder.create();
				reqB.setCharset(Consts.UTF_8);
				for(String key : paramMap.keySet()){
					if(!StringUtil.isEmpty(paramMap.get(key))){
						reqB.addPart(key, new StringBody(paramMap.get(key), TEXT_PLAIN_UTF8));
					}
				}
				HttpEntity reqEntity = reqB.build();
				httppost.setEntity(reqEntity);
			}
			httppost.setConfig(requestConfig);
			response = client.execute(httppost);
			HttpEntity entity = response.getEntity();
			String res = EntityUtils.toString(entity, Consts.UTF_8);
			LOG.debug(url +",参数map："+ JSON.toJSONString(paramMap)+" 返回数据:" + res);
			return JSONObject.parseObject(res, cls);
		} finally {
			// 关闭连接
			if(response!=null){
				response.close();
			}
		}
	}

	public <T> T postJsonRequest(String url,String body, Class<T> cls)
			throws IOException {
		CloseableHttpResponse response = null;
		try {
			HttpPost httppost = new HttpPost(url);
			StringEntity bodyContent = new StringEntity(body, Consts.UTF_8);
			httppost.setEntity(bodyContent);

			httppost.setConfig(requestConfig);
			response = client.execute(httppost);
			HttpEntity entity = response.getEntity();
			String res = EntityUtils.toString(entity, Consts.UTF_8);
			LOG.debug(url +",参数body："+ body+" 返回数据:" + res);
			return JSONObject.parseObject(res, cls);
		} finally {
//			// 关闭连接
			if(response!=null){
				response.close();
			}
		}
	}

}
