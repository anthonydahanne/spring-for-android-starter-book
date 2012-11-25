package net.dahanne.spring.android.ch3.message.converters.jackson;


import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
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

		AsyncTask<String, Void, IfConfigMeJson> simpleGetTask =  new AsyncTask<String, Void, IfConfigMeJson>() {
			@Override
			protected IfConfigMeJson doInBackground(String... params) {
				RestTemplate restTemplate = new RestTemplate();
				MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
				restTemplate.getMessageConverters().add(mappingJacksonHttpMessageConverter);
				return restTemplate.getForObject(params[0], IfConfigMeJson.class);
			}
			
			@Override
			protected void onPostExecute(IfConfigMeJson result) {
				String resultAsString =  new StringBuilder().append("Your current IP is : ")
						                                    .append(result.getIpAddr()).toString();
				resultTextView.setText(resultAsString );
			}
			
		};
		
		String url = "http://ifconfig.me/all.json";
		// triggers the task; it will update the resultTextView once it is done
		simpleGetTask.execute(url);
	}
}
