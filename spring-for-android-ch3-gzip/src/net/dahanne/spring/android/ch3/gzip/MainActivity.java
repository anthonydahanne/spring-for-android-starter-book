package net.dahanne.spring.android.ch3.gzip;


import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final TextView resultTextView = (TextView) findViewById(R.id.result_text);

		AsyncTask<String, Void, IfConfigMeJson> simpleGetTask =  new AsyncTask<String, Void, IfConfigMeJson>() {
			@Override
			protected IfConfigMeJson doInBackground(String... params) {
				// Add the gzip Accept-Encoding header
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.setAcceptEncoding(ContentCodingType.IDENTITY);
			    //requestHeaders.setAcceptEncoding(ContentCodingType.GZIP);
				HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
				RestTemplate restTemplate = new RestTemplate();
				MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
				restTemplate.getMessageConverters().add(mappingJacksonHttpMessageConverter);
				ResponseEntity<IfConfigMeJson> response = restTemplate.exchange(params[0], HttpMethod.GET, requestEntity, IfConfigMeJson.class);
				return response.getBody();
			}
			
			@Override
			protected void onPostExecute(IfConfigMeJson result) {
				String resultAsString =  new StringBuilder().append("We said we supported the following encoding for the response : ")
						                                    .append(result.getEncoding())
						                                    .append("\n Here is your IP : ")
						                                    .append(result.getIpAddr()).toString();
				resultTextView.setText(resultAsString );
			}
			
		};
		
		String url = "http://ifconfig.me/all.json";
		// triggers the task; it will update the resultTextView once it is done
		simpleGetTask.execute(url);
	}
}
