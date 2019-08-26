import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebsiteOperations {
	
	public WebsiteOperations(ArrayList<String> approvedLinks, ArrayList<String> disApprovedLinks,String standartURL) {
		super();
		this.approvedLinks = approvedLinks;
		this.disApprovedLinks = disApprovedLinks;
		this.standartURL = standartURL;
	}

	private String baseUri = "D://wiki/info/";
	private ArrayList<String> approvedLinks;
	private ArrayList<String> disApprovedLinks;
	private String standartURL;

	
	public Document getSite(String url){
		Document html = null;
		try {
		
			html = Jsoup.connect(absolutePublicURL(url)).get();
			
		} catch (HttpStatusException e){
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return html;
	}
	
	private String absolutePublicURL(String url){
		
		if(url.contains("http")){
			
		}else{
			if(url.contains("//")){
				//System.out.println("//");
				url = "https:"+url;
			}else{
			//	System.out.println("standartURL");
				url = standartURL+url;
			}
		}
		if(url.contains("/revision")){
			return url.substring(0, url.indexOf("/revision"));
		}else{
			return url;
		}
	}
	
	
	
	
	
	
	
	

	
	
	
	
	
}
