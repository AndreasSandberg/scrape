package se.sandberg.message;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Scraper {

	public static void main(String[] args) {
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "fatal");

		Scraper s = new Scraper();
		try {
			String url = "ENTER URL HERE";
			s.scrape(url);
		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void scrape(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		Executor e = Executors.newFixedThreadPool(8);
		WebClient webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setDownloadImages(false);
		HtmlPage page = webClient.getPage(url);
		List<HtmlAnchor> hrefs = new ArrayList<>();
		for(HtmlAnchor href : page.getAnchors()){
			if(href.getHrefAttribute().contains("/c/")){
				hrefs.add(href);
			}
		}
		webClient.getOptions().setJavaScriptEnabled(false);
		final Map<String, String> result = new ConcurrentHashMap<String, String>();
		for(final HtmlAnchor href : hrefs){
			e.execute(new Runnable(){
				@Override
				public void run() {
					Page page2;
					try {
						System.out.println("Running " + href.getHrefAttribute());
						page2 = href.click();
						result.put("",page2.getUrl().toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		for(String s : result.values()){
			System.err.println(s);
		}
	}
}
