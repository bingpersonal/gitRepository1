package com.bing.processor;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
/**
 * 找的下载小说url，另行使用了HTTPclient开启post提交，将response中文件流保存到本地
 * 表单提交中的url（下载小说url）不会自动提取，根据url规律直接拼接了
 * 网站有时会连接不上
 */
public class MyProcessor implements PageProcessor {
	private static Logger logger = LoggerFactory.getLogger(MyProcessor.class);
	// 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setRetryTimes(3).setTimeOut(20000)
			.setSleepTime(5000).setCycleRetryTimes(3);
	private static int count =0;

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {

		//	List<String> urlList0= page.getHtml().links().all();  
		//获取列表区域内所有url
		List<String> urlList= page.getHtml().xpath("//section[@class=\"le\"]").links().regex("http://www.shubao97.com/book/[0-9]*").all();

		String urlNextPage = page.getHtml().xpath("//section[@class=\"pages\"]/a[@rel=\"next\"]").links().get();


	/*	page.putField("name", page.getHtml().xpath("//section[@class=\"ibook\"]/h1/text()").toString());
		page.putField("type", page.getHtml().xpath("//section[@class=\"ibook\"]/figure/figcaption/span[1]/a/text()").toString());
		page.putField("size", page.getHtml().xpath("//section[@class=\"ibook\"]/figure/figcaption/span[2]/text()").toString());
		page.putField("author", page.getHtml().xpath("//section[@class=\"ibook\"]/figure/figcaption/span[4]/text()").toString());
		//	page.putField("status", page.getHtml().xpath("https://github\\.com/(\\w+)/.*").toString());
		page.putField("totalClick", page.getHtml().xpath("//section[@class=\"ibook\"]/figure/figcaption/span[5]/text()").toString());
		page.putField("totalCollection", page.getHtml().xpath("//section[@class=\"ibook\"]/figure/figcaption/span[6]/text()").toString());
		page.putField("updateTime", page.getHtml().xpath("//section[@class=\"ibook\"]/figure/figcaption/span[8]/text()").toString());
		page.putField("welcome", page.getHtml().xpath("//section[@class=\"votes\"]/a[1]/span[1]/tidyText()").toString());
		page.putField("dislike", page.getHtml().xpath("//section[@class=\"votes\"]/a[2]/span[1]/tidyText()").toString());
		String currentPageUrl=page.getUrl().toString();
		page.putField("num", currentPageUrl.substring(currentPageUrl.lastIndexOf("/")+1));*/
		if (page.getUrl().toString().contains("/hxiaoshuo/")) {
			//skip this page
			page.setSkip(true);


			page.addTargetRequests(urlList);
			page.addTargetRequest(urlNextPage);
			logger.error("urlNextPage"+urlNextPage);
			logger.error("urlList"+urlList.toString());
			//	System.out.println( urlList);


		}else {


		/*	try {

				download(page.getResultItems().get("num"));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		//    page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));







		//	page.addTargetRequest(urlNextPage);


		//	System.out.println( urlNextPage);
		// 	System.out.println( urlList);


		//System.out.println( "page.getUrl()"+page.getUrl());
		//   System.out.println( page.getHtml().all());
		count++;
	}

	private static Set<String> set = new HashSet<>();
	public static void main(String[] args) throws FileNotFoundException {
		/*File file=new File("D:\\www.shubao97.com");
		String[] files =file.list();

		for (String string : files) {
			set.add(string);
		}*/



		long startTime, endTime;
		System.out.println("开始爬取...");
		startTime = System.currentTimeMillis();
		//	Spider.create(new MyProcessor()).addUrl("http://www.shubao97.com/book/77239").run();
		Spider.create(new MyProcessor()).addUrl("http://www.shubao97.com/hxiaoshuo/191")
		.thread(5)//.addPipeline(new JsonFilePipeline("D:\\webmagic\\"))
		.run();
		endTime = System.currentTimeMillis();
		System.out.println("爬取结束，耗时约" + ((endTime - startTime) / 1000) + "秒，抓取了"+count+"条记录");



	}







	void download(String num) throws ClientProtocolException, IOException{
		
		CloseableHttpClient httpClient = HttpClients.createDefault();

		//创建一个post对象
		String url = "http://www.shubao97.com/book/down/"+num+"/0";
		HttpPost post =new HttpPost(url);

		//创建一个Entity。模拟一个表单

		List<NameValuePair>kvList = new ArrayList<>();

		kvList.add(new BasicNameValuePair("checkcode","www.shubao97.com"));

		//kvList.add(new BasicNameValuePair("password","123"));

		//包装成一个Entity对象

		StringEntity entity = new UrlEncodedFormEntity(kvList,"utf-8");

		//设置请求的内容

		post.setEntity(entity);

		//执行post请求http://www.shubao97.com/download/77239

		CloseableHttpResponse response =httpClient.execute(post);

		HttpEntity httpEntity = response.getEntity();

		String desc = response.getFirstHeader("Content-Disposition").toString();


		String fstr = "filename=";
		int pos1 = desc.indexOf(fstr);

		String fn = desc.substring(pos1 + fstr.length(),desc.length()-1);



		//	String code1 = "windows-1252";
		String code2 = "ISO-8859-1";
		//	String name = fn;
		//System.out.println(new String(name.getBytes(code1), "GBK"));
		// System.out.println(URLDecoder.decode(fn,"utf-8"));
		//System.out.println(new String(name.getBytes(code2), "GBK"));
		//  System.out.println(URLDecoder.decode(name,"utf-8"));
		String filename = new String(fn.getBytes(code2), "GBK");

		if (isValidFileName(filename)) {

		}else {
			logger.error("filename:"+filename+":"+num);
			System.out.println(filename+":"+num);
			filename = num;
		}
		if(set.contains(filename)) {
			logger.error("已下载过："+filename);
			System.out.println("已下载过："+filename);
			response.close();
			httpClient.close();
			return;
		}
		System.out.println("download"+num);

		InputStream is = httpEntity.getContent();
		// 根据InputStream 下载文件
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int r = 0;
		while ((r = is.read(buffer)) > 0) {
			output.write(buffer, 0, r);
		}





		FileOutputStream fos = new FileOutputStream("D:\\www.shubao97.com\\"+filename);
		output.writeTo(fos);
		output.flush();
		output.close();
		fos.close();
		EntityUtils.consume(httpEntity);

		response.close();

		httpClient.close();
		System.out.println("结束download"+num);
	}
	public static boolean isValidFileName(String fileName) { 
		if (fileName == null || fileName.length() > 255) {
			return false; 
		}else {
			return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$"); }
	}
}