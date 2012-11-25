/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dahanne.android.google.client;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This class is based on the FacebookActivity by Roy Clarkson
 * 
 */
public class GoogleActivity extends AbstractAsyncActivity {

	protected static final String TAG = GoogleActivity.class.getSimpleName();

	private ConnectionRepository connectionRepository;

	private GoogleConnectionFactory connectionFactory;

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.google_activity_layout);
		this.connectionRepository = getApplicationContext().getConnectionRepository();
		this.connectionFactory = getApplicationContext().getGoogleConnectionFactory();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (isConnected()) {
			showGoogleOptions();
		} else {
			showConnectOption();
		}
	}


	// ***************************************
	// Private methods
	// ***************************************
	private boolean isConnected() {
		return connectionRepository.findPrimaryConnection(Google.class) != null;
	}

	private void disconnect() {
		this.connectionRepository.removeConnections(this.connectionFactory.getProviderId());
	}

	private void showConnectOption() {
		String[] options = { "Connect" };
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
		ListView listView = (ListView) this.findViewById(R.id.google_activity_options_list);
		listView.setAdapter(arrayAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
				switch (position) {
				case 0:
					displayGoogleAuthorization();
					break;
				default:
					break;
				}
			}
		});
	}

	private void showGoogleOptions() {
		Connection<Google> findPrimaryConnection = connectionRepository.findPrimaryConnection(Google.class);
		Log.e(TAG, findPrimaryConnection.getDisplayName());
		
		String[] options = { "Disconnect", "Profile" };
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
		ListView listView = (ListView) this.findViewById(R.id.google_activity_options_list);
		listView.setAdapter(arrayAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
				Intent intent;
				switch (position) {
				case 0:
					disconnect();
					showConnectOption();
					break;
				case 1:
					intent = new Intent();
					intent.setClass(parentView.getContext(), GoogleProfileActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}
		});
	}

	private void displayGoogleAuthorization() {
		Intent intent = new Intent();
		intent.setClass(this, GoogleWebOAuthActivity.class);
		startActivity(intent);
		finish();
	}

}
