package indi.IPrepo.crawl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;

class Asyn_IP {

	/**
	 * Initilize the asyn service, open the file handler
	 * @param AsynService as
	 */
	public Asyn_IP(AsynService as) {

		this.asynService = as;
		this.starttime = System.currentTimeMillis();
		this.run = true;
		file = new File(Context.sharedContext().getValueByName("urlPath"));
		file.mkdirs();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}		
		file = new File(Context.sharedContext().getValueByName("urlPath")+"/url.txt");		
	}

	/**
	 * Start to crawl the IP library
	 */
	public void begin() {

		asyn_connectURL("http://www.xicidaili.com/nn/", 0, false);
		asyn_connectURL("http://www.xicidaili.com/nt/", 0, false);
		asyn_connectURL("http://www.xicidaili.com/wn/", 0, false);
		asyn_connectURL("http://www.xicidaili.com/wt/", 0, false);
		asyn_connectURL("http://www.xicidaili.com/qq/", 0, false);				
	}

	private void asyn_connectURL(final String strUrl, final int repeat, final boolean isProxy) {

		String method = "connect";
		if(isProxy) {
			method = "proxy_connect";
		}
		asynService.addWork(new Asyn_Http(), method, new Object[] {strUrl, repeat}, new AsynCallBack() {

	        @Override   
	        public void doNotify() {  
	        	Object[] res = (Object[])methodResult;
	        	int errno = (int)res[0];
	        	if(errno == -1) {
	        		asyn_debug("Connecting $ " + strUrl +" $ Error: open connection failed");
	        		return;
	        	}
	        	if(errno == 0) {
	        		asyn_debug("Connecting $ " + strUrl +" $ Successful");
	        		asyn_fetchURL(strUrl, (URL)res[1], (URLConnection)res[2], 0, isProxy);
	        		return;
	        	}
	        	if(errno <= 3) {
	        		asyn_debug("Connecting $ " + strUrl +" $ Warning: connection timeout for "+ (repeat+1) +" times");
	        		asyn_connectURL(strUrl, repeat+1, isProxy);
	        		return;
	        	}
	        }
		});
	}

	private void asyn_fetchURL(final String strUrl, final URL url, URLConnection connection, final int repeat, final boolean isProxy) {

		asynService.addWork(new Asyn_Http(), "fetch", new Object[] {url, connection}, new AsynCallBack() {

	        @Override   
	        public void doNotify() {  

	        	Object[] res = (Object[])methodResult;
	        	int errno = (int)res[0];

	        	if(errno == -1) {
	        		if(repeat == 3) {
	        			asyn_debug("Fetching $ " + strUrl +" $ Error: get html page failed");
	        			return;
	        		}
	        		asyn_debug("Fetching $ " + strUrl +" $ Warning: get html page timeout fot "+(repeat+1) + " times");
	        		asyn_fetchURL(strUrl, url, connection, repeat+1, isProxy);
	        	}	       
	        	else {
	        		asyn_debug("Fetching $ " + strUrl +" $ Successful");
	        		asyn_parseURL(strUrl, (String)res[1], isProxy);
	        	} 	
	        }
		});

	}


	private void asyn_parseURL(final String strUrl, String html, final boolean isProxy) {

		if(isProxy) {
			asyn_write2txt(strUrl);

		asynService.addWork(new Html_Parse(), "parse", new Object[] { html }, new AsynCallBack() {
	        @Override   
	        public void doNotify() {  

				@SuppressWarnings("unchecked")
				Vector<String> IPs = (Vector<String>)methodResult;
				synchronized(lock) {
					int size = 0;
					for(String i : IPs) {
						if(!urlFound.contains(i)) {
							size ++;
							if(size >20) {
								return;
							}
							urlFound.add(i);
							asyn_verifyURL(i);
						}
					}
				}
	        }
		});
	}

	private void asyn_verifyURL(String str) {

		asyn_connectURL(str, 0, true);
	}

	private void asyn_write2txt(String entry) {

		asynService.addWork(new Asyn_Http(), "write2txt", new Object[] { entry, file }, new AsynCallBack() {
			@Override
			public void doNotify() {
				asyn_debug( "Find vaild IP $ " + entry);
				synchronized(lock) {
					if(run == true) {
						if(run == false) {
							return;
						}
						run = false;
						asyn_close();
					}
				}				
			}
		});
	}

	private void asyn_debug(String entry) {

		asynService.addWork(new Asyn_Http(), "log", new Object[] {entry});
	}

	private void asyn_close() {

		asynService.addWork(new closeService(), "close", new Object[]{});
	}

	protected class closeService {
	
		public void close() {

			new Thread() {
				@Override
				public void run() {
					long total = 0;
					long execute = 0;
					long  callback = 0;
					while(true) {						
						try {
							Thread.sleep(3000);			
							long t = asynService.getRunStatMap().get("total");
							long e = asynService.getRunStatMap().get("execute");
							long c = asynService.getRunStatMap().get("callback");
							Log.d("total" + total +"t" +t);
							Log.d("execute" + execute +"e" +t);
							Log.d("callback" + callback + "c" + c);
							if(total != t || execute != e || callback != c || !asynService.getRunStatMap().get("total").equals(asynService.getRunStatMap().get("execute"))) {
								total = t;
								execute = e;
								callback = c;
							}
							else {
								Log.l("finish crawling");
								long endtime = System.currentTimeMillis();	
								Log.l((double)(endtime-starttime)/60000 + "mins");	
								asynService.close();
								break;
							}
						}catch(InterruptedException e) {

						}
					}

				}
			}.start();		
		}

	}



	private Set<String> urlFound = new HashSet<String>();
	private AsynService asynService;
	private long starttime;
	private File file;
	private Object lock = new Object();
	private boolean run;
	
	public static void main(String[] args) {


        AsynService asynService =  AsynServiceImpl.getService(300, 10000L, 8, 2, 3000L);   
        //asynService.setWorkQueueFullHandler(new CacheAsynWorkHandler()); 
        asynService.init();  
		Asyn_IP crawl = new Asyn_IP(asynService);
		crawl.begin();
	}
}