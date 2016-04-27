package com.sina.crawl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * @author DianaCody
 * @version 1.0
 * @filename LoadHTML.java
 * @note (1) Download html pages according to url (search keywords),
 * (2) Defined 3 methods to get html: normal, custom cookie policy, and proxyIP.
 * @since 2014-09-27 15:23:28
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */
public class LoadHTML {
    /**
     *
     * @param url 需要爬取的URL
     * @return html[0] 状态码
     *          html[1] 网页源码
     *          请求超时时状态码为null 网页源码为 "null"
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String[] getHTML(String url) throws ClientProtocolException, IOException {
        return getHTML(url,2000);
    }

    /**
     *
     * @param url 需要爬取的URL
     * @param delay  超时毫秒数
     * @return html[0] 状态码
     *          html[1] 网页源码
     *          请求超时时状态码为null 网页源码为 "null"
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String[] getHTML(String url,int delay) throws ClientProtocolException, IOException {
        String[] html = new String[2];
        html[1] = "null";
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(delay) //设置socket超时
                .setConnectTimeout(delay) //设置connect超时
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
        //设置cookie实现模拟登陆
        //httpGet.setHeader("Cookie","SINAGLOBAL=1412067995406.6873.1459996236314; _ga=GA1.2.1750097018.1460394345; crtg_rta=; wb_publish_vip_1733919541=5; YF-V5-G0=c948c7abbe2dbb5da556924587966312; _s_tentry=login.sina.com.cn; Apache=3290468933992.088.1461730698496; ULV=1461730698502:22:22:4:3290468933992.088.1461730698496:1461729614994; YF-Page-G0=d0adfff33b42523753dc3806dc660aa7; myuid=1733919541; YF-Ugrow-G0=56862bac2f6bf97368b95873bc687eef; login_sid_t=55a205bce2d019889e3c32d825bbffce; UOR=,,login.sina.com.cn; SUS=SID-1733919541-1461730726-GZ-b2j16-a4bc4f63a5a9f985ee75a5ef6f59c1f5; SUE=es%3Df10f64231843c2041f596f51f7e7a580%26ev%3Dv1%26es2%3D3fd544bedc1216a95ff6a5b3d66b2280%26rs0%3DQVP93VzW1RtgF76uBXUflBQrYx0uGQu%252Fjk3msGAwR66PsqJgqn03IlmLFwLwXzyq7i6Fr4ntXJoedIBiN2YMzNVyD789RDAxNjceCBnPZMblzp%252B1deXqp9s7CWvz%252BknU7TC2f31WVXjnLMhbTi2SyCDWse7GaFjmN4j5mSFxihE%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1461730726%26et%3D1461817126%26d%3Dc909%26i%3Dc1f5%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D0%26st%3D0%26uid%3D1733919541%26name%3D497045390%2540qq.com%26nick%3D497045390%26fmp%3D%26lcp%3D2013-10-15%252013%253A29%253A48; SUB=_2A256JE32DeRxGedJ6FEY8SfJzz2IHXVZUDg-rDV8PUNbuNBeLRD4kW9LHesTh6CmungMCzmxUCSaEv8tsZQ5Ow..; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5.WMcs8CYgYl1Il08eVeyh5JpX5K2t; SUHB=0wn0epwEM5iUTc; ALF=1493266726; SSOLoginState=1461730727; un=497045390@qq.com; wvr=6");
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            html[0] = String.valueOf(response.getStatusLine().getStatusCode());
            html[1] = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            System.out.println("Connection timeout...");
        }
        return html;
    }
    /**
     * cookie方法的getHTMl(): 设置cookie策略,防止cookie rejected问题,拒绝写入cookie
     * @return return the state code
     */
    public String[] getHTML(String url, String hostName, int port) throws URISyntaxException, ClientProtocolException, IOException {
        return getHTML(url,hostName,port,2000);
    }
    /**
     * cookie方法的getHTMl(): 设置cookie策略,防止cookie rejected问题,拒绝写入cookie
     * @return return the state code
     */
    public String[] getHTML(String url, String hostName, int port,int delay) throws URISyntaxException, ClientProtocolException, IOException {
        //自定义的cookie策略,解决cookie rejected问题(cookie拒绝写入)
        HttpHost proxy = new HttpHost(hostName, port);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        CookieSpecProvider cookieSpecProvider = new CookieSpecProvider() {
            public CookieSpec create(HttpContext context) {
                return new BrowserCompatSpec() {
                    @Override
                    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
                        //Oh, I am easy;
                    }
                };
            }
        };
        Registry<CookieSpecProvider> r = RegistryBuilder
                .<CookieSpecProvider>create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
                .register("easy", cookieSpecProvider)
                .build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("easy")
                .setSocketTimeout(4000) //设置socket超时
                .setConnectTimeout(4000) //设置connect超时
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieSpecRegistry(r)
                .setRoutePlanner(routePlanner)
                .build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
        httpGet.setConfig(requestConfig);
        String[] html = new String[2];
        html[0]= null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            html[0] = String.valueOf(response.getStatusLine().getStatusCode());
            html[1] = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            //System.out.println("Connection timeout...");
        }
        return html;
    }
    /**
     * proxy代理IP方法 用于测试IP有效性？
     * @return html content
     */
    public String getHTMLbyProxy(String targetURL, String hostName, int port ) throws ClientProtocolException, IOException {
        return getHTMLbyProxy(targetURL,hostName,port,2000);
    }
    /**
     * proxy代理IP方法 用于测试IP有效性？
     * @return html content
     */
    public String getHTMLbyProxy(String targetURL, String hostName, int port ,int delay) throws ClientProtocolException, IOException {
        HttpHost proxy = new HttpHost(hostName, port);
        String html = null;
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(delay) //设置socket超时
                .setConnectTimeout(delay) //设置connect超时
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .setDefaultRequestConfig(requestConfig)
                .build();
        HttpGet httpGet = new HttpGet(targetURL); //"http://iframe.ip138.com/ic.asp"
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println(response.getStatusLine().getStatusCode());
            if (statusCode == HttpStatus.SC_OK) {
                html = EntityUtils.toString(response.getEntity(), "gb2312");
            }
            response.close();
            //System.out.println(html);
        } catch (IOException e) {
            //System.out.println("Connection timeout...");
        }
        return html;
    }

    /**
     * 测试用方法
     * @param arg
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void main(String[] arg) throws IOException,URISyntaxException{
        String url = "http://weibo.com/wwh1980?refer_flag=1001030103_&is_all=1";
        String[] html = new LoadHTML().getHTML(url);
        //System.out.println(html);
        writeToFile(html[1],1);
    }

    public static void writeToFile(String content,int pageNum){
        File file = new File("E:\\test\\page"+pageNum+".html");// 要写入的文本文件
        if(content == null)
            content = "null";
        try{
            if (!file.exists()) {// 如果文件不存在，则创建该文件
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);// 获取该文件的输出流
            writer.write(content);// 写内容
            writer.flush();// 清空缓冲区，立即将输出流里的内容写到文件里
            writer.close();// 关闭输出流，施放资源
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

}
