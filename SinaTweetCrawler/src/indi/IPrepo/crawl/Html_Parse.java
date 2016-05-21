package indi.IPrepo.crawl;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Html_Parse {

	public Vector<String> parse(String html){

		Vector<String> IPs = new Vector<String>();
		Pattern p = Pattern.compile("<td>\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}</td>\\n*\\s*<td>\\d{1,5}</td>"); //<td>\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3}</td>\n+?\s+?<td>\d{1,5}</td>
//		Pattern p = Pattern.compile("<td>\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3}</td>\n+?\s+?<td>\d{1,5}</td>");
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

	public String proxy_parse(String html) {

		Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
		Matcher m = p.matcher(html);
		String s;
		if(m.find()) {
			s = m.group();
			return s;
		}
		else {
			return "0";
		}
	}

}

