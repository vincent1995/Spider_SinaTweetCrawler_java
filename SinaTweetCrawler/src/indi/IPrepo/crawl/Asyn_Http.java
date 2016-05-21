package indi.IPrepo.crawl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class Asyn_Http {

	public Object[] connect(String strUrl, int repeat) {

		URL url;
		URLConnection connection;
		try {
			url = new URL(strUrl);
			connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			connection.connect();

		}
		catch(SocketTimeoutException se) {

    		return new Object[] {repeat+1};         		
    	}
    	catch(Exception e) {
    		return new Object[] {-1};
    	}
    	return new Object[] { 0, url, connection };
	}

	public Object[] proxy_connect(String strUrl, int repeat) {

		URL url;
		URLConnection connection;
        String hostName = strUrl.split(":")[0];
        int port = Integer.parseInt(strUrl.split(":")[1]);
        String varifyURL = "http://www.scut.edu.cn/";
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port));  
		try {
			url = new URL(varifyURL);
			connection = url.openConnection(proxy);
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			connection.connect();

		}
		catch(SocketTimeoutException se) {

    		return new Object[] {repeat+1};         		
    	}
    	catch(Exception e) {
    		return new Object[] {-1};
    	}
    	return new Object[] { 0, url, connection };
	}

	public Object[] fetch(URL url, URLConnection connection) {

		String html = new String();
		try {

	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine())!=null) {
                html += line;
            }
		}
		catch (IOException e) {
			return new Object[] {-1};
		}
		return new Object[] {0, html};

	}

	public synchronized void write2txt(String s, File file) {
		
		try {
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(s+ "\r\n");
			bw.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}


	public void log(String entry) {
			Log.d("$ Thread:"+Thread.currentThread().getId()+" $ " + entry);
	}

	public static void main(String[] args) {

		Asyn_Http h = new Asyn_Http();
		while(true) {
 			Object[] o = h.proxy_connect("182.90.40.183	80", 0);
			Log.d("connect");
			if((int)o[0] != 0) {
				continue;
			}

			o = h.fetch((URL)o[1], (URLConnection)o[2]);
			if((int)o[0] != -1) {
				Log.d((String)o[1]);
			}
			else {
				Log.d("fetch");
			}
		}
	}

}