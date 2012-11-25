package net.dahanne.spring.android.ch3.message.converters.jackson;


import android.app.Activity;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

	private IfConfigMeJson all;

	//inject the view to the activity layout
    @ViewById(R.id.result_text)
    TextView resultTextView;
    
    //inject the Rest service that wraps RestTemplate
    @RestService
	IfConfigMeRestClient restClient;
    
    //Use the Rest Service in a background thread
    @Background
    @AfterInject
    void getAllInfo() {
    	all = restClient.getAll();
    }
    
    //wait a few seconds for the service to finish and show the results
	@UiThread(delay = 5000)
	@AfterViews
	void afterViews() {
		resultTextView.setText("Your IP is : "+all.getIpAddr());
	}

}
