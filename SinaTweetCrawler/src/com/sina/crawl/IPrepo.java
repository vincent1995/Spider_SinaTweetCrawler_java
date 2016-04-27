package com.sina.crawl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.ClientProtocolException;

/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * @filename IPrepo.java
 * @version  1.0
 * @note     Get local IP repository
 *             (1) Find candidate IPs from proxyIP website;
 *             (2) Verify IPs to use them;
 * @author   DianaCody
 * @since    2014-09-27 15:23:28
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
public class IPrepo {
	
	/** candidate IPs from proxyIP website */
	public static Vector<String> getProxyIPs(String html) throws ClientProtocolException, IOException {
		Vector<String> IPs = new Vector<String>();
		Pattern p = Pattern.compile("<td>\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}</td>\\n+?\\s+?<td>\\d{1,5}</td>"); //<td>\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3}</td>\n+?\s+?<td>\d{1,5}</td>
		Matcher m = p.matcher(html);
		String s, ip, port, address;
		while(m.find()) {
			s = m.group();
			ip = s.split("</td>")[0].replace("<td>", "");
			port = s.split("</td>")[1].split("<td>")[1].replace("</td>","");
			address = ip + ":" +port;
			if(Integer.parseInt(port) < 65535) { //port:0~65535
				if(! IPs.contains(address)) {
					IPs.add(address);
				}
			}
		}
		return IPs;
	}
	
	/**  Find proxy IPs from website "http://www.xici.net.co/" */
	public static Vector<String> getIPsPageLinks() throws ClientProtocolException, URISyntaxException, IOException {
		Vector<String> IPsPageLinks = new Vector<String>();
		IPsPageLinks.add("http://www.xici.net.co/nn/"); //national High anonymity IPs
		//IPsPageLinks.add("http://www.xici.net.co/nt/"); //national transparent IPs
		//IPsPageLinks.add("http://www.xici.net.co/wn/"); //international High anonymity IPs
		//IPsPageLinks.add("http://www.xici.net.co/wt/"); //international transparent IPs
		//IPsPageLinks.add("http://www.xici.net.co/qq/"); //qq proxy IPs
		return IPsPageLinks;
	}
	
	/**
	 * Get all unverified proxy in all IP library links.
	 * @param ipLibURL: a specified URL "http://www.xici.net.co/"
	 * @return a String Vector contains all unverified IPs
	 */
	public static Vector<String> getAllProxyIPs(String ipLibURL) throws ClientProtocolException, IOException, URISyntaxException {
		Vector<String> IPsPageLinks = getIPsPageLinks(); //"http://www.xici.net.co/"
		Vector<String> onePageIPs = new Vector<String>();
		Vector<String> allIPs = new Vector<String>();
		for(int i=0; i<IPsPageLinks.size(); i++) {
			String origin_url = IPsPageLinks.get(i);
			String url = origin_url;
			String[] html = new LoadHTML().getHTML(url);
			int page = 2;
			while(true) {
				if(html[0] != null) {
					FileWR.write(html[1],"e:/tweet/IpPages/ip"+(page-1)+".html");
					if(html[0].equals("404"))
						break;
					//System.out.println("状态码 " + html[0]);
					System.out.println("start finding proxy IPs under this link: " + url);
					onePageIPs = getProxyIPs(html[1]);
					for(int j=0; j<onePageIPs.size(); j++) {
						String s = onePageIPs.get(j);
						if(! allIPs.contains(s)) {
							allIPs.add(s);
						}
					}
				}
				url = origin_url + page;
				html = new LoadHTML().getHTML(url);
				page ++;
                //for test
                break;
			}
		}
		System.out.println("total proxy IP number: " + allIPs.size());
		return allIPs;
	}
	
