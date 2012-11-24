package net.dahanne.spring.android.firstexample;

import org.springframework.http.converter.StringHttpMessageConverter;
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

		AsyncTask<String, Void, String> simpleGetTask =  new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... params) {
				// executed by a background thread

				// create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				
				// add the String message converter, since the result of the call will be a String
				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
				
				// Make the HTTP GET request on the url (params[0]), marshaling the response to a String
				return restTemplate.getForObject(params[0], String.class);
			}
			
			@Override
			protected void onPostExecute(String result) {
				// executed by the UI thread once the background thread is done getting the result
				resultTextView.setText(result);
			}
			
		};
		
		String url = "http://ifconfig.me/all";
		// triggers the task; it will update the resultTextView once it is done
		simpleGetTask.execute(url);
	}
}
