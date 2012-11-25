package net.dahanne.spring.android.ch3.message.converters.simplexml;


import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final TextView resultTextView = (TextView) findViewById(R.id.result_text);

		AsyncTask<String, Void, IfConfigMeXml> simpleGetTask =  new AsyncTask<String, Void, IfConfigMeXml>() {
			@Override
			protected IfConfigMeXml doInBackground(String... params) {
				RestTemplate restTemplate = new RestTemplate();
				SimpleXmlHttpMessageConverter mappingSimpleXmlHttpMessageConverter = new SimpleXmlHttpMessageConverter();
				restTemplate.getMessageConverters().add(mappingSimpleXmlHttpMessageConverter);
				return restTemplate.getForObject(params[0], IfConfigMeXml.class);
			}
			
			@Override
			protected void onPostExecute(IfConfigMeXml result) {
				String resultAsString =  new StringBuilder().append("Your current IP is : ")
						                                    .append(result.getIpAddr()).toString();
				resultTextView.setText(resultAsString );
			}
			
		};
		
		String url = "http://ifconfig.me/all.xml";
		// triggers the task; it will update the resultTextView once it is done
		simpleGetTask.execute(url);
	}
}
