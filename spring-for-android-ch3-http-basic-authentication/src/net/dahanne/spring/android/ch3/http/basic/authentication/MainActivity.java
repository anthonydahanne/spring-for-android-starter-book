package net.dahanne.spring.android.ch3.http.basic.authentication;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
				// Set the username and password for creating a Basic Auth request
				HttpAuthentication authHeader = new HttpBasicAuthentication("s4a", "s4a");
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.setAuthorization(authHeader);
				HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

				RestTemplate restTemplate = new RestTemplate();

				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

				try {
				    // Make the HTTP GET request to the Basic Auth protected URL
				    ResponseEntity<String> response = restTemplate.exchange(params[0], HttpMethod.GET, requestEntity, String.class);
				    return response.getBody();
				} catch (HttpClientErrorException e) {
				    // Handle 401 Unauthorized response
					Log.e("MainActivity",e.getLocalizedMessage(),e);
					return "Wrong credentials";
				}
			}
			
			@Override
			protected void onPostExecute(String result) {
				// executed by the UI thread once the background thread is done getting the result
				resultTextView.setText(result);
			}
			
		};
		
		String url = "http://restfulapp.appspot.com/hello";
		// triggers the task; it will update the resultTextView once it is done
		simpleGetTask.execute(url);
	}
}
