package net.dahanne.spring.android.ch3.message.converters.feedreader;
import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.rss.Channel;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.rss.Item;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final WebView resultTextView = (WebView) findViewById(R.id.result_text);

		AsyncTask<String, Void, Channel> simpleGetTask =  new AsyncTask<String, Void, Channel>() {
			@Override
			protected Channel doInBackground(String... params) {
				RestTemplate restTemplate = new RestTemplate();
				// Configure the RSS message converter.
                RssChannelHttpMessageConverter rssChannelConverter = new RssChannelHttpMessageConverter();
                rssChannelConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_XML));

                // Add the RSS message converter to the RestTemplate instance
                restTemplate.getMessageConverters().add(rssChannelConverter);
				
				// Make the HTTP GET request on the url (params[0]), marshaling the response to a String
				return restTemplate.getForObject(params[0], Channel.class);
			}
			
			@Override
			protected void onPostExecute(Channel result) {
				//get the latest article from the blog
				Item item = (Item) result.getItems().get(1);
				
				// load the content of the article into the WebView
				resultTextView.loadData(item.getContent().getValue(), "text/html", "UTF-8");
			}
			
		};
		
		String url = "http://blog.dahanne.net/feed/";
		// triggers the task; it will update the resultTextView once it is done
		simpleGetTask.execute(url);
	}

}
