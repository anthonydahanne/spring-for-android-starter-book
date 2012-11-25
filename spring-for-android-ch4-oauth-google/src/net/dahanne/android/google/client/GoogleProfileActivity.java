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


import java.net.URL;

import org.springframework.social.ExpiredAuthorizationException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.legacyprofile.LegacyGoogleProfile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class is based on the FacebookProfileActivity by Roy Clarkson
 */
public class GoogleProfileActivity extends Activity{

	protected static final String TAG = GoogleProfileActivity.class.getSimpleName();

	private ConnectionRepository connectionRepository;

	private Google google;
	
	private ProgressDialog progressDialog;

	private boolean destroyed = false;
	
	private ListView listView;

	private ImageView profilePictureView;
	
	private WebView aboutMeView;
	
	private Bitmap profileBitmap;
	
	private String aboutMe;
	
	
	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.google_profile_layout);
		listView = (ListView) findViewById(R.id.google_profile_list);
		profilePictureView = (ImageView) findViewById(R.id.image_view);
		aboutMeView = (WebView) findViewById(R.id.text_view_about_me);
		
		
		this.google = getApplicationContext().getConnectionRepository().findPrimaryConnection(Google.class)
				.getApi();
		
		this.connectionRepository = getApplicationContext().getConnectionRepository();
	}

	@Override
	public void onStart() {
		super.onStart();
		new FetchProfileTask().execute();
	}

	// ***************************************
	// Private methods
	// ***************************************
	private void showResult(LegacyGoogleProfile googleProfile) {
		if (googleProfile != null) {
			GoogleProfileListAdapter adapter = new GoogleProfileListAdapter(this, googleProfile);
			listView.setAdapter(adapter);
			if(profileBitmap !=null) {
				profilePictureView.setImageBitmap(profileBitmap);
			}
			if (aboutMe != null) {
				aboutMeView.loadData(aboutMe, "text/html", "US-ASCII");
			}
		}
	}
	

	// ***************************************
	// Private classes
	// ***************************************
	private class FetchProfileTask extends AsyncTask<Void, Void, LegacyGoogleProfile> {

		private Exception exception;
		
		@Override
		protected void onPreExecute() {
			showProgressDialog("Fetching profile...");
		}

		@Override
		protected LegacyGoogleProfile doInBackground(Void... params) {
			try {
				LegacyGoogleProfile userProfile = google.userOperations().getUserProfile();
				aboutMe = google.personOperations().getGoogleProfile().getAboutMe();
				profileBitmap = BitmapFactory.decodeStream(new URL(userProfile.getProfilePictureUrl()).openConnection().getInputStream());
				return userProfile;
				
			} catch (ExpiredAuthorizationException e) {
				//the connection has expired, we try to refresh it
				try {
					Connection<Google> primaryConnection = connectionRepository.getPrimaryConnection(Google.class);
					primaryConnection.refresh();
				} catch (Exception e2) {
					Log.e(TAG, e2.getLocalizedMessage(), e2);
					exception = e2;
				}
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				exception = e;
			}
			return null;
		}

		@Override
		protected void onPostExecute(LegacyGoogleProfile profile) {
			dismissProgressDialog();
			if(profile != null) {
				showResult(profile);
			} else if (exception != null) {
				Toast.makeText(GoogleProfileActivity.this, "Something went wrong while fetching the profile : " + exception.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

	}


	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public MainApplication getApplicationContext() {
		return (MainApplication) super.getApplicationContext();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.destroyed = true;
	}

	// ***************************************
	// Public methods
	// ***************************************
	public void showLoadingProgressDialog() {
		this.showProgressDialog("Loading. Please wait...");
	}

	public void showProgressDialog(CharSequence message) {
		if (this.progressDialog == null) {
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setIndeterminate(true);
		}

		this.progressDialog.setMessage(message);
		this.progressDialog.show();
	}

	public void dismissProgressDialog() {
		if (this.progressDialog != null && !this.destroyed) {
			this.progressDialog.dismiss();
		}
	}
	

}