	/** Test all proxy IPs and select a valid one from all candidate proxy IPs.
	 * @param allIPs Vector<String>: All proxy IPs
	 * @return Vector<String>: All valid IPs
	 * @throws ClientProtocolException, IOException
	 */
	public static Vector<String> getValidProxyIPs(Vector<String> allIPs) throws ClientProtocolException, IOException {
		System.out.println("Start getting valid proxy IPs...");
		Vector<String> validHostname = new Vector<String>();
		Vector<String> validIPs = new Vector<String>();
		int validIPNum = 0;
		for(int i=0; i<allIPs.size(); i++) {
			if(i%10 == 0) {
                System.out.println("already varifed " + i+" ips, have "+validIPNum+" valid IP");
			}
			String ip = allIPs.get(i);
            String hostName = ip.split(":")[0];
            int port = Integer.parseInt(ip.split(":")[1]);
            String varifyURL = "http://www.ip138.com/";
            String html = new LoadHTML().getHTMLbyProxy(varifyURL, hostName, port,1000);
//			int iReconn = 0;
//			while(html.equals("null")) { //reconnect 2 times (total 3 times connection)
//				if(iReconn == 2) {
//					break;
//				}
//				html = new LoadHTML().getHTMLbyProxy(varifyURL, hostName, port);
//				iReconn ++;
//			}
            if(html !=null){
                validIPNum ++;
                validIPs.add(ip);
            }
//			Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
//			Matcher m = p.matcher(html);
//			String s;
//			if(m.find()) {
//				s = m.group();
//				if(! validHostname.contains(s)) {
//					validHostname.add(s);
//					validIPs.add(s+":"+String.valueOf(port));
//					//bw.write(s+"\r\n"); //write a valid proxy ip
//					validIPNum ++;
//					System.out.println("Valid proxy IP"+s+": "+String.valueOf(port));
//				}
//			}
//			else {
//				System.out.println("Html doesn't contain an IP.");
//			}
		}
		System.out.println("Total number of valid IPs: " + validIPNum);
		return validIPs;
	}
	
	/**
	 * Verify all valid IPs then save to the IPrepo (data file).
	 * @param validIPs -the Vector<String> contains all valid IPs
	 * @param repoPath -a String giving a path to save all final usable IPs. "Final usable" IP indicate
	 * @return IPrepo
	 * @throws ClientProtocolException, IOException
	 */
	public static Vector<String> classifyIPs(Vector<String> validIPs, String repoPath) throws ClientProtocolException, IOException {
		final String verificationURL = "http://s.weibo.com/weibo/abc?topnav=1&wvr=6&b=1";
		//Vector<String> utf8IPs = new Vector<String>();
		Vector<String> IPrepo = new Vector<String>();
		int ipNum = 0;
		String ip;
		//int ConnectionTimes = 0;
		for(int i=0; i<validIPs.size(); i++) {
			if(i%10 == 0) {
				System.out.println("already varifed " + i+" ips, have "+ipNum+" valid IP");
			}
			ip = validIPs.get(i);
			String html = new LoadHTML().getHTMLbyProxy(verificationURL, ip.split(":")[0], Integer.parseInt(ip.split(":")[1]));
//			int iReconnectTimes = 0;
//			while(html.equals("null")) {
//				if(iReconnectTimes == 4) {
//					break;
//				}
//				html = new LoadHTML().getHTMLbyProxy(verificationURL, ip.split(":")[0], Integer.parseInt(ip.split(":")[1]));
//				iReconnectTimes ++;
//				System.out.println(ip+" is reconnecting the "+iReconnectTimes+" times");
//			}
			if(html!=null){
				IPrepo.add(ip);
				ipNum++;
			}
		}
		System.out.println("Total number of valid IPs: " + ipNum);
		return IPrepo;
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException, URISyntaxException, InterruptedException {
		long starttime = System.currentTimeMillis();
		File dirRawIp = new File("e:/tweet/IpPages/");
		dirRawIp.mkdirs();
		String ipLibURL = "http://www.xici.net.co/";
		Vector<String> allIPs = getAllProxyIPs(ipLibURL);
		Vector<String> validIPs = getValidProxyIPs(allIPs);
		//Vector<String> IPrepo = classifyIPs(validIPs, "e:/tweet/IPrepo.txt");
		FileWR.write(allIPs, "e:/tweet/allIPs.txt");
		FileWR.write(validIPs, "e:/tweet/validIPs.txt");
		//FileWR.write2txt(IPrepo, "e:/tweet/IPrepo.txt");
		
		long endtime = System.currentTimeMillis();
		System.out.println((double)(endtime-starttime)/60000 + "mins");
	}
//
//	public static void writeToFile(String content,int pageNum){
//		try{
//			File file = new File("E:\\test\\page"+pageNum+".html");// 要写入的文本文件
//			if (!file.exists()) {// 如果文件不存在，则创建该文件
//				file.createNewFile();
//			}
//			FileWriter writer = new FileWriter(file);// 获取该文件的输出流
//			writer.write(content);// 写内容
//			writer.flush();// 清空缓冲区，立即将输出流里的内容写到文件里
//			writer.close();// 关闭输出流，施放资源
//		}
//		catch(IOException e){
//			System.out.println(e.getMessage());
//		}
//	}

}
